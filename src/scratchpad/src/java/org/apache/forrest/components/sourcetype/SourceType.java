package org.apache.forrest.components.sourcetype;

import org.apache.avalon.framework.configuration.*;

import java.util.*;

/**
 * Represents a sourcetype. A sourcetype has a name and a number of rules
 * which are used to determine if a certain document is of this sourcetype.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class SourceType implements Configurable
{
    protected List rules = new ArrayList();
    protected String name;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        name = configuration.getAttribute("name");

        Configuration[] ruleConfs = configuration.getChildren();
        for (int i = 0; i < ruleConfs.length; i++)
        {
            SourceTypeRule rule;
            if (ruleConfs[i].getName().equals("document-declaration"))
                rule = new DocDeclRule();
            else if (ruleConfs[i].getName().equals("processing-instruction"))
                rule = new ProcessingInstructionRule();
            else if (ruleConfs[i].getName().equals("w3c-xml-schema"))
                rule = new ProcessingInstructionRule();
            else if (ruleConfs[i].getName().equals("document-element"))
                rule = new DocumentElementRule();
            else
                throw new ConfigurationException("Unsupported element " + ruleConfs[i].getName() + " at "
                        + ruleConfs[i].getLocation());

            rule.configure(ruleConfs[i]);
            rules.add(rule);
        }
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        Iterator rulesIt = rules.iterator();
        boolean matches = true;
        while (rulesIt.hasNext())
        {
            SourceTypeRule rule = (SourceTypeRule)rulesIt.next();
            matches = matches && rule.matches(sourceInfo);
            if (!matches)
                return false;
        }
        return matches;
    }

    public String getName()
    {
        return name;
    }
}
