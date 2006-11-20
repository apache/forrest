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
package org.apache.forrest.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.AggregateInteralDocument;
import org.apache.forrest.core.document.DefaultOutputDocument;
import org.apache.forrest.core.document.IDocument;
import org.apache.forrest.core.document.InternalDocument;
import org.apache.forrest.core.exception.LocationmapException;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.Location;
import org.apache.forrest.core.locationMap.LocationMap;
import org.apache.forrest.core.plugin.AbstractInputPlugin;
import org.apache.forrest.core.plugin.BaseOutputPlugin;
import org.apache.forrest.core.plugin.PassThroughInputPlugin;
import org.apache.forrest.reader.IReader;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.xml.sax.SAXException;

/**
 * The Controller manages all things Forrest. It is responsible for:
 * 
 * <ol>
 * <li>resolving the input document(s)</li>
 * <li>loading the input document(s)</li>
 * <li>selecting an appropriate input plugin(s)</li>
 * <li>processing the input document with the selected plugin</li>
 * <li>selecting an appropriate output plugin</li>
 * <li>processing the internal document with the output plugin</li>
 * <li>streaming the results to the client</li>
 * </ol>
 * 
 * 
 */
public class Controller implements IController {

	private final String sourceURLExtension = ".forrestSource";

	private final String internalURLExtension = ".forrestInternal";

	private final Map<URI, List<Location>> sourceLocationsCache = new HashMap<URI, List<Location>>();

	private final Map<URI, List<AbstractSourceDocument>> sourceDocsCache = new HashMap<URI, List<AbstractSourceDocument>>();

	private final Map<URI, List<InternalDocument>> internalDocsCache = new HashMap<URI, List<InternalDocument>>();

	private final Map<URI, AbstractOutputDocument> outputDocCache = new HashMap<URI, AbstractOutputDocument>();

	private LocationMap locationMap;

	private AbstractXmlApplicationContext context;

	/**
	 * Create a controller that uses the locationmap definition file at the
	 * provided location.
	 * 
	 * @param locationmapPath -
	 *            path to the locationmap definition file
	 * @throws URISyntaxException
	 * @throws IOException
	 *             If the locationmap cannot be loaded
	 * @throws SAXException
	 *             If the locationmap is invalid
	 * 
	 */
	public Controller(final String locationmapPath, final String contextPath)
			throws URISyntaxException, SAXException, IOException {
		final File file = new File(contextPath);
		if (file.exists()) {
			this.context = new FileSystemXmlApplicationContext(file
					.getAbsolutePath());
		} else {
			this.context = new ClassPathXmlApplicationContext(
					"defaultForrestContext.xml");
		}
		this.initLocationmap(locationmapPath);
	}

	/**
	 * Create a controller that uses the default location for the locationmap
	 * definition file and forrest context file. That is "src/locationmap.xml"
	 * and "src/forrestContext.xml" respectively..
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 *             If the locationmap cannot be loaded
	 * @throws SAXException
	 *             If the locationmap is invalid
	 * 
	 */
	public Controller() throws URISyntaxException, SAXException, IOException {
		this("src/locationmap.xml", "src/forrestContext.xml");
	}

	/**
	 * Initialises the locationmap for use.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws SAXException
	 * @FIXME read the locationmap from a config file.
	 */
	private void initLocationmap(final String locationmapPath)
			throws URISyntaxException, SAXException, IOException {
		this.locationMap = new LocationMap(locationmapPath);
	}

	/**
	 * Process a request for the resource identified by a given URI.
	 * 
	 * @param requestURI
	 * @throws IOException
	 * @throws LocationmapException
	 * @throws ProcessingException
	 */
	private AbstractOutputDocument processRequest(final URI requestURI)
			throws IOException, LocationmapException, ProcessingException {
		final List<Location> sourceLocs = this.resolveSources(requestURI);
		this.sourceLocationsCache.put(requestURI, sourceLocs);

		final List<AbstractSourceDocument> sourceDocs = this
				.loadSourceDocuments(requestURI, sourceLocs);
		this.sourceDocsCache.put(requestURI, sourceDocs);

		final List<InternalDocument> internalDocs = this
				.processInputPlugins(sourceDocs);
		this.internalDocsCache.put(requestURI, internalDocs);

		final AbstractOutputDocument output = this
				.processOutputPlugins(requestURI);
		this.outputDocCache.put(requestURI, output);
		return output;
	}

