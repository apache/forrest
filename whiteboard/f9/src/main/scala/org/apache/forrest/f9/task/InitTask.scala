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
 * Checks to ensure the current directory is actually a project.  If not,
 * offer to create it.
 * 
 */

object InitTask extends Task("init", true) {
	def dirs = List("output",
					"sources",
					"sources/docs",
					"sources/resources/images",
					"sources/resources/scripts")


	
	def call(state: AppState): Boolean = {
		val f = state.projectDir
		
		validate(f) match {
			case false =>
			  print("Project doesn't exist, create now? (y,[n])  ")
			  Console.readLine match {
			 	  case "y" =>
			 	  	println("ok(creating).")
			 	  	create(f)
			 	  case _ =>
			 	   	println("not(created).")
			 	   	return false
			  }
			  
			case _ =>
			  println("ok(valid).")
		}

		Pipeline.init
		state.state += ("initialized"->true)
		return true
		
	}
	
	private def validate(root: File): Boolean = {
		dirs.foreach(f => {
			val file = new File(root, f)
			
			if(!file.exists) return false
		})
		return true
	}
	
	private def create(root: File): Boolean = {
		dirs.foreach(f => {
			val file = new File(root, f)
			
			if(!file.exists) file.mkdirs
		})
		return true		
	}
}

case class Dir(name: String, children: List[Dir])