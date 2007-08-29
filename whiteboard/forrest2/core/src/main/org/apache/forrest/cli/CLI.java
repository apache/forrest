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
package org.apache.forrest.cli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.forrest.core.Controller;
import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.xml.sax.SAXException;

/**
 * A command line interface for Forrest.
 * 
 */
public class CLI {
	private static final Log log = LogFactory.getLog(CLI.class);

	private static Set<String> processedUris = new HashSet<String>();

	private static Set<String> unProcessedUris = new HashSet<String>();

	private static IController controller;

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		System.out.println("Forrest 2 Command Line Interface");
		System.out.println("================================");

		if (args[0].equals("--help")) {
			displayHelp();
			return;
		}

		try {
			controller = new Controller();
		} catch (Exception e) {
			log.fatal("Uable to get controller", e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("\n Processing request for " + args[0]);
		unProcessedUris.add(args[0]);
		while (unProcessedUris.size() > 0) {
			processURIs(unProcessedUris);
		}
	}

	/**
	 * Processes a URI to get the response document. Any local links found in
	 * the document are added to the list of documents to be processed.
	 * 
	 * @param uri
	 * @param controller
	 * @throws MalformedURLException
	 * @throws ProcessingException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void processURIs(final Set<String> uris) {
		AbstractOutputDocument doc;
		HashSet<String> processingUris = new HashSet<String>(uris);
		unProcessedUris = new HashSet<String>();
		for (String strUri : processingUris) {
			try {
				URI uri = new URI(strUri);
				if (!(processedUris.contains(strUri))) {
					log.debug("Processing: " + strUri);
					doc = controller.getOutputDocument(uri);
					unProcessedUris.addAll(doc.getLocalDocumentLinks());
					outputDocument(doc, uri);
					processedUris.add(strUri);
				}
			} catch (URISyntaxException e) {
				log.error("Unable to process " + strUri, e);
			} catch (MalformedURLException e) {
				log.error("Unable to process " + strUri, e);
			} catch (ProcessingException e) {
				log.error("Unable to process " + strUri, e);
			} catch (IOException e) {
				log.error("Unable to process " + strUri, e);
			}
		}
	}

	/**
	 * Output the document.
	 * 
	 * @param doc
	 * @param uri
	 * @throws IOException
	 */
	private static void outputDocument(AbstractOutputDocument doc, URI uri)
			throws IOException {
		System.out.println("\n Resulting document for request " + uri
				+ " is:\n");
		System.out.println(doc.getContentAsString());

		String path = getContentBuildPath();
		File outPath = new File(path);
		outPath.mkdirs();
		File outFile = new File(path + doc.getPath());
		FileWriter writer = new FileWriter(outFile);
		writer.write(doc.getContentAsString());
		writer.close();
		log.debug("Written document to " + path + doc.getPath());
	}

	private static String getContentBuildPath() {
		return System.getProperty("user.dir") + "/build/content/";
	}

	/**
	 * Outputs a short description on how to use the CLI.
	 */
	private static void displayHelp() {
		System.out.println("Help");
		System.out.println("====");
		System.out.println("CLI [URI]");
		System.out.println("URI is the request URI that you wish to process");
		System.out
				.println("The default behaviour is to process the request and output the results to the System.out");
	}

}
