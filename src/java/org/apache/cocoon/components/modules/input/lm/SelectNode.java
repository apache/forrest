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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentSelector;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.components.treeprocessor.InvokeContext;
import org.apache.cocoon.selection.Selector;


/**
 * Locationmap select statement.
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public final class SelectNode extends AbstractNode {
    
    // the containing LocatorNode
    private final LocatorNode m_ln;
    
    // the selector that does the work
    private Selector m_selector;
    
    // the type of selector for this node
    private String m_type;
    
    // the locations to test against
    private AbstractNode[] m_nodes;
    
    
    public SelectNode(LocatorNode ln, ComponentManager manager) {
        super(manager);
        m_ln = ln;
    }
    
    public void build(Configuration configuration) throws ConfigurationException {
        
        super.build(configuration);
        
        // get the selector
        m_type = configuration.getAttribute("type",m_ln.getDefaultSelector());
        try {
            final ComponentSelector selectors = (ComponentSelector) 
                super.m_manager.lookup(Selector.ROLE + "Selector");
            m_selector = (Selector) selectors.select(m_type);
        } catch (ComponentException e) {
            final String message = "Unable to get Selector of type " + m_type;
            throw new ConfigurationException(message,e);
        }
        
        // build the child nodes
        final Configuration[] children = configuration.getChildren();
        final List nodes = new ArrayList(children.length);
        for (int i = 0; i < children.length; i++) {
            AbstractNode node = null;
            String name = children[i].getName();
            if (name.equals("location")) {
                node = new LocationNode(m_ln,super.m_manager);
            } 
            else if (name.equals("match")) {
                node = new MatchNode(m_ln,super.m_manager);
            }
            else if (name.equals("select")) {
                node = new SelectNode(m_ln,super.m_manager);
            }
            else if (!name.equals("parameter")) {
                final String message = "Unknown select node child:" + name;
                throw new ConfigurationException(message);
            }
            if (node != null) {
                node.enableLogging(getLogger());
                node.build(children[i]);
                nodes.add(node);
            }
        }
        m_nodes = (AbstractNode[]) nodes.toArray(new AbstractNode[nodes.size()]);
    }
    
    public String locate(Map om, InvokeContext context) throws Exception {
        
        Parameters parameters = resolveParameters(context,om);
        for (int i = 0; i < m_nodes.length; i++) {
            String location = m_nodes[i].locate(om,context);
            if (m_selector.select(location,om,parameters)) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("selected: " + location);
                }
                return location;
            }
        }
        return null;
    }
}