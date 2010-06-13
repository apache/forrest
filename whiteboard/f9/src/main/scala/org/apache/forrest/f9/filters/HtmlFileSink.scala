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

import scala.actors.AbstractActor
import scala.xml.Elem
import scala.xml.Node
import java.io.File
import java.io.FileWriter


class HtmlFileSink(base: String) extends Filter {
  private val xmldecl = "<?xml version='1.0' encoding='UTF-8'?>\n"
  private val extension = ".xhtml"
  private val pinst = "<?xml-stylesheet type=\"text/xsl\" href=\"/resources/f9/site.xsl\"?>\n"
  
  def process(model: Map[String, Any]) = {
    println("Processing HtmlFileSink: " + model("requestUri"))	
  	if (model.contains("status")) {
  		println("Error: " + model)
  		model
  	} else {
  		  val e = model("entityBody").asInstanceOf[Elem]
  		  val path = model("resourceUri").asInstanceOf[String]
  		  try {
  		 	val f = new File(base + path)
  		 	f.getParentFile.mkdirs
  		 	
  		 	val fwriter = new FileWriter(f + extension)
  		 	fwriter.write(xmldecl)
  		 	fwriter.write(pinst)
  		    scala.xml.XML.write(fwriter, e, "UTF-8", false, null)
  		    fwriter.flush
  		    fwriter.close
  		    
  		  } catch {
  		 	  case ex: Exception =>
  		 	    println("Error occurred: " + ex.toString)
  		 	    println("Error Model: " + model)
  		  }
  		  model	
  	}

  }

}
