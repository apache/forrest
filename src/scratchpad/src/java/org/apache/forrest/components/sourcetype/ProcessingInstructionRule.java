package org.apache.forrest.components.sourcetype;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A rule which checks that a processing instruction with certain data is present.
 */
public class ProcessingInstructionRule implements SourceTypeRule
{
    protected String target;
    protected String data;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        target = configuration.getAttribute("target");
        data = configuration.getAttribute("data", null);
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        if (sourceInfo.hasProcessingInstruction(target))
        {
            if (sourceInfo.getProcessingInstructionData(target) == null && data == null)
                return true;
            if (sourceInfo.getProcessingInstructionData(target) != null && sourceInfo.getProcessingInstructionData(target).equals(data))
                return true;
        }
        return false;
    }

}
