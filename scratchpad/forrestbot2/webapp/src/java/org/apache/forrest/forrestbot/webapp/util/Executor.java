/*
* Copyright 2002-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on Mar 9, 2004
 */
package org.apache.forrest.forrestbot.webapp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.forrest.forrestbot.webapp.Config;
import org.apache.log4j.Logger;

class ExecutorThread extends Thread {
	private Process proc;
	private Logger log;
	private boolean debug; 
	
	public ExecutorThread(String id, Process p) {
		super(id);
		proc = p;
		log = Logger.getLogger(Executor.class + " " + id);
		debug = Boolean.valueOf(Config.getProperty("debug-exec")).booleanValue();
	}

	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader brErr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		try {
			String line;
			while ((line = brErr.readLine()) != null) {
				if (debug)
					log.error(line);
			}
			while ((line = br.readLine()) != null) {
				if (debug)
					log.debug(line);
			}
		} catch (IOException e) {
			log.warn("error reading from process output", e);
			return;
		}
	}

}

public class Executor {
	private static Logger log = Logger.getLogger(Executor.class);

	private static void run(String target, String project) throws IOException {
		String command = Config.getProperty("forrest-exec") + " -f " + project + ".xml " + target;
		File workingDir = new File(Config.getProperty("config-dir"));

		log.debug("executing '" + command + "' in " + workingDir);

		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(command, null, workingDir);
		ExecutorThread execThread = new ExecutorThread(project, proc);
		execThread.start();
		// don't wait for it to finish
	}

	public static void build(String project) throws IOException {
		run(Config.getProperty("targets.build"), project);

	}

	public static void deploy(String project) throws IOException {
		run(Config.getProperty("targets.deploy"), project);
	}

}
