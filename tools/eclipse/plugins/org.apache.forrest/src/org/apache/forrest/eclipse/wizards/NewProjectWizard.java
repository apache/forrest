/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.actions.Utilities;
import org.apache.forrest.eclipse.views.DOMUtilities;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Create a new Content Package project.
 */

public class NewProjectWizard extends Wizard implements INewWizard {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(NewProjectWizard.class);

	private WizardNewProjectCreationPage page;

	private ActivatePluginsPage pluginPage;

	private SiteOptionsPage siteOptionsPage;

	private String projectPath;

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
		String projectPath = page.getLocationPath().toOSString();
		addPage(page);
		siteOptionsPage = new SiteOptionsPage();
		siteOptionsPage.setTitle("Site Options");
		siteOptionsPage.setDescription("Site Options ");
		addPage(siteOptionsPage);
		pluginPage = new ActivatePluginsPage(projectPath);
		pluginPage.setTitle("Activate Plugins");
		pluginPage.setDescription("Activate Plugins");
		addPage(pluginPage);

	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 */
	public boolean performFinish() {
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				finishPage(monitor);
			}
		};
		try {
			getContainer().run(false, true, op);
		} catch (InvocationTargetException e) {
            logger.error("Failed to correctly setup the project", e);
			return false; // FIXME: report this error to the user
		} catch (InterruptedException e) {
            logger.debug("Project setup appears to have been cancelled", e);
			return false; // canceled
		}
		return true;
	}

	private void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		int exitValue = -1;
		try {
			String strName = page.getProjectName();
			monitor.beginTask("Creating " + strName + " Forrest Project", 3);

			IProject project = page.getProjectHandle();
			IPath locationPath = page.getLocationPath();

			// create the project
			IProjectDescription desc = project.getWorkspace()
					.newProjectDescription(project.getName());
			if (!page.useDefaults()) {
				desc.setLocation(locationPath);
			}
			project.create(desc, new SubProgressMonitor(monitor, 1));
			project.open(new SubProgressMonitor(monitor, 1));

			// seed the project
			ForrestPlugin plugin = ForrestPlugin.getDefault();

			String strPath = locationPath.toOSString();
			String cmdString = null;

			if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
				cmdString = "forrest -Dbasedir=" + strPath + "/" + strName
						+ " seed";
			} else if (System.getProperty("os.name").toLowerCase().startsWith(
					"windows")) {
				cmdString = "cmd /c forrest -Dbasedir=" + strPath + "\\"
						+ strName + " seed";
			}

			try {
				String lineRead = null;
				Process seedProc = Runtime.getRuntime().exec(cmdString);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(seedProc.getInputStream()));
				while ((lineRead = reader.readLine()) != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("finishPage(IProgressMonitor)" + lineRead);
					}
				}
				exitValue = seedProc.exitValue();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("finishPage(IProgressMonitor)", e);
			}

			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);

			if (pluginPage.getActivateViewValue()) {
				Utilities.activateForrestProperty(strPath + "\\" + strName
						+ "\\forrest.properties", "project.skin=leather-dev");
				Utilities
						.addForrestPluginProperty(
								strPath + "\\" + strName
										+ "\\forrest.properties",
								"org.apache.forrest.plugin.output.viewHelper.xhtml,org.apache.forrest.plugin.internal.view");
			}
			if (pluginPage.getSelectedPlugins() != null ) {
			  Utilities.addForrestPluginProperty(strPath + "\\" + strName
					+ "\\forrest.properties", pluginPage.getSelectedPlugins());
            }
			updateConfig(strPath + "/" + strName + "/");
		} finally {
			monitor.done();
		}
	}


	/**
	 * This updated a configuration file based on the settings in siteconfig.xml  
	 * 
	 */
	public void updateConfig(String path) {
		Document document = siteOptionsPage.getOptionsValue();
		NodeList configList = document.getElementsByTagName("configFile");
		for (int y = 0; y < configList.getLength(); y++) {
			Element config = (Element) configList.item(y);
			Document configDoc = DOMUtilities.loadDOM(path
					+ config.getAttribute("location"));
			NodeList itemList = document.getElementsByTagName("field");
			for (int x = 0; x < itemList.getLength(); x++) {
				Element oneItem = (Element) itemList.item(x);
				String argument1 = oneItem.getAttribute("tag");
				NodeList configNodes = configDoc.getElementsByTagName(argument1);
				for (int i = 0; i < configNodes.getLength(); i++) {
					Element configItem = (Element) configNodes.item(i);
					String argument2 = configItem.getNodeName();
					if (argument1.equals(argument2)){
						Text value = (Text) configItem.getFirstChild();
						value.setData(oneItem.getAttribute("default"));
					}
				}
				
			}
			
					DOMUtilities.SaveDOM(configDoc, path
					+ config.getAttribute("location"));
			// TODO: change to the perspective specified in the plugin.xml
		}

	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}