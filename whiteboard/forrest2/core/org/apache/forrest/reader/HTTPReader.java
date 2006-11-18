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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.DefaultSourceDocument;
import org.apache.forrest.core.exception.SourceException;
import org.apache.forrest.core.locationMap.Location;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.w3c.tidy.Tidy;

/**
 * An HTTP reader reads a resource using the HTTP protocol.
 * 
 */
public class HTTPReader extends AbstractReader {
	private HttpClient client;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.reader.IReader#init()
	 */
	public void init() {
		this.client = new HttpClient(new MultiThreadedHttpConnectionManager());
		this.client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(30000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.reader.IReader#read(org.apache.forrest.test.core.locationMap.Location)
	 */
	public AbstractSourceDocument read(AbstractXmlApplicationContext context, final Location location)
			throws MalformedURLException {
		InputStream is;
		DefaultSourceDocument result = null;
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final GetMethod get = new GetMethod(location.getSourceURL()
				.toExternalForm());
		get.setFollowRedirects(true);
		try {
			this.client.executeMethod(get);
			is = get.getResponseBodyAsStream();
			final Tidy tidy = new Tidy();
			tidy.setXHTML(true);
			tidy.parseDOM(is, out);
			result = new DefaultSourceDocument(out.toString());
		} catch (final Exception e) {
			if (location.isRequired())
				throw new SourceException("Source URL is invalid", e);
		} finally {
			get.releaseConnection();
		}
		return result;
	}
}
