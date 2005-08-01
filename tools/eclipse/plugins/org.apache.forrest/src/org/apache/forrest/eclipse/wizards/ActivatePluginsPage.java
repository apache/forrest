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


import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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
	private ListViewer availablePluginsListViewer;
	private Button removeButton;
	private Label activateLabel;
	private ListViewer selectedPluginsListViewer;
	private IStructuredSelection selection ;
	private Vector selectedPlugins;
	
	
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
	 * Returns a list of plugins available for use with Forrest 
	 * 
	 */ 
	 private Vector getAvailablePlugins() {
		
		Vector availablePlugins = new Vector();
		availablePlugins.add("org.apache.forrest.plugin.input.dtdx");
		availablePlugins.add("org.apache.forrest.plugin.input.excel");
		availablePlugins.add("org.apache.forrest.plugin.input.feeder");
		availablePlugins.add("org.apache.forrest.plugin.input.listLocations");
		availablePlugins.add("org.apache.forrest.plugin.input.OpenOffice.org");
		availablePlugins.add("org.apache.forrest.plugin.input.PhotoGallery");
		availablePlugins.add("org.apache.forrest.plugin.input.projectInfo");
		availablePlugins.add("org.apache.forrest.plugin.input.simplifiedDocbook");
		availablePlugins.add("org.apache.forrest.plugin.input.wiki");
		availablePlugins.add("org.rblasch.forrest.plugin.input.pod");
		availablePlugins.add("org.apache.forrest.plugin.output.pdf");
		availablePlugins.add("s5");
		availablePlugins.add("org.apache.forrest.plugin.internal.IMSManifest");
		availablePlugins.add("org.apache.forrest.plugin.input.Daisy");
		availablePlugins.add("org.apache.forrest.plugin.input.logs");
		availablePlugins.add("org.apache.forrest.plugin.output.Chart");
		availablePlugins.add("org.apache.forrest.plugin.output.htmlArea");
		availablePlugins.add("org.apache.forrest.plugin.internal.view");
	    return availablePlugins;
	
	}
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		parent.setSize(593, 364);
		Composite container = new Composite(parent, SWT.NULL);
		FormLayout containerLayout = new FormLayout();
	    container.setLayout(containerLayout);
		activateLabel = new Label(container, SWT.NULL);
		FormData activateLabelPosition = new FormData();
		activateLabelPosition.width = 73;
		activateLabelPosition.height = 13;
		activateLabelPosition.left =  new FormAttachment(9, 1000, 0);
		activateLabelPosition.right =  new FormAttachment(132, 1000, 0);
		activateLabelPosition.top =  new FormAttachment(17, 1000, 0);
		activateLabelPosition.bottom =  new FormAttachment(53, 1000, 0);
		activateLabel.setLayoutData(activateLabelPosition);
		activateLabel.setText("Activate Views ");
		activateView = new Button (container, SWT.CHECK);
	    FormData activateViewPosition = new FormData();
	    activateViewPosition.width = 13;
	    activateViewPosition.height = 16;
	    activateViewPosition.left =  new FormAttachment(140, 1000, 0);
	    activateViewPosition.right =  new FormAttachment(162, 1000, 0);
	    activateViewPosition.top =  new FormAttachment(15, 1000, 0);
	    activateViewPosition.bottom =  new FormAttachment(59, 1000, 0);
	    activateView.setLayoutData(activateViewPosition);
	    activateView.setSelection(false);
	    selectedPlugins = new Vector();
	    availablePluginsListViewer = new ListViewer(container);
	    availablePluginsListViewer.setContentProvider(new IStructuredContentProvider() {
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
	    
	    FormData availablePluginsListViewerPosition = new FormData();
	    availablePluginsListViewerPosition.width = 235;
	    availablePluginsListViewerPosition.height = 305;
	    availablePluginsListViewerPosition.left =  new FormAttachment(17, 1000, 0);
	    availablePluginsListViewerPosition.right =  new FormAttachment(419, 1000, 0);
	    availablePluginsListViewerPosition.top =  new FormAttachment(86, 1000, 0);
	    availablePluginsListViewerPosition.bottom =  new FormAttachment(924, 1000, 0);
	    availablePluginsListViewer.getControl().setLayoutData(availablePluginsListViewerPosition);
	    availablePluginsListViewer.setInput(getAvailablePlugins());
		
	    Button addButton = new Button(container, SWT.PUSH);
		FormData addButtonPosition = new FormData();
		addButtonPosition.width = 42;
		addButtonPosition.height = 23;
		addButtonPosition.left =  new FormAttachment(466, 1000, 0);
		addButtonPosition.right =  new FormAttachment(537, 1000, 0);
		addButtonPosition.top =  new FormAttachment(199, 1000, 0);
		addButtonPosition.bottom =  new FormAttachment(262, 1000, 0);
		addButton.setLayoutData(addButtonPosition);
		addButton.setText("Add->");
		addButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					for (Iterator iterator = selection.iterator(); iterator
						.hasNext();) {
						selectedPlugins.add(iterator.next());
						
					}
					    selectedPluginsListViewer.refresh();

				}
			});
		  
		
		removeButton = new Button(container, SWT.PUSH | SWT.CENTER);
		FormData removeButtonPosition = new FormData();
		removeButtonPosition.width = 62;
		removeButtonPosition.height = 23;
		removeButtonPosition.left =  new FormAttachment(447, 1000, 0);
		removeButtonPosition.top =  new FormAttachment(314, 1000, 0);
		removeButtonPosition.right =  new FormAttachment(552, 1000, 0);
		removeButtonPosition.bottom =  new FormAttachment(377, 1000, 0);
		removeButton.setLayoutData(removeButtonPosition);
		removeButton.setText("Remove ");
		removeButton.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent e) {
		        IStructuredSelection selection = (IStructuredSelection)selectedPluginsListViewer.getSelection();
		        String plugin = selection.getFirstElement().toString();
		        selectedPlugins.remove(plugin);
		        selectedPluginsListViewer.refresh();
		      }
		    });
		
		availablePluginsListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
	        public void selectionChanged(SelectionChangedEvent event) {
	          selection = (IStructuredSelection)event.getSelection();
	          }
	      });
	    
		selectedPluginsListViewer = new ListViewer(container);
	    selectedPluginsListViewer.setContentProvider(new IStructuredContentProvider() {
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
	    
	    FormData selectedPluginsListViewerPosition = new FormData();
	    selectedPluginsListViewerPosition.width = 224;
	    selectedPluginsListViewerPosition.height = 303;
	    selectedPluginsListViewerPosition.left =  new FormAttachment(570, 1000, 0);
	    selectedPluginsListViewerPosition.right =  new FormAttachment(953, 1000, 0);
	    selectedPluginsListViewerPosition.top =  new FormAttachment(72, 1000, 0);
	    selectedPluginsListViewerPosition.bottom =  new FormAttachment(905, 1000, 0);
	    selectedPluginsListViewer.getControl().setLayoutData(selectedPluginsListViewerPosition);
		selectedPluginsListViewer.setInput(selectedPlugins);
	    setControl(container);
	 
	}
	
	public boolean getActivateViewValue() {
		return activateView.getSelection();
	}
	
	public String getSelectedPlugins() {
		String pluginList = "";
		if (selectedPlugins != null) {
		  for (Iterator iterator = selectedPlugins.iterator(); iterator
		    .hasNext();) {
		    pluginList= (pluginList + iterator.next().toString() + ",");
		  } 
		}
		return pluginList.substring(0,(pluginList.length()-1)); 
	}	 
}