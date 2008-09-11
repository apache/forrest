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
package org.apache.forrest.dispatcher;

import java.beans.Beans;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.cocoon.xml.dom.DOMBuilder;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.forrest.dispatcher.lenya.xml.NamespaceHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
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

    private SourceResolver resolver;

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
    public Document getDocument(String uri) throws Exception {
        Document doc = org.apache.forrest.dispatcher.util.SourceUtil.readDOM(
                uri, this.resolver);
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

    public Transformer createTransformer()
            throws TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
        // "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        return transformer;

    }

    public Transformer createTransformer(Source source)
            throws TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(source);
        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
        // "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        return transformer;

    }

    /**
     * void setAttributesDOM(Attributes attr, Node xpathNode) sets all
     * attributes to the node (like <xsl:copy-of select="@*"/>)
     * 
     * @param attr
     * @param xpathNode
     * @throws DOMException
     */
    public void setAttributesDOM(Attributes attr, Node xpathNode)
            throws DOMException {
        for (int i = 0; i < attr.getLength(); i++) {
            String localName = attr.getLocalName(i);
            String value = attr.getValue(i);
            ((Element) xpathNode).setAttribute(localName, value);
        }
    }

    public String setAttributesXPath(Attributes attr, String path)
            throws DOMException {
        if (attr.getLength() > 0) {
            String xpath = "[";
            for (int i = 0; i < attr.getLength(); i++) {
                String localName = attr.getLocalName(i);
                String value = attr.getValue(i);
                xpath = xpath + "@" + localName + "='" + value + "'";
                if (i < (attr.getLength() - 1)) {
                    xpath = xpath + " and ";
                }
            }
            xpath = xpath + "]";
            path = path + xpath;
        }
        return path;

    }

    /**
     * setAttributesXPathWithPosition(Attributes attr, String path, int position) generates an XPath with the supplied attributes
     * and add the condition on the position() to avoid confusion with hooks using the same name.
     * @param attr Attributes of the XPath
     * @param path The path
     * @param position The position of the node
     * @throws DOMException
     */
    public String setAttributesXPathWithPosition(Attributes attr, String path, int position)
            throws DOMException {
        String xpath = "[";
        if (attr.getLength() > 0) {
            for (int i = 0; i < attr.getLength(); i++) {
                String localName = attr.getLocalName(i);
                String value = attr.getValue(i);
                xpath = xpath + "@" + localName + "='" + value + "'";
                if (i < (attr.getLength() )) {
                    xpath = xpath + " and ";
                }
            }
            xpath = xpath + " position()=" + position + "]";
            path = path + xpath;
        }
        return path;

    }

    public DispatcherHelper(SourceResolver resolver)
            throws ParserConfigurationException {
        this.resolver = resolver;
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
