package org.apache.forrest.components.sourcetype;

import org.apache.avalon.framework.configuration.*;

/**
 * Rule which checks that the public id has a certain value.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class DocDeclRule implements SourceTypeRule
{
    protected String publicId;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        publicId = configuration.getAttribute("public-id");
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        if (publicId.equals(sourceInfo.getPublicId()))
            return true;
        else
            return false;
    }
}
