/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.solr.client;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SolrQueryTransformer extends AbstractSAXTransformer {

    private AttributesImpl attributes;
    private int rows=10,found = 0,start = 0;
    private String queryString;

    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
        this.attributes = new AttributesImpl();
        String userRows =request.getParameter("rows");
        if (null!=userRows)
            rows=Integer.parseInt(userRows);
        else
            rows=10;
        LinkedHashSet set = new LinkedHashSet();
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String element = (String) keys.nextElement();
            if(!"start".equals(element)){
                String value = (String) request.getParameter(element);
                set.add(element+"="+value);
            }
        }
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = set.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            buffer.append(element);
            buffer.append("&");
        }
        queryString = buffer.toString();
    }

    public void startElement(String uri, String name, String raw, Attributes attr) throws SAXException {
        super.startElement(uri, name, raw, attr);
        if("result".equals(name)){
            for (int i = 0; i < attr.getLength(); i++) {
                String attribute = attr.getLocalName(i);
                if ("numFound".equals(attribute)){
                    found = Integer.parseInt(attr.getValue(i));
                }else if ("start".equals(attribute)){
                    start= Integer.parseInt(attr.getValue(i));
                }
            }
            int pages = (int) Math.ceil((float)found / (float)rows);
            attributes.clear();
            attributes.addAttribute("", "found", "found",
                    "CDATA", String.valueOf(found));
            attributes.addAttribute("", "start", "start",
                    "CDATA", String.valueOf(start));
            attributes.addAttribute("", "rows", "rows",
                    "CDATA", String.valueOf(rows));
            attributes.addAttribute("", "pages", "pages",
                    "CDATA", String.valueOf(pages));
            contentHandler.startElement("", "paginator", "paginator", attributes);
            for (int i = 0; i < pages; i++) {
                attributes.clear();
                attributes.addAttribute("", "id", "id",
                        "CDATA", String.valueOf(i+1));
                attributes.addAttribute("", "queryString", "queryString",
                        "CDATA", queryString+"start="+(i*rows));
                if ((rows*i<=start&start<rows*(i+1)))
                    attributes.addAttribute("", "current", "current",
                            "CDATA", "true");
                contentHandler.startElement("", "page", "page", attributes);
                contentHandler.endElement("", "page", "page");
            }
            contentHandler.endElement("", "paginator", "paginator");
        }
    }

}
