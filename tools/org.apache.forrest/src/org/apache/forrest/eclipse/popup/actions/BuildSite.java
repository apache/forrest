/*
 * Copyright 1999-2004 The Apache Software Foundation or its licensors,
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
package org.apache.forrest.eclipse.popup.actions;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.job.ForrestManager;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
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

public class BuildSite 
implements IObjectActionDelegate, IJavaLaunchConfigurationConstants {

	private IProject activeProject;
	
	/**
	 * Constructor for Action1.
	 */
	public BuildSite() {
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
		String cmdString = null;
		IPath path = JavaCore.getClasspathVariable("ECLIPSE_HOME");

		// TODO: move preferences code to utilities class
		String fhome = ForrestPlugin.getDefault().getPluginPreferences()
				.getString(ForrestPreferences.FORREST_HOME);
		
		if (fhome.equals("")) {
			Shell dialog = new Shell(new Shell());
			dialog.setText("Configure Forrest");
			dialog.setSize(400, 100);
			Label statusMsg = new Label(dialog, SWT.NONE);
			statusMsg
					.setText("Please configure Forrest by providing values for the required preferences");
			statusMsg.setLocation(30, 25);
			statusMsg.pack();
			// TODO: Add an OK button
			dialog.open();
			// TODO: open the properties editor
			return;
		}

		IPath workingDirectory = activeProject.getLocation();
		
		Job forrest = ForrestManager.getInstance().getForrestJob(workingDirectory.toOSString(), ForrestManager.COMMAND_BUILD);
		forrest.setUser(true);
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

}