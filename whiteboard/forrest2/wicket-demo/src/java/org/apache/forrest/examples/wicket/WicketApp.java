package org.apache.forrest.examples.wicket;

import wicket.Component;
import wicket.ISessionFactory;
import wicket.Request;
import wicket.RestartResponseAtInterceptPageException;
import wicket.Session;
import wicket.authorization.Action;
import wicket.authorization.IAuthorizationStrategy;
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

	/**
	 * @see wicket.examples.WicketExampleApplication#init()
	 */
	@Override
	protected void init() {
		getSecuritySettings().setAuthorizationStrategy(
				new IAuthorizationStrategy() {
					public boolean isActionAuthorized(Component component,
							Action action) {
						return true;
					}

					public boolean isInstantiationAuthorized(
							Class componentClass) {
						if (AuthenticatedWebPage.class
								.isAssignableFrom(componentClass)) {
							// Is user signed in?
							if (((LogInSession) Session.get()).isSignedIn()) {
								// okay to proceed
								return true;
							}

							// Force sign in
							throw new RestartResponseAtInterceptPageException(
									LogInPage.class);
						}
						return true;
					}
				});
	}

	/**
	 * @see wicket.protocol.http.WebApplication#getSessionFactory()
	 */
	@Override
	public ISessionFactory getSessionFactory() {
		return new ISessionFactory() {
			public Session newSession(Request request) {
				return new LogInSession(WicketApp.this);
			}
		};
	}
}
