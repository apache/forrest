/*
 * Created on Feb 11, 2004
 */
package org.apache.forrest.forrestbot.webapp.form;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;


public class BaseDynaActionForm extends DynaActionForm {
	private static Logger log = Logger.getLogger(BaseDynaActionForm.class);
	
	protected boolean isEmptyString(Object o) {
		return o == null || 
			o.getClass() != String.class || 
			((String) o).trim().equals("");
	}

	protected ActionMessages checkRequiredFields(String [] fields) {
		ActionMessages errors = new ActionMessages();
		for (int i = 0; i < fields.length; i++)
		if (isEmptyString(get(fields[i]))) {
			log.debug(fields[i] + " is empty string");
			errors.add(fields[i], new ActionError("error.required", fields[i]));
		}

		return errors;
	}
}
