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
package org.apache.forrest.eclipse.servletEngine;

import java.io.File;
import java.util.Hashtable;


/**
 * Creates a launch configuration for an instance of JettyPlus.
 *
 * WARNING - This class is not fully tested, it works in one use case,
 * but it is expected that more work is required. If you do not need
 * the JettyPlus services then use the more stable Jetty superclass.
 */
public class JettyPlus extends Jetty {
	
	protected final String serverClass = "org.mortbay.jetty.plus.Server";

	/**
	 * @param confPath
	 * @param workingDir
	 * @param endorsedDir
	 * @param properties
	 * @param classpathFiles
	 */
	public JettyPlus(String confPath, String workingDir, String endorsedDir,
			Hashtable properties, File[] classpathFiles) {
		super(confPath, workingDir, endorsedDir, properties, classpathFiles);
	}

}
