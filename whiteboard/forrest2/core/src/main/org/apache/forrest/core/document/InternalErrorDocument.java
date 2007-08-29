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

import java.io.IOException;
import java.net.URI;

public class InternalErrorDocument extends InternalDocument {

	public InternalErrorDocument(AbstractSourceDocument sourceDoc, String message) {
		super (sourceDoc, message);
	}

	@Override
	public String getContentAsString() throws IOException {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<html xmlns=\"http://www.w3.org/2002/06/xhtml2\" xml:lang=\"en\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd\">");
		sb.append("<head><title>Internal Forrest Error</title></head>");
		sb.append("<body>");
		sb.append("<h>");
		sb.append("Error processing request for " + getRequestURI());
		sb.append("</h>");
		sb.append("<p>");
		sb.append(super.getContentAsString());
		sb.append("</p>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

}
