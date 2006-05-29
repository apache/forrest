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
package org.apache.forrest.eclipse.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * 
 * Some handy utilities for working with plugins.
 */
public class Utilities {
	   
	private static final Logger logger = Logger
       .getLogger(Utilities.class);
	
    /**
     * Retrieve the relative path to the XDocs directory.
     * @retuen platform specific path to XDocs directory
     */
	public static String getPathToXDocs() {
        if (logger.isDebugEnabled()) {
            logger.debug("getPathToXDocs() - start");
        }

        String path = getPathToContent() + java.io.File.separator
                + "xdocs";

        if (logger.isDebugEnabled()) {
            logger.debug("getPathToXDocs() - end");
        }
        return path;
    }
     
	/**
     * Retrieve the relative path to the Content directory.
     * @retuen platform specific path to Content directory
     */
	public static String getPathToContent() {
        if (logger.isDebugEnabled()) {
            logger.debug("getPathToContent() - start");
        }

        // FIXME: get this value from the package config file (forrest.properties)
        String path = "src" + java.io.File.separator + "documentation"
                + java.io.File.separator + "content" ;

        if (logger.isDebugEnabled()) {
            logger.debug("getPathToContent() - end");
        }
        return path;
    }
	/**
	 * @param directory
	 * @return @throws
	 *         FileNotFoundException
	 */
	static public List getFileListing(File directory)
			throws FileNotFoundException {
		class JARFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".jar"));
			}
		}

		List result = new ArrayList();
		File[] filesAndDirs = directory.listFiles();
		List filesDirs = Arrays.asList(filesAndDirs);
		Iterator filesIter = filesDirs.iterator();
		File file = null;

		while (filesIter.hasNext()) {
			file = (File) filesIter.next();

			if (!file.isFile()) {
				List deeperList = getFileListing(file);
				result.addAll(deeperList);
			} else {
				result.add(file);
			}
		}

		return result;
	}
	
	/**
	 * Checks to see if the port is available. 
	 * @return true if the port is available
	 */ 
	static public boolean isPortFree(int portNumber) {
		try {
			Socket echoSocket = new Socket("localhost", portNumber);
			echoSocket.close();
			return false;
		} catch (UnknownHostException e) {
			return true;
			
		} catch (IOException e) {
			return true;
			
		}	
		
	}; 
	
	
	/**
	 * Sets a property as active in a .properties file. 
	 * @return true if the port is available
	 */ 
	static public void activateForrestProperty(String path, String property){
		 try {
		       BufferedReader in = new BufferedReader(new FileReader(path));
		       String str;
		       String outFile = null;
		       while ((str = in.readLine()) != null) {
		           if (str.startsWith("#" + property)) {
		    	   outFile = outFile + property + System.getProperty( "line.separator" );
		    	   }
		           else
		           {
		           outFile = outFile + str + System.getProperty( "line.separator" );
		           }
		       }
		        in.close();
		        BufferedWriter out = new BufferedWriter(new FileWriter(path));
		        out.write(outFile);
		        out.close();
		       } catch (IOException e) {
		    }
		
	}
	/**
	 * Adds a plugin or plugins to the project.required.plugins property in forrest.properties 
     * @param path - path to the forrest.properties file
     * @param pluginName - a comma separated list of plugins to add
     *  
	 */ 
	static public void addForrestPluginProperty(String path, String pluginName){
		 try {
		       BufferedReader in = new BufferedReader(new FileReader(path));
		       String str;
		       String outFile = null;
		       while ((str = in.readLine()) != null) {
		           String[] tokens = str.split("=");
		           if (tokens[0].startsWith("project.required.plugins")) {
		    	     outFile = outFile + str + "," + pluginName + System.getProperty( "line.separator" );
		    	   } else {
		             outFile = outFile + str + System.getProperty( "line.separator" );
		           }
		        }
		        in.close();
		        BufferedWriter out = new BufferedWriter(new FileWriter(path));
		        out.write(outFile);
		        out.close();
		       } catch (IOException e) {
		    }	
	}

    /**
	 * @param cmdString
	 */
	static public void RunExtCommand(final String cmdString) {
		Runnable r = new Runnable() {
			public void run() {
				Process p;
				try {
					System.out.println(cmdString);
					p = Runtime.getRuntime().exec(cmdString);

					BufferedReader br = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String str;
					while ((str = br.readLine()) != null)
						System.out.println(str);

					p.waitFor();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		};
		r.run();
	}
}
