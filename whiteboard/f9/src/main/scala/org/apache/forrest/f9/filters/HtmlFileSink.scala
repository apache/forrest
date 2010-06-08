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