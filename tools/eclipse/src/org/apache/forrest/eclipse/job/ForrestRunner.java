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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.burrokeet.servletEngine.Jetty;
import org.burrokeet.servletEngine.Server;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
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

	private static final int BROWSER_ERROR = 200;

	private static final int IO_EXCEPTION = 300;

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
			ForrestManager forrestManager = ForrestManager.getInstance();
			String confPath = ForrestManager.FORREST_CORE_WEBAPP + File.separatorChar + "jettyconf.xml";
			try {
				Server jetty = new Jetty(confPath, 
					  			  ForrestManager.FORREST_CORE_WEBAPP, 
								  ForrestManager.FORREST_ENDORSED_LIB,
								  forrestManager.getProperties(workingDir),
								  ForrestManager.getClasspathFiles());
				status = jetty.start(monitor);
				if (status.isOK()) {
					ForrestManager.setServerLaunch(jetty.getLaunch());
				} else {
					ForrestManager.setServerLaunch(null);
				}
			} catch (IOException e) {
				logger.error("run(IProgressMonitor)", e);
				status = new Status(Status.ERROR,ForrestPlugin.ID, IO_EXCEPTION, "Unable to start Jetty server", e);
			}
		}
		
		// FIXME: only open the browser once JETTY has fully started
		if ( status.isOK() & ! openBrowser(monitor)) {
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