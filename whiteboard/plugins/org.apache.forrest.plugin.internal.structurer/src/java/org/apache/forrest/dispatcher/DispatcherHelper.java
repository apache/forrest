/*
 * Copyright 1999-2005 The Apache Software Foundation or its licensors,
 * as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher;

import java.beans.Beans;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.xml.IncludeXMLConsumer;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.cocoon.xml.dom.DOMBuilder;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DispatcherHelper extends Beans {
    /**
     * The namespace for the dispatcher configuration is
     * http://apache.org/forrest/templates/1.0
     */
    static public final String DISPATCHER_NAMESPACE_URI = "http://apache.org/forrest/templates/1.0";

    /**
     * The namespace prefix for this namespace.
     */
    static public final String DISPATCHER_PREFIX = "forrest";

    private NamespaceHelper namespaceHelper;

    private ServiceManager manager;

    /**
     * Create a DOM representation of this dispatcher.
     * 
     * @return A DOM document.
     * @throws ServiceException
     *             if a general error occurs.
     * @throws SourceNotFoundException
     *             if the dispatcher's source can not be found
     * @throws ParserConfigurationException
     *             if an error occurs during XML parsing.
     * @throws SAXException
     *             if an error occurs during XML parsing.
     * @throws IOException
     *             if an error occurs during source access..
     */
    protected Document getDocument(String uri) throws Exception {
        Document doc = org.apache.forrest.dispatcher.util.SourceUtil.readDOM(uri,
                this.manager);
        if (doc != null) {
            this.namespaceHelper = new NamespaceHelper(
                    DISPATCHER_NAMESPACE_URI, DISPATCHER_PREFIX, doc);
        }
        return namespaceHelper.getDocument();
    }

    /**
     * Creates a new document with the param rootName
     * 
     * @param rootName
     * @return
     * @throws SAXException
     */
    public Document createDocument(String rootName) throws SAXException {
        DOMBuilder builder = new DOMBuilder();
        builder.startDocument();
        builder.startElement("", rootName, rootName, XMLUtils.EMPTY_ATTRIBUTES);
        builder.endElement("", rootName, rootName);
        builder.endDocument();
        return builder.getDocument();

    }

    public DispatcherHelper(ServiceManager manager)
            throws ParserConfigurationException {
        this.manager = manager;
        this.namespaceHelper = new NamespaceHelper(DISPATCHER_NAMESPACE_URI,
                DISPATCHER_PREFIX, "foo");
    }

    public NamespaceHelper getNamespaceHelper() {
        return namespaceHelper;
    }

    public void setNamespaceHelper(NamespaceHelper namespaceHelper) {
        this.namespaceHelper = namespaceHelper;
    }

    public void setNamespaceHelper(String uri, String prefix, String localName)
            throws ParserConfigurationException {
        this.namespaceHelper = new NamespaceHelper(uri, prefix, localName);
    }

}
