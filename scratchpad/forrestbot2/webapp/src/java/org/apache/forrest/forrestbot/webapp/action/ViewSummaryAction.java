package org.apache.forrest.forrestbot.webapp.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.forrest.forrestbot.webapp.Constants;
import org.apache.forrest.forrestbot.webapp.util.Project;

public final class ViewSummaryAction extends BaseAction {
	private static Logger log = Logger.getLogger(ViewSummaryAction.class);

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		super.execute(mapping, form, request, response);

		response.setHeader("Pragma", "no-cache");

		request.setAttribute("projects", Project.getAllProjects());
		request.setAttribute("serverTime", new Date());
		
		log.debug("set summary beans");


		//MessageResources messages = getResources(request);

		if (form != null && !PropertyUtils.getSimpleProperty(form, "submit").equals("unsubmitted")) {
			log.debug("processing form");
			ActionErrors errors = form.validate(mapping, request);
			
			String username =
				(String) PropertyUtils.getSimpleProperty(form, "username");
			String password =
				(String) PropertyUtils.getSimpleProperty(form, "password");

			if (!username.equals("admin")) {
				log.debug("not admin");
				errors.add(
					"username",
					new ActionError("error.authorization"));
				saveErrors(request, errors);
				return mapping.findForward(Constants.FORWARD_NAME_FAILURE);
			} else if (!password.equals("asdf")) {
				log.debug("bad password");
				errors.add(
					"password",
					new ActionError("error.authentication"));
				saveErrors(request, errors);
				return mapping.findForward(Constants.FORWARD_NAME_FAILURE);
			} else {
				log.debug("authenticated");
				return mapping.findForward(Constants.FORWARD_NAME_AUTHORIZED);
			}
		}

		return mapping.findForward(Constants.FORWARD_NAME_SUCCESS);

	}
}
