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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.DefaultsModule;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

/**
 * Input module for accessing the base properties used in Forrest. The main
 * values are the locations of the <b>source </b> directories and of the
 * <b>forrest </b> directories. The values are gotten from the
 * 
 * <pre>
 * forrest.properties
 * </pre>
 * 
 * ant property file and resolved relative to the java system properties named
 * 
 * <pre>
 * forrest.home
 * </pre>
 * 
 * and
 * 
 * <pre>
 * project.home
 * </pre>, that the forrest scripts set using the calling directory and the
 * environment variable
 * 
 * <pre>
 * FORREST_HOME
 * </pre>. If Forrest is run from a war, it won't have these properties set, so
 * the directories are resolved relative to the current directory, that in this
 * case is the forrest webapp root.
 */
public class ForrestConfModule extends DefaultsModule implements InputModule,
                Initializable, ThreadSafe, Serviceable
{

    private String         forrestHome, projectHome;
    private SourceResolver m_resolver;
    private AntProperties  filteringProperties;

    public void service(ServiceManager manager) throws ServiceException {
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    }

    public void initialize() throws Exception {
        // get location of forrest.home
        //
        //NOTE: Don't do this:
        //
        //        forrestHome = System.getenv("FORREST_HOME");
        //
        //      as it will get FORREST_HOME even when the app
        //      is run as a .war
        forrestHome = getSystemProperty("forrest.home");

        // get location of project.home
        projectHome = getSystemProperty("project.home");
        

        // get default-forrest.properties and load the values
        String defaultRorrestPropertiesStringURI = forrestHome
                        + SystemUtils.FILE_SEPARATOR
                        + "default-forrest.properties";

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("Searching for default forrest.properties in:"
                        + defaultRorrestPropertiesStringURI);
        }

        Source source = null;
        InputStream in = null;
        try {
            source = m_resolver.resolveURI(defaultRorrestPropertiesStringURI);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                                "Searching for default forrest.properties in [RESOLVED]:"
                                                + source.getURI());
            }

            in = source.getInputStream();
            filteringProperties = new AntProperties();
            filteringProperties.load(in);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                                "Loaded default forrest.properties:"
                                                + filteringProperties);
            }

        } finally {
            if (source != null) {
                m_resolver.release(source);
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
        }

        // get forrest.properties and load the values
        String forrestPropertiesStringURI = projectHome
                        + SystemUtils.FILE_SEPARATOR + "forrest.properties";

        if (getLogger().isDebugEnabled()) {
            getLogger()
                            .debug(
                                            "Searching for project forrest.properties in:"
                                                            + defaultRorrestPropertiesStringURI);
        }

        try {
            source = m_resolver.resolveURI(forrestPropertiesStringURI);

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(
                                "Searching for project forrest.properties in [RESOLVED]:"
                                                + source.getURI());
            }

            in = source.getInputStream();
            filteringProperties = new AntProperties(filteringProperties);
            filteringProperties.load(in);
        } finally {
            if (source != null) {
                m_resolver.release(source);
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
        }

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                            "Loaded project forrest.properties:"
                                            + filteringProperties);
        }

        //add forrest.home and project.home to properties
        filteringProperties.setProperty("forrest.home", forrestHome);
        filteringProperties.setProperty("project.home", projectHome);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                            "Global forrest.properties:" + filteringProperties);
        }

    }

    public Object[] getAttributeValues(String name, Configuration modeConf,
                    Map objectModel) throws ConfigurationException {
        Object[] attributeValues = super.getAttributeValues(name, modeConf,
                        objectModel);
        for (int i = 0; i < attributeValues.length; i++) {
            attributeValues[i] = filteringProperties.filter(attributeValues[i]
                            .toString());
        }

        return attributeValues;
    }

    public Object getAttribute(String name, Configuration modeConf,
                    Map objectModel) throws ConfigurationException {
        String original = super.getAttributeValues(name, modeConf, objectModel)[0]
                        .toString();
        String attributeValue = this.getAttributeValues(name, modeConf,
                        objectModel)[0].toString();

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(" - Requested:" + name);
            getLogger().debug(" - Unfiltered:" + original);
            getLogger().debug(" - Given:" + attributeValue);
        }
        return attributeValue;
    }

    private final String getSystemProperty(String propertyName)
                    throws MalformedURLException, IOException {

        String propertyValue = System.getProperty(propertyName, ".");

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(
                            "system property " + propertyName + "="
                                            + propertyValue);
        }

        return propertyValue;
    }

}

