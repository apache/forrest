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
package org.apache.forrest.core.locationMap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A location is a possible source location for a given request URI. There may
 * be more than one location for any request URI, each of the possible locations
 * may be optional or requried.
 * 
 */
public class Location {
	private String requestPattern;

	private boolean isRequired;

	private URI sourceURI;

	public Location(final String pattern, final URL sourceURL,
			final boolean isRequired) throws URISyntaxException {
		this.init(pattern, sourceURL.toURI(), isRequired);
	}

	/**
	 * Construct a new Location from an XML node.
	 * 
	 * @param element
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public Location(final Node element) throws URISyntaxException, IOException {
		String pattern = null;
		String url = null;
		boolean isOptional = false;

		final NamedNodeMap atts = element.getAttributes();
		pattern = atts.getNamedItem("pattern").getNodeValue();
		final NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			if (child.getNodeName().equals("source")) {
				url = child.getAttributes().getNamedItem("href").getNodeValue();
				final Node required = child.getAttributes().getNamedItem(
						"required");
				if (required != null) {
					isOptional = required.getNodeValue().equals("false");
				}
			}
		}
		if (url.contains("classpath:")) {
			final String path = url.substring(url.lastIndexOf(':') + 1);
			final URL resourceURL = this.getClass().getResource(path);
			if (resourceURL == null)
				throw new IOException("Cannot find the resource: " + path);
			URI uri;
			if (url.indexOf("classpath:") != 0) {
				final String psudeoProtocol = url.substring(0, url
						.indexOf("classpath:") - 1);
				final String ssp = resourceURL.toURI().toURL().toExternalForm();
				uri = new URI(psudeoProtocol, ssp, null);
			} else {
				uri = resourceURL.toURI();
			}
			this.init(pattern, uri, isOptional);
		} else {
			this.init(pattern, new URI(url), isOptional);
		}
	}

	private void init(final String pattern, final URI uri,
			final boolean isOptional) throws URISyntaxException {
		if (pattern == null)
			throw new IllegalArgumentException(
					"requestURIPattern cannot be null");
		if (uri == null)
			throw new IllegalArgumentException("sourceURI cannot be null");
		this.setRequestPattern(pattern);
		this.setSourceURI(uri);
		this.setRequired(this.isRequired);
	}

	public boolean isRequired() {
		return this.isRequired;
	}

	public void setRequired(final boolean isRequired) {
		this.isRequired = isRequired;
	}

	public String getRequestPattern() {
		return this.requestPattern;
	}

	public void setRequestPattern(final String pattern) {
		this.requestPattern = pattern;
	}

	public URL getSourceURL() throws MalformedURLException {
		return this.getSourceURI().toURL();
	}

	public void setSourceURL(final URL sourceURL) throws URISyntaxException {
		this.setSourceURI(sourceURL.toURI());
	}

	public URI getSourceURI() {
		return this.sourceURI;
	}

	public void setSourceURI(final URI sourceURI) {
		this.sourceURI = sourceURI;
	}

}
