/*
 * Created on Feb 11, 2004
 */
package org.apache.forrest.forrestbot.webapp.form;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

public class LoginForm extends BaseDynaActionForm {

	private static Logger log = Logger.getLogger(LoginForm.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		ActionErrors errors = new ActionErrors();
		errors.add(checkRequiredFields(new String[] {"username", "password"}));
		return errors;
	}

}
