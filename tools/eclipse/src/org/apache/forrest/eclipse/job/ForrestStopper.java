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

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;


/**
 * Run a version of FOrrest
 */
public class ForrestStopper extends ForrestJob  {
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(ForrestStopper.class);
	
	private static final int EXCEPTION_UNABLE_TO_STOP = 3010;
	
	/**
	 * Create a Forrest runner that will run a JEtty server on a given directory
	 * @param workingDir - the working directory for the command
	 */
	protected ForrestStopper(String workingDir) {
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

		try {
			ForrestManager.stopServer(workingDir);
			logger.info("run() - Forrest server stopped");
			status = Status.OK_STATUS;
		} catch (DebugException e) {
			logger.error("run(IProgressMonitor)", e);
			status = new Status(Status.ERROR, ForrestPlugin.ID, ForrestStopper.EXCEPTION_UNABLE_TO_STOP, "Unable to stop Forrest Server", e);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("run(IProgressMonitor) - end");
		}
		return status;
	}
	
}