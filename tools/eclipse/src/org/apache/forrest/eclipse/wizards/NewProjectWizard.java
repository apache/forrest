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
package org.apache.forrest.eclipse.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * Create a new Content Package project.
 */

public class NewProjectWizard extends Wizard implements INewWizard {
	private WizardNewProjectCreationPage page;

	/**
	 * Constructor for ContentPackageWizard.
	 */
	public NewProjectWizard() {
		super();
		setWindowTitle("New Content Package");
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new WizardNewProjectCreationPage("NewProjectCreationWizard");
		page.setTitle("New");
		page.setDescription("Create a new Content Package.");
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		WorkspaceModifyOperation op= new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				finishPage(monitor);
			}
		};
		try {
			getContainer().run(false, true, op);
		} catch (InvocationTargetException e) {
			return false; // TODO: should open error dialog and log
		} catch  (InterruptedException e) {
			return false; // canceled
		}
		return true;
	}
	
	private void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		try {		
			String strName = page.getProjectName();
			monitor.beginTask("Creating "+ strName + " Forrest Project", 3);

			IProject project= page.getProjectHandle();
			IPath locationPath= page.getLocationPath();
		
			// create the project
			IProjectDescription desc= project.getWorkspace().newProjectDescription(project.getName());
			if (!page.useDefaults()) {
				desc.setLocation(locationPath);
			}
			project.create(desc, new SubProgressMonitor(monitor, 1));
			project.open(new SubProgressMonitor(monitor, 1));
			
			// seed the project
			ForrestPlugin plugin = ForrestPlugin.getDefault();
			
			String strPath = locationPath.toOSString();
			String cmdString =  null;
			
			if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
				cmdString = "forrest -Dbasedir=" + strPath + "/" + strName
						+ " seed";
			} else if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
				cmdString = "cmd /c forrest -Dbasedir=" + strPath + "\\" + strName
						+ " seed";
			}
			try {
				// TODO: if fhome is not set the wizard will fail
				Runtime.getRuntime().exec(cmdString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			
			// TODO: configure your page / nature
	
			// TODO: change to the perspective specified in the plugin.xml			
		} finally {
			monitor.done();
		}
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.burrokeet.application", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}