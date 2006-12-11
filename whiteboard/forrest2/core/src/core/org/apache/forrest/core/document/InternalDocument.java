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

import java.net.URI;

/**
 * An InternalDocument is a document that has been converted from a Source
 * IDocument into the internal Forrest XML format for processing.
 * 
 */
public class InternalDocument extends AbstractDocument {
	
	private AbstractSourceDocument sourceDocument;

	public InternalDocument(AbstractSourceDocument sourceDoc) {
		setSourceDocument(sourceDoc);
	}

	public InternalDocument(AbstractSourceDocument sourceDoc, final String content) {
		setSourceDocument(sourceDoc);
		this.setRequestURI(sourceDoc.getRequestURI());
		this.setContent(content);
	}

	/**
	 * Get the source document that resulted in this
	 * document being created.
	 * 
	 * @return
	 */
	public AbstractSourceDocument getSourceDocument() {
		return sourceDocument;
	}

	/**
	 * Set the source documen that was used to create this
	 * internal document.
	 * 
	 * @param sourceDocument
	 */
	public void setSourceDocument(AbstractSourceDocument sourceDocument) {
		this.sourceDocument = sourceDocument;
	}
}
