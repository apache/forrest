package org.apache.forrest.forrestbot.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.forrest.forrestbot.webapp.Constants;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public final class LogoutAction extends BaseAction {
	private static Logger log = Logger.getLogger(LogoutAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		super.execute(mapping, form, request, response);

		// session
		request.getSession(true).removeAttribute("auth");
		
		// request & application for good measure
		request.removeAttribute("auth");
		request.getSession(true).getServletContext().removeAttribute("auth");
		
		return mapping.findForward(Constants.FORWARD_NAME_SUCCESS);
	}
}
