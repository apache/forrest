/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cocoon.components.modules.input.lm;

import java.util.Map;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.cocoon.components.treeprocessor.InvokeContext;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolver;
import org.apache.cocoon.components.treeprocessor.variables.VariableResolverFactory;
import org.apache.cocoon.sitemap.PatternException;
import org.apache.excalibur.source.SourceUtil;

/**
 * locationmap leaf statement identifying a location.
 * 
 * <p>
 *  The <code>&lt;location&gt;</code> element has one
 *  required attribute <code>src</code> that contains the
 *  location string.
 * </p>
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public class LocationNode extends AbstractNode {

    private final LocatorNode m_ln;
    
    // the resolvable location source
    private VariableResolver m_src;
    
    public LocationNode(final LocatorNode ln, final ComponentManager manager) {
        super(manager);
        m_ln = ln;
    }
    
    public void build(final Configuration configuration) throws ConfigurationException {
        try {
            m_src = VariableResolverFactory.getResolver(
                configuration.getAttribute("src"),super.m_manager);
        } catch (PatternException e) {
            final String message = "Illegal pattern syntax at for location attribute 'src' " +                "at " + configuration.getLocation();
            throw new ConfigurationException(message,e);
        }

    }
    
    /**
     * Resolve the location string against the InvokeContext.
     */
    public String locate(Map om, InvokeContext context) throws Exception {
        
        String src = m_src.resolve(context,om);
        
        // relative, prefix with locator base
        if (src.charAt(0) != '/' && SourceUtil.indexOfSchemeColon(src) == -1) {
            String base = (String) context.getMapByAnchor(LocationMap.ANCHOR_NAME).get("base");
            src =  base + (base.charAt(base.length()-1) == '/' ? "" : "/") + src;
        }
        
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("location: " + src);
        }
        
        return src;
    }

}
