package org.apache.forrest.components.sourcetype;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A Rule which checks the local name and namespace of the document element.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class DocumentElementRule implements SourceTypeRule
{
    protected String localName;
    protected String namespace;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        localName = configuration.getAttribute("local-name", null);
        namespace = configuration.getAttribute("namespace", null);
        if (localName == null && namespace == null)
            throw new ConfigurationException("Missing local-name and/or namespace attribute on document-element element at " + configuration.getLocation());
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        if (localName != null && namespace != null
                && localName.equals(sourceInfo.getDocumentElementLocalName())
                && namespace.equals(sourceInfo.getDocumentElementNamespace()))
            return true;
        else if (localName != null && localName.equals(sourceInfo.getDocumentElementLocalName()))
            return true;
        else if (namespace != null && namespace.equals(sourceInfo.getDocumentElementNamespace()))
            return true;
        else
            return false;
    }

}
