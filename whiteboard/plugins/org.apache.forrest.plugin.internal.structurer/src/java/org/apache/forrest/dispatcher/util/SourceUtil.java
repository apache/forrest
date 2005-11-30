package org.apache.forrest.dispatcher.util;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.lenya.xml.DocumentHelper;
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
