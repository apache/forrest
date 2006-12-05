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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.forrest.core.exception.LocationmapException;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;

/**
 * A locationmap is a collection of all mappings from a request URI to source
 * URLs.
 * 
 */
public class LocationMap {
	Logger log = Logger.getLogger(LocationMap.class);
	
	private final Map<String, List<LocationNode>> locations = new HashMap<String, List<LocationNode>>();

	/**
	 * Create a new locationmap, configured by the file at the given path within
	 * the classpath.
	 * 
	 * @param locationmapPath
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SAXException
	 */
	public LocationMap(final String locationmapPath) throws URISyntaxException,
			SAXException, IOException {
		log.debug("Initialise locationmap from config file " + locationmapPath);
		final File file = new File(locationmapPath);
		final URL lmURL = file.toURL();

		final DOMParser parser = new DOMParser();
		parser.parse(lmURL.toString());
		final Document doc = parser.getDocument();
		final Node root = doc.getDocumentElement();
		final NodeList nodes = root.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			final Node element = nodes.item(i);
			if (element.getNodeName().equals("location")) {
				LocationNode loc = new LocationNode(element);
				this.put(loc);
				log.debug("Adding " + loc.toString());
			}
		}
	}

	/**
	 * Adds a location to the possible locations for a given requestURI.
	 * 
	 * @param requestURI
	 * @param location
	 */
	public void put(final LocationNode location) {
		List<LocationNode> sourceLocations = this.locations.get(location
				.getMatcher().getPattern());
		if (sourceLocations == null)
			sourceLocations = new ArrayList<LocationNode>();
		if (sourceLocations.contains(location) == false)
			sourceLocations.add(location);
		this.locations.put(location.getMatcher().getPattern(), sourceLocations);
	}

	/**
	 * Get all matching sets of locations for the given URI.
	 * A matching location is one that provides a source
	 * location that <em>may</em> provide a source document
	 * for the request. That is, the existence of the source
	 * document is not checked before adding the location
	 * to the results.
	 * 
	 * @param requestURI
	 * @return
	 * @throws MalformedURLException
	 *             if the Request URI is invalid
	 * @throws LocationmapException
	 */
	public List<List<LocationNode>> get(final URI requestURI)
			throws MalformedURLException, LocationmapException {
		log.debug("Getting potential locations for request of " + requestURI.toASCIIString());
		final List<List<LocationNode>> results = new ArrayList<List<LocationNode>>();
		final Set<String> locPatterns = this.locations.keySet();
		for (final String pattern : locPatterns) {
			try {
				if (this.isMatch(pattern, requestURI)) {
					List<LocationNode> locs = this.locations.get(pattern);
					results.add(locs);
					log.info(locs.size() + " potenatial location from pattern " + pattern);
				}
			} catch (final RESyntaxException e) {
				throw new LocationmapException(
						"Pattern is not a valid regular expression (" + pattern
								+ "): " + e.getMessage());
			}
		}
		return results;
	}

	/**
	 * Test to see if this location is applicable to a given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws MalformedURLException
	 * @throws MalformedURLException
	 *             if the request URI cannot be converted to a valid URL
	 * @throws RESyntaxException
	 *             if the pattern supplied is not a valid regular expression
	 * @FIXME use pattern matching to test for a match
	 */
	public boolean isMatch(final String pattern, final URI requestURI)
			throws MalformedURLException, RESyntaxException {
		URL url;
		try {
			url = requestURI.toURL();
		} catch (final IllegalArgumentException e) {
			// we'll assume that this is not an absolute URL and therefore
			// refers to a file
			url = new URL("file://" + requestURI);
		}
		final String urlString = url.toExternalForm();

		final RE r = new RE(pattern);
		return r.match(urlString);
	}
}
