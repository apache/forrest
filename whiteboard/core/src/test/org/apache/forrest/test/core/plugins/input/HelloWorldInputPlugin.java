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
package org.apache.forrest.test.core.plugins.input;

import org.apache.forrest.core.document.IDocument;
import org.apache.forrest.core.document.InternalDocument;
import org.apache.forrest.core.plugin.AbstractInputPlugin;

/**
 * A very simple plugin that alwasy produces a Hello World document, regardless
 * of the input document.
 * 
 */
public class HelloWorldInputPlugin extends AbstractInputPlugin {

	public static final String CONTENT = "<html xmlns=\"http://www.w3.org/2002/06/xhtml2\" xml:lang=\"en\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd\">  <head>    <title>XHTML 2 Simple Sample Page</title>   </head>  <body>  <h>Hello World</h>  </body></html>";

	public IDocument process(final IDocument doc) {
		return new InternalDocument(CONTENT);
	}
}
