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
package org.apache.forrest.eclipse.servletEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Creates a launch configuration for an instance of Jetty.
 */
public class Jetty implements IJavaLaunchConfigurationConstants, Server {

	private static final int CORE_EXCEPTION = 100;
	
	private ILaunch launch;

	private String confPath;

	private String workingDir;

	private String endorsedDir;

	private Hashtable properties;

	private File[] classpathFiles;

	protected final String serverClass = "org.mortbay.jetty.Server";

	/**
	 * @param confPath
	 * @param workingDir
	 * @param endorsedDir
	 * @param properties
	 * @param classpathFiles
	 */
	public Jetty(String confPath, String workingDir, String endorsedDir, Hashtable properties, File[] classpathFiles) {
		this.confPath = confPath;
		this.workingDir = workingDir;
		this.endorsedDir = endorsedDir;
		this.properties = properties;
		this.classpathFiles = classpathFiles;
	}

	/**
	 * Start an instance of Jetty using the config file at the indicated path
	 * 
	 * @param jettyConfPath
	 * @param monitor
	 * @param strEndorsedDirs
	 * @param props
	 * @param files
	 * @return
	 */
	public IStatus start(IProgressMonitor monitor) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(ID_JAVA_APPLICATION);
		ILaunchConfiguration[] configurations;
		ILaunchConfigurationWorkingCopy workingCopy;
		try {
			configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals("Start Jetty")) {
					configuration.delete();
					break;
				}
			}
			workingCopy = type.newInstance(null, "Start Jetty");
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
					serverClass);
			
			if (confPath != null) {
				workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, confPath);
			}
			
			workingCopy.setAttribute(ATTR_CLASSPATH, getClasspath(classpathFiles));
			workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
			
			StringBuffer props = new StringBuffer();
			if (properties != null) {
				props.append(getPropertyString(properties));
			}
			if (endorsedDir != null) {
				props.append("-Djava.endorsed.dirs=\"");
				props.append(endorsedDir);
				props.append("\"");
			}
			
			if (props.length() >0) {
			  workingCopy.setAttribute(ATTR_VM_ARGUMENTS, props.toString());
			}
			
			if (workingDir != null) {
			  workingCopy.setAttribute(ATTR_WORKING_DIRECTORY, workingDir);
			}

			ILaunchConfiguration jettyConfig = workingCopy.doSave();
			launch = jettyConfig.launch(ILaunchManager.RUN_MODE,
					monitor);
		} catch (CoreException e) {
			return new Status(Status.ERROR, ServletEnginePlugin.ID, CORE_EXCEPTION,
					"Unable to start Jetty server", e);
		}
		return Status.OK_STATUS;
	}

	/**
	 * @param props
	 * @return
	 */
	private String getPropertyString(Hashtable props) {
		String propName;
		String propValue;
		StringBuffer sbVars = new StringBuffer();
		for (Enumeration e = props.keys(); e.hasMoreElements();) {
			propName = (String) e.nextElement();
			propValue = (String) props.get(propName);
			sbVars.append("-D");
			sbVars.append(propName);
			sbVars.append("=");
			sbVars.append(propValue);
			sbVars.append(" ");
		}
		return sbVars.toString();
	}

	/**
	 * @return
	 * @throws CoreException
	 */
	private List getClasspath(File[] files) throws CoreException {
		List classpath = new ArrayList();
		IPath classpathEntryPath;
		IRuntimeClasspathEntry classpathEntry;
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				classpathEntryPath = new Path(files[i].getAbsolutePath());
				classpathEntry = JavaRuntime
						.newArchiveRuntimeClasspathEntry(classpathEntryPath);
				classpathEntry
						.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
				classpath.add(classpathEntry.getMemento());
			}
		}
		classpath.addAll(getJettyClasspath());
		
		IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime
				.newRuntimeContainerClasspathEntry(systemLibsPath,
						IRuntimeClasspathEntry.STANDARD_CLASSES);
		classpath.add(systemLibsEntry.getMemento());
		return classpath;
	}

	/**
	 * Get a List of Jetty files and directories to add to the classpath.
	 * @return
	 * @throws CoreException
	 */
	protected List getJettyClasspath() throws CoreException {
		URL installURL = Platform.getBundle(ServletEnginePlugin.ID).getEntry("/");
		String libDir;
		try {
			libDir = Platform.resolve(installURL).getFile() + File.separator + "lib" + File.separator;
			return getLibFiles(libDir);
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, ServletEnginePlugin.ID, 0, "Unable to work with lib directory", e));
		}
	}
	
	/**
	 * Return an array of URLs that point to lib files.
	 * 
	 * @param dir -
	 *            directory in which to look for libs
	 * @return ArrayList of URLs
	 * @throws FileNotFoundException -
	 *             if the directory doesn't exist
	 * @throws CoreException
	 */
	static public List getLibFiles(String dir) throws FileNotFoundException, CoreException {
		List classpath = new ArrayList();
		File directory = new File(dir);
		ArrayList files = new ArrayList();
		ArrayList result = new ArrayList();
		List dirs = Arrays.asList(directory.listFiles());
		Iterator filesIter = dirs.iterator();
		File file = null;
		IPath classpathEntryPath;
		IRuntimeClasspathEntry classpathEntry;

		while (filesIter.hasNext()) {
			file = (File) filesIter.next();

			if (file.isDirectory()) {
				List deeperList = getLibFiles(file.toString());
				files.addAll(deeperList);
			} else if (file.toString().endsWith(".jar")
					|| file.toString().endsWith(".zip")) {
				files.add(file);
			}
		}
		
		Iterator itr = files.iterator();
		while (itr.hasNext()) {
			file = (File)itr.next();
			classpathEntryPath = new Path(file.toString());
			classpathEntry = JavaRuntime
						.newArchiveRuntimeClasspathEntry(classpathEntryPath);
			classpathEntry
						.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			classpath.add(classpathEntry.getMemento());
		}
		
		return classpath;
	}


	/**
	 * @return
	 */
	public ILaunch getLaunch() {
		return launch;
	}

}
