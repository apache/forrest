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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.component.DefaultComponentSelector;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.cocoon.components.treeprocessor.InvokeContext;
import org.apache.cocoon.matching.Matcher;
import org.apache.cocoon.selection.Selector;

/**
 * A LocationMap defines a mapping from requests to locations.
 * <p>
 * The syntax of the locationmap is similar to the way a sitemap 
 * maps requests to pipelines. In the locationmap's case 
 * Matchers and Selectors are not used to identify pipelines but
 * location strings.
 * </p>
 * <p>
 * The locationmap was conceived to:
 * <ul>
 *  <li>
 *   Provide a tool for more powerful virtual linking.
 *  </li>
 *  <li>
 *   Enable Forrest with a standard configuration override
 *   mechanism.
 *  </li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public final class LocationMap extends AbstractLogEnabled {
    
    /** 
     * The locationmap namespace: 
     * <code>http://apache.org/forrest/locationmap/1.0</code>
     */
    public static final String URI = "http://apache.org/forrest/locationmap/1.0";
    
    /**
     * Name of the special anchor map passed into the VariableContext.
     * The value is <code>lm</code> .
     * <p>
     * This makes it possible to use special locationmap variables
     * inside the locationmap definition.
     * </p>
     * <p>
     * Special locationmap parameters are available through this anchor map.
     * For instance the hint parameter defined below can be accessed in 
     * the locationmap definition as follows: <code>{#lm:hint}</code>
     * </p>
     * All anchor map parameters are also passed implicitly into each selector
     * and matcher at run-time.
     */
    public static final String ANCHOR_NAME = "lm";
    
    /**
     * Special anchor map key holding the 'hint' or 'argument' the LocationMap was
     * called with.
     * 
     * <p>
     * The value is <code>hint</code>.
     * </p>
     */
    public static final String HINT_KEY = "hint";
    
    /**
     * The hint's parameter name.
     */
    public static final String HINT_PARAM = "#" + ANCHOR_NAME + ":" + HINT_KEY;
    
    // Component manager containing the locationmap components
    // as declared in the components section.
    private LocationMapComponentManager m_manager;
    
    // default matcher as configured in the components section
    private String m_defaultMatcher;
    
    // default selector as configured in the components section
    private String m_defaultSelector;
    
    // the list of LocatorNodes
    private LocatorNode[] m_locatorNodes;
    
    
    public LocationMap(ComponentManager manager) {
        m_manager = new LocationMapComponentManager(manager);
    }
    
    /**
     * Build the LocationMap by creating the components and recursively building
     * the LocatorNodes.
     */
    public void build(final Configuration configuration) throws ConfigurationException {
        
        // components
        final Configuration components = configuration.getChild("components");
        
        // matchers
        Configuration child = components.getChild("matchers",false);
        if (child != null) {
            final DefaultComponentSelector matcherSelector = new DefaultComponentSelector();
            m_defaultMatcher = child.getAttribute("default");
            final Configuration[] matchers = child.getChildren("matcher");
            for (int i = 0; i < matchers.length; i++) {
                String name = matchers[i].getAttribute("name");
                String src  = matchers[i].getAttribute("src");
                Matcher matcher = (Matcher) createComponent(src,matchers[i]);
                matcherSelector.put(name,matcher);
            }
            matcherSelector.makeReadOnly();
            if (!matcherSelector.hasComponent(m_defaultMatcher)) {
                throw new ConfigurationException("Default matcher is not defined.");
            }
            m_manager.put(Matcher.ROLE+"Selector",matcherSelector);
        }
        
        // selectors
        child = components.getChild("selectors",false);
        if (child != null) {
            final DefaultComponentSelector selectorSelector = new DefaultComponentSelector();
            m_defaultSelector = child.getAttribute("default");
            final Configuration[] selectors = child.getChildren("selector");
            for (int i = 0; i < selectors.length; i++) {
                String name = selectors[i].getAttribute("name");
                String src  = selectors[i].getAttribute("src");
                Selector selector = (Selector) createComponent(src,selectors[i]);
                selectorSelector.put(name,selector);
            }
            selectorSelector.makeReadOnly();
            if (!selectorSelector.hasComponent(m_defaultSelector)) {
                throw new ConfigurationException("Default selector is not defined.");
            }
            m_manager.put(Selector.ROLE+"Selector",selectorSelector);
            m_manager.makeReadOnly();
        }
        
        // locators
        final Configuration[] children = configuration.getChildren("locator");
        m_locatorNodes = new LocatorNode[children.length];
        for (int i = 0; i < children.length; i++) {
            m_locatorNodes[i] = new LocatorNode(this,m_manager);
            m_locatorNodes[i].enableLogging(getLogger());
            m_locatorNodes[i].build(children[i]);
        }
    }
    
    /**
     * Creates a LocationMap component.
     * <p>
     *  supported component creation lifecycles that are:
     *  - LogEnabled
     *  - Configurable
     *  - Initializable
     * </p>
     */
    private Object createComponent(String src, Configuration config) throws ConfigurationException {
        Object component = null;
        try {
            component = Class.forName(src).newInstance();
            ContainerUtil.enableLogging(component,getLogger());
            ContainerUtil.configure(component,config);
            ContainerUtil.initialize(component);
        } catch (Exception e) {
            throw new ConfigurationException("Couldn't create object of type " + src,e);
        }
        return component;
    }
    
    public void dispose() {
        Iterator components = m_manager.getComponents();
        while(components.hasNext()) {
            Object component = components.next();
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
        m_manager = null;
        m_locatorNodes = null;
    }
    
    /**
     * Loop through the list of locator nodes invoking
     * the <code>locate()</code> method on each and return
     * the first non-null result.
     */
    public String locate(String hint, Map om) throws Exception {
        
        String location = null;
        
        final InvokeContext context = new InvokeContext();
        final Logger contextLogger = getLogger().getChildLogger("ctx");
        context.enableLogging(contextLogger);
        context.compose(m_manager);
        
        final Map anchorMap = new HashMap(2);
        anchorMap.put(HINT_KEY,hint);
        context.pushMap(ANCHOR_NAME,anchorMap);
        
        for (int i = 0; i < m_locatorNodes.length; i++) {
            location = m_locatorNodes[i].locate(om,context);
            if (location != null) {
                break;
            }
        }
        
        context.dispose();
        
        if (getLogger().isDebugEnabled() && location == null) {
            getLogger().debug("No location matched request with hint " + hint);
        }
        
        return location;
    }
    
    /**
     * Expose the default Matcher to LocatorNodes
     */
    String getDefaultMatcher() {
        return m_defaultMatcher;
    }
    
    /**
     * Expose the default Selector to LocatorNodes
     */
    String getDefaultSelector() {
        return m_defaultSelector;
    }
    
    /**
     * Overide DefaultComponentManager to access the list of all
     * components.
     */
    private static class LocationMapComponentManager extends DefaultComponentManager {
        
        LocationMapComponentManager(ComponentManager parent) {
            super(parent);
        }
        
        Iterator getComponents() {
            return super.getComponentMap().values().iterator();
        }
    }
}
