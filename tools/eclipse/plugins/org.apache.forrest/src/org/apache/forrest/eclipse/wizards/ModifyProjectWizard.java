/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * license agreements.  See the NOTICE file distributed with
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

import org.apache.forrest.eclipse.views.DOMUtilities;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Create a new Content Package project.
 */

public class ModifyProjectWizard extends Wizard implements INewWizard {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(ModifyProjectWizard.class);

    private WizardNewProjectCreationPage page;

    private ActivatePluginsPage pluginPage;

    private SiteOptionsPage siteOptionsPage;

    private String path;

    /**
     * Constructor for ModifyProjectWizard.
     * 
     * @param activeProject
     */
    public ModifyProjectWizard(String newPath) {
        super();
        setWindowTitle("Modify Forrest Project");
        path = newPath;
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages() {

        siteOptionsPage = new SiteOptionsPage();
        siteOptionsPage.setTitle("Modify Site Options");
        siteOptionsPage.setDescription("Modify Site Options ");
        addPage(siteOptionsPage);

    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
        updateConfig(path);

        return true;
    }

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
                NodeList configNodes = configDoc
                        .getElementsByTagName(argument1);
                for (int i = 0; i < configNodes.getLength(); i++) {
                    Element configItem = (Element) configNodes.item(i);
                    String argument2 = configItem.getNodeName();
                    if (argument1.equals(argument2)) {
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