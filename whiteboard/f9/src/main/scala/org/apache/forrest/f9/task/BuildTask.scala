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
object BuildTask extends Task("site", false) {
	var base: File = null
		
	def call(state: AppState):Boolean = {
		println("Incoming state: " + state.projectDir)
		base = new File(state.projectDir, "sources/docs")
		val resBase = new File(state.projectDir, "src/main/resources/project")
		val resOut = new File(state.projectDir, "output/resources/f9")
		
		//Copy all the static resources over.
		FileUtils.copyDirectory(resBase, resOut)
		
		//Maybe create some autonav for them.
		val srcNav = new File(state.projectDir, "sources/docs/nav.xml")
		
		if(!srcNav.exists) {
		   FileUtils.writeStringToFile( new File(state.projectDir, "output/nav.xml"),
		  		   "<?xml version=\"1.0\"?>" + getNav(base, state))
		}
		
		//Now, go ahead and generate the main content.
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
		 
  	  if(!f.isDirectory() && !f.isHidden()) {
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
	
  
      
  def getNav(d: File, state: AppState): String = {
  	val buf = new StringBuilder
  	//println("CONTENT_DIR: " + d.getAbsolutePath)
  	//println(".. relative: " + d.getAbsolutePath.replace(contentDirFile.getAbsolutePath,""))
  	if (d.isHidden) return buf.toString
  	
  	if(d.isDirectory() && !d.isHidden) {
  		
  	  if(isVisible(d)) {
  	    buf.append("<ul><li>")
  	    buf.append(format(d.getName))
  	    buf.append("<ul>")
  		d.listFiles().foreach((f: File) => {
  			buf.append(getNav(f, state))
  		})	
  		buf.append("</ul>")
  		buf.append("</li></ul>")
  	  }
  	} else {
  		val srcpath = new File(state.projectDir, "sources/docs")
  		val path = srcpath.toURI.relativize(d.toURI)
  		buf.append("<li><a href=\"/")
  		/**
  		*  TODO:  I'm not at all sure where this is going.  I'd like to be able to auto
  		*  generate some nav for simple sites.  I reckon we need some notion of a default
  		*  Media Type which implies an extension, but not sure.  Ideally, no extension with
  		*  HTTP Conneg would take place maybe.
  		**/
  		buf.append(path.toString.replace(".xml",".xhtml"))
  		buf.append("\">")
  		buf.append(format(d.getName))
  		buf.append("</a></li>")
  	}
 
  	buf.toString
  }
  
  def format(s: String): String = {
  	val s2 = s.replace("_"," ").capitalize
  	
  	
  	if (s2.lastIndexOf(".") > 0)
  	  s2.substring(0,s2.lastIndexOf("."))
  	else 
  	  s2
  }
   def isVisible(dir: File): Boolean = {
	  val hideme = new File(dir.getAbsolutePath() + "/hideme")

	  return !hideme.exists()
	  
  } 

}