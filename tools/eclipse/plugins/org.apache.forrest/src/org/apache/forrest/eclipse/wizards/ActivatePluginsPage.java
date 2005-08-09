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


import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.actions.Utilities;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.forrest.eclipse.views.DOMUtilities;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	private Document document;
	protected String xDocPath;
    protected IProject activeProject;
	
	/**
	 * Create the new page.
	 * @param path 
	 * @param selection 
	 * @param selection 
	 * @param pageName
	 */
	public ActivatePluginsPage(String path) {
		super("wizardPage");
		setTitle("ActivatePlugins");
		setDescription("This allows you to activate plugins for your new Forrest Project.");
		System.out.println(path);
		xDocPath = path;
	}

	/**
	 * Retrieve a list of plugin descriptor files used by this site.
	 * @return an array of URLs pointing to plugin descriptor files
	 */
	public List getPluginDescriptorURLs(){
		String forrestHome = ForrestPlugin.getDefault().getPluginPreferences()
        .getString(ForrestPreferences.FORREST_HOME);
		List URLs = null;
		String descriptorPath = xDocPath + "/forrest.properties";
		String descriptors = Utilities.getProperty(descriptorPath,"forrest.plugins.descriptors");
		if (descriptors == null) {
			descriptorPath = forrestHome + "/main/webapp/default-forrest.properties";
			descriptors = Utilities.getProperty(descriptorPath,"forrest.plugins.descriptors");	
			
		}
		if (descriptors != null){
	    URLs = Arrays.asList( descriptors.split(",") );}
		return URLs;
		
	}
	 /**
		 * Returns a list of plugins available for use with Forrest 
		 * It checks a source online first and if that fails, it checks a local
		 * list of plugins
		 */ 
		 private Vector getAvailablePlugins() {
			 List pluginDescriptors = getPluginDescriptorURLs();
			 Vector availablePlugins = new Vector();
			 for (int x = 0; x < pluginDescriptors.size(); x++) {
		     document = DOMUtilities.loadXMLFromURL(pluginDescriptors.get(x).toString());
			if (document != null) {
			
			NodeList nl = document.getElementsByTagName("plugin");
			for (int i = 0; i < nl.getLength(); i++) {
				Element plugin = (Element) nl.item(i);
				availablePlugins.add(plugin.getAttribute("name"));
			}}
			
			}
			return availablePlugins;
	
		}
		 
		 /**
			 * Returns a list of plugins that have already been set for this project 
			 * 
			 */ 
			 private Vector getSetPlugins() {
				String propertiesPath = xDocPath + "/forrest.properties";
				String setPlugins = Utilities.getProperty(propertiesPath,"project.required.plugins"); 
				String[] pluginList = setPlugins.split(",");
				Vector v = new Vector (pluginList.length);
				for (int i=0; i < pluginList.length; i++)
				    v.addElement (pluginList[i]); 
				return  v;
		
			}
	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		FormLayout containerLayout = new FormLayout();
	    container.setLayout(containerLayout);
		activateLabel = new Label(container, SWT.NULL);
		FormData activateLabelPosition = new FormData();
		activateLabelPosition.width = 73;
		activateLabelPosition.height = 15;
		activateLabelPosition.left =  new FormAttachment(7, 1000, 0);
		activateLabelPosition.right =  new FormAttachment(130, 1000, 0);
		activateLabelPosition.top =  new FormAttachment(18, 1000, 0);
		activateLabelPosition.bottom =  new FormAttachment(55, 1000, 0);
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
	    availablePluginsListViewerPosition.width = 247;
	    availablePluginsListViewerPosition.height = 308;
	    availablePluginsListViewerPosition.left =  new FormAttachment(17, 1000, 0);
	    availablePluginsListViewerPosition.right =  new FormAttachment(439, 1000, 0);
	    availablePluginsListViewerPosition.top =  new FormAttachment(85, 1000, 0);
	    availablePluginsListViewerPosition.bottom =  new FormAttachment(851, 1000, 0);
	    availablePluginsListViewer.getControl().setLayoutData(availablePluginsListViewerPosition);
	    availablePluginsListViewer.setInput(getAvailablePlugins());
		
	    Button addButton = new Button(container, SWT.PUSH);
		FormData addButtonPosition = new FormData();
		addButtonPosition.width = 62;
		addButtonPosition.height = 26;
		addButtonPosition.left =  new FormAttachment(454, 1000, 0);
		addButtonPosition.right =  new FormAttachment(559, 1000, 0);
		addButtonPosition.top =  new FormAttachment(190, 1000, 0);
		addButtonPosition.bottom =  new FormAttachment(254, 1000, 0);
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
		removeButtonPosition.width = 65;
		removeButtonPosition.height = 25;
		removeButtonPosition.left =  new FormAttachment(451, 1000, 0);
		removeButtonPosition.top =  new FormAttachment(312, 1000, 0);
		removeButtonPosition.right =  new FormAttachment(560, 1000, 0);
		removeButtonPosition.bottom =  new FormAttachment(374, 1000, 0);
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
	    selectedPluginsListViewerPosition.width = 238;
	    selectedPluginsListViewerPosition.height = 311;
	    selectedPluginsListViewerPosition.left =  new FormAttachment(570, 1000, 0);
	    selectedPluginsListViewerPosition.right =  new FormAttachment(977, 1000, 0);
	    selectedPluginsListViewerPosition.top =  new FormAttachment(70, 1000, 0);
	    selectedPluginsListViewerPosition.bottom =  new FormAttachment(844, 1000, 0);
	    selectedPluginsListViewer.getControl().setLayoutData(selectedPluginsListViewerPosition);
		selectedPlugins = getSetPlugins();
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
		if (pluginList != "") {  
		  return pluginList.substring(0,(pluginList.length()-1)); 
        } else {
		  return null; 
        }
	}	 
	
	
	}

