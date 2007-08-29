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
import java.net.URI;
import java.net.URISyntaxException;

import org.w3c.dom.Node;

/**
 * A location is a possible source location for a given request URI. There may
 * be more than one location for any request URI, each of the possible locations
 * may be optional or requried.
 * 
 */
public class SourceNode extends AbstractSourceNode {

	public SourceNode(Node element) throws URISyntaxException, IOException {
		super(element);
	}

	public SourceNode(URI requestURI, Boolean isRequired) {
		super(requestURI, isRequired);
	}
	
}
