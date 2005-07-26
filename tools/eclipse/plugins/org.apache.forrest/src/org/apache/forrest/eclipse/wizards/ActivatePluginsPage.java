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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * The "Activate Plugin" wizard page allows you to select Plugins that  
 * you would like to use in a particular project
 * 
 * 
 */

public class ActivatePluginsPage extends WizardPage {
    
	private Button activateView;
	

	/**
	 * Create the new page.
	 * @param selection 
	 * @param selection 
	 * @param pageName
	 */
	public ActivatePluginsPage() {
		super("wizardPage");
		setTitle("ActivatePlugins");
		setDescription("This allows you to activate plugins for your new Forrest Project.");
		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
	    container.setLayout(new GridLayout(2, false));
	    new Label(container, SWT.NULL).setText("Activate Views ");
	    activateView = new Button (container, SWT.CHECK);
	    activateView.setSelection(false);
	    setControl(container);
	   	
	}
	
	public boolean getActivateViewValue() {
		return activateView.getSelection();
	}
	
	
}