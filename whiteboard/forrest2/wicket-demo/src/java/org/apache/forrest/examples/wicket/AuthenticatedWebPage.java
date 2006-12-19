package org.apache.forrest.examples.wicket;

/**
 * If a page should require a user to log in before being
 * displayed it should extend this class.
 */
public class AuthenticatedWebPage extends BasePage {

	public AuthenticatedWebPage() {
		super();
		new Forrest2Panel(this, "body", "body/placeholder.html");
	}
}
