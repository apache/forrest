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
package org.apache.forrest.f9

import java.io.File
import scala.io.Source

import org.apache.forrest.f9.task._

object Main {
  private val caret = "f9> "
  private val appState = new AppState(new File("."))
	  
  def main(args: Array[String]): Unit = {  
	  //var state = if(args.length>0) args(0) else "init"
	  //The above will be cool with dependent states, until then,
	  // we have to always init first, so ignore CL args
	  var state = "init"
	  var next = ""
	  
	  while(true) {
	   state match {
	  	  case InitTask.text =>
	  	    if(ok(InitTask)) state = retState else state = "exit"
	 	  case BuildTask.text => 
	  	    ok(BuildTask)
	
	 	 	state = retState
	 	  case CleanTask.text =>
	 	 	ok(CleanTask)
	 	 	
	 	 	state = retState
	 	  case RunTask.text =>
	 	 	ok(RunTask)
	 	 	
	 	 	state = retState
	 	  case StopTask.text =>
	 	 	ok(StopTask)
	 	 	
	 	 	state = retState
	 	  case "exit" =>
	 	 	Pipeline.exit
	 	 	return 	    
	 	  case _ =>
	 	    println("unknown(" + state + ").")
	 	    state = retState
	 	   
	   }
	  } 
  } 
  
  def retState():String = {
	  print(caret)
	  readLine
  }
  def ok(task: Task):Boolean = {
	  println("ok(" + task.text + ").")
	  
	  task.call(appState)
  }
}
