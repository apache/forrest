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
import java.net.URISyntaxException;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.DefaultSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.matcher.AbstractMatcher;

/**
 * A chained reader implements a psuedo protocol. It is commonly used when you
 * need to retrieve a document that whose type cannot be identified from the raw
 * source alone.
 * 
 * It is defined in forrestContext.xml as follows:
 * 
 * <bean id="fooProtocol" class="org.apache.forrest.core.reader.ChainedReader" >
 * <property name="docType" value="org.foo.Bar" /> </bean>
 * 
 * We can then define a chain of readers like this:
 * 
 * <location pattern="classpath/foo.*"> <source
 * href="fooProtocol:classpath:/xdocs/exampleFeed.xml"/> </location>
 * 
 * <location pattern="file/foo.*"> <source
 * href="fooProtocol:file:/xdocs/exampleFeed.xml"/> </location>
 * 
 * <location pattern="http/foo.*"> <source
 * href="fooProtocol:http:/xdocs/exampleFeed.xml"/> </location>
 * 
 * etc.
 * 
 */
public class ChainedReader extends AbstractReader {

	private String docType;

	public AbstractSourceDocument read(IController controller, URI requestURI,
			final AbstractSourceNode sourceNode, AbstractMatcher matcher)
			throws ProcessingException {
		DefaultSourceDocument doc = null;
		final String ssp = sourceNode.getSourceURI().getSchemeSpecificPart();
		URI subSourceURI;
		try {
			subSourceURI = new URI(ssp);
			IReader reader;
			reader = (IReader) controller.getReader(subSourceURI);
			doc = (DefaultSourceDocument) reader.read(controller, requestURI,
					sourceNode, matcher);
			if (doc != null) {
				doc.setType(getDocType());
			}
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

}
