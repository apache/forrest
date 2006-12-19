package org.apache.forrest.examples.wicket;

import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebSession;

/**
 * Session class for signin example. Holds and authenticates users.
 * 
 * @author Jonathan Locke
 */
public final class LogInSession extends WebSession
{
	/** Trivial user representation */
	private String user;

	/**
	 * Constructor
	 * 
	 * @param application
	 *            The application
	 */
	protected LogInSession(final WebApplication application)
	{
		super(application);
	}

	/**
	 * Checks the given username and password, returning a User object if if the
	 * username and password identify a valid user.
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return True if the user was authenticated
	 */
	public final boolean authenticate(final String username, final String password)
	{
		if (user == null)
		{
			// Trivial password "db"
			if ("wicket".equalsIgnoreCase(username) && "wicket".equalsIgnoreCase(password))
			{
				user = username;
			}
		}

		return user != null;
	}

	/**
	 * @return True if user is signed in
	 */
	public boolean isSignedIn()
	{
		return user != null;
	}

	/**
	 * @return User
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *            New user
	 */
	public void setUser(final String user)
	{
		this.user = user;
	}
}
