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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Run and monitor a version of Forrest..
 */
public class ForrestRunner implements Runnable {
	/** Indicates the ForrestRunner is initialised and ready to be started */
	public static final int INITIALISED = 0;
	/** Indicates the ForrestRunner is confiuguring the environment for the server */
	public static final int CONFIGURING = 5;
	/** Indicates the Forrest server is starting up */
	public static final int STARTING = 10;
	/** Indicates the Forrest server is running */
	public static final int RUNNING = 20;
	/** Indicates the Forrest server is stopping */
	public static final int STOPPING = 30;
	/** Indicates the Forrest server has stopped */
	public static final int STOPPED = 40;
	/** Indicates the Forrest server has hit an exception condition */
	public static final int EXCEPTION = 101; 
	
	private int stop_port = Integer.getInteger("STOP.PORT",8079).intValue();
	private String _key = System.getProperty("STOP.KEY","mortbay");
	
	protected String exceptionMessage;
	
	private static ForrestRunner INSTANCE;
	protected File workingDirectory;
	protected String cmdString;
	Process forrestProcess;
	protected Thread forrestThread;
	protected BufferedReader forrestOutput;
	protected int status;

	/**
	 * Create a JettyRunner to execute Jetty on the
	 * supplied working directory. 
	 */
	public ForrestRunner(String workingDir) {
		this.workingDirectory = new File(workingDir);
		if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
			cmdString = "forrest -Dbasedir=" + workingDir
			+ " run";
		} else if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			cmdString = "cmd /c forrest -Dbasedir=" + workingDir
			+ " run";
		} // FIXME: implement for Mac and other OS's

		forrestThread = new Thread(this);
		INSTANCE = this;
		status = INITIALISED;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			String str;
			while ((str = forrestOutput.readLine()) != null) {
				// FIXME: Output should be sent to log not console
				System.out.println(str);
				if (str.indexOf("init-props:") != -1) {
					status = CONFIGURING;
				} else if (str.indexOf("run_default_jetty:") != -1 || str.indexOf("run_custom_jetty:") != -1) {
					status = STARTING;
				} else if (str.indexOf("EVENT  Started org.mortbay.jetty.Server") != -1) {
					status = RUNNING;
				} else if (str.indexOf("FIXME: what is the message that indicates server is stopping") != -1) {
					status = STOPPING;
				} else if (str.indexOf("FIXME: what is the message that indicates server has stopped") != -1) {
					status = STOPPED;
				} else if (str.indexOf("org.mortbay.util.MultiException[java.net.BindException: Address already in use: JVM_Bind]") != -1) {
					exceptionMessage = str;
					status = EXCEPTION;
				} 
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Start this version of Forrest.
	 *
	 */
	public void start(){
		System.out.println("Starting Forrest with the command " + cmdString);
		try {
			forrestProcess = Runtime.getRuntime().exec(cmdString, null, workingDirectory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		forrestOutput = new BufferedReader(
				new InputStreamReader(forrestProcess.getInputStream()));
		forrestThread.start();
	}
	
	/**
	 * Stop this version of Forrest.
	 *
	 */
	public void stop() {
		try {
            Socket s=new Socket(InetAddress.getByName("127.0.0.1"), stop_port);
            OutputStream out = s.getOutputStream();
            out.write((_key+"\r\nstop\r\n").getBytes());
            out.flush();
            s.shutdownOutput();
            s.close();
        } catch (Exception e) {
			exceptionMessage = "Unable to Stop Server (" + e.getMessage() + ")";
        	status = EXCEPTION;
        	// TODO: log this error correctly
            e.printStackTrace();
        }
		forrestProcess.destroy();
		forrestThread.stop();
	}
	
	
	/**
	 * @return Returns the running Forrest instance.
	 */
	public static ForrestRunner getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Get the current status of this ForrestRunner.
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}
	
	/** 
	 * Get the message associated with the last exception thrown by this version of Forrest.
	 * @return A message describing the last exception
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}
}