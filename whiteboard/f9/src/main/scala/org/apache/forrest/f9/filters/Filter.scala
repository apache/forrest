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

import scala.actors.Actor
import scala.actors.Actor._
import scala.xml.Elem

trait Filter extends Actor {
    
	def act() {
	  loop {
	   react {
	     case (model: Map[String, Any], p: List[Filter]) if p.length == 0 =>
	       process(model)
	     case(model: Map[String, Any], p: List[Filter]) if model.contains("status") =>
	        println("Shortcircuit pipeline for code: " + model("status"))
	        
	        p.last ! (model, List())
         case (model: Map[String, Any], p: List[Filter]) =>
	       val data = process(model)
	       p(0) ! (data, p.drop(1))
         case "exit_now" =>
	        println("Exiting pipe: " + this.getClass.getName)
	        exit_hook()
         	exit()
	     case msg =>
	       println("Filter not understood." + msg)
	   }
     }
	}
	def process(model: Map[String, Any]): Map[String, Any] 
	def exit_hook() {}
}
