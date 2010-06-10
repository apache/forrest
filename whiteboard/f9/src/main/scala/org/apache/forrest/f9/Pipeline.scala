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
import org.apache.forrest.f9.filters._

object Pipeline {
	  var started = false
	  
	  val pipeline:List[Filter] = List(
	 		  new SourceLocatorFilter(new File(".")),
	 		  new SourceTypeFilter(),
	 		  new NormalizingFilter(),
	 		  new OutputTypeFilter("./output"))
	 		  
	def init() {
      pipeline.foreach(p => p.start)
      started = true
	}
	  
	def exit() {
	  if(started == true) 
	    pipeline.foreach(p => p ! "exit_now")
	}
	
	def pump(r: Map[String,Any]) {
	  println("Pumping " + r("requestUri") + " across " + pipeline.length + " pipes.")
      pipeline(0) ! (r, pipeline.drop(1))
	}
}