package org.apache.forrest.forrestbot.webapp.action;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.forrest.forrestbot.webapp.Config;
import org.apache.forrest.forrestbot.webapp.Constants;
import org.apache.forrest.forrestbot.webapp.dto.ProjectDTO;
import org.apache.forrest.forrestbot.webapp.util.Project;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionError;

public final class ViewLogBodyAction extends BaseAction {
	private static Logger log = Logger.getLogger(ViewLogBodyAction.class);

	ActionErrors errors;

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		super.execute(mapping, form, request, response);

		errors = new ActionErrors();

		String refreshrate = Config.getProperty("refreshrate");
		String project = request.getParameter("project");
		String logfile = null;

		// security checks
		checkProjectExists(project);
		if (errors.isEmpty()) {
			logfile = Config.getProperty("logs-dir") + "/" + project + ".log";
			log.debug(logfile);
			File f = new File(logfile);
			if (!f.isFile()) {
				log.warn("couldn't find file: " + logfile);
				errors.add(
					"logfile",
					new ActionError("logfile.file.invalid", logfile));
			}
		}
		log.debug("errors: " + errors.size());
		saveErrors(request, errors);

		response.addHeader("Pragma", "no-cache");
		response.addHeader("Refresh", refreshrate);

		//request.setAttribute("refreshrate", refreshrate);
		if (errors.isEmpty())
			request.setAttribute("logfile", logfile);
		else
			request.setAttribute("logfile", null);

		return mapping.findForward(Constants.FORWARD_NAME_SUCCESS);

	}

	private void checkProjectExists(String project) {
		Collection c = Project.getAllProjects();
		boolean foundProject = false;
		for (Iterator i = c.iterator(); i.hasNext();) {
			if (((ProjectDTO)i.next()).getName().equals(project)) {
				foundProject = true;
				break;
			}
		}
		if (!foundProject) {
			log.warn("project doesn't exist: " + project);
			errors.add(
				"logfile",
				new ActionError("logfile.project.invalid", project));
		}
	}
}
