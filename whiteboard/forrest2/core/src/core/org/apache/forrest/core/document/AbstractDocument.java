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

import java.io.IOException;
import java.net.URI;
import java.util.Date;

public abstract class AbstractDocument implements IDocument {
	private Date lastModified;

	String content;
	
	private URI requestURI;

	public AbstractDocument() {
	}

	public AbstractDocument(final URI requestURI, final String content) {
		setContent(content);
		setRequestURI(requestURI);
	}

	public String getContentAsString() throws IOException {
		return this.content;
	}

	/**
	 * Set the content of this document. The last modified date will also be
	 * updated.
	 * 
	 * @param content
	 */
	public void setContent(final String content) {
		this.content = content;
		this.lastModified = new Date();
	}

	/**
	 * Get the date and time that this IDocument was last modified. In the case
	 * of a AbstractSourceDocument this will be the time the source was last
	 * retrieved. In the case of an Internal IDocument this will be the last
	 * time the source was processed by the input plugins to generate the
	 * internal document. In the case of an outputDocument this will be the last
	 * time the Internal IDocument was last processed using the output plugins.
	 * 
	 * @return
	 */
	public Date getLastModified() {
		return this.lastModified;
	}
	
	/**
	 * Set the URI that was used to request this document.
	 * 
	 * @return
	 */
	public void setRequestURI(URI requestURI) {
		this.requestURI = requestURI;
	}

	/**
	 * Get the URI that was used to request this document.
	 * 
	 * @return
	 */
	public URI getRequestURI() {
		return this.requestURI;
	}


}
