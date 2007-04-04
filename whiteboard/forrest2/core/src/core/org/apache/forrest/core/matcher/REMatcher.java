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
 * An REMatcher uses a regular expression to define a match.
 * 
 */
public class REMatcher extends AbstractMatcher {

	Logger log = Logger.getLogger(REMatcher.class);

	/**
	 * Create an REMatcher using the supplied Regular Expression.
	 * 
	 * @param pattern
	 */
	public REMatcher(String re) {
		this.setPattern(re);
	}

	@Override
	public URI substituteVariables(URI requestURI, URI sourceURI)
			throws ProcessingException {
		URL url;
		RE r;

		try {
			r = new RE(getPattern());
		} catch (RESyntaxException re) {
			throw new ProcessingException(
					"Unable to extract variable values from request: "
							+ re.getMessage(), re);
		}

		String urlString = requestURI.getPath();

		String sourceSSP = sourceURI.getSchemeSpecificPart();

		if (r.match(urlString)) {
			String variable;
			String value;
			for (int i = 0; i < r.getParenCount(); i++) {
				variable = "$(" + i + ")";
				value = r.getParen(i);
				if (value != null) {
					sourceSSP = sourceSSP.replace(variable, value);
				}
			}
			log.debug("After variable substitution a potential source path is "
					+ sourceSSP);
		} else {
			throw new ProcessingException(
					"Unable to extract variable values from '" + urlString + "' using Regular Expression: " + getPattern());
		}

		URI newURI;
		try {
			newURI = new URI(sourceURI.getScheme(), sourceSSP, null);
		} catch (URISyntaxException e) {
			throw new ProcessingException(
					"Unable to perform variable substitution on the source uri "
							+ sourceURI + " cuased by " + e.getMessage(), e);
		}
		return newURI;
	}

}
