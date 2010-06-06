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

/**
 * Cleans the all the generated content directories.
 */

object CleanTask extends Task("clean", false) {
	//Just list the root of the generated dirs to del.
	val generatedDirs = List("output")
	
	
	def call(state: AppState):Boolean = {
	 try {
		clean(state.projectDir)
		true
	 } catch {
		 case e =>
		 	println("error(clean).")
		 	println("[error] - " + e.getMessage)
		 	false
	 }
	}
	
	private def clean(root: File) {
		generatedDirs.foreach(f => {
			val file = new File(root, f) 
			del(file.listFiles)
		})
	}
	
	private def del(files: Array[File]) {
		files.foreach(f=> {
			
			if(f.isDirectory) {
				del(f.listFiles)
			} else {
				f.delete
			}
			f.delete
		})
		
	}

}