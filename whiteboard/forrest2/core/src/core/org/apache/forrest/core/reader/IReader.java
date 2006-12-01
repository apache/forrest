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
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.Location;

public interface IReader {

	/**
	 * Initialise the reader. This method is called by the readers constructors
	 * and should prepare the reader for operation.
	 * 
	 */
	abstract void init();

	/**
	 * Read a resource from a given uri. If the resource cannot be read,
	 * but it is an optional location then return null, if it cannot be read and
	 * it is a required location throw SourceException.
	 * @param context 
	 * 
	 * @param controller - the forrest controller in use
	 * @param requestURI - the URI being requested
	 * @param uri - the uri we are to read the document from
	 * @param sourceURI - the source URI we are trying to read
	 * 
	 * @return
	 * @throws MalformedURLException
	 * @throws ProcessingException 
	 */
	public abstract AbstractSourceDocument read(IController controller, URI requestURI, Location location, URI sourceURI)
			throws MalformedURLException, ProcessingException;

}