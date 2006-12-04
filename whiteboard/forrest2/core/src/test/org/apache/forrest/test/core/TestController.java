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
import org.apache.forrest.core.document.AggregatedSourceDocument;
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
			+ "testPage.forrestSource";

	public static final String INTERNAL_REQUEST_URI = BASE_REQUEST_URI
			+ "testPage.forrestInternal";

	public static final String PIPELINE_REQUEST_URI = BASE_REQUEST_URI
			+ "testPage.forrestPipeline";

	public static final String VARIABLE_SUBSTITUTION_REQUEST_URI = BASE_REQUEST_URI
			+ "variable/sample_simple.forrestSource";

	public static final String XHTML_REQUEST_URI = BASE_REQUEST_URI
			+ "testPage.html";

	public static final String HELLO_WORLD_REQUEST_URI = BASE_REQUEST_URI
			+ "helloWorld.html";

	public static final String OPTIONAL_RESOURCES_REQUEST_URI = BASE_REQUEST_URI
			+ "optional/sample_simple.forrestSource";

	public static final String REQUIRED_RESOURCES_REQUEST_URI = BASE_REQUEST_URI
			+ "required/sample_simple.forrestSource";

	private IController controller;

	@Override
	public void setUp() {
		try {
			this.controller = new Controller("src/test/locationmap.xml",
					"src/test/forrestContext.xml");
		} catch (final Exception e) {
			fail("Unable to set up tests: " + e.getMessage());
		}
	}

	public void testSourceURLs() throws IOException, LocationmapException,
			ProcessingException, URISyntaxException {
		final List<Location> sourceURLs = this.controller
				.getSourceLocations(new URI(XHTML_REQUEST_URI));
		assertNotNull(sourceURLs);
	}

	public void testSourceDocuments() throws IOException, ProcessingException,
			URISyntaxException {
		final AbstractSourceDocument document = this.controller
				.getSourceDocuments(new URI(XHTML_REQUEST_URI));
		assertNotNull(document);
	}

	public void testInternalDocuments() throws IOException,
			ProcessingException, URISyntaxException {
		final AbstractSourceDocument srcDoc = this.controller
				.getSourceDocuments(new URI(XHTML_REQUEST_URI));
		final InternalDocument internalDoc = this.controller
				.getInternalDocument(new URI(XHTML_REQUEST_URI));
		final AbstractSourceDocument firstSrcDoc = srcDoc;
		assertFalse(firstSrcDoc.equals(internalDoc));
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
		final AbstractSourceDocument source = this.controller
				.getSourceDocuments(new URI(HELLO_WORLD_REQUEST_URI));
		assertTrue(
				"Should not have an aggregated document for Hello World request",
				!(source instanceof AggregatedSourceDocument));
		assertEquals("Document type read by Hello world is incorrect",
				"org.apache.forrest.helloWorld", source.getType());
	}

	public void testForrestSourceRequest() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(SOURCE_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("http://www.w3.org/2002/06/xhtml2"));
	}

	public void testForrestInternalRequest() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(INTERNAL_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("http://www.w3.org/2002/06/xhtml2"));
	}

	public void testForrestPipelineRequest() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(PIPELINE_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content does not appear to be a pipeline description", output.getContentAsString()
				.contains("forrestPipeline"));
		assertTrue("Pipeline does not define source location", output.getContentAsString()
				.contains("<source"));
		assertTrue("Pipeline does not define a reader", output.getContentAsString()
				.contains("<reader"));
		assertTrue("Pipeline does not define an input plugin", output.getContentAsString()
				.contains("<inputPlugin"));
		assertTrue("Pipeline does not define an output plugin", output.getContentAsString()
				.contains("<outputPlugin"));
	}

	public void testVariableSubstitution() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(VARIABLE_SUBSTITUTION_REQUEST_URI));
		assertNotNull(output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("http://www.w3.org/2002/06/xhtml2"));
	}

	public void testOptionalResources() throws ProcessingException,
			URISyntaxException, IOException {
		final AbstractOutputDocument output = this.controller
				.getOutputDocument(new URI(OPTIONAL_RESOURCES_REQUEST_URI));
		assertNotNull(
				"We should have a result document from optional requests",
				output);
		assertTrue("Content is not as expected", output.getContentAsString()
				.contains("http://www.w3.org/2002/06/xhtml2"));
	}

	public void testRequiredResources() throws ProcessingException,
			URISyntaxException, IOException {
		try {
			final AbstractOutputDocument output = this.controller
					.getOutputDocument(new URI(REQUIRED_RESOURCES_REQUEST_URI));
		} catch (ProcessingException e) {
			return;
		}
		fail("We should throw a processing exception when a required document is missing");
	}

}
