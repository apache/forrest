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
package org.apache.forrest.examples.reader;

import java.net.URI;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.matcher.AbstractMatcher;
import org.apache.forrest.core.reader.ChainedReader;

public class NavigationReader extends ChainedReader {

	public AbstractSourceDocument read(IController controller, URI requestURI,
			AbstractSourceNode location, AbstractMatcher matcher) throws ProcessingException {
		return super.read(controller, requestURI, location, matcher);
	}
	
	public String getDocType() {
		return "org.apache.forrest.example.Navigation";
	}

}
