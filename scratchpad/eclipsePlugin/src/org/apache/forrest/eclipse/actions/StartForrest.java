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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.*;
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
public class StartForrest implements IWorkbenchWindowActionDelegate,
		IJavaLaunchConfigurationConstants {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public StartForrest() {
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
					+ " webapp-local";
			Utilities.RunExtCommand(cmdString);
		}

		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			cmdString = "cmd /c forrest -Dbasedir=" + workingDirectory
					+ " webapp-local";
			Utilities.RunExtCommand(cmdString);
		}

		try {
			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();
			ILaunchConfigurationType type = manager
					.getLaunchConfigurationType(ID_JAVA_APPLICATION);
			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
					null, "Start Jetty");

			// find the JRE used to start
			IVMInstall jre = JavaRuntime.getDefaultVMInstall();

			IPath forrestHome = new Path(fhome);

			// specify a JRE
			workingCopy.setAttribute(ATTR_VM_INSTALL_NAME, jre.getName());
			workingCopy.setAttribute(ATTR_VM_INSTALL_TYPE, jre
					.getVMInstallType().getId());

			// specify main type and program arguments
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,
					"org.mortbay.jetty.Server");
			workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, workingDirectory
					+ "/build/webapp/jettyconf.xml");

			// specify classpath
			List classpath = new ArrayList();

			String parent = ".." + File.separator;
			
			String sPath = fhome + File.separator + parent + parent + "lib";
			File searchDir = new File(sPath);
			File forrestBuildDir = new File(fhome + File.separator + parent + parent + "build");
			File jettyDir = new File(fhome + File.separator + parent + parent + "tools"
					+ File.separator + "jetty");

			try {
				//FIXME: check that the search directory exists, if it doesn't eclipse throws an unhandled loop exception
				List allfiles = Utilities.getFileListing(searchDir);
				allfiles.addAll(Utilities.getFileListing(forrestBuildDir));
				allfiles.addAll(Utilities.getFileListing(jettyDir));
				Iterator filesListing = allfiles.iterator();
				String thisFile;

				while (filesListing.hasNext()) {
					thisFile = filesListing.next().toString();

					if (thisFile.toString().toLowerCase().endsWith("jar")) { //$NON-NLS-1$
						IPath forrestCorePath = new Path(thisFile.toString());
						IRuntimeClasspathEntry forrestCoreEntry = JavaRuntime
								.newArchiveRuntimeClasspathEntry(forrestCorePath);
						forrestCoreEntry
								.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
						classpath.add(forrestCoreEntry.getMemento());
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime
					.newRuntimeContainerClasspathEntry(systemLibsPath,
							IRuntimeClasspathEntry.STANDARD_CLASSES);
			classpath.add(systemLibsEntry.getMemento());
			workingCopy.setAttribute(ATTR_CLASSPATH, classpath);
			workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, false);

			// specify working diretory
			File workingDir = workingDirectory.append("build").append("webapp")
								.toFile();
						workingCopy.setAttribute(ATTR_WORKING_DIRECTORY, workingDir
								.getAbsolutePath());
			
			workingCopy.setAttribute(ATTR_VM_ARGUMENTS, "-Dproject.home=\"" + wdir + "\"" 
					+ " -Dforrest.home=\"" + fhome + "\"");

			ILaunchConfiguration configuration = workingCopy.doSave();
			IProgressMonitor monitor = new NullProgressMonitor();
			ILaunch launch = DebugUITools.buildAndLaunch(configuration,
					ILaunchManager.RUN_MODE, monitor);
			Utilities.jetty = launch;
			dialog.close();
			shell.setFocus();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
