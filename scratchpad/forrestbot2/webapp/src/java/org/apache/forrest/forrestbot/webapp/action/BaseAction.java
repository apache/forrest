package org.apache.forrest.forrestbot.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.forrest.forrestbot.webapp.Config;
import org.apache.forrest.forrestbot.webapp.Constants;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class BaseAction extends Action {
	private static Logger log = Logger.getLogger(BaseAction.class);
	
	public BaseAction() {
		super();
		Config.getInstance(); // set up log4j
	}

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		super.execute(mapping, form, request, response);
		
		/*
		 * Make the constants available to all JSP expressions
		 */
		request.setAttribute("Constants", Constants.getConstantFieldsAsMap());
		
		response.setHeader("Pragma", "no-cache");

		return mapping.findForward(Constants.FORWARD_NAME_SUCCESS);
	}
	
	protected boolean checkAuthorized(HttpServletRequest request, HttpServletResponse response, boolean setErrors) {
		Object attr = request.getSession(true).getAttribute("auth");
		if (attr != null && ((Boolean)attr).booleanValue()) {
			return true;
		} else {
			if (setErrors) {
				ActionErrors errors = new ActionErrors();
				errors.add("authorize", new ActionError("error.authorization"));
				saveErrors(request, errors);
			}
			return false;
		}
	}
	
	protected boolean checkAuthorized(HttpServletRequest request, HttpServletResponse response) {
		return checkAuthorized(request, response, true);
	}

}
