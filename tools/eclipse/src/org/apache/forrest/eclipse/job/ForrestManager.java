/*
 * Copyright 1999-2004 The Apache Software Foundation or its licensors,
 * as applicable.
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
package org.apache.forrest.eclipse.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.LoadProperties;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;

/**
 * Manages instances of Forrest that are running on the local server.
 */
public class ForrestManager {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ForrestManager.class);

	public static final String COMMAND_BUILD = "site";

	public static final String COMMAND_START = "run";

	public static final String COMMAND_STOP = "stop";

	private static final int UNKOWN_COMMAND = 0;

	private static final int BUILD = 1;

	private static final int START = 2;

	private static final int STOP = 3;

	private static final ForrestManager INSTANCE = new ForrestManager();

	// FIXME: dir configuration should be in preferences and should be set by
	// reading forrest.properties and forrest.build.xml (where some defaults are set)

	public static final String FORREST_HOME = ForrestPlugin.getDefault()
			.getPluginPreferences().getString(ForrestPreferences.FORREST_HOME);
	
	public static final String FORREST_CORE_LIB = FORREST_HOME
			+ File.separator + "lib" + File.separator + "core" + File.separator;

	public static final String FORREST_ENDORSED_LIB = FORREST_HOME
			+ File.separator + "lib" + File.separator + "endorsed"
			+ File.separator;

	public static final String FORREST_OPTIONAL_LIB = FORREST_HOME
			+ File.separator + "lib" + File.separator + "optional"
			+ File.separator;

	public static final String JETTY_LIB = FORREST_HOME
			+ File.separator + "tools" + File.separator + "jetty"
			+ File.separator;
	
	public static final String FORREST_CORE_WEBAPP = FORREST_HOME
			+ File.separatorChar + "main" + File.separatorChar + "webapp";
	
	public static final String FORREST_JAR = FORREST_HOME
	+ File.separatorChar + "build" + File.separatorChar + "xml-forrest.jar";

	public static final String FORREST_CLASSES = FORREST_CORE_WEBAPP
			+ File.separator + "WEB-INF" + File.separator + "classes" + File.separator;

	public static final String FORREST_LIB = FORREST_HOME
			+ File.separator + "build" + File.separator + "xml-forrest.jar";

	public static final String FORREST_ANT = FORREST_HOME
			+ File.separator + "tools" + File.separator + "ant" + File.separator;
	
	public static final String FORREST_ANTTASK_CLASSES = FORREST_HOME
			+ File.separator + "tools" + File.separator + "anttasks" + File.separator;
	
	public static final String FORREST_PLUGINS = FORREST_HOME
			+ File.separator + "build" + File.separator + "plugins" + File.separator;

	private static final String FORREST_DEFAULT_PROPERTIES_FILE = FORREST_CORE_WEBAPP
	+ File.separatorChar + "default-forrest.properties";
	
	private static ILaunch serverLaunch;
	
	/**
	 * Create a manager, this is not intended to be called from your code. Use
	 * Manager.getInstance() instead.
	 */
	private ForrestManager() {
		super();
	}

	/**
	 * Get the properties for a working directory
	 * @param workingDir the working dir of the project
	 * @return the properties for this project
	 * @throws IOException if unable to read either the defaults file or the project file
	 * @TODO Cache the project properties file
	 * @refactor Move project creation code to own method
	 */
	public Hashtable getProperties(String workingDir) throws IOException {
		// TODO: keep a record of the projects created, this is how we will cache the properties
		Project project = new Project();
		project.setName(workingDir);
		
		LoadProperties props = new LoadProperties();
		props.setProject(project);
		
		project.setProperty("project.home", workingDir);
		
		props.setSrcFile(new File(workingDir + File.separatorChar + "forrest.properties"));
		props.execute();
		
		props.setSrcFile(new File(FORREST_DEFAULT_PROPERTIES_FILE));
		props.execute();
		
		project.setProperty("forrest.home", FORREST_HOME);
		project.setProperty("forrest.plugins", FORREST_PLUGINS);
		
		return project.getProperties();
	}
	

	public static ForrestManager getInstance() {
		return ForrestManager.INSTANCE;
	}

	/**
	 * Create a Forrest Job.
	 * 
	 * @param workingDir -
	 *            the directory on which the job is to work
	 * @param cmd -
	 *            the command the job is to carry out
	 * @return
	 */
	public Job getForrestJob(String workingDir, String cmd) {
		Job theJob;
		if (cmd.equals(COMMAND_STOP)) {
			theJob = new ForrestStopper(workingDir);
		} else if (cmd.equals(COMMAND_START)) {
			theJob = new ForrestRunner(workingDir);
		} else if (cmd.equals(COMMAND_BUILD)) {
			theJob = new ForrestBuilder(workingDir);
		} else {
			theJob = null;
		}
		return theJob;
	}

	/**
	 * Get an array of Files to be placed in the
	 * classpath for Forrest.
	 * 
	 * @return an array of Files
	 */
	protected static File[] getClasspathFiles() {
		Vector vctFiles = new Vector();

		try {
			// FIXME: cache the classpath
			// add Forrest classes
			vctFiles.add(new File(FORREST_CLASSES));
			vctFiles.add(new File(FORREST_JAR));
			// add core libs
			vctFiles.addAll(getLibFiles(FORREST_CORE_LIB));
			//			 add endorsed libs
			vctFiles.addAll(getLibFiles(FORREST_ENDORSED_LIB));
			//			 add optional libs
			vctFiles.addAll(getLibFiles(FORREST_OPTIONAL_LIB));
		} catch (FileNotFoundException e) {
			logger.error("getClasspathFiles()", e);

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File[] files = new File[vctFiles.size()];
		File[] returnFileArray = (File[]) vctFiles.toArray(files);
		return returnFileArray;
	}

	/**
	 * Return an array of URLs that point to lib files.
	 * 
	 * @param dir -
	 *            directory in which to look for libs
	 * @return ArrayList of URLs
	 * @throws FileNotFoundException -
	 *             if the directory doesn't exist
	 */
	static public ArrayList getLibFiles(String dir) throws FileNotFoundException {
		File directory = new File(dir);
		ArrayList result = new ArrayList();
		List dirs = Arrays.asList(directory.listFiles());
		Iterator filesIter = dirs.iterator();
		File file = null;

		while (filesIter.hasNext()) {
			file = (File) filesIter.next();

			if (file.isDirectory()) {
				List deeperList = getLibFiles(file.toString());
				result.addAll(deeperList);
			} else if (file.toString().endsWith(".jar")
					|| file.toString().endsWith(".zip")) {
				result.add(file);
			}
		}
		
		return result;
	}

	/**
	 * Stop the server for the given project
	 * 
	 * @param projectDir
	 * @throws InterruptedException
	 * @throws DebugException
	 */
	public static void stopServer(String projectDir)
			throws DebugException {
		serverLaunch.terminate();
	}

	/**
	 * Set the server launch.
	 * @param launch - the launch
	 */
	public static void setServerLaunch(ILaunch launch) {
		serverLaunch = launch;
	}
	
}