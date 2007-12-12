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
package org.apache.forrest.dispatcher.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.forrest.dispatcher.lenya.xml.DocumentHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SourceUtil {
    /**
     * Reads a DOM from a source.
     * @param sourceUri The source URI.
     * @param manager The service manager.
     * @return A document or <code>null</code> if the source does not exist.
     * @throws ServiceException if an error occurs.
     * @throws SourceNotFoundException if an error occurs.
     * @throws ParserConfigurationException if an error occurs.
     * @throws SAXException if an error occurs.
     * @throws IOException if an error occurs.
     */
    public static Document readDOM(String sourceUri, ServiceManager manager)
            throws ServiceException, SourceNotFoundException, ParserConfigurationException,
            SAXException, IOException {
        SourceResolver resolver = null;
        Source source = null;
        Document document = null;
        try {

            resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
            source = resolver.resolveURI(sourceUri);

            if (source.exists()) {
                document = DocumentHelper.readDocument(source.getInputStream());
            }
        } finally {
            if (resolver != null) {
                if (source != null) {
                    resolver.release(source);
                }
                manager.release(resolver);
            }
        }
        return document;
    }
}
