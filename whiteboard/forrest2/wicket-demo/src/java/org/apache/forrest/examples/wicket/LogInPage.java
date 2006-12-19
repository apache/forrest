package org.apache.forrest.examples.wicket;

import wicket.MarkupContainer;
import wicket.PageParameters;
import wicket.Session;
import wicket.markup.html.form.PasswordTextField;
import wicket.markup.html.form.StatelessForm;
import wicket.markup.html.form.TextField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.PropertyModel;
import wicket.util.value.ValueMap;

/**
 * Simple example of a sign in page.
 * 
 * @author Jonathan Locke
 */
public final class LogInPage extends BasePage {
	/**
	 * Constructor
	 */
	public LogInPage() {
		this(null);
	}

	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            The page parameters
	 */
	public LogInPage(final PageParameters parameters) {
		super();
		// Create feedback panel and add to page
		new FeedbackPanel(this, "feedback");

		// Add sign-in form to page, passing feedback panel as validation error
		// handler
		new SignInForm(this, "signInForm");
	}

	/**
	 * Sign in form
	 * 
	 * @author Jonathan Locke
	 */
	public final class SignInForm extends StatelessForm {
		// El-cheapo model for form
		private final ValueMap properties = new ValueMap();

		/**
		 * Constructor
		 * 
		 * @param parent
		 * @param id
		 *            id of the form component
		 */
		public SignInForm(MarkupContainer parent, final String id) {
			super(parent, id);

			// Attach textfield components that edit properties map model
			new TextField<String>(this, "username", new PropertyModel<String>(
					properties, "username"));
			new PasswordTextField(this, "password", new PropertyModel<String>(
					properties, "password"));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		@Override
		public final void onSubmit() {
			LogInSession session = (LogInSession) getSession();

			if (session.authenticate(properties.getString("username"),
					properties.getString("password"))) {
				if (!continueToOriginalDestination()) {
					setResponsePage(new WelcomePage());
				}
			} else {
				final String errmsg = getLocalizer().getString("loginError",
						this, "Unable to sign you in");
				error(errmsg);
			}
		}
	}
}
