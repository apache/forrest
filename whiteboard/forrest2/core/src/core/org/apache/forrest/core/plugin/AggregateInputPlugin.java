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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.forrest.core.Controller;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.AggregatedSourceDocument;
import org.apache.forrest.core.document.IDocument;
import org.apache.forrest.core.document.InternalDocument;
import org.apache.forrest.core.exception.ProcessingException;

/**
 * The aggregate plugin iis a wrapper for other plugins. It is used to parse
 * aggregated documents. It iterates over each of the documents within the
 * aggregated document and delegates the processing of each one to the relevant
 * input plugin. The resulting output document is a single Forrest internal
 * document that contains the body of each of the component documents.
 * 
 */
public class AggregateInputPlugin extends AbstractInputPlugin {

	public AggregateInputPlugin() {
		super();
	}

	public IDocument process(final Controller controller,
			final IDocument aggregateDoc) throws IOException, ProcessingException {
		StringBuffer sb = new StringBuffer(getDocumentHeader());
		for (AbstractSourceDocument doc : ((AggregatedSourceDocument) aggregateDoc)
				.getDocuments()) {
			AbstractInputPlugin inputPlugin = controller.getInputPlugin(doc);
			IDocument internalDoc = inputPlugin.process(controller, doc);
			String content = internalDoc.getContentAsString();
			int startPos = content.toLowerCase().indexOf("<body>");
			int endPos = content.toLowerCase().indexOf("</body>");
			if (startPos > -1 && endPos > -1) {
				sb.append(content.substring(startPos + 6, endPos - 1));
			} else {
				throw new ProcessingException("Invalid document in the aggregate collection");
			}
		}
		sb.append(getDocumentFooter());

		return new InternalDocument(aggregateDoc.getRequestURI(), sb.toString());
	}

	private CharSequence getDocumentHeader() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<html xmlns=\"http://www.w3.org/2002/06/xhtml2\" xml:lang=\"en\"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"    xsi:schemaLocation=\"http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd\" >  <head>   <title>XHTML 2 Simple Sample Page</title>   </head> <body> ";
	}

	private CharSequence getDocumentFooter() {
		return "</body></html>";
	}

	public URI getXsltURI() {
		return null;
	}
}
