package org.apache.forrest.examples.wicket;

import wicket.markup.html.WebPage;

public class WelcomePage extends WebPage {
	
	public WelcomePage() {
		new Forrest2Panel(this, "header", "header/welcome.html");
		new Forrest2Panel(this, "mainNavigation", "navigation/welcome.html");
		new Forrest2Panel(this, "body", "body/welcome.html");
		new Forrest2Panel(this, "footer", "footer/welcome.html");
	}
}
