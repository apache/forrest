package org.apache.forrest.components.sourcetype;

import org.apache.avalon.framework.configuration.Configurable;

/**
 * Interface to be implemented by all soucetype-defining rules.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public interface SourceTypeRule extends Configurable
{
    /**
     * Returns true if this rule is satisfied by the given SourceInfo.
     */
    public boolean matches(SourceInfo sourceInfo);
}
