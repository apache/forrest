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
package org.apache.forrest.eclipse.job;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.webbrowser.WebBrowser;
import org.eclipse.webbrowser.WebBrowserEditorInput;


/**
 * Run a version of Forrest
 */
public class ForrestRunner extends ForrestJob implements IJavaLaunchConfigurationConstants{
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(ForrestRunner.class);
	
	private static final int EXCEPTION_UNABLE_TO_START = 2010;
	
	private String workingDir;

	private static final int CORE_EXCEPTION = 100;

	private static final int IO_EXCEPTION = 101;

	private static final int BROWSER_ERROR = 200;
	
	/**
	 * Create a Forrest runner that will run a Jetty server on a given directory
	 * @param workingDir - the working directory for the command
	 */
	protected ForrestRunner(String workingDir) {
		super("Forrest Runner");
		
		this.workingDir = workingDir;
	}
	
	/* Run the Forrest server in a separate thread and return that thread to the Forrest manager.
	 * @see java.lang.Runnable#run()
	 * @refactor lots of potential to tidy up this code, for example extract a few methods, move relevant code to ForrestManager
	 */
	public IStatus run(IProgressMonitor monitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("run(IProgressMonitor) - start");
		}
		
		monitor.subTask("Initialising project");
		IStatus status = Status.OK_STATUS;
		String fhome = ForrestPlugin.getDefault().getPluginPreferences()
		  .getString(ForrestPreferences.FORREST_HOME);
		StringBuffer sb = new StringBuffer("-Dproject.home=");
		sb.append(workingDir);
		sb.append(" -Dbasedir=");
		sb.append(fhome + File.separatorChar + "main");
		sb.append(" ");
		sb.append("init");		
		status = runAnt(monitor, sb.toString());
			
		monitor.subTask("Starting Server");
		if(status.isOK()) {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType type =
			      manager.getLaunchConfigurationType(ID_JAVA_APPLICATION);
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
	              "org.mortbay.jetty.Server");
			   // TODO: allow project specific jettyconf.xml
			   String args = ForrestManager.FORREST_CORE_WEBAPP + File.separatorChar + "jettyconf.xml";
			   workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, args);
			   
			   List classpath = new ArrayList();
			   
			   File[] files = ForrestManager.getClasspathFiles();
			   IPath classpathEntryPath;
			   IRuntimeClasspathEntry classpathEntry;
			   for (int i = 0; i < files.length; i++) {
				   classpathEntryPath = new Path(files[i].getAbsolutePath());
				   classpathEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(classpathEntryPath);
				   classpathEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
				   classpath.add(classpathEntry.getMemento());
			   }
			   
			   IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
			   IRuntimeClasspathEntry systemLibsEntry =
			      JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath,
			         IRuntimeClasspathEntry.STANDARD_CLASSES);
			   classpath.add(systemLibsEntry.getMemento());
			   
			   workingCopy.setAttribute(ATTR_CLASSPATH, classpath);
			   workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);
			   
			   ForrestManager forrestManager = ForrestManager.getInstance();
			   Hashtable props = forrestManager.getProperties(workingDir);
			   
			   String propName;
			   String propValue;
			   StringBuffer sbVars = new StringBuffer();
			   for (Enumeration e = props.keys() ; e.hasMoreElements() ;) {
		         propName = (String)e.nextElement();
		         propValue = (String)props.get(propName);
			   	 sbVars.append("-D");
			   	 sbVars.append(propName);
			   	 sbVars.append("=");
			   	 sbVars.append(propValue);
			   	 sbVars.append(" ");
		         if (logger.isInfoEnabled()) {
					logger.info("Project property  : " + propName + " = " + propValue);
				 } 
		       }
			   
			   String strEndorsedLibs = "-Djava.endorsed.dirs=\"" + ForrestManager.FORREST_ENDORSED_LIB + "\"";
			   workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
			        sbVars.toString() + strEndorsedLibs);
			   
			   workingCopy.setAttribute(ATTR_WORKING_DIRECTORY, ForrestManager.FORREST_CORE_WEBAPP);
			   
			   ILaunchConfiguration jettyConfig = workingCopy.doSave();
			   //DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
			   ForrestManager.setServerLaunch( jettyConfig.launch(ILaunchManager.RUN_MODE, monitor));
			} catch (CoreException e) {
				logger.error("run(IProgressMonitor)", e);
				return new Status(Status.ERROR, ForrestPlugin.ID, CORE_EXCEPTION, "Unable to start Jetty server", e);
			} catch (IOException e) {
				logger.error("run(IProgressMonitor)", e);
				return new Status(Status.ERROR,ForrestPlugin.ID, IO_EXCEPTION, "Unable to start Jetty server", e);
			}
		}
		
		if ( ! openBrowser(monitor)) {
			status = new Status(Status.WARNING, ForrestPlugin.ID, BROWSER_ERROR, "Unable to open browser", null);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("run(IProgressMonitor) - end");
		}
		return status;
	}

	/**
	 * Open a web browser on the index page.
	 * @param monitor - the progress monitor for this job
	 * @return boolean - true if browser opened OK
	 */
	private boolean openBrowser(IProgressMonitor monitor) {

		monitor.subTask("Open index page");
		URL url;
		try {
			url = new URL("http://localhost:8888");
			WebBrowserEditorInput browserInput = new WebBrowserEditorInput(url, WebBrowserEditorInput.SHOW_ALL);
			WebBrowser.openURL(browserInput);
		} catch (MalformedURLException e1) {
			// Should never be thrown
			logger.error("openBrowser(IProgressMonitor)", e1);
			return false;
		}		
		monitor.worked(3);
		return true;
	}
}