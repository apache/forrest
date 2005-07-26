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
package org.apache.forrest.eclipse.views;

import org.apache.forrest.eclipse.actions.Utilities;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;

/**
 * An abstract class that provides common functionality for views that are 
 * used to edit navigation documents such as site.xml and tabs.xml.
 */
public abstract class NavigationView extends ViewPart implements IMenuListener,
        ISelectionListener {
    protected TreeViewer treeViewer;
    protected Document document;
    protected String projectName;
    protected String path;
    protected String xDocPath;
    protected IStructuredSelection treeSelection;
    protected IProject activeProject;

    /**
     * 
     */
    public NavigationView() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow(IMenuManager manager) {
        // TODO Auto-generated method stub

    }
    
    /**
     * When the selection in the navigator view is changed 
     * we look to see if the new selection is an IProject.
     * If it is then we load the site.xml file into this
     * SiteXMLView.
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object first = ((IStructuredSelection) selection).getFirstElement();
            IResource resource = (IResource) first;
            if (resource instanceof IProject) {
                activeProject = (IProject) resource;
                projectName = activeProject.getProject().getName();
                xDocPath = (activeProject.getProject().getLocation()
                        .toString()
                        + java.io.File.separator);
                path = xDocPath + getFilename();
                document = DOMUtilities.loadDOM(path);
                treeViewer.setInput(document);
            }
        }
    }

    /**
     * Get the anme and path of the file this editor view represents.
     * This name does not include the path. For example.
     * 'site.xml' or 'tabs.xml'
     * @return the name (without pat) of the document to view
     */
    protected abstract String getFilename();
    
   
}
