package org.apache.forrest.forrestbot.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.forrest.forrestbot.webapp.Constants;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;

public final class ViewLogFrameAction extends BaseAction {
	private static Logger log = Logger.getLogger(ViewLogFrameAction.class);

	ActionErrors errors = new ActionErrors();

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		super.execute(mapping, form, request, response);

		String project = request.getParameter("project");
		
		request.setAttribute("project", project);
		
		return mapping.findForward(Constants.FORWARD_NAME_SUCCESS);

	}
}
