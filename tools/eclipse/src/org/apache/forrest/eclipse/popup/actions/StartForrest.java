/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
package org.apache.forrest.eclipse.popup.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.forrest.ForrestRunner;
import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.webbrowser.WebBrowser;
import org.eclipse.webbrowser.WebBrowserEditorInput;

public class StartForrest 
implements IObjectActionDelegate, IJavaLaunchConfigurationConstants {

	private IProject activeProject;
	
	/**
	 * Constructor for Action1.
	 */
	public StartForrest() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Shell messageDialog;
		Label statusMsg;
		if (activeProject == null) {
			return;
		}
		
		Shell shell = new Shell();
		IPath path = JavaCore.getClasspathVariable("ECLIPSE_HOME");
		// TODO: This should be a monitor messageDialog
		messageDialog = new Shell(shell);

		String fhome = ForrestPlugin.getDefault().getPluginPreferences()
				.getString(ForrestPreferences.FORREST_HOME);
		
		IPath workingDirectory = activeProject.getLocation();
		
		if (fhome.equals("")) {
			messageDialog.setText("Configure Forrest");
			messageDialog.setSize(400, 100);
			statusMsg = new Label(messageDialog, SWT.NONE);
			statusMsg
					.setText("Please configure Forrest by providing values for the required preferences");
			statusMsg.setLocation(30, 25);
			statusMsg.pack();
			// TODO: Add an OK button
			messageDialog.open();
			// TODO: open the properties editor
			return;
		}
		
		Job forrest = new ForrestStarter(workingDirectory.toOSString());
		forrest.schedule();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object first = ((IStructuredSelection)selection).getFirstElement();
            IResource resource = (IResource)first;
            if (resource instanceof IProject) {
                activeProject = (IProject)resource;
            }            
		}
	}
	
	/**
	 * Manage the starting a Forrest server in a specific working directory.
	 * Once Forrest has started open a web browser for the server.
	 */
	class ForrestStarter extends Job {
		ForrestRunner forrestRunner;
		IProgressMonitor monitor;
		
		/**
		 * Create a new Forrest starter for the given working directory.
		 * @param string
		 * @refactor Need a factory to create instances for each Working Directory, run Forrest on a different port for each 
		 */
		public ForrestStarter(String wdir) {
			super("Server Starter");
			this.setUser(true);
			forrestRunner = new ForrestRunner(wdir);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		public IStatus run(IProgressMonitor monitor) {
			this.monitor = monitor;
			monitor.beginTask("Starting Test Server", 2);
			monitor.subTask("Start Server");
			forrestRunner.start();
			while (forrestRunner.getStatus() != ForrestRunner.RUNNING
					&& forrestRunner.getStatus() != ForrestRunner.EXCEPTION) {
			}
			monitor.worked(1);
			if (forrestRunner.getStatus() != ForrestRunner.EXCEPTION) {
				//FIXME: Handle startup exception
			}
			monitor.subTask("Open index page");
			URL url;
			try {
				url = new URL("http://localhost:8888");
				WebBrowserEditorInput browserInput = new WebBrowserEditorInput(url, WebBrowserEditorInput.SHOW_ALL);
				WebBrowser.openURL(browserInput);
			} catch (MalformedURLException e1) {
				// Should never be thrown
				e1.printStackTrace();
			}		
			monitor.worked(2);
			return Status.OK_STATUS;
		}
	}
}