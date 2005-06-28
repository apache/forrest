/*
 * Copyright 1999-2005 The Apache Software Foundation or its licensors,
 * as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.eclipse.job;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.actions.Utilities;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;
import org.burrokeet.servletEngine.Jetty;
import org.burrokeet.servletEngine.Server;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.wst.internet.webbrowser.internal.Trace;
import org.eclipse.wst.internet.webbrowser.internal.WebBrowserUIPlugin;

/**
 * Run a version of Forrest
 */
public class ForrestRunner extends ForrestJob implements
        IJavaLaunchConfigurationConstants {
    /**
     * Logger for this class
     */
    protected static final Logger logger = Logger
            .getLogger(ForrestRunner.class);

    private static final int EXCEPTION_UNABLE_TO_START = 2010;

    private String workingDir;

    private static final int BROWSER_ERROR = 200;

    private static final int IO_EXCEPTION = 300;

    /**
     * Create a Forrest runner that will run a Jetty server on a given directory
     * 
     * @param workingDir -
     *            the working directory for the command
     */
    protected ForrestRunner(String workingDir) {
        super("Forrest Runner");

        this.workingDir = workingDir;
    }

    /*
     * Run the Forrest server in a separate thread and return that thread to the
     * Forrest manager.
     * 
     * @see java.lang.Runnable#run() @refactor lots of potential to tidy up this
     *      code, for example extract a few methods, move relevant code to
     *      ForrestManager
     */
    public IStatus run(IProgressMonitor monitor) {
        IStatus status = Status.OK_STATUS;
        // FIXME: PORT should be retrieved from properties
        int port = 8888;

        if (Utilities.isPortFree(port)) {

            if (logger.isDebugEnabled()) {
                logger.debug("run(IProgressMonitor) - start");
            }

            monitor.subTask("Initialising project");
            String fhome = ForrestPlugin.getDefault().getPluginPreferences()
                    .getString(ForrestPreferences.FORREST_HOME);
            StringBuffer sb = new StringBuffer("-Dproject.home=");
            sb.append(workingDir);
            sb.append(" -Dbasedir=");
            sb.append(fhome + File.separatorChar + "main");
            sb.append(" ");
            sb.append("init");
            status = runAnt(monitor, sb.toString());

            monitor.subTask("Starting Server");
            if (status.isOK()) {
                ForrestManager forrestManager = ForrestManager.getInstance();
                String confPath = ForrestManager.FORREST_CORE_WEBAPP
                        + File.separatorChar + "jettyconf.xml";
                try {
                    Server jetty = new Jetty(confPath,
                            ForrestManager.FORREST_CORE_WEBAPP,
                            ForrestManager.FORREST_ENDORSED_LIB, forrestManager
                                    .getProperties(workingDir), ForrestManager
                                    .getClasspathFiles());
                    status = jetty.start(monitor);
                    if (status.isOK()) {
                        ForrestManager.setServerLaunch(jetty.getLaunch());
                    } else {
                        ForrestManager.setServerLaunch(null);
                    }
                } catch (IOException e) {
                    logger.error("run(IProgressMonitor)", e);
                    status = new Status(Status.ERROR, ForrestPlugin.ID,
                            IO_EXCEPTION, "Unable to start Jetty server", e);
                }
            } else {
                return status;
            }

            // FIXME: Timeout delay should be a preference
            long endTime = System.currentTimeMillis() + (1000 * 30);
            while (Utilities.isPortFree(port)
                    && endTime < System.currentTimeMillis()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    // no problem (I think ;-))
                }
            }

            if (!Utilities.isPortFree(port)) {
                status = new Status(
                        Status.WARNING,
                        ForrestPlugin.ID,
                        EXCEPTION_UNABLE_TO_START,
                        "Server did not start within specified timeout period, have not tried to open browser.",
                        null);
                return status;
            }

            if (!openBrowser(monitor)) {
                status = new Status(Status.WARNING, ForrestPlugin.ID,
                        BROWSER_ERROR, "Unable to open browser", null);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("run(IProgressMonitor) - end");
            }
        } else {
            status = new Status(Status.WARNING, ForrestPlugin.ID,
                    EXCEPTION_UNABLE_TO_START, "Unable to start Forrest, port "
                            + port + " already in use", null);
        }
        return status;
    }

    /**
     * Open a web browser on the index page.
     * 
     * @param monitor -
     *            the progress monitor for this job
     * @return boolean - true if browser opened OK
     */
    private boolean openBrowser(IProgressMonitor monitor) {

        monitor.subTask("Open index page");
        // FIXME: port should come from the config files
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                URL url;
                try {
                    url = new URL("http://localhost:8888");
                    IWorkbenchBrowserSupport browserSupport = WebBrowserUIPlugin
                            .getInstance().getWorkbench().getBrowserSupport();
                    IWebBrowser browser = browserSupport.createBrowser(
                            IWorkbenchBrowserSupport.LOCATION_BAR
                                    | IWorkbenchBrowserSupport.NAVIGATION_BAR,
                            null, null, null);
                    browser.openURL(url);
                } catch (Exception e) {
                    Trace.trace(Trace.SEVERE, "Error opening browser", e);
                }
            }
        });

        monitor.worked(3);
        return true;
    }
}