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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.AggregatedSourceDocument;
import org.apache.forrest.core.document.DefaultOutputDocument;
import org.apache.forrest.core.document.InternalDocument;
import org.apache.forrest.core.document.InternalErrorDocument;
import org.apache.forrest.core.exception.LocationmapException;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.locationMap.AggregateNode;
import org.apache.forrest.core.locationMap.LocationNode;
import org.apache.forrest.core.locationMap.LocationMap;
import org.apache.forrest.core.plugin.AbstractInputPlugin;
import org.apache.forrest.core.plugin.AggregateInputPlugin;
import org.apache.forrest.core.plugin.BaseOutputPlugin;
import org.apache.forrest.core.plugin.PassThroughInputPlugin;
import org.apache.forrest.core.reader.AggregateReader;
import org.apache.forrest.core.reader.IReader;
import org.apache.log4j.Logger;
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

	Logger log = Logger.getLogger(Controller.class);

	private final Map<URI, List<LocationNode>> sourceLocationsCache = new HashMap<URI, List<LocationNode>>();

	private final Map<URI, AbstractSourceDocument> sourceDocsCache = new HashMap<URI, AbstractSourceDocument>();

	private final Map<URI, InternalDocument> internalDocsCache = new HashMap<URI, InternalDocument>();

	private final Map<URI, AbstractOutputDocument> outputDocCache = new HashMap<URI, AbstractOutputDocument>();

	private LocationMap locationMap;

	private AbstractXmlApplicationContext context;

	private String outputPluginClass;

	private String inputPluginClass;

	private String readerClass;

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
			log.info("Using Spring Context definition in " + contextPath);
			this.context = new FileSystemXmlApplicationContext(file.getPath());
		} else {
			log.info("Using default spring context definition");
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
		final List<LocationNode> sourceLocs = this
				.resolveSourceLocations(requestURI);
		this.sourceLocationsCache.put(requestURI, sourceLocs);

		final AbstractSourceDocument sourceDocs = this
				.loadAllSourceDocuments(requestURI, sourceLocs);

		if (sourceDocs == null) {
			throw new ProcessingException("Unable to get a source document for request " + requestURI);
		}
		final InternalDocument internalDoc = this.processInputPlugins(
				requestURI, sourceDocs);
		this.internalDocsCache.put(requestURI, internalDoc);

		final AbstractOutputDocument output = this
				.processOutputPlugins(requestURI);
		this.outputDocCache.put(requestURI, output);
		return output;
	}

	/**
	 * Process each of the documents supplied with the appropriate input plugins
	 * to get a document in our internal format.
	 * 
	 * @param requestURI
	 * 
	 * @param sourceDocuments
	 * @throws IOException
	 * @throws ProcessingException
	 */
	private InternalDocument processInputPlugins(URI requestURI,
			final AbstractSourceDocument sourceDocument)
			throws IOException, ProcessingException {
		InternalDocument result = null;
		if (sourceDocument == null) {
			result = new InternalErrorDocument(sourceDocument,
					"Unable to load source document");
		} else {
				if (sourceDocument == null) {
					throw new ProcessingException(
							"No source document is available.");
				}
				AbstractInputPlugin plugin = getInputPlugin(sourceDocument);
				result = (InternalDocument) plugin.process(this, sourceDocument);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getInputPlugin(org.apache.forrest.core.document.AbstractSourceDocument)
	 */
	public AbstractInputPlugin getInputPlugin(final AbstractSourceDocument doc) {
		if (doc instanceof AggregatedSourceDocument) {
			return new AggregateInputPlugin();
		}
		AbstractInputPlugin plugin;
		try {
			plugin = (AbstractInputPlugin) this.context.getBean(doc.getType());
		} catch (final NoSuchBeanDefinitionException e) {
			plugin = new PassThroughInputPlugin();
		}
		inputPluginClass = plugin.getClass().toString();
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
		final InternalDocument intDoc = this.getInternalDocument(requestURI);
		BaseOutputPlugin plugin = getOutputPlugin(requestURI);
		return (AbstractOutputDocument) plugin.process(this, intDoc);
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
		outputPluginClass = plugin.getClass().toString();
		return plugin;
	}

	/**
	 * Load each of the source documents into the document cache.
	 * 
	 * @throws MalformedURLException
	 * @throws ProcessingException
	 * 
	 * @fixme cache the resource
	 * @fixme handle document types other than HTML
	 * @fixme resource handlers should be provided from a factory class
	 */
	private AbstractSourceDocument loadAllSourceDocuments(URI requestURI,
			final List<LocationNode> sourceLocations) throws MalformedURLException,
			ProcessingException {
		AbstractSourceDocument result = null;

		for (int i = 0; i < sourceLocations.size(); i++) {
			final LocationNode location = sourceLocations.get(i);
			result = loadSourceDocument(requestURI,
					location);
			if (result != null) break;
		}
		return result;
	}

	private AbstractSourceDocument loadSourceDocument(URI requestURI,
			final LocationNode location) throws ProcessingException,
			MalformedURLException {
		AbstractSourceDocument doc = sourceDocsCache.get(requestURI);
		if (doc == null) {
			for (AbstractSourceNode node : location.getSourceNodes()) {
				IReader reader = getReader(node);
				log.debug("Reader to use is " + reader.toString());
				doc = reader.read(this, requestURI, node, location.getMatcher());
				if (doc != null) {
					addToSourceDocCache(requestURI, doc);
					break;
				}
			}
		}
		return doc;
	}

	private void addToSourceDocCache(URI requestURI, AbstractSourceDocument doc) {
		AbstractSourceDocument sourceDoc = sourceDocsCache.get(requestURI);
		if (sourceDoc instanceof AggregatedSourceDocument) {
			AggregatedSourceDocument aggDoc = (AggregatedSourceDocument) sourceDoc;
			if (!aggDoc.contains(doc)) {
				aggDoc.add(doc);
			}
		}
		this.sourceDocsCache.put(requestURI, doc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getReader(org.apache.forrest.core.locationMap.LocationNode)
	 */
	public IReader getReader(final URI uri) throws ProcessingException {
		IReader reader;
		String scheme = uri.getScheme();
		if (scheme.equals("classpath")) {
			scheme = "file";
		}
		try {
			reader = (IReader) this.context.getBean(scheme);
		} catch (Exception e) {
			throw new ProcessingException(
					"Unable to get a reader for : " + uri, e);
		}
		readerClass = reader.getClass().toString();
		return reader;
	}
	
	

	public IReader getReader(AbstractSourceNode sourceNode) throws ProcessingException {
		if (sourceNode instanceof AggregateNode) {
			return new AggregateReader();
		} else {
			return getReader(sourceNode.getSourceURI());
		}
	}

	/**
	 * Resolve the source locations for a given request. The result is a list of
	 * locations that make up the source document. If the list contains a single
	 * location then the document will be read. If it contains multiple
	 * locations they will be aggregated into a single document.
	 * 
	 * @param requestURI
	 * @return
	 * @throws LocationmapException
	 * @throws MalformedURLException
	 *             if the Request URI is not valid
	 * @throws ProcessingException
	 * @FIXME handle fall through if the first location is not correct
	 */
	private List<LocationNode> resolveSourceLocations(final URI requestURI)
			throws LocationmapException, MalformedURLException,
			ProcessingException {
		final List<List<LocationNode>> possibleLocs = this.locationMap
				.get(requestURI);
		if (possibleLocs == null || possibleLocs.size() == 0)
			throw new LocationmapException(
					"Unable to find any potential source locationa for " + requestURI + ". This means that there is no location node in your locationmap that matches this request.");

		List<LocationNode> result = new ArrayList<LocationNode>();
		Boolean isValid = false;
		for (List<LocationNode> locs : possibleLocs) {
			result = new ArrayList<LocationNode>();
			isValid = true;
			Iterator<LocationNode> sourceLocs = locs.iterator();
			LocationNode loc;
			while (sourceLocs.hasNext() && isValid) {
				loc = sourceLocs.next();
				if (sourceExists(requestURI, loc)) {
					result.add(loc);
				}
			}
			if (isValid)
				break;
		}
		if (!isValid) {
			throw new ProcessingException(
					"Unable to find a valid source location for "
							+ requestURI.toASCIIString());
		}
		return result;
	}

	/**
	 * Test to see if a given source document exists.
	 * 
	 * @param loc
	 * @return
	 * @throws ProcessingException
	 * @throws MalformedURLException
	 * @TODO we need a more efficient test for existence.
	 */
	private boolean sourceExists(URI requestURI, LocationNode location)
			throws MalformedURLException, ProcessingException {
		AbstractSourceDocument doc = loadSourceDocument(requestURI, location);
		return doc != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getSourceLocations(java.net.URI)
	 */
	public List<LocationNode> getSourceLocations(final URI requestURI)
			throws IOException, LocationmapException, ProcessingException {
		List<LocationNode> locs = this.sourceLocationsCache.get(requestURI);
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
	public AbstractSourceDocument getSourceDocuments(final URI requestURI)
			throws MalformedURLException, ProcessingException {
		AbstractSourceDocument source = this.sourceDocsCache.get(requestURI);
		if (source == null)
			try {
				this.processRequest(requestURI);
				source = this.sourceDocsCache.get(requestURI);
			} catch (final Exception e) {
				throw new ProcessingException(
						"Unable to retrieve the source documents for "
								+ requestURI, e);
			}
		return source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getInternalDocuments(java.net.URI)
	 */
	public InternalDocument getInternalDocument(final URI requestURI)
			throws ProcessingException {
		InternalDocument internalDoc = this.internalDocsCache.get(requestURI);
		if (internalDoc == null)
			try {
				this.processRequest(requestURI);
				internalDoc = this.internalDocsCache.get(requestURI);
			} catch (final Exception e) {
				throw new ProcessingException(
						"Unable to create the internal representation of the source documents for "
								+ requestURI.toString(), e);
			}

		return internalDoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.core.IController#getOutputDocument(java.net.URI)
	 */
	public AbstractOutputDocument getOutputDocument(final URI requestURI)
			throws MalformedURLException, ProcessingException {
		if (requestURI.getPath().endsWith(this.sourceURLExtension)) {
			return getSourceDocumentAsOutput(requestURI);
		} else if (requestURI.getPath().endsWith(this.internalURLExtension)) {
			return getInternalDocumentAsOutput(requestURI);
		} else if (requestURI.getPath().endsWith(this.pipelineURLExtension)) {
			return getPipelineAsOutput(requestURI);
		}

		AbstractOutputDocument output = this.outputDocCache.get(requestURI);
		if (output == null)
			try {
				output = this.processRequest(requestURI);
			} catch (final Exception e) {
				throw new ProcessingException(
						"Unable to create the output documents for "
								+ requestURI, e);
			}
		return output;
	}

	/**
	 * Return an XML document that represents the processing pipeline for the
	 * given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws ProcessingException 
	 */
	private AbstractOutputDocument getPipelineAsOutput(URI requestURI) throws ProcessingException {
		try {
			this.processRequest(requestURI);
		} catch (final Exception e) {
			throw new ProcessingException(
					"Unable to create the output documents for "
							+ requestURI, e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<forrestPipeline request=\"");
		sb.append(requestURI);
		sb.append("\">");
		sb.append("<source url=\"");
		sb.append("URL calculation not implemented yet\">");
		sb.append("</source>");
		sb.append("<!-- FIXME: if there are a chain of readers we should show them all here -->");
		sb.append("<reader class=\"");
		sb.append(readerClass);
		sb.append("\">");
		sb.append("</reader>");
		sb.append("<!-- FIXME: if there are a chain of input plugins we should show them all here -->");
		sb.append("<inputPlugin class=\"");
		sb.append(inputPluginClass);
		sb.append("\">");
		sb.append("</inputPlugin>");
		sb.append("<outputPlugin class=\"");
		sb.append(outputPluginClass);
		sb.append("\">");
		sb.append("</outputPlugin>");
		sb.append("<forrestPipeline>");
		return new DefaultOutputDocument(requestURI, sb.toString());
	}

	/**
	 * Get an output document that contains the internal document gneerated by
	 * the given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws ProcessingException
	 */
	private DefaultOutputDocument getInternalDocumentAsOutput(
			final URI requestURI) throws ProcessingException {
		final InternalDocument doc = this.getInternalDocument(requestURI);
		final StringBuffer content = new StringBuffer();
		try {
			content.append(doc.getContentAsString());
		} catch (final IOException e) {
			content.append("<error>Unable to read source document for ");
			content.append(requestURI);
			content.append("</error>");
		}
		return new DefaultOutputDocument(requestURI, content.toString());
	}

	/**
	 * Get an output document that contains the original source document for the
	 * given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws MalformedURLException
	 * @throws ProcessingException
	 */
	private DefaultOutputDocument getSourceDocumentAsOutput(final URI requestURI)
			throws MalformedURLException, ProcessingException {
		final AbstractSourceDocument doc = this.getSourceDocuments(requestURI);
		final StringBuffer content = new StringBuffer();
		try {
			content.append(doc.getContentAsString());
		} catch (final IOException e) {
			content.append("<error>Unable to read source document for ");
			content.append(requestURI);
			content.append("</error>");
		}

		return new DefaultOutputDocument(requestURI, content.toString());
	}
}
