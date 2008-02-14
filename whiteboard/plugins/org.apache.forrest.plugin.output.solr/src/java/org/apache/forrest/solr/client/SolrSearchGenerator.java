/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.ServiceableGenerator;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SolrSearchGenerator extends ServiceableGenerator {
    private static final String QUERY_PARAM = "q";

    public static final String AGENT = "forrest[" + SolrSearchGenerator.class.getName() + "]";
    
    private static final String DESTINATION_URL = "destinationUrl";

    private static final String SOLR_UPDATE_URL = "http://localhost:8983/solr/select";

    private SAXParser parser;

    private String destination;

    private HttpClient client;

    private Request request;

    private String query;

    private Map map;

    private int statusCode;


    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
        map= new HashMap();
        this.request = ObjectModelHelper.getRequest(objectModel);
        destination = par.getParameter(DESTINATION_URL, SOLR_UPDATE_URL);
        query = request.getParameter(QUERY_PARAM);
        if (null== query){
            query = "DEFAULT_QUERY_STRING";
        }
        // cocoon-2.1.x compatible
        for (Enumeration iter = request.getParameterNames(); iter.hasMoreElements();) {
            String name = (String) iter.nextElement();
            getLogger().debug("parameter "+name);
            map.put(name, request.getParameter(name));
        }
    }

    public void generate() throws IOException, SAXException, ProcessingException {
        client = new HttpClient();
        PostMethod filePost = preparePost();
        statusCode = client.executeMethod(filePost);
        try {
            parser = (SAXParser) manager.lookup(SAXParser.ROLE);
            parser.parse(new InputSource(filePost.getResponseBodyAsStream()),contentHandler);
        } catch (ServiceException e) {
            throw new ProcessingException("parser lookup faild.");
        }
        
    }

    private PostMethod preparePost() {
        PostMethod filePost = new PostMethod(destination);
        filePost.addRequestHeader("User-Agent", AGENT);
        Iterator keys = map.keySet().iterator();
        HashSet set = new HashSet();
        while (keys.hasNext()) {
            String element = (String) keys.next();
            if(!QUERY_PARAM.equals(element)){
                String value = (String) map.get(element);
                set.add(new NameValuePair(element,value));
            }
        }
        //make sure we send the query (even if null) to get a response
        set.add(new NameValuePair(QUERY_PARAM,query));
        for (Iterator iter = set.iterator(); iter.hasNext();) {
            filePost.addParameter((NameValuePair) iter.next());
        }
        return filePost;
    }

}
