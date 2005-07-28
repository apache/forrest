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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * A table view for forrest.properties files. 
 */
public class ForrestPropertiesView extends NavigationView {
	
	private Table table;
	private TableViewer tableViewer;
	
	//	 Set the table column property names
	private final String ACTIVE_PROPERTY 		= "Active";
	private final String PROPERTY_NAME_COLUMN 	= "Property";
	private final String PROPERTY_VALUE_COLUMN 	= "Value";
	

	// Set column names
	private String[] columnNames = new String[] { 
			ACTIVE_PROPERTY, 
			PROPERTY_NAME_COLUMN,
			PROPERTY_VALUE_COLUMN
			
			};
	// Combo box choices
	static final String[] OWNERS_ARRAY = { "?", "Nancy", "Larry", "Joe" };

	/**
	 * The constructor.
	 */
	public ForrestPropertiesView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		createTable(parent);
		createTableViewer();
	
	}

	
	private void showMessage(String message) {
		MessageDialog.openInformation(treeViewer.getControl().getShell(),
				"Sample View", message);
	}

	protected String getFilename() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		final int NUMBER_COLUMNS = 3;

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with image/checkboxes - NOTE: The SWT.CENTER has no effect!!
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);		
		column.setText("ACTIVE");
		column.setWidth(20);
		
		// 2nd column with task Description
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("PROPERTY");
		column.setWidth(400);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				
			}
		});


		// 3rd column with task Owner
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("VALUE");
		column.setWidth(100);
		// Add listener to column so tasks are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		
	}

	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];

		// Column 1 : Completed (Checkbox)
		editors[0] = new CheckboxCellEditor(table);

		// Column 2 : Description (Free text)
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[1] = textEditor;
		
		// Column 3 : Owner (Combo Box) 
		editors[2] = textEditor;

			// Assign the cell editors to the viewer 
		tableViewer.setCellEditors(editors);
	
	}
	
}
