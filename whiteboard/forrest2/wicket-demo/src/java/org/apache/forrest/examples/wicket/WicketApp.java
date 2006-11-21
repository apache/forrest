package org.apache.forrest.examples.wicket;

import wicket.protocol.http.WebApplication;

/**
 * @author Ross Gardler
 */
public class WicketApp extends WebApplication {
    public WicketApp() {
        
    }

	@Override
	public Class<WelcomePage> getHomePage() {
		return WelcomePage.class;
	}
}
  