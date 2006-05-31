/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * license agreements.  See the NOTICE file distributed with
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
package org.apache.forrest.eclipse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ForrestPlugin extends AbstractUIPlugin {
	public final static String ID = "org.apache.forrest";

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(ForrestPlugin.class);
	//The shared instance.
	private static ForrestPlugin plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;

    private static final String LOG_PROPERTIES_FILE = "/conf/log4j.xml";

	/**
	 * The constructor.
	 *  
	 */
	public ForrestPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("org.apache.forrest.ForrestPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return
	 */
	public static ForrestPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 * 
	 * @param key
	 * @return
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ForrestPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 * 
	 * @return
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeDefaultPreferences(org.eclipse.jface.preference.IPreferenceStore)
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		super.initializeDefaultPreferences(store);
		
	   HashMap envVariables = new HashMap();
	   BufferedReader reader = null;

	   //"env" works on Linux & Unix Variants but if we're on Windows,
	   //use "cmd /c set".
	   String envCommand = "env";
	   if (System.getProperty("os.name").toLowerCase().startsWith("win"))
	     envCommand = "cmd /c set";

	   try {
	     //First we launch the command and attach a Reader to the Output
	     Process p = Runtime.getRuntime().exec(envCommand);
	     reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

	     //Now we parse the output, filling up the Hashtable
	     String theLine = null;
	     while ( (theLine = reader.readLine()) != null) {
	       int equalsIndex = theLine.indexOf("=");
	       if (equalsIndex < 0)
	        continue;

	       String name = theLine.substring(0,equalsIndex);
	       String value = "";
	       //Test for an empty value such as "FOO="
	       if ((equalsIndex + 1) < theLine.length())
	         value = theLine.substring(equalsIndex+1, theLine.length());
	       envVariables.put(name, value);
	     }
	   }
	   catch (IOException e) {
	   	// FIXME: Handle this error
	   	e.printStackTrace();
	   }

		store = getPreferenceStore();
		if (envVariables.containsKey(ForrestPreferences.FORREST_HOME)) {
			store.setDefault(ForrestPreferences.FORREST_HOME, (String)envVariables.get(ForrestPreferences.FORREST_HOME));
		}
	}
	
	/**
	 * Set the preference value for Forrest Home.
	 * @param forrestHome
	 */
	public void setForrestHome(String forrestHome) {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(ForrestPreferences.FORREST_HOME, forrestHome);
	}

    public void start(BundleContext context) throws Exception {
        super.start(context);
        configureLog();
    }

    private void configureLog() {
        URL configURL = getBundle().getEntry(LOG_PROPERTIES_FILE);      
        try {
            URL log4jConf = Platform.resolve(configURL);
            DOMConfigurator.configure(log4jConf);
        } catch (IOException e) {
            String message = "Error while initializing log properties." +
                             e.getMessage();
            IStatus status = new Status(IStatus.ERROR,
              getDefault().getBundle().getSymbolicName(),
              IStatus.ERROR, message, e);
            getLog().log(status);
            // having logged this problem we will continue without log4j logging
         }
    }
}
