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
package org.apache.forrest.locationmap;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.apache.forrest.locationmap.lm.LocationMap;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.NamespacedSAXConfigurationHandler;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.xml.sax.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolves a request against a LocationMap.
 */
public class LocationMapModule extends AbstractLogEnabled 
    implements InputModule, Serviceable, Configurable, Disposable, ThreadSafe {
    
    private static final Iterator ATTNAMES = Collections.EMPTY_LIST.iterator();
    
    private ServiceManager m_manager;
    private SourceResolver m_resolver;
    private String m_src;
    private SourceValidity m_srcVal;
    private LocationMap m_lm;
    
    // ---------------------------------------------------- lifecycle
    
    public LocationMapModule() {
    }
    
    public void service(ServiceManager manager) throws ServiceException {
        m_manager = manager;
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }
    
    public void configure(Configuration configuration) throws ConfigurationException {
        m_src = configuration.getChild("file").getAttribute("src");
    }
    
    public void dispose() {
        m_lm.dispose();
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
                            m_lm.dispose();
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
        catch (ServiceException e) {
            throw new ConfigurationException("Unable to build LocationMap.",e);
        }
        finally {
            if (parser != null) {
                m_manager.release(parser);
            }
        }
        return configuration;
    }
    
    // ---------------------------------------------------- Module implementation
    
    /**
     * Execute the current request against the locationmap returning the
     * resulting string.
     */
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
     * Therefore returns null.
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
