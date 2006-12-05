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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.forrest.core.matcher.AbstractMatcher;
import org.apache.forrest.core.matcher.REMatcher;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A location is a possible source location for a given request URI. There may
 * be more than one location for any request URI, each of the possible locations
 * may be optional or requried.
 * 
 */
public class LocationNode {

	Logger log = Logger.getLogger(LocationNode.class);
	
	private AbstractMatcher matcher;
	
	private List<AbstractSourceNode> sourceNodes;

	/**
	 * Construct a new LocationNode from an XML node.
	 * 
	 * @param element
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public LocationNode(final Node element) throws URISyntaxException, IOException {
		String pattern = null;
		List<AbstractSourceNode> nodes = new ArrayList<AbstractSourceNode>();
		
		final NamedNodeMap atts = element.getAttributes();
		pattern = atts.getNamedItem("regexp").getNodeValue();
		final NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			final Node child = children.item(i);
			String nodeName = child.getNodeName();
			if (nodeName != null && nodeName.equals("source")) {
				nodes.add(new SourceNode(child));
			}
		}
		this.init(pattern, nodes);
	}

	private void init(final String pattern, final List<AbstractSourceNode> nodes) throws URISyntaxException {
		if (pattern == null)
			throw new IllegalArgumentException(
					"requestURIPattern cannot be null");
		if (nodes == null || nodes.size() == 0)
			throw new IllegalArgumentException(
					"There must be at least one source node for a location");
		this.setMatcher(new REMatcher(pattern));
		this.setSourceNodes(nodes);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("location: ");
		sb.append("Matcher:");
		sb.append(this.getMatcher().toString());
		
		return sb.toString();
	}

	public List<AbstractSourceNode> getSourceNodes() {
		return sourceNodes;
	}

	public void setSourceNodes(List<AbstractSourceNode> sourceNodes) {
		this.sourceNodes = sourceNodes;
	}

	public AbstractMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(AbstractMatcher matcher) {
		this.matcher = matcher;
	}

}
