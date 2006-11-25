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
import java.util.ArrayList;
import java.util.List;

/**
 * An aggregated source document is used to represent
 * a source document that consists of a number of 
 * separate documents.
 * 
 */
public class AggregatedSourceDocument extends AbstractSourceDocument {

	public AggregatedSourceDocument(URI requestURI, String content) {
		super(requestURI, content);
	}

	List<AbstractSourceDocument> docs = new ArrayList<AbstractSourceDocument>();

	/**
	 * Look to see if this aggregation of documents contains
	 * a specificed document.
	 * 
	 * @param doc
	 * @return
	 */
	public boolean contains(AbstractSourceDocument doc) {
		return docs.contains(doc);
	}

	/**
	 * Add a document to the aggregated documents list.
	 * @param doc
	 */
	public boolean add(AbstractSourceDocument doc) {
		return docs.add(doc);
	}
	
}