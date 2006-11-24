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
package org.apache.forrest.core.document;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * The most basic of output documents. The document itself is nothing more than
 * a String.
 * 
 */
public class DefaultOutputDocument extends AbstractOutputDocument {
	
	Logger log = Logger.getLogger(DefaultOutputDocument.class);

	public DefaultOutputDocument(final String content) {
		this.setContent(content);
	}

	@Override
	public String getContentAsString() {
		return this.content;
	}

	/**
	 * Get the links that should be crawled from this document. Since type of
	 * this document is not known (it's a string) it can be difficult to
	 * identify links. However, if the document appears to be an HTML string
	 * then href attributes of anchors are retrieved (only local links will be
	 * returned in the resutls).
	 */
	@Override
	public Set<String> getLocalDocumentLinks() {
		Set<String> results = new HashSet<String>();
		String content = getContentAsString();
		if (content.contains("html") || content.contains("HTML")) {
			String rePattern = "<[a|A]\\s*href=\"([^\"#]+)\"\\s*>([^*<]+)</[a|A]>";
			Pattern pattern = Pattern.compile(rePattern);
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				String href = matcher.group(1);
				if (href.startsWith("#") || href.startsWith("href://")) {
					log.debug("Ignoring non-local href: " + href);
				} else {
		            results.add(href);
		            log.debug("Added local href: " + href);
				}
	        }
		}
		return results;
	}

}
