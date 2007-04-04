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
import java.net.URL;

import org.apache.forrest.core.exception.ProcessingException;


/**
 * A matcher is used to match a pattern against a given 
 * string. 
 *
 */
public abstract class AbstractMatcher {
	
	String pattern;

	/**
	 * Get the pattern used by this mathcer.
	 * The syntax of the pattern is dependant on the
	 * implementation of the matcher.
	 * @return
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Set the pattern used by this mathcer.
	 * The syntax of the pattern is dependant on the
	 * implementation of the matcher.
	 * @param pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Perform any necessary variable substitution in the
	 * supplied sourceURI using the given requestURI to find
	 * values for variables.
	 * @throws ProcessingException 
	 * 
	 */
	public abstract URI substituteVariables(URI requestURI, URI sourceURI) throws ProcessingException;
}
