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
