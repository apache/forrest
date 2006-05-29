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

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Create a new Location Element project.
 */

public class NewLocationElement extends Wizard implements INewWizard {
	protected IStructuredSelection selection;
	private Document document;
	
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(NewLocationElement.class);

	private NewLocationElementPage page;

	/**
	 * Constructor for New Location Element.
	 * @param treeSelection 
	 * @param document 
	 */
	public NewLocationElement(IStructuredSelection treeSelection, Document newDocument) {
		super();
		setWindowTitle("New Element");
		setNeedsProgressMonitor(true);
		selection= treeSelection;
		document = newDocument;
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewLocationElementPage();
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
				 Node insertionElement = (Element) selection.getFirstElement();	
		    	  Element element = document.createElement("location");
		    	  element.setAttribute("src", page.getLocation());
		    	  insertionElement.appendChild(element);
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
	
	
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		
	}


	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}
}

