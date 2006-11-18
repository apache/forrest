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
package org.apache.forrest.test.core.readers;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.DefaultSourceDocument;
import org.apache.forrest.core.locationMap.Location;
import org.apache.forrest.reader.FileReader;

public class HelloReader extends FileReader {

	@Override
	public AbstractSourceDocument read(final Location location) {
		DefaultSourceDocument doc = null;
		final URI psudeoURI = location.getSourceURI();
		final String ssp = psudeoURI.getSchemeSpecificPart();
		URI uri;
		try {
			uri = new URI(ssp);
			location.setSourceURI(uri);
			doc = (DefaultSourceDocument) super.read(location);
			doc.setType("org.apache.forrest.helloWorld");
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

}
