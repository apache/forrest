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

import wicket.Component;
import wicket.ISessionFactory;
import wicket.Request;
import wicket.RestartResponseAtInterceptPageException;
import wicket.Session;
import wicket.authorization.Action;
import wicket.authorization.IAuthorizationStrategy;
import wicket.protocol.http.WebApplication;

/**
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
