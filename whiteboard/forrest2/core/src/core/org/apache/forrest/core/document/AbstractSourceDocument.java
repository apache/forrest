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
import java.io.IOException;
import java.net.URI;

/**
 * A source document is a single input document as retrieved from its source
 * location.
 * 
 */
public abstract class AbstractSourceDocument extends AbstractDocument {
	String type;

	public AbstractSourceDocument(URI requestURI, String content) {
		setRequestURI(requestURI);
		setContent(content);
	}

	@Override
	public String getContentAsString() throws IOException {
		if (this.isComplete() == false) {
			this.load();
		}
		return super.getContentAsString();
	}

	/**
	 * Loads the remainder of the content from the input stream.
	 * 
	 * @throws IOException
	 * 
	 */
	private void load() throws IOException {
		final StringBuffer fileData = new StringBuffer(1000);
		final BufferedReader reader = this.getReader();
		char[] buf = new char[1000];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			final String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1000];
		}
		this.addContent(fileData.toString());
		this.setComplete(true);
		reader.close();
	}

	/**
	 * Adds some conntent to the end of the current content.
	 * 
	 * @param newContent
	 */
	private void addContent(final String newContent) {
		this.content = this.content + newContent;
	}

	private boolean isComplete;

	private BufferedReader reader;

	/**
	 * Indicate whether the complete document is available or not.
	 * 
	 * @param b
	 */
	protected void setComplete(final boolean b) {
		this.isComplete = b;
	}

	/**
	 * Indicates whether the complete document is available or not. If it is not
	 * avilable then the rest can be read using the load() method.
	 * 
	 * @param b
	 */
	public boolean isComplete() {
		return this.isComplete;
	}

	/**
	 * Set the reader from which the remains of a partially loaded file can be
	 * read.
	 * 
	 * @param reader
	 */
	protected void setReader(final BufferedReader reader) {
		this.reader = reader;
	}

	protected BufferedReader getReader() {
		return this.reader;
	}

	/**
	 * Gets a string that defines the type of document this is. This value is
	 * used to identify which input plugin will be used top generate the
	 * internal document for further processing. This string is usually a
	 * mime-type, but may be any unique string.
	 * 
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

}
