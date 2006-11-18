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

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.forrest.core.Controller;
import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractOutputDocument;

/**
 * A command line interface for Forrest.
 * 
 */
public class CLI {
	private static final Log log = LogFactory.getLog(CLI.class);

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
			AbstractOutputDocument doc = null;
			System.out.println("\n Processing request for " + args[0]);
			final URI requestURI = new URI(args[0]);
			final IController controller = new Controller();
			doc = controller.getOutputDocument(requestURI);

			System.out.println("\n Resulting document for request " + args[0]
					+ " is:\n");
			System.out.println(doc.getContentAsString());

		} catch (final Exception e) {
			e.printStackTrace();
			log.error(e);
			System.exit(1);
		}
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
