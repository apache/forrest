/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
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
 * <p>
 * The locationmap module works as any other {@link InputModule}.
 * It acts as a location resolver where you can configure a series
 * of fallbacks.
 * <p>
 * It can be seen as a reduced sitemap with the only concern of 
 * resolving locations strings to the final location.
 * <p> 
 * This module is configured via a sitemap similar DSL which looks
 * something like:
 * &lt;locationmap&gt;<br>
 *  &lt;components/&gt;<br>
 *  &lt;locator/&gt;<br>
 *  &lt;/locationmap&gt;<br>
 *  <p>
 *  You can use the same actions and selectors like you can use 
 *  in any other sitemap.
 */
public class LocationMapModule extends AbstractLogEnabled
    implements InputModule, Serviceable, Configurable, Disposable, ThreadSafe {
    
    private ServiceManager m_manager;
    private SourceResolver m_resolver;
    private String m_src;
    private SourceValidity m_srcVal;
    private LocationMap m_lm;
    private boolean m_cacheAll; 
    private Cache m_cache;
    private CacheManager m_cacheManager;

    // ---------------------------------------------------- lifecycle

    public LocationMapModule() {
    }

    public void service(ServiceManager manager) throws ServiceException {
        m_manager = manager;
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }

    public void configure(Configuration configuration) throws ConfigurationException {
        m_src = configuration.getChild("file").getAttribute("src");
        m_cacheAll = configuration.getChild("cacheable").getValueAsBoolean(true);
        Configuration oldCacheConf = configuration.getChild("cache-lifespan", false);
        
        if(oldCacheConf != null) {
          getLogger().warn("Locationmap cache configuration is deprecated through cocoon.xconf.  " 
        		 + "Please use ehcache.xml instead.");
        }
        
        if(m_cacheAll == true) {
            m_cacheManager = new CacheManager();
            m_cache = m_cacheManager.getCache("lm_Cache");
            debug("LM caching enabled and configured.");
        }
    }

    public void dispose() {
        m_lm.dispose();
        m_cacheManager.shutdown();
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
    	
    	Object result = null;
    	boolean hasBeenCached = false;

        try {
        	if (this.m_cacheAll == true) {
        		hasBeenCached = m_cache.isKeyInCache(name);
        		if (hasBeenCached == true && m_cache.get(name)!=null) {
        			result =  m_cache.get(name).getObjectValue();
        			if (getLogger().isDebugEnabled()) {
        				getLogger().debug("Locationmap cached location returned for hint: " + name + " value: " + result);
        			}
        		} else{
                            result = getFreshResult(name, objectModel);
                  }
        	}
        	
        	if (hasBeenCached == false) {
        		result = getFreshResult(name, objectModel);
        	}
          
          if (result == null) {
                String msg = "Locationmap did not return a location for hint " + name;
                getLogger().debug(msg);
          }
          
        	return result;
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
     * Method to create a new locationmap if needed. Needs to be
     * synchronized otherwise the module will fail with concurrency.
     * @see FOR-1082
     * @param name - location that we want to look up
     * @param objectModel - the current object model
     * @return the location resolved via the locationmap or null if not found.
     * @throws Exception
     */
    private Object getFreshResult(final String name, final Map objectModel)
        throws Exception {
      Object result;
      result = getLocationMap().locate(name,objectModel);
      
      if (m_cacheAll == true) {
          Element elem = new Element(name, result);
          m_cache.put(elem);
          
          if (getLogger().isDebugEnabled()) {
      		getLogger().debug("Locationmap caching hint: " + name + " value: " + result);
          }
      }
      return result;
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

        /**
     * If debugging is turned on then log a debug message.
     * @param debugString - the debug message
     */
    private final void debug(String debugString) {
      if (getLogger().isDebugEnabled()) {
        getLogger().debug(debugString);
      }
    }
}
