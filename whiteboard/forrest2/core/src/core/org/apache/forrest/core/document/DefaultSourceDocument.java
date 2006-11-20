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
package org.apache.forrest.core.document;

import java.io.BufferedReader;

/**
 * A basic source document. It holds the document as a string.
 * 
 */
public class DefaultSourceDocument extends AbstractSourceDocument {

	public DefaultSourceDocument(final String content) {
		this.setContent(content);
		this.setComplete(true);
	}

	/**
	 * Create a new stringSourceDocument that has partial data read from a
	 * reader, and partial data left to be read.
	 * 
	 * @param fileData
	 * @param reader
	 * @param type
	 *            The mime type of the document. May be null if unkown.
	 */
	public DefaultSourceDocument(final String fileData,
			final BufferedReader reader, final String type) {
		this.setContent(fileData);
		this.setReader(reader);
		this.setComplete(false);
		this.setType(type);
	}

	public DefaultSourceDocument(final String content, final String type) {
		this.setContent(content);
		this.setComplete(true);
		this.setType(type);
	}
}