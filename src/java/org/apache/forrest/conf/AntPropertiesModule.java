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
package org.apache.forrest.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.lang.StringUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

/**
 * Input module for accessing properties in a properties file 
 * roughly compatible with Ant property files, where ${name}
 * is replaced with the value of the property 'name' if
 * declared beforehand.
 * 
 * <p>
 *  The properties file can only be configured statically and
 *  is resolved via the SourceResolver system.
 * </p>
 * 
 * @author <a href="mailto:nicolaken@apache.org">Nicola Ken Barozzi</a>
 * @author <a href="mailto:unico@apache.org">Unico Hommes</a>
*/
public class AntPropertiesModule extends AbstractJXPathModule 
implements InputModule, Serviceable, Configurable, ThreadSafe {
    
    private SourceResolver m_resolver;
    private Properties m_properties;
    
    public void service(ServiceManager manager) throws ServiceException {
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }
    
    /**
     * Configure the location of the properties file:
     * <p>
     *  <code>&lt;file src="resource://my.properties" /&gt;</code>
     * </p>
     */
    public void configure(Configuration configuration) throws ConfigurationException {
        super.configure(configuration);
        String file = configuration.getChild("file").getAttribute("src");
        load(file);
    }
    
    protected void load(String file) throws ConfigurationException {
        Source source = null;
        BufferedReader in = null;
        try {
            source = m_resolver.resolveURI(file);
           
            in = new BufferedReader(new InputStreamReader(source.getInputStream()));
            
            m_properties = new Properties();
            String currentLine, name, value;
            int splitIndex;
            Enumeration names;
             
            while((currentLine = in.readLine()) != null) {
                // # == comment
                if(!currentLine.startsWith("#")&&!(currentLine.trim().length()==0)){ 
                    splitIndex =  currentLine.indexOf('='); 
                    name = currentLine.substring(0, splitIndex).trim();
                    //if the property is already there don't overwrite, as in Ant
                    //properties defined first take precedence
                    if(!m_properties.containsKey(name)){
                        value = currentLine.substring(splitIndex+1).trim();
                        names = m_properties.propertyNames();
                        while( names.hasMoreElements() ) {
                            String currentName = (String) names.nextElement();
                            String valueToSearchFor = "${"+currentName+"}";
                            String valueToReplaceWith = (String) m_properties.get(currentName);
                            value = StringUtils.replace(value, valueToSearchFor, valueToReplaceWith);
                        }
                        m_properties.put(name,value);
                    }    
                }    
            }
        }
        catch (IOException e) {
            throw new ConfigurationException("Cannot load properties file " + file);
        }
        finally {
            if (source != null) {
                m_resolver.release(source);
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                }
            }
        }
    }
    
    protected Object getContextObject(Configuration modeConf, Map objectModel)
        throws ConfigurationException {
        
        return m_properties;
    }

}
