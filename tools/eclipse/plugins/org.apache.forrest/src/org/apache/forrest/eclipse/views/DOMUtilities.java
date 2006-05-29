/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * license agreements.  See the NOTICE file distributed with
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

package org.apache.forrest.eclipse.views;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DOMUtilities {
	private static Document document;

	/**
	 * This loads an XML file into a DOM called document
	 * 
	 */
	public static Document loadDOM(String path) {
		// Loads the XML file iton the DOM document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		try {
			parser = factory.newDocumentBuilder();
			document = null;
			document = parser.parse(new File(path));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;

	}

	/**
	 * This loads the XML file from a URL
	 * 
	 */
	public static Document loadXMLFromURL (String url) {
	     try {
	       DocumentBuilderFactory documentBuilderFactory =
	DocumentBuilderFactory.newInstance();
	       documentBuilderFactory.setNamespaceAware(true);
	       documentBuilderFactory.setIgnoringElementContentWhitespace(false);
	       DocumentBuilder documentBuilder =
	documentBuilderFactory.newDocumentBuilder();
	       return documentBuilder.parse(url);
	     }
	     catch (Exception e) {
	       //FIXME: Log the error when a host is not found
	       return null;
	     }
	   } 
	
	
	/**
	 * This writes the DOM to an XML file
	 * 
	 */
	public static void SaveDOM(Document document, String filename) {
		try {

			Source source = new DOMSource(document);
			File file = new File(filename);
			Result result = new StreamResult(file);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
	}

	

}
