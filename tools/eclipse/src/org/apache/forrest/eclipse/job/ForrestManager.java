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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.jobs.Job;

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
	
	/**
	 * Create a manager, this is not intended to be called from your code.
	 * Use Manager.getInstance() instead. 
	 */
	private ForrestManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static ForrestManager getInstance() {
		return ForrestManager.INSTANCE;
	}
	
	/**
	 * Create a Forrest Job. 
	 * @param workingDir - the directory on which the job is to work
	 * @param cmd - the command the job is to carry out
	 * @return
	 */
	public Job getForrestJob(String workingDir, String cmd) {
		Job theJob;
		if (cmd.equals(COMMAND_STOP)) {
			theJob = new ForrestStopper(workingDir);
		} else if (cmd.equals(COMMAND_START)){
			theJob = new ForrestRunner(workingDir);
		} else if (cmd.equals(COMMAND_BUILD)) {
			theJob = new ForrestBuilder(workingDir);
		} else {
			theJob = null;
		}
		return theJob;
	}

}
