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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.DocumentFactory;
import org.apache.forrest.core.exception.SourceException;
import org.apache.forrest.core.locationMap.Location;

/**
 * An File reader reads a resource using the file protocol, i.e. it will read
 * from local storage.
 * 
 */
public class FileReader extends AbstractReader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.forrest.reader.IReader#read(org.apache.forrest.test.core.locationMap.Location)
	 */
	public AbstractSourceDocument read(IController controller, final Location location) {
		AbstractSourceDocument result = null;
		try {
			final InputStream is = new FileInputStream(new File(location
					.getSourceURL().toURI()));
			result = DocumentFactory.getSourceDocumentFor(is);
		} catch (final Exception e) {
			if (location.isRequired())
				throw new SourceException("Source URL is invalid", e);
		}
		return result;
	}
}
