/*
 * Created on 13-Dec-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.forrest.eclipse.job;

import java.io.File;
import java.net.URL;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Ross Gardler
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ForrestJob extends Job {
	/**
	 * Logger for this class
	 */
	protected static final Logger logger = Logger.getLogger(ForrestJob.class);

	public static final int EXCEPTION_UNIDENTIFIED = 1001;

	/**
	 * @param name
	 */
	public ForrestJob(String name) {
		super(name);
		
		ForrestPlugin plugin = ForrestPlugin.getDefault();
		URL urlPluginDir = plugin.getBundle().getEntry("/");
		String strLog4jConf = "D:\\projects\\burrokeet\\forrestplugin\\conf\\log4j.xml";
		DOMConfigurator.configure(strLog4jConf);
	}

	protected String workingDir;

	/**
	 * @param monitor
	 * @param cmdString
	 * @return
	 */
	protected IStatus runAnt(IProgressMonitor monitor, String cmdString) {
		if (logger.isDebugEnabled()) {
			logger.debug("runAnt(IProgressMonitor, String) - start");
		}
	
		IStatus status = Status.OK_STATUS;
		
		if (cmdString != null) {
			String fhome = ForrestPlugin.getDefault().getPluginPreferences()
			  .getString(ForrestPreferences.FORREST_HOME);
			AntRunner runner = new AntRunner();
			String antFile = fhome + File.separatorChar + "main" + File.separatorChar + "forrest.build.xml";
			try {
				runner.setBuildFileLocation(antFile);
				runner.setArguments(cmdString);
				logger.info("Running ANT with command string = " + cmdString);
				runner.run(monitor);
			} catch (CoreException e) {
				logger.error("run(IProgressMonitor)", e);
				status = new Status(Status.ERROR, null, ForrestRunner.EXCEPTION_UNIDENTIFIED, "Forrest Server Exception", null);
			}
		}
	
		if (logger.isDebugEnabled()) {
			logger.debug("runAnt(IProgressMonitor, String) - end");
		}
		return status;
	}

}
