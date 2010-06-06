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

import scala.xml.Elem
import java.io.File

class XMLFileSink(base: String) extends Filter {
  //val baseDir = new File(base)
  
  def process(model: Map[String, Any]) = {
    	
  	if (model.contains("status")) {
  		println("Error: " + model)
  		model
  	} else {
  		  val e = model("entityBody").asInstanceOf[Elem]
  		  val path = model("requestUri").asInstanceOf[String]
  		  try {
  		 	val f = new File(base + path)
  		 	f.getParentFile.mkdirs
  		 	println("Writing to: " + base + path)
  		    scala.xml.XML.saveFull(base + path, e, "UTF-8", true, null)
  		  } catch {
  		 	  case ex: Exception =>
  		 	    println("Error occurred: " + ex.toString)
  		 	    println("Error Model: " + model)
  		  }
  		  model	
  	}

  }
}