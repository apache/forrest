package org.apache.forrest.eclipse.job;

import org.apache.log4j.Logger;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void buildFinished(BuildEvent event) {
		logger.debug("Ant Finished Build: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void buildStarted(BuildEvent event) {
		logger.debug("Ant Started Build: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
	 */
	public void messageLogged(BuildEvent event) {
		logger.debug("Anr Message: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void targetFinished(BuildEvent event) {
		logger.debug("Ant Target Finished: " + event.getMessage());
	}
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void targetStarted(BuildEvent event) {
		logger.debug("Ant Target Started: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
	 */
	public void taskFinished(BuildEvent event) {
		logger.debug("Ant Task Finished: " + event.getMessage());
	}
	
	/* (non-Javadoc)
	 * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
	 */
	public void taskStarted(BuildEvent event) {
		logger.debug("Ant Task Started: " + event.getMessage());

	}
}
