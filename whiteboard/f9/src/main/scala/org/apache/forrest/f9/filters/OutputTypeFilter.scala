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
package org.apache.forrest.f9.filters


class OutputTypeFilter(base: String) extends Filter {
	val supportedTypes = Map("application/xhtml+xml"->new HtmlFileSink(base))

	supportedTypes.values.foreach(t => t.start)
	
 	def process(model: Map[String, Any]) = {
 		println("Attempting output for: " + model("resourceUri"))
 		
 		val typ = model("contentType").asInstanceOf[String]
 		
 		if(supportedTypes.contains(typ)) {
 		   val sink = supportedTypes(typ).asInstanceOf[Filter]
 		
 		   sink ! (model, List())
 		} else {
 		   println("Not supported: " + model("resourceUri"))
 		}

 		model
	}
	
	override def exit_hook() {
		supportedTypes.values.foreach(t => t ! "exit_now")
	}
}