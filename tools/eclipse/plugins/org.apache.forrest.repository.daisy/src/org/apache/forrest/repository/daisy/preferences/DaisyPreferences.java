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
package org.apache.forrest.repository.daisy.preferences;



import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog.
 * This page is used to set the preferences for Daisy Repository.
 */

public class DaisyPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public static final String REPOSITORY_URL = "REPOSITORY_URL";
	public static final String REPOSITORY_PORT = "REPOSITORY_PORT";
	public static final String REPOSITORY_USERNAME = "REPOSITORY_USERNAME";
	public static final String REPOSITORY_PASSWORD = "REPOSITORY_PASSWORD";
	
	

	public DaisyPreferences() {
		super(GRID);
		setPreferenceStore(org.apache.forrest.repository.daisy.DaisyPlugin.getDefault().getPreferenceStore());
		setDescription("Configuration Preferences for Diasy Repository");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new StringFieldEditor(REPOSITORY_URL, "&Repository Location: http://",
				getFieldEditorParent()));
		addField(new StringFieldEditor(REPOSITORY_PORT, "&Repository Port:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(REPOSITORY_USERNAME, "&Repository Username:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(REPOSITORY_PASSWORD, "&Repository Password:",
				getFieldEditorParent()));

	}

	public void init(IWorkbench workbench) {
		setPreferenceStore(org.apache.forrest.repository.daisy.DaisyPlugin.getDefault().getPreferenceStore());
	}
	
}
