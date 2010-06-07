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
package org.apache.forrest.f9.task

import java.io.File
import org.apache.commons.io.FileUtils

/**
 * Task to actually build the content through the pipeline.
 */
object BuildTask extends Task("build", false) {
	var base: File = null
		
	def call(state: AppState):Boolean = {
		println("Incoming state: " + state.projectDir)
		base = new File(state.projectDir, "sources/docs")
		val resBase = new File(state.projectDir, "sources/resources")
		val resOut = new File(state.projectDir, "output/resources")
		
		FileUtils.copyDirectory(resBase, resOut)
		call(base)
		true
	}
	
	/**
	 * A bit of awkwardness that needs explaining.  I wanted to keep the pipeline to 
	 * accepting of abstract resource (URI) requests.  That means, to do the static 
	 * build, we need to iterate over the source files and turn each of those paths
	 * into an abstract equivalent.  For example, we have to take a request for:
	 * $PROJECT_HOME/sources/docs/index.html => /index.html  and the pipeline is responsible
	 * for doing the location resolution. 
	 */
	private def call(f: File) {
		 
  	  if(!f.isDirectory()) {
  		val file = new File(base, f.getPath)
  		val r = Map("requestUri"->(f.getCanonicalPath.replace(base.getCanonicalPath, "")),
  				    "contentType"->"application/xhtml+xml")
		
  	    Pipeline.pump(r)
  	  } else {
  		f.listFiles().foreach((fil: File) => {
  			call(fil)
  		})
      }
	}

}