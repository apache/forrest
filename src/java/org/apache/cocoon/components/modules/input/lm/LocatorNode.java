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


import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.treeprocessor.InvokeContext;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolver;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolverFactory;
import org.apache.cocoon.sitemap.PatternException;


/**
 * Top level locate statement containing 
 * <code>MatchNode</code>s and <code>SelectNode</code>s.
 * 
 * <p>
 * A locator defines a common location context for its
 * contained location statements. Location strings resolved within
 * the context of a given locator will be prefixed with the
 * locator's base location. This base location is specified
 * using the <code>base</code> attribute on the <code>&lt;locator&gt;</code>
 * element and defaults to <code>.</code>.
 * </p>
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public final class LocatorNode extends AbstractNode {
    
    // the containing LocationMap
    private final LocationMap m_lm;
    
    // location base resolver
    private VariableResolver m_baseLocation;
    
    // the contained Match- and SelectNodes
    private AbstractNode[] m_nodes;
    
    public LocatorNode(final LocationMap lm, final ComponentManager manager) {
        super(manager);
        m_lm = lm;
    }
    
    public void build(final Configuration configuration) throws ConfigurationException {
        String base = configuration.getAttribute("base",".");
        try {
            m_baseLocation = VariableResolverFactory.getResolver(base,super.m_manager);
        } catch (PatternException e) {
            final String message = "Illegal pattern syntax for locator attribute 'base' " +                "at " + configuration.getLocation(); 
            throw new ConfigurationException(message);
        }
        final Configuration[] children = configuration.getChildren();
        final List nodes = new ArrayList(children.length);
        for (int i = 0; i < children.length; i++) {
            AbstractNode node = null;
            if (children[i].getName().equals("match")) {
                node = new MatchNode(this,super.m_manager);
            }
            else if (children[i].getName().equals("select")) {
                node = new SelectNode(this,super.m_manager);
            }
            else {
                final String message = "Illegal locator node child: " 
                    + children[i].getName();
                throw new ConfigurationException(message);
            }
            node.enableLogging(getLogger());
            node.build(children[i]);
            nodes.add(node);
        }
        m_nodes = (AbstractNode[]) nodes.toArray(new AbstractNode[nodes.size()]);
    }
    
    /**
     * Loop over the list of match and select nodes and call their
     * respective <code>locate()</code> methods returning the first
     * non-null result.
     */
    public String locate(Map om, InvokeContext context) throws Exception {
        
        // resolve the base location and put it in the anchor map
        Map anchorMap = context.getMapByAnchor(LocationMap.ANCHOR_NAME);
        anchorMap.put("base",m_baseLocation.resolve(context,om));
        
        for (int i = 0; i < m_nodes.length; i++) {
            final String location = m_nodes[i].locate(om,context);
            if (location != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("located: " + location);
                }
                return location;
            }
        }
        return null;
    }
    
    String getDefaultMatcher() {
        return m_lm.getDefaultMatcher();
    }
    
    String getDefaultSelector() {
        return m_lm.getDefaultSelector();
    }
    
}