package org.apache.forrest.forrestbot.webapp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.forrest.forrestbot.webapp.Config;
import org.apache.forrest.forrestbot.webapp.Constants;

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
		
		return mapping.findForward(Constants.FORWARD_NAME_SUCCESS);
	}

}
