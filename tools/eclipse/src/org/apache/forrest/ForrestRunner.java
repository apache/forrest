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
package org.apache.forrest;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


import java.io.File;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Run and monitor a version of Forrest..
 */
public class ForrestRunner extends Job  {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(ForrestRunner.class);


	public static final String COMMAND_BUILD = "site";
	public static final String COMMAND_START = "run";
	public static final String COMMAND_STOP = "stop";
	
	private static final int UNKOWN_COMMAND = 0;
	private static final int BUILD = 1;
	private static final int START = 2;
	private static final int STOP = 3;
	
	public static final int EXCEPTION_UNIDENTIFIED = 1001;
	public static final int EXCEPTION_UNABLE_TO_STOP = 1010;
	public static final int EXCEPTION_UNKOWN_COMMAND = 1020;
	
	private int stop_port = Integer.getInteger("STOP.PORT",8079).intValue();
	private String _key = System.getProperty("STOP.KEY","mortbay");
	
	private static ForrestRunner INSTANCE;
	private int commandCode;
	private String workingDir;
	
	/**
	 * Create a Forrest runner to execute a specified command on a given directory
	 * @param workingDir - the working directory for the command
	 * @param cmd - the forrest command
	 */
	public ForrestRunner(String workingDir, String cmd) {
		super("Forrest Runner");
		
		ForrestPlugin plugin = ForrestPlugin.getDefault();
		URL urlPluginDir = plugin.getBundle().getEntry("/");
		String strLog4jConf = "D:\\projects\\burrokeet\\forrestplugin\\conf\\log4j.xml";
		DOMConfigurator.configure(strLog4jConf);
		
		if (cmd.equals(COMMAND_STOP)) {
			commandCode = STOP;
		} else if (cmd.equals(COMMAND_START)){
			commandCode = START;
		} else if (cmd.equals(COMMAND_BUILD)) {
			commandCode = BUILD;
		} else {
			commandCode = UNKOWN_COMMAND;
		}
		this.workingDir = workingDir;
		INSTANCE = this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public IStatus run(IProgressMonitor monitor) {
		if (logger.isDebugEnabled()) {
			logger.debug("run(IProgressMonitor) - start");
		}
		IStatus status;
		String cmdString = null;
		
		if (commandCode == STOP) {
	    	status = this.stop();
		} else if (commandCode != UNKOWN_COMMAND){
		    status = runAnt(monitor, getAntCommand());
		} else {
			status = new Status(Status.ERROR, null, ForrestRunner.EXCEPTION_UNKOWN_COMMAND, "Unkown Forrest Command", null);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("run(IProgressMonitor) - end");
		}
		return status;
	}
	
	/**
	 * @return a string that is passed to ANT as a command
	 */
	private String getAntCommand() {
		if (logger.isDebugEnabled()) {
			logger.debug("getAntCommand(int) - start");
		}

		String cmdString;
		String fhome = ForrestPlugin.getDefault().getPluginPreferences()
		  .getString(ForrestPreferences.FORREST_HOME);
		StringBuffer sb = new StringBuffer("-Dproject.home=");
		sb.append(workingDir);
		sb.append(" -Dbasedir=");
		sb.append(fhome + File.separatorChar + "main");
		sb.append(" ");
		
		switch (commandCode) {
			case START:
				sb.append(COMMAND_START);
				break;
		    case STOP:
		    	sb = null;
				break;
		    case BUILD:
		    	sb.append(COMMAND_BUILD);
		}
		
		if (sb != null) {
			cmdString = sb.toString();
		} else {
			cmdString = null;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("getAntCommand(int) - end");
		}
		return cmdString;
	}

	/**
	 * @param monitor
	 * @param cmdString
	 * @return
	 */
	private IStatus runAnt(IProgressMonitor monitor, String cmdString) {
		if (logger.isDebugEnabled()) {
			logger.debug("runAnt(IProgressMonitor, String) - start");
		}

		IStatus status = Status.OK_STATUS;
		
		if (cmdString != null) {
			String fhome = ForrestPlugin.getDefault().getPluginPreferences()
			  .getString(ForrestPreferences.FORREST_HOME);
			AntRunner runner = new AntRunner();
			String antFile = fhome + File.separatorChar + "main" + File.separatorChar + "forrest.build.xml";
			try {
				runner.setBuildFileLocation(antFile);
				runner.setArguments(cmdString);
				logger.info("Running ANT with command string = " + cmdString);
				runner.run(monitor);
			} catch (CoreException e) {
				logger.error("run(IProgressMonitor)", e);
				status = new Status(Status.ERROR, null, ForrestRunner.EXCEPTION_UNIDENTIFIED, "Forrest Exception", null);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("runAnt(IProgressMonitor, String) - end");
		}
		return status;
	}
	
	/**
	 * Stop this version of Forrest.
	 *
	 */
	public Status stop() {
		if (logger.isDebugEnabled()) {
			logger.debug("stop() - start");
		}
		IStatus status;

		try {
            Socket s=new Socket(InetAddress.getByName("127.0.0.1"), stop_port);
            OutputStream out = s.getOutputStream();
            out.write((_key+"\r\nstop\r\n").getBytes());
            out.flush();
            s.shutdownOutput();
            s.close();
            status = Status.OK_STATUS;
        } catch (Exception e) {
			logger.error("stop()", e);
			status = new Status(Status.ERROR, null, ForrestRunner.EXCEPTION_UNABLE_TO_STOP, "Forrest Exception", null);
        }

		if (logger.isDebugEnabled()) {
			logger.debug("stop() - end");
		}
		return (Status)status;
	}
	
	
	/**
	 * @return Returns the running Forrest instance.
	 */
	public static ForrestRunner getInstance() {
		if (logger.isDebugEnabled()) {
			logger.debug("getInstance() - start");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getInstance() - end");
		}
		return INSTANCE;
	}
}