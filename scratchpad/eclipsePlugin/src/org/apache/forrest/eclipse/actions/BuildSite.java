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
package org.apache.forrest.eclipse.actions;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class BuildSite implements IWorkbenchWindowActionDelegate,
		IJavaLaunchConfigurationConstants {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public BuildSite() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @param action
	 * @throws NullPointerException
	 * 
	 *  
	 */
	public void run(IAction action) throws NullPointerException {
		String cmdString = null;
		IPath path = JavaCore.getClasspathVariable("ECLIPSE_HOME");
		Shell shell = window.getShell();
		Shell dialog = new Shell(shell);

		// FIXME: move preferences code to utilities class
		String fhome = ForrestPlugin.getDefault().getPluginPreferences()
				.getString(ForrestPreferences.FORREST_HOME);

		// TODO: Working diretory should not be a property it should be whatever project directory Eclipse is currently working with
		String wdir = ForrestPlugin.getDefault().getPluginPreferences()
				.getString(ForrestPreferences.WORKING_DIR);
		
		if (fhome.equals("") || wdir.equals("")) {
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

		dialog.setText("Forrest Server");
		dialog.setSize(500, 250);
		Label statusMsg = new Label(dialog, SWT.NONE);
		StringBuffer sb = new StringBuffer("Forrest server is starting.\n");
		sb.append("\n\nPlease wait...");
		statusMsg.setText(sb.toString());
		statusMsg.setLocation(30, 25);
		statusMsg.pack();
		dialog.open();

		IPath workingDirectory = new Path(wdir);

		if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
			cmdString = "forrest -Dbasedir=" + workingDirectory
					+ " site";
			Utilities.RunExtCommand(cmdString);
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			cmdString = "cmd /c forrest -Dbasedir=" + workingDirectory
					+ " site";
			Utilities.RunExtCommand(cmdString);
		}		
		
		dialog.close();
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @param action
	 * @param selection
	 * 
	 *  
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @param window
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}
