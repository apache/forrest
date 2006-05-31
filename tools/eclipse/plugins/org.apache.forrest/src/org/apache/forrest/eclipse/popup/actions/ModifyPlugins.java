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
package org.apache.forrest.eclipse.popup.actions;

import org.apache.forrest.eclipse.wizards.ModifyPluginsWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class ModifyPlugins implements IObjectActionDelegate,
        IJavaLaunchConfigurationConstants {

    private IProject activeProject;

    private Shell parent;

    protected String projectName;

    protected String projectPath;

    /**
     * Constructor for Action1.
     */
    public ModifyPlugins() {
        super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        parent = targetPart.getSite().getShell();
    }

    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        ModifyPluginsWizard elementCreation_ = new ModifyPluginsWizard(projectPath);
        elementCreation_.init(PlatformUI.getWorkbench(), null); // initializes
        // the
        // wizard
        WizardDialog dialog = new WizardDialog(parent, elementCreation_);
        dialog.open();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // REFACTOR: see http://issues.apache.org/jira/browse/FOR-599
        Object first = ((IStructuredSelection) selection).getFirstElement();
        IResource resource = (IResource) first;
        if (resource != null) {
            IProject newActiveProject = resource.getProject();
            if (newActiveProject != activeProject) {
                // TODO: only attempt to load config file if this is a Forrest
                // project
                activeProject = newActiveProject;
                projectName = activeProject.getProject().getName();
                projectPath = (activeProject.getProject().getLocation().toString() + java.io.File.separator);
            }
        }
    }

}
