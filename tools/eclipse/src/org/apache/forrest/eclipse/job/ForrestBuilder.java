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

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;


/**
 * Run a version of Forrest
 */
public class ForrestBuilder extends ForrestJob  {
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(ForrestBuilder.class);
	
	/**
	 * Create a Forrest runner that will run a JEtty server on a given directory
	 * @param workingDir - the working directory for the command
	 */
	protected ForrestBuilder(String workingDir) {
		super("Forrest Runner");
		
		this.workingDir = workingDir;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public IStatus run(IProgressMonitor monitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("run(IProgressMonitor) - start");
		}
		
		IStatus status = null;
		String fhome = ForrestPlugin.getDefault().getPluginPreferences()
		  .getString(ForrestPreferences.FORREST_HOME);
		StringBuffer sb = new StringBuffer("-Dproject.home=");
		sb.append(workingDir);
		sb.append(" -Dbasedir=");
		sb.append(fhome + File.separatorChar + "main");
		sb.append(" site");
        status = runAnt(monitor, sb.toString());
		return status;
	}
	
}
