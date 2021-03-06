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
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.forrest.core.Controller;
import org.apache.forrest.core.document.DefaultOutputDocument;
import org.apache.forrest.core.document.IDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.log4j.Logger;

/**
 * A plugin that performs an XSLT transformation on the internal documents to
 * create the output document.
 * 
 */
public class XSLTOutputPlugin extends BaseOutputPlugin {

	Logger log = Logger.getLogger(XSLTInputPlugin.class);

	private String xsltPath;

	private XSLTOutputPlugin() {
		super();
	}

	public XSLTOutputPlugin(final String requestURI) {
		this.setPattern(requestURI);
	}

	@Override
	public IDocument process(final Controller controller, final IDocument doc) throws IOException {
		final TransformerFactory tFactory = TransformerFactory.newInstance();
		log.debug("Processing document with output stylesheet from " + this.getXsltPath());
		try {
            final AbstractSourceDocument xsltDoc = controller.getSourceDocuments(new URI(this.getXsltPath()));
			Transformer transformer;
			transformer = tFactory.newTransformer(new StreamSource(new StringReader(xsltDoc.getContentAsString())));
            log.debug("Got transformer ");
			final StringReader reader = new StringReader(doc.getContentAsString());
			final StreamSource in = new StreamSource(reader);
			final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			final StreamResult out = new StreamResult(outStream);
			transformer.transform(in, out);
            log.debug("Transformed");
			return new DefaultOutputDocument(doc.getRequestURI(), outStream.toString());
		} catch (final TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (final TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (final ProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
	}

	public String getXsltPath() {
		return this.xsltPath;
	}

	public void setXsltPath(final String xsltURL) {
		this.xsltPath = xsltURL;
	}
}
