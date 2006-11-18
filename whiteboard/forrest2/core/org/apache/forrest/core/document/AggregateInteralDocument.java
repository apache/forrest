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
import java.util.List;

/**
 * A colelction of intenral documents that are to be processed as a single
 * document by the output plugins.
 * 
 * @FIXME when aggregating content ensure that the result is XHTML2 compliant
 * 
 */
public class AggregateInteralDocument extends InternalDocument {

	private List<InternalDocument> documents;

	/**
	 * Create a new Aggregate document containing all the supplied internal
	 * documents.
	 * 
	 * @param intDocs
	 */
	public AggregateInteralDocument(final List<InternalDocument> internalDocs) {
		this.documents = internalDocs;
	}

	@Override
	public String getContentAsString() throws IOException {
		final StringBuffer result = new StringBuffer("<aggregate>");
		for (int i = 0; i < this.documents.size(); i++) {
			result.append(this.documents.get(i).getContentAsString());
		}
		result.append("</aggregate>");
		return result.toString();
	}

}
