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

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New Element" wizard page allows you to insert a 
 * new element into the site.xml file and specify the element
 * name, location, path and descriotion.
 * 
 */

public class NewSiteElementPage extends WizardPage {
    
	private Text hrefText;
	private Text descriptionText;
	private Text labelText;
	private Text elementName;
	

	/**
	 * Create the new page.
	 * @param selection 
	 * @param selection 
	 * @param pageName
	 */
	public NewSiteElementPage() {
		super("wizardPage");
		setTitle("New Site Element");
		setDescription("This wizard creates a new element in site.xml.");
		
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&HREF:");

		hrefText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		hrefText.setLayoutData(gd);
				
		label = new Label(container, SWT.NULL);
		label.setText("&Description:");

		descriptionText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		descriptionText.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("&Label:");

		labelText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		labelText.setLayoutData(gd);

		label = new Label(container, SWT.NULL);
		label.setText("&Element Name:");

		elementName = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		elementName.setLayoutData(gd);
		setControl(container);
	}
	
	public String getElementName() {
		return elementName.getText();
	}
	
	public String getDescription() {
		return descriptionText.getText();
	}
	
	public String getLabel() {
		return labelText.getText();
	}
	
	public String getHref() {
		return hrefText.getText();
	}
	
}