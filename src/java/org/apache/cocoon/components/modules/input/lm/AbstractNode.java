/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.cocoon.components.modules.input.lm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.treeprocessor.InvokeContext;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolver;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolverFactory;
import org.apache.cocoon.sitemap.PatternException;

/**
 * Base class for LocationMap nodes.
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public abstract class AbstractNode extends AbstractLogEnabled {
    
    
    protected final ComponentManager m_manager;
    
    // optional parameters defined 
    // by the node's configuration
    private Map m_parameters;
    
    
    public AbstractNode(final ComponentManager manager) {
        m_manager = manager;
    }
    
    public void build(final Configuration configuration) throws ConfigurationException {
        m_parameters = getParameters(configuration);
    }
    
    /**
     * Create a Map of resolvable parameters.
     * 
     * @param configuration  the configuration to build parameters from.
     * @return  a Map of parameters wrapped in VariableResolver objects,
     * <code>null</code> if the configuration contained no parameters.
     * @throws ConfigurationException
     */
    protected final Map getParameters(final Configuration configuration) 
        throws ConfigurationException {
        
        final Configuration[] children = configuration.getChildren("parameter");
        if (children.length == 0) {
            return null;
        }
        final Map parameters = new HashMap();
        for (int i = 0; i < children.length; i++) {
            final String name = children[i].getAttribute("name");
            final String value = children[i].getAttribute("value");
            try {
                parameters.put(
                    VariableResolverFactory.getResolver(name,m_manager),
                    VariableResolverFactory.getResolver(value,m_manager));
            } catch(PatternException pe) {
                String msg = "Invalid pattern '" + value + "' at " 
                    + children[i].getLocation();
                throw new ConfigurationException(msg, pe);
            }
        }

        return parameters;
    }
    
    /**
     * Resolve the parameters. Also passes the LocationMap special
     * variables into the Parameters object.
     * 
     * @param context  InvokeContext used during resolution.
     * @param om  object model used during resolution.
     * @return  the resolved parameters or null if this node contains no parameters.
     * @throws PatternException
     */
    protected final Parameters resolveParameters(
        final InvokeContext context, 
        final Map om) throws PatternException {
        
        Parameters parameters = null;
        if (m_parameters != null) {
            parameters = VariableResolver.buildParameters(m_parameters,context,om);
        }
        else {
            parameters = new Parameters();
        }
        // also pass the anchor map as parameters
        // for use by components
        Map anchorMap = context.getMapByAnchor("lm");
        Iterator entries = anchorMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            parameters.setParameter("#lm:"+entry.getKey(),entry.getValue().toString());
        }
        return parameters;
    }
    
    public abstract String locate(Map objectModel, InvokeContext context) throws Exception;
        
}