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
package org.apache.forrest.eclipse.wizards;

import org.apache.forrest.eclipse.actions.Utilities;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Create a new Content Package project.
 */

public class ModifyPluginsWizard extends Wizard implements INewWizard {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(ModifyPluginsWizard.class);

    private ActivatePluginsPage pluginPage;

    private SiteOptionsPage siteOptionsPage;

    private String path;

    /**
     * Constructor for ModifyProjectWizard.
     * 
     * @param activeProject
     */
    public ModifyPluginsWizard(String newPath) {
        super();
        setWindowTitle("Modify Plugin Selection");
        path = newPath;
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages() {

        pluginPage = new ActivatePluginsPage();
        pluginPage.setTitle("Modify Plugin Selection");
        pluginPage.setDescription("Modify Plugin Selectiion");
        addPage(pluginPage);

    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
        if (pluginPage.getActivateViewValue()) {
            Utilities.activateForrestProperty(path + "forrest.properties",
                    "project.skin=leather-dev");
            Utilities
                    .addForrestPluginProperty(
                            path + "\\forrest.properties",
                            "org.apache.forrest.plugin.output.viewHelper.xhtml,org.apache.forrest.plugin.internal.view");
        }
        if (pluginPage.getSelectedPlugins() != null) {
            Utilities.addForrestPluginProperty(path + "forrest.properties",
                    pluginPage.getSelectedPlugins());
        }

        return true;
    }

    /**
     * This updated a configuration file based on the settings in siteconfig.xml
     * 
     */

    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

}