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
package org.apache.forrest.core.reader;

import java.net.MalformedURLException;
import java.net.URI;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.AggregatedSourceDocument;
import org.apache.forrest.core.document.DefaultSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.locationMap.AggregateNode;
import org.apache.forrest.core.matcher.AbstractMatcher;

/**
 * An AggregateReader reader is a wrapper for other readers. It is not intended
 * to be used directly by the user, instead the Controller will use it whenever
 * an aggregate document is required. This reader will iterate over all sources
 * in the aggregate document and will delegate to the relevant reader to get the
 * source document from each location.
 * 
 */
public class AggregateReader extends AbstractReader {

	public AbstractSourceDocument read(IController controller, URI requestURI,
			final AbstractSourceNode sourceNode, AbstractMatcher matcher)
			throws ProcessingException {
		AggregatedSourceDocument doc = new AggregatedSourceDocument(requestURI);
		AggregateNode aggregateNode = (AggregateNode) sourceNode;
		for (AbstractSourceNode node : aggregateNode.getNodes()) {
			IReader reader = (IReader) controller.getReader(node);
			try {
				doc.add((DefaultSourceDocument) reader.read(controller,
						requestURI, node, matcher));
			} catch (MalformedURLException e) {
				throw new ProcessingException(e.getMessage(), e);
			}
		}
		return doc;
	}

}
