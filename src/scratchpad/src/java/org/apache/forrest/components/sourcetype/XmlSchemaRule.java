package org.apache.forrest.components.sourcetype;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A Rule which checks the value of the xsi:schemaLocation and xsi:noNamespaceSchemaLocation
 * attributes.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class XmlSchemaRule implements SourceTypeRule
{
    protected String schemaLocation;
    protected String noNamespaceSchemaLocation;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        schemaLocation = configuration.getAttribute("schema-location", null);
        noNamespaceSchemaLocation = configuration.getAttribute("no-namespace-schema-location", null);
        if (schemaLocation == null && noNamespaceSchemaLocation == null)
            throw new ConfigurationException("Missing schema-location and/or no-namespace-schema-location attribute on w3c-xml-schema element at " + configuration.getLocation());
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        if (schemaLocation != null && noNamespaceSchemaLocation != null
                && schemaLocation.equals(sourceInfo.getXsiSchemaLocation())
                && noNamespaceSchemaLocation.equals(sourceInfo.getXsiNoNamespaceSchemaLocation()))
            return true;
        else if (schemaLocation != null && noNamespaceSchemaLocation == null && schemaLocation.equals(sourceInfo.getXsiSchemaLocation()))
            return true;
        else if (schemaLocation == null && noNamespaceSchemaLocation != null && noNamespaceSchemaLocation.equals(sourceInfo.getXsiNoNamespaceSchemaLocation()))
            return true;
        else
            return false;
    }

}
