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
package org.apache.forrest.core.plugin;

import java.io.IOException;
import java.net.URI;

import org.apache.forrest.core.document.DefaultOutputDocument;
import org.apache.forrest.core.document.IDocument;
import org.apache.regexp.RE;

/**
 * A base output plugin from which all other output plugins should be etended.
 * This plugin does not actually do anything, it is essentially a pass through
 * plugin.
 * 
 */
public class BaseOutputPlugin implements IOutputPlugin {

	String requestURIPattern;

	/**
	 * Get the pattern that a requestURI must match for this plugin to be
	 * applied to the internal documents.
	 */
	public String getPattern() {
		return this.requestURIPattern;
	}

	/**
	 * Set the URI pattern this plugin should match befre being applied.
	 * 
	 * @param requestURIPattern
	 */
	public void setPattern(final String requestURIPattern) {
		this.requestURIPattern = requestURIPattern;
	}

	/**
	 * Tests to see if there is match between this plugins request URI pattern
	 * and a given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @fixme implement proper matching
	 */
	public boolean isMatch(final URI requestURI) {
		final RE r = new RE(this.getPattern());
		return r.match(requestURI.getPath());
	}

	public IDocument process(final IDocument doc) throws IOException {
		return new DefaultOutputDocument(doc.getContentAsString());
	}

}