	/**
	 * Process each of the documents supplied with the appropriate input plugins
	 * to get a document in our internal format.
	 * 
	 * @param sourceDocuments
	 * @throws IOException
	 */
	private List<InternalDocument> processInputPlugins(
			final List<AbstractSourceDocument> sourceDocuments)
			throws IOException {
		final List<InternalDocument> results = new ArrayList<InternalDocument>(
				sourceDocuments.size());
		for (int i = 0; i < sourceDocuments.size(); i++) {
			final AbstractSourceDocument doc = sourceDocuments.get(i);
			AbstractInputPlugin plugin = getInputPlugin(doc);
			results.add((InternalDocument) plugin.process(doc));
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getInputPlugin(org.apache.forrest.core.document.AbstractSourceDocument)
	 */
	public AbstractInputPlugin getInputPlugin(final AbstractSourceDocument doc) {
		AbstractInputPlugin plugin;
		try {
			plugin = (AbstractInputPlugin) this.context.getBean(doc.getType());
		} catch (final NoSuchBeanDefinitionException e) {
			plugin = new PassThroughInputPlugin();
		}
		return plugin;
	}

	/**
	 * Aggregate all the internal documents into a single document and then
	 * process it will the appropraite output plugin for the given requestURI.
	 * 
	 * @param requestURI
	 * @throws IOException
	 * @throws ProcessingException
	 * @throws IOException
	 */
	private AbstractOutputDocument processOutputPlugins(final URI requestURI)
			throws ProcessingException, IOException {
		IDocument doc = null;
		final List<InternalDocument> intDocs = this
				.getInternalDocuments(requestURI);
		if (intDocs.size() > 1) {
			doc = new AggregateInteralDocument(intDocs);
		} else {
			doc = intDocs.get(0);
		}

		BaseOutputPlugin plugin = getOutputPlugin(requestURI);

		return (AbstractOutputDocument) plugin.process(doc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getOutputPlugin(java.net.URI)
	 */
	public BaseOutputPlugin getOutputPlugin(final URI requestURI) {
		BaseOutputPlugin plugin = null;
		final String[] names = this.context
				.getBeanNamesForType(BaseOutputPlugin.class);
		for (int i = 0; i < names.length; i = i + 1) {
			plugin = (BaseOutputPlugin) this.context.getBean(names[i]);
			if (plugin.isMatch(requestURI)) {
				break;
			} else {
				plugin = null;
			}
		}
		if (plugin == null) {
			plugin = new BaseOutputPlugin();
		}
		return plugin;
	}

	/**
	 * Load each of the source documents.
	 * 
	 * @throws MalformedURLException
	 * @throws ProcessingException
	 * 
	 * @fixme cache the resource
	 * @fixme handle document types other than HTML
	 * @fixme resource handlers should be provided from a factory class
	 */
	private List<AbstractSourceDocument> loadSourceDocuments(
			URI requestURI, final List<Location> sourceLocations) throws MalformedURLException,
			ProcessingException {
		final List<AbstractSourceDocument> results = new ArrayList<AbstractSourceDocument>(
				sourceLocations.size());

		for (int i = 0; i < sourceLocations.size(); i++) {
			final Location location = sourceLocations.get(i);
			IReader reader = getReader(location);
			results.add(reader.read(this, requestURI, location));
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getReader(org.apache.forrest.core.locationMap.Location)
	 */
	public IReader getReader(final Location location)
			throws ProcessingException {
		IReader reader;
		try {
			reader = (IReader) this.context.getBean(location.getScheme());
		} catch (Exception e) {
			throw new ProcessingException("Unable to get a reader for : "
					+ location.getRequestPattern(), e);
		}
		return reader;
	}

	/**
	 * Resolve the input documents for a given request.
	 * 
	 * @param requestURI
	 * @return
	 * @throws LocationmapException
	 * @throws MalformedURLException
	 *             if the Request URI is not valid
	 * @FIXME handle fall through if the first location is not correct
	 */
	private List<Location> resolveSources(final URI requestURI)
			throws LocationmapException, MalformedURLException {
		final List<List<Location>> possibleLocs = this.locationMap
				.get(requestURI);
		if (possibleLocs == null || possibleLocs.size() == 0)
			throw new LocationmapException(
					"Unable to find a source location for " + requestURI);
		// TODO: we need to test for the validity of a location and return the
		// first matching one instead of returning the first location regardless
		return possibleLocs.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getSourceLocations(java.net.URI)
	 */
	public List<Location> getSourceLocations(final URI requestURI)
			throws IOException, LocationmapException, ProcessingException {
		List<Location> locs = this.sourceLocationsCache.get(requestURI);
		if (locs == null) {
			this.processRequest(requestURI);
			locs = this.sourceLocationsCache.get(requestURI);
		}
		return locs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getSourceDocuments(java.net.URI)
	 */
	public List<AbstractSourceDocument> getSourceDocuments(final URI requestURI)
			throws MalformedURLException, ProcessingException {
		List<AbstractSourceDocument> sources = this.sourceDocsCache
				.get(requestURI);
		if (sources == null)
			try {
				this.processRequest(requestURI);
				sources = this.sourceDocsCache.get(requestURI);
			} catch (final Exception e) {
				throw new ProcessingException(
						"Unable to retrieve the source documents for "
								+ requestURI, e);
			}
		return sources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getInternalDocuments(java.net.URI)
	 */
	public List<InternalDocument> getInternalDocuments(final URI requestURI)
			throws ProcessingException {
		List<InternalDocument> internalDocs = this.internalDocsCache
				.get(requestURI);
		if (internalDocs == null)
			try {
				this.processRequest(requestURI);
				internalDocs = this.internalDocsCache.get(requestURI);
			} catch (final Exception e) {
				throw new ProcessingException(
						"Unable to create the internal representation of the source documents for "
								+ requestURI.toString(), e);
			}

		return internalDocs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getOutputDocument(java.net.URI)
	 */
	public AbstractOutputDocument getOutputDocument(final URI requestURI)
			throws MalformedURLException, ProcessingException {
		if (requestURI.getPath().endsWith(this.sourceURLExtension)) {
			final List<AbstractSourceDocument> sources = this
					.getSourceDocuments(requestURI);
			final StringBuffer content = new StringBuffer();
			for (final AbstractSourceDocument doc : sources) {
				try {
					content.append(doc.getContentAsString());
				} catch (final IOException e) {
					content
							.append("<error>Unable to read source document for ");
					content.append(requestURI);
					content.append("</error>");
				}
			}
			final DefaultOutputDocument output = new DefaultOutputDocument(
					content.toString());
			return output;
		} else if (requestURI.getPath().endsWith(this.internalURLExtension)) {
			final List<InternalDocument> docs = this
					.getInternalDocuments(requestURI);
			final StringBuffer content = new StringBuffer();
			for (final InternalDocument doc : docs) {
				try {
					content.append(doc.getContentAsString());
				} catch (final IOException e) {
					content
							.append("<error>Unable to read source document for ");
					content.append(requestURI);
					content.append("</error>");
				}
			}
			final DefaultOutputDocument output = new DefaultOutputDocument(
					content.toString());
			return output;
		}

		AbstractOutputDocument output = this.outputDocCache.get(requestURI);
		if (output == null)
			try {
				output = this.processRequest(requestURI);
			} catch (final Exception e) {
				throw new ProcessingException(
						"Unable to create the output documents for "
								+ requestURI + " because " + e.getMessage(), e);
			}
		return output;
	}
}
