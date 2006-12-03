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
import java.util.ArrayList;
import java.util.List;

import org.apache.forrest.core.exception.ProcessingException;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;

/**
 * A location is a possible source location for a given request URI. There may
 * be more than one location for any request URI, each of the possible locations
 * may be optional or requried.
 * 
 */
public class Location {

	Logger log = Logger.getLogger(Location.class);

	private String requestPattern;

	private boolean isRequired;

	private List<URI> sourceURIs;

	public Location(final String pattern, final URL sourceURL,
			final boolean isRequired) throws URISyntaxException {
		List<URI> uris = new ArrayList<URI>();
		uris.add(sourceURL.toURI());
		this.init(pattern, uris, isRequired);
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
		List<URI> uris = new ArrayList<URI>();
		boolean isRequired = false;

		final NamedNodeMap atts = element.getAttributes();
		pattern = atts.getNamedItem("regexp").getNodeValue();
		final NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			String nodeName = child.getNodeName();
			if (nodeName != null && nodeName.equals("source")) {
				url = child.getAttributes().getNamedItem("href").getNodeValue();
				final Node required = child.getAttributes().getNamedItem(
						"required");
				if (required != null) {
					isRequired = required.getNodeValue().equals("true");
				}
				uris.add(new URI(url));
			}
		}
		this.init(pattern, uris, isRequired);
	}

	private void init(final String pattern, final List<URI> uris,
			final boolean isRequired) throws URISyntaxException {
		if (pattern == null)
			throw new IllegalArgumentException(
					"requestURIPattern cannot be null");
		if (uris == null || uris.size() == 0)
			throw new IllegalArgumentException(
					"There must be at least one postential source uri");
		this.setRequestPattern(pattern);
		this.setSourceURIs(uris);
		this.setRequired(isRequired);
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

	/**
	 * Get the source URLs, that may be used to read the source document. A
	 * source URL is the sourceURI modified appropriately for the given request.
	 * Note that the resulting list of URLs have not been verified with respect
	 * to the existence of a document, it is only a potential location.
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws ProcessingException
	 */
	public List<URL> getResolvedSourceURL(URI requestURI)
			throws MalformedURLException, ProcessingException {
		List<URL> resolvedUrls = new ArrayList<URL>();
		for (URI sourceURI : getSourceURIs()) {
			resolvedUrls.add(resolveURL(requestURI, sourceURI));
		}
		return resolvedUrls;
	}

	/**
	 * 
	 * @param sourcePath
	 * @return
	 * @throws ProcessingException - if the path to the resource cannot be resolved
	 */
	private URL resolveClasspathURI(final String sourcePath)
			throws ProcessingException {
		URL resourceURL;
		resourceURL = this.getClass().getResource(sourcePath);
		if (resourceURL == null)
			throw new ProcessingException(
					"Cannot find the classpath resource: " + sourcePath);
		return resourceURL;
	}

	public List<URI> getSourceURIs() {
		return this.sourceURIs;
	}

	/**
	 * Set the list of potential source URIs for this document.
	 * 
	 * @param sourceURI
	 */
	public void setSourceURIs(final List<URI> sourceURIs) {
		this.sourceURIs = sourceURIs;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (isRequired()) {
			sb.append("Required ");
		} else {
			sb.append("Optional ");
		}
		sb.append("location: ");
		sb.append("Pattern: ");
		sb.append(this.getRequestPattern());
		sb.append(" Potential sourceURIs: ");
		for (URI uri : getSourceURIs()) {
		  sb.append(uri.toASCIIString());
		  sb.append(" ");
		}

		return sb.toString();
	}

	/**
	 * Resolve the supplied URI and return a URL that can
	 * be used to attempt to retrieve the resource. A resolved
	 * uri has all variables substituted with their values.
	 * 
	 * @param sourceURI
	 * @return
	 * @throws MalformedURLException 
	 * @throws ProcessingException 
	 */
	public URL resolveURL(URI requestURI, URI sourceURI) throws MalformedURLException, ProcessingException {
		URL url;
		RE r;

		try {
			r = new RE(getRequestPattern());
		} catch (RESyntaxException re) {
			throw new ProcessingException(
					"Unable to extract variable values from request: "
							+ re.getMessage(), re);
		}

		try {
			url = requestURI.toURL();
		} catch (final IllegalArgumentException e) {
			// we'll assume that this is not an absolute URL and therefore
			// refers to a file
			url = new URL("file://" + requestURI);
		}
		final String urlString = url.toExternalForm();

			String sourceSSP = sourceURI.getSchemeSpecificPart();

			if (r.match(urlString)) {
				String variable;
				String value;
				for (int i = 0; i < r.getParenCount(); i++) {
					variable = "$(" + i + ")";
					value = r.getParen(i);
					if (value != null) {
						sourceSSP = sourceSSP.replace(variable, value);
					}
				}
				log.debug("After variable substitution a potential source path is "
						+ sourceSSP);
			} else {
				throw new ProcessingException(
						"Unable to extract variable values from requestURI");
			}

			URL resolvedURL;
			if (sourceURI.getScheme().equals("classpath")) {
				resolvedURL = resolveClasspathURI(sourceSSP);
			} else {
				String strURI = sourceURI.getSchemeSpecificPart();
				if (strURI.contains(":")) {
					String subProtocol = strURI.substring(0, strURI
							.lastIndexOf(':'));
					sourceSSP = strURI.substring(strURI.lastIndexOf(':') + 1);
					if (subProtocol.equals("classpath")) {
						resolvedURL = resolveClasspathURI(sourceSSP);
					} else {
						URI subURI;
						try {
							subURI = new URI(subProtocol, sourceSSP, null);
							resolvedURL = subURI.toURL();
						} catch (URISyntaxException e) {
							throw new MalformedURLException(
									"Unable to work out sub protocol URI");
						}
					}
				} else {
					resolvedURL = sourceURI.toURL();
				}
			}
			return resolvedURL;
		
	}

}
