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

import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.locationMap.Location;
import org.springframework.context.support.AbstractXmlApplicationContext;

public interface IReader {

	/**
	 * Initialise the reader. This method is called by the readers constructors
	 * and should prepare the reader for operation.
	 * 
	 */
	abstract void init();

	/**
	 * Read a resource from a given location. If the resource cannot be read,
	 * but it is an optional location then return null, if it cannot be read and
	 * it is a required location throw SourceException.
	 * @param context 
	 * 
	 * @param location
	 * @return
	 * @throws MalformedURLException
	 */
	public abstract AbstractSourceDocument read(AbstractXmlApplicationContext context, Location location)
			throws MalformedURLException;

}