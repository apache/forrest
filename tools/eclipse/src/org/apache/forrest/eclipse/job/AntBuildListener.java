package org.apache.forrest.eclipse.job;

import java.net.URL;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * A listener for Ant Builds, all events are logged to the standard logger.
 */
public class AntBuildListener implements BuildListener{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(AntBuildListener.class);

	/**
	 * 
	 */
	public AntBuildListener() {
		super();
		Bundle bundle = Platform.getBundle(ForrestPlugin.ID);
		URL log4jConf = Platform.find(bundle, new Path("conf/log4j.xml"));
		DOMConfigurator.configure(log4jConf);
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void buildFinished(BuildEvent event) {
		logger.info("Ant Finished Build: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void buildStarted(BuildEvent event) {
		logger.info("Ant Started Build: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
	 */
	public void messageLogged(BuildEvent event) {
		logger.info("Ant Message: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void targetFinished(BuildEvent event) {
		logger.info("Ant Target Finished: " + 
				event.getTarget().getName());
	}
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void targetStarted(BuildEvent event) {
		logger.info("Ant Target Started: " + 
				event.getTarget().getName());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void taskFinished(BuildEvent event) {
		logger.debug("Ant Task Finished: " + event.getTask().getTaskName());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void taskStarted(BuildEvent event) {
		logger.debug("Ant Task Started: " + event.getTask().getTaskName());

	}
}
