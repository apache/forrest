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
package org.apache.forrest.examples.wicket;

import wicket.markup.html.WebPage;

public class BasePage extends WebPage {
	
	public BasePage() {
		String path = this.getRequest().getPath();
		/*
		 * When running in the WicketBench (Eclipse IDE Plugin) the test
		 * environment mounts the application at "/home". We'll stip this
		 * from the path. Note that this could cause a problem in production
		 * applications if they use a "/home*" URLspace.
		 * 
		 * We need to make the mount point of a WicketBench test configurable.
		 */
		if (path.startsWith("/home")) {
			path = path.substring(5);
		}
		// FIXME: the default page request should be configured elsewhere (e.g. web.xml)
		if (path.length() == 0 || path.equals("/")) {
			path = "/index.html";
		}
		new Forrest2Panel(this, "header", "header" + path);
		new Forrest2Panel(this, "mainNavigation", "navigation" + path);
		new Forrest2Panel(this, "footer", "footer" + path);
	}
}
