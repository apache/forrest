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

import org.xml.sax.SAXParseException
import java.io.{FileNotFoundException, File}
import scala.io.Source
import javax.activation.MimetypesFileTypeMap

class SourceLocatorFilter(baseDir: File) extends Filter {
	val sourceDir = new File(baseDir,"sources/docs")
	val supportedTypes = Map(".xml"->"application/xml")
	//val typeMap = new MimetypesFileTypeMap("src/conf/mime.types")
	
	def process(model: Map[String, Any]) = {
	  println("Locating source for: " + model("requestUri"))
	  var internalModel = model
	  val path = model("requestUri").asInstanceOf[String]
	  val rawUri = path.substring(0,path.lastIndexOf("."))
	  
	  internalModel = model + ("resourceUri"->rawUri)
	  var sourceFile = new File(sourceDir, path)
	  println("Resolved location: " + sourceFile.getCanonicalPath)
	  var sourceType = "Unknown"
	 	  
	  supportedTypes.foreach( (k) => {
	 	  val f = new File(sourceDir,rawUri + k._1)
	 	  println("Type testing: " + f.getCanonicalPath)
	 	   if(f.exists) {
	 	  	 sourceFile = f
	 	  	 sourceType = k._2
	 	   }
	  })
	  
	 	
	  try {
	 	  if(sourceType == "Unknown") {
	 	 	println("Ooops... unable to find a suitable source for: " + sourceFile.getPath)
	 	 	internalModel+("status"->404)
	 	  } else {
	 	 	  
	 	   //println("FILE TYPE: " + typeMap.getContentType(sourceFile))
	 	   internalModel = internalModel + ("entityBody"->sourceFile) 
	 	    
	 	    internalModel + ("sourceType"->sourceType)	  
	 	  }
	 	  
	  } catch {
	 		case fnfe: FileNotFoundException =>
	 			println("Ooops.. couldn't find: " + sourceFile.getPath)
	 			internalModel + ("status"->404)
	 		case pe: SAXParseException => 
	 			println("Ooops... invalid document: " + pe.getMessage)
	 			internalModel + ("status"->500)
	  }
	  
	}

}