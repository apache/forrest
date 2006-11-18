/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.test.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import junit.framework.TestCase;

import org.apache.forrest.core.Controller;
import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.InternalDocument;
import org.apache.forrest.core.exception.LocationmapException;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.Location;

public class TestController extends TestCase {
	static final String SOURCE_DOCUMENT_XHTML2_SIMPLE = "/xdocs/samples/xhtml2/sample_simple.html";

	public static final String SOURCE_DOCUMENT_XHTML2_COMPLETE = "/xdocs/samples/xhtml2/sample_complete.html";

	public static final String SOURCE_DOCUMENT_HELLO_WORLD = "/xdocs/samples/xml/helloWorld.xml";

	public static final String XSLT_INTERNAL_TO_HTML = "/xdocs/samples/xslt/internal-to-html.xsl";

	public static final String BASE_REQUEST_URI = "http://localhost:8888/xhtml/";

	public static final String SOURCE_REQUEST_URI = BASE_REQUEST_URI
			+ "testPage.forestSource";

	public static final String INTERNAL_REQUEST_URI = BASE_REQUEST_URI
			+ "testPage.forestInternal";

	public static final String XHTML_REQUEST_URI = BASE_REQUEST_URI
			+ "testPage.html";

	public static final String HELLO_WORLD_REQUEST_URI = BASE_REQUEST_URI
			+ "helloWorld.html";

	private IController controller;

	@Override
	public void setUp() {
		try {
			this.controller = new Controller("test/locationmap.xml",
					"test/forrestContext.xml");
		} catch (final Exception e) {
			fail("Unable to set up tests: " + e.getMessage());
		}
	}

	public void testSourceURLs() throws IOException, LocationmapException,
			ProcessingException, URISyntaxException {
		final List<Location> sourceURLs = this.controller
				.getSourceLocations(new URI(XHTML_REQUEST_URI));
		assertNotNull(sourceURLs);
		// assertTrue(sourceURLs.get(0).getSourceURL().equals(firstSourceURL));
		// assertTrue(sourceURLs.get(1).getSourceURL().equals(secondSourceURL));
	}

	public void testSourceDocuments() throws IOException, ProcessingException,
			URISyntaxException {
		final List<AbstractSourceDocument> documents = this.controller
				.getSourceDocuments(new URI(XHTML_REQUEST_URI));
		assertNotNull(documents);
		// AbstractSourceDocument firstDoc = documents.get(0);
		// AbstractSourceDocument secondDoc = documents.get(1);
		// assertFalse(firstDoc.equals(secondDoc));
	}

	public void testInternalDocuments() throws IOException,
			ProcessingException, URISyntaxException {
		final List<AbstractSourceDocument> srcDocs = this.controller
				.getSourceDocuments(new URI(XHTML_REQUEST_URI));
		final List<InternalDocument> internalDocs = this.controller
				.getInternalDocuments(new URI(XHTML_REQUEST_URI));
		final AbstractSourceDocument firstSrcDoc = srcDocs.get(0);
		// AbstractSourceDocument secondSrcDoc = srcDocs.get(1);
		final InternalDocument firstIntDoc = internalDocs.get(0);
		// InternalDocument secondIntDoc = internalDocs.get(1);
		assertFalse(firstSrcDoc.equals(firstIntDoc));
		// assertFalse(secondSrcDoc.equals(secondIntDoc));
		// assertFalse(firstIntDoc.equals(secondIntDoc));
	}

	public void testProcessRequest() throws IOException, ProcessingException,
			URISyntaxException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(XHTML_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("<title>XHTML 2 Simple Sample Page"));
		assertFalse("Content is not as expected", output.getContentAsString()
				.contains("xml-stylesheet"));
	}

	public void testReader() throws ProcessingException, MalformedURLException,
			URISyntaxException {
		final List<AbstractSourceDocument> source = this.controller
				.getSourceDocuments(new URI(HELLO_WORLD_REQUEST_URI));
		assertEquals(
				"Should have a single internal document for Hello World request",
				1, source.size());
		final AbstractSourceDocument doc = source.get(0);
		assertEquals("Document type read by Hello world is incorrect",
				"org.apache.forrest.helloWorld", doc.getType());
	}

	public void testForrestSourceRequest() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(SOURCE_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("xmlns:html=\"http://www.w3.org/2002/06/xhtml2\""));
	}

	public void testForrestInternalRequest() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(INTERNAL_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("Forrest Generated (Internal) Document"));
	}

}
