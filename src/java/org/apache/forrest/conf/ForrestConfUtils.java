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

import org.apache.commons.lang.SystemUtils;

/**
 * Utility class for accessing the base properties used in Forrest. The main
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
public class ForrestConfUtils
{
    private String             forrestHome, projectHome, contextHome;

    public final static String defaultHome = "context:/";

    private final static String getSystemProperty(String propertyName) {

        //if the property is not set, default to the webapp context
        String propertyValue = System.getProperty(propertyName, defaultHome);

        return propertyValue;
    }

    public static String getForrestHome() throws Exception {

        //NOTE: Don't do this:
        //
        //        forrestHome = System.getenv("FORREST_HOME");
        //
        //      as it will get FORREST_HOME even when the app
        //      is run as a .war
        return getSystemProperty("forrest.home");
    }

    public static String getProjectHome() throws Exception {
        String projectHome = getSystemProperty("project.home");
        if (projectHome.equals(defaultHome)) {
            projectHome = defaultHome + SystemUtils.FILE_SEPARATOR + "/project";
        }
        return projectHome;
    }

    public static String getContextHome() throws Exception {
        String forrestHome = getForrestHome();
        String contextHome;
        if (forrestHome.equals(defaultHome)) {
            contextHome = defaultHome;
        } else {
            contextHome = forrestHome + SystemUtils.FILE_SEPARATOR + "/context";
        }
        return contextHome;
    }

    /**
     * For backwards compatibility, alias old skin names to new ones. This must
     * be kept in sync with aliasing in forrest.build.xml/init-props
     * 
     * @param properties to filter
     */
    public static void aliasSkinProperties(AntProperties props) {
        // AntProperties.setProperty doesn't let you override, so we have to remove the property then add it again
        String skinName = props.getProperty("project.skin");
        if (skinName.equals("krysalis-site") || skinName.equals("forrest-site")
                        || skinName.equals("forrest-css")) {
            props.remove("project.skin");
            props.setProperty("project.skin", "crust");
        } else if (skinName.equals("avalon-tigris")
                        || skinName.equals("tigris-style")) {
            props.remove("project.skin");
            props.setProperty("project.skin", "tigris");
        }
    }

}