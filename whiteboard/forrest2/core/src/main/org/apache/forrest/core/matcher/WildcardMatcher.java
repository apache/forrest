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
package org.apache.forrest.core.matcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.forrest.core.exception.ProcessingException;
import org.apache.log4j.Logger;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;

/**
 * A Wildcard Matcher is based on Cocoons Widcard Matcher.
 * 
 * @TODO actual implement something useful rather than always
 * return true
 */
public class WildcardMatcher extends AbstractMatcher {

	Logger log = Logger.getLogger(WildcardMatcher.class);

	/**
	 * Create an REMatcher using the supplied Regular Expression.
	 * 
	 * @param pattern
	 */
	public WildcardMatcher(String pattern) {
		this.setPattern(pattern);
	}

	@Override
	public URI substituteVariables(URI requestURI, URI sourceURI)
			throws ProcessingException {
		return sourceURI;
	}

}
