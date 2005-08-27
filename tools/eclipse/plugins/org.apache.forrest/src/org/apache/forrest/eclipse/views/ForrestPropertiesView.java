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

import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * A table view for forrest.properties files. 
 */
public class ForrestPropertiesView extends NavigationView {
	
	private Table table;
	public static TableViewer tableViewer;
	public static Element editorElement;
	
	//	 Set the table column property names
	private final String PROPERTY_NAME_COLUMN 	= "Property";
	private final String PROPERTY_VALUE_COLUMN 	= "Value";
	
	


	// Set column names
	private String[] columnNames = new String[] { 
			 
			PROPERTY_NAME_COLUMN,
			PROPERTY_VALUE_COLUMN
			
			};
	
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

		

		table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		
		// 1st column with Property
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("PROPERTY");
		column.setWidth(100);
		// Add listener to column so tasks are sorted by description when clicked 
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				
			}
		});


		// 2nd column with Value
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("VALUE");
		column.setWidth(100);
		// Add listener to column so tasks are sorted by owner when clicked
		column.addSelectionListener(new SelectionAdapter() {
       	
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		
	}
	 public static  Vector getContent(Element element){
		 Vector objectList =  new Vector(); 
		 if (element != null) {
		 NamedNodeMap atrs = element.getAttributes();
		 if(atrs != null) 
			 for(int i=0; i<atrs.getLength(); i++) objectList.add(new EditableTableItem(atrs.item(i).getNodeName(),atrs.item(i).getNodeValue()));
		 }
		 return objectList; 
	      
	 };
	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[0] = textEditor;
		
		// Column 3 : Owner (Combo Box) 
		editors[1] = textEditor;

			// Assign the cell editors to the viewer 
		tableViewer.setCellEditors(editors);
		
		tableViewer.setContentProvider(new IStructuredContentProvider() {
		      public Object[] getElements(Object inputElement) {
		    	  Vector v = (Vector)inputElement;
		          return v.toArray();
		      }

		      public void dispose() {
		      }

		  

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				// TODO Auto-generated method stub
				
			}
    });
		 
		tableViewer.setLabelProvider(new ITableLabelProvider() {
		      public Image getColumnImage(Object element, int columnIndex) {
		        return null;
		      }

		      public String getColumnText(Object element, int columnIndex) {
		        switch (columnIndex) {
		        case 0:
		          return ((EditableTableItem) element).name;
		        case 1:
		          return ((EditableTableItem) element).value;
		          
		        default:
		          return "Invalid column: " + columnIndex;
		        }
		      }

		      public void dispose() {
		      }

		      public boolean isLabelProperty(Object element, String property) {
		        return false;
		      }

			public void addListener(ILabelProviderListener listener) {
				
			}

			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

		    
		    });
		   
		 tableViewer.setCellModifier(new ICellModifier() {
		      public boolean canModify(Object element, String property) {
		        return true;
		      }

		      public Object getValue(Object element, String property) {
		        if (property.equals("name"))
		          return ((EditableTableItem) element).name;
		        else
		          return ((EditableTableItem) element).value;
		      }

		      public void modify(Object element, String property, Object value) {
		        TableItem tableItem = (TableItem) element;
		        EditableTableItem data = (EditableTableItem) tableItem
		            .getData();
		        if (property.equals("name"))
		            data.name = value.toString();
		          else
		            data.value = value.toString();
		        
		        if (editorElement != null) {
					System.out.println(editorElement.getNodeName());}
					
		       
		        tableViewer.refresh(data);
		      }
		    });
		tableViewer.setInput(getContent(editorElement));
	
	}

	private static class EditableTableItem {
		  public String name;

		  public String value;

		  public EditableTableItem(String n, String v) {
		    name = n;
		    value = v;
		  }
	}
	
	public static void setElement(Element newElement) { 
	editorElement = newElement;	
	};
	
	public static void refreshTree() { 
		tableViewer.setInput(getContent(editorElement));
		};
}
