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
package org.apache.cocoon.components.modules.input;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.NamespacedSAXConfigurationHandler;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.lm.*;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolves a request against a LocationMap.
 * 
 * @author <a href="mailto:unico@hippo.nl">Unico Hommes</a>
 */
public class LocationMapModule extends AbstractLogEnabled 
    implements InputModule, Composable, Configurable, ThreadSafe {
    
    private static final Iterator ATTNAMES = Collections.EMPTY_LIST.iterator();
    
    private ComponentManager m_manager;
    private SourceResolver m_resolver;
    private String m_src;
    private SourceValidity m_srcVal;
    private LocationMap m_lm;
    
    public void compose(ComponentManager manager) throws ComponentException {
        m_manager = manager;
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }
    
    public void configure(Configuration configuration) throws ConfigurationException {
        m_src = configuration.getChild("file").getAttribute("src");
    }
    
    private LocationMap getLocationMap() throws Exception {
        Source source = null;
        try {
            source = m_resolver.resolveURI(m_src);
            if (m_lm == null) {
                synchronized (this) {
                    if (m_lm == null) {
                        if (getLogger().isDebugEnabled()) {
                            getLogger().debug("loading location map at " + m_src);
                        }
                        m_srcVal = source.getValidity();
                        m_lm = new LocationMap(m_manager);
                        m_lm.enableLogging(getLogger());
                        m_lm.build(loadConfiguration(source));
                    }
                }
            }
            else {
                SourceValidity valid = source.getValidity();
                if (m_srcVal != null && m_srcVal.isValid(valid) != 1) {
                    synchronized (this) {
                        if (m_srcVal != null && m_srcVal.isValid(valid) != 1) {
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("reloading location map at " + m_src);
                            }
                            m_srcVal = valid;
                            m_lm = new LocationMap(m_manager);
                            m_lm.enableLogging(getLogger());
                            m_lm.build(loadConfiguration(source));
                        }
                    }
                }
            }
        }
        finally {
            if (source != null) {
                m_resolver.release(source);
            }
        }
        return m_lm;
    }
    
    private Configuration loadConfiguration(Source source) throws ConfigurationException {
        Configuration configuration = null;
        SAXParser parser = null;
        try {
            parser = (SAXParser) m_manager.lookup(SAXParser.ROLE);
            NamespacedSAXConfigurationHandler handler = 
                new NamespacedSAXConfigurationHandler();
            parser.parse(new InputSource(source.getInputStream()),handler);
            configuration = handler.getConfiguration();
        }
        catch (IOException e) {
            throw new ConfigurationException("Unable to build LocationMap.",e);
        }
        catch (SAXException e) {
            throw new ConfigurationException("Unable to build LocationMap.",e);
        }
        catch (ComponentException e) {
            throw new ConfigurationException("Unable to build LocationMap.",e);
        }
        finally {
            if (parser != null) {
                m_manager.release((Component) parser);
            }
        }
        return configuration;
    }
    
    public Object getAttribute(
        final String name,
        final Configuration modeConf,
        final Map objectModel)
        throws ConfigurationException {
        
        try {
            return getLocationMap().locate(name,objectModel);
        }
        catch (ConfigurationException e) {
            throw e;
        }
        catch (Exception e) {
            getLogger().error("Failure processing LocationMap.",e);
        }
        return null;
    }
    
    /**
     * The possibilities are endless. No way to enumerate them all.
     * Therefore returns an empty Iterator.
     */
    public Iterator getAttributeNames(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        
        return null;
    }
    
    /**
     * Always returns only one value. Use getAttribute() instead.
     */
    public Object[] getAttributeValues(
        String name,
        Configuration modeConf,
        Map objectModel)
        throws ConfigurationException {
        
        return new Object[] {getAttribute(name,modeConf,objectModel)};
    }
    
}
