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
package org.apache.forrest.core.plugin;

import java.io.IOException;

import org.apache.forrest.core.Controller;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.IDocument;
import org.apache.forrest.core.document.InternalDocument;

/**
 * This is plugin does not process the content in any way, it simply repackages
 * the sourceDocument as an internal document. It will be applied if no other
 * plugin is available that is suitable for the source document.
 * 
 */
public class PassThroughInputPlugin extends AbstractInputPlugin {

	public IDocument process(final Controller controller, final IDocument doc) throws IOException {
		return new InternalDocument((AbstractSourceDocument)doc, doc.getContentAsString());
	}

}
