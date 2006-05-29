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

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.views.DOMUtilities;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The "Set Options" wizard page allows you to select configuration options for
 * the project
 * 
 * 
 */

public class SiteOptionsPage extends WizardPage {

	protected Document document;
	protected String projectName;
	protected String path;
	protected String xDocPath;
	protected IProject activeProject;
	protected Text[] textField;
	protected Button[] buttonField;
	protected Button buttonSaveDefaults;

	/**
	 * Create the new page.
	 * 
	 * @param selection
	 * @param selection
	 * @param pageName
	 */
	public SiteOptionsPage() {
		super("wizardPage");
		setTitle("SetOptions");
		setDescription("This allows you to set options for your new Forrest Project.");

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

		// path = ForrestPlugin.getDefault().getStateLocation()+
		// "/conf/siteconfig.xml";
		path = ForrestPlugin.getDefault().getPluginPreferences().getString(
				"FORREST_HOME")
				+ "/tools/eclipse/plugins/org.apache.forrest/conf/siteconfig.xml";
		document = DOMUtilities.loadDOM(path);
		NodeList itemList = document.getElementsByTagName("field");
		textField = new Text[50];
		buttonField = new Button[50];
				 
		if (itemList.getLength() > 0) {
			for (int i = 0; i < itemList.getLength(); i++) {
				Element oneItem = (Element) itemList.item(i);
				Label label = new Label(container, SWT.NONE);
				label.setText(oneItem.getAttribute("label"));
				GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
				label.setLayoutData(data);
				if (oneItem.getAttribute("type").equals("text")) {
					data = new GridData(GridData.FILL_HORIZONTAL);
					textField[i] = new Text(container, SWT.NONE);
					textField[i].setText(oneItem.getAttribute("default"));
					textField[i].setToolTipText(oneItem
							.getAttribute("description"));
					textField[i].setLayoutData(data);
					
				}
				if (oneItem.getAttribute("type").equals("boolean")) {
					data = new GridData(GridData.HORIZONTAL_ALIGN_END);
					buttonField[i] = new Button(container, SWT.CHECK);
					if (oneItem.getAttribute("default").equals("true")) {
					buttonField[i].setSelection(true);}
					else {
						buttonField[i].setSelection(false);
					}
					buttonField[i].setToolTipText(oneItem
							.getAttribute("description"));
					buttonField[i].setLayoutData(data);
				}
			}
		}
		Label label = new Label(container, SWT.NONE);
		label.setText("Save these selections as your default settings");
		buttonSaveDefaults = new Button(container, SWT.CHECK);
		container.pack();
		setControl(container);

	}

	public Document getOptionsValue() {
		NodeList itemList = document.getElementsByTagName("field");
		if (itemList.getLength() > 0) {
			for (int i = 0; i < itemList.getLength(); i++) {
				Element oneItem = (Element) itemList.item(i);
				if (oneItem.getAttribute("type").equals("text")) {
				oneItem.setAttribute("default",textField[i].getText());
				}
				if (oneItem.getAttribute("type").equals("boolean")) {
					if (buttonField[i].getSelection()) {
						oneItem.setAttribute("default","true");}
						else {
							oneItem.setAttribute("default","false");
						}
				}
			}
		}
		if (buttonSaveDefaults.getSelection()) {
		DOMUtilities.SaveDOM(document, path);
		}
		return document;
	}

}