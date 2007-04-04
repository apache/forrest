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
package org.apache.forrest.reader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.matcher.AbstractMatcher;
import org.apache.forrest.core.reader.AbstractReader;

/**
 * Reads a document using the "site:" psuedo protocol
 * from Forrest 1.
 *
 */
public class Forrest1SiteElementReader extends AbstractReader {

	public AbstractSourceDocument read(IController controller, URI requestURI, AbstractSourceNode sourceNode, AbstractMatcher matcher) throws ProcessingException {
		URI siteURI;
		AbstractOutputDocument siteDoc;
		try {
			siteURI = new URI("classpath:/xdocs/site.xml");
			siteDoc = controller.getOutputDocument(siteURI);
			String href = siteDoc.getValue(requestURI.getSchemeSpecificPart() + "@href");
			return controller.getSourceDocuments(new URI(href));
		} catch (URISyntaxException e) {
			// Should never be thrown
			throw new ProcessingException("Unable create URI for site.xml document", e);
		} catch (MalformedURLException e) {
			throw new ProcessingException("Unable to get site.xml document", e);
		}
	}

}
