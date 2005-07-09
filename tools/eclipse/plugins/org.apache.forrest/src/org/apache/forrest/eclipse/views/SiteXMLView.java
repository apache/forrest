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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.forrest.eclipse.actions.Utilities;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;

import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;





/**
 * A tree view for site.xml files. The view handles drag and
 * drop from the navigator and supports a number of context 
 * menus for editing. 
 */
public class SiteXMLView extends ViewPart implements IMenuListener,
		ISelectionListener {
	private TreeViewer treeViewer;
	private DocumentBuilder parser;
	private Document document;
	private String projectName;
	private String path;
	private IStructuredSelection treeSelection;
	private Action AddElement;
	private Action RemoveElement;
	private Action SaveDocument;
	private Text elementText;
	private Text hrefText;
	private Text locationText;
	private Text labelText;
	private Text descriptionText;
	
	
	
	protected IProject activeProject;

	
	
	

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	
	/**
	 * The constructor.
	 */
	public SiteXMLView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] types = new Transfer[] { FileTransfer.getInstance()};
		treeViewer.addDropSupport(operations, types, new SiteDropListener(projectName ,document, treeViewer));
		treeViewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object element) {
				ArrayList ch = new ArrayList();
				NodeList nl = ((Node) element).getChildNodes();
				for (int i = 0; i < nl.getLength(); i++)
					if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
						ch.add(nl.item(i));
				return ch.toArray();
			}

			public Object getParent(Object element) {
				return ((Node) element).getParentNode();
			}

			public Object[] getElements(Object element) {
				return getChildren(element);
			}

			public boolean hasChildren(Object element) {
				return getChildren(element).length > 0;
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object old_input,
					Object new_input) {
			}
		});

		treeViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof Attr)
					return "@" + ((Attr) element).getNodeName() + " " +((Attr) element).getNodeValue();
				else
					return ((Node) element).getNodeName();
			}
		});

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {

			parser = factory.newDocumentBuilder();
			document = parser.parse(new File(""));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getViewSite().getPage().addSelectionListener(this);
		
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event) {
					if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					treeSelection = selection;
					
					
				}
			}
		});
	
		//System.out.println(document.toString());
		treeViewer.setInput(document);
		makeActions();
		hookContextMenu();
	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void menuAboutToShow(IMenuManager manager) {
		// TODO Auto-generated method stub

	}

	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object first = ((IStructuredSelection) selection).getFirstElement();
			IResource resource = (IResource) first;
			if (resource instanceof IProject) {
				activeProject = (IProject) resource;
				projectName = activeProject.getProject().getName();
				path = (activeProject.getProject().getLocation()
						.toString()
						+ java.io.File.separator
						+ Utilities.getPathToXDocs()
						+ java.io.File.separator + "site.xml");
				try {
					
				
					Document newDocument = parser.parse(new File(path));
					if ((document != (newDocument))) {
							document = newDocument;
							treeViewer.setInput(document);
					}
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
	
			}
		}

	}

		private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
            SiteXMLView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}



		private void fillContextMenu(IMenuManager manager) {
			manager.add(AddElement);
			manager.add(RemoveElement);
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			manager.add(SaveDocument);
		}
	


		private void makeActions() {
		AddElement = new Action() {
			public void run() {
				if (treeSelection != null) {
				insertElementDialog();
				}
			}
		};
		AddElement.setText("Add Element");
		AddElement.setToolTipText("Add Element tooltip");
		AddElement.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		RemoveElement = new Action() {
			public void run() {
				if (treeSelection != null) {
					//TODO: Code to remove Element does here.
					Node deletionElement = (Element) treeSelection.getFirstElement();	
					Node deletionParent = deletionElement.getParentNode();
					deletionParent.removeChild(deletionElement);
					treeViewer.refresh();
					
				}
			}
		};
		RemoveElement.setText("DeleteElement");
		RemoveElement.setToolTipText("Delete Element");
		RemoveElement.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		SaveDocument = new Action() {
			public void run() {
				SaveXmlFile(document,path);
			}
		};
		
		SaveDocument.setText("Save");
		SaveDocument.setToolTipText("Save XML Document");
		SaveDocument.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	 public static void SaveXmlFile(Document document, String filename) {
	        try {
	           
	            Source source = new DOMSource(document);
	            File file = new File(filename);
	            Result result = new StreamResult(file);
	            Transformer transformer = TransformerFactory.newInstance().newTransformer();
	            transformer.transform(source, result);
	        } catch (TransformerConfigurationException e) {
	        } catch (TransformerException e) {
	        }
	    }

	private void showMessage(String message) {
		MessageDialog.openInformation(
			treeViewer.getControl().getShell(),
			"Sample View",
			message);
	}

	private void insertElementDialog () {
		
			
			Shell shell = treeViewer.getControl().getShell();
			shell.open ();
			
			final Shell dialog = new Shell (shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			GridData data = new GridData();
			
			dialog.setLayout(new GridLayout(2, true));
			dialog.setText("Add Element");
			dialog.setSize(300,150);
			Label elementLabel = new Label (dialog, SWT.FLAT);
			elementLabel.setText ("Element Name");
			elementText = new Text (dialog, SWT.FILL);
			
			Label descriptionLabel = new Label (dialog, SWT.FLAT);
			descriptionLabel.setText ("Description");
			descriptionText = new Text (dialog, SWT.FILL);
			
			Label hrefLabel = new Label (dialog, SWT.FLAT);
			hrefLabel.setText ("Href");
			hrefText = new Text (dialog, SWT.FILL);
			
			Label labelLabel = new Label (dialog, SWT.FLAT);
			labelLabel.setText ("Label");
			labelText = new Text (dialog, SWT.FILL);
			
			final Button ok = new Button (dialog, SWT.PUSH);
			ok.setText ("Ok");
			Button cancel = new Button (dialog, SWT.PUSH);
			cancel.setText ("Cancel");
			
			ok.addListener(SWT.Selection, new Listener() {
			      public void handleEvent(Event event) {
			    	  Node insertionElement = (Element) treeSelection.getFirstElement();	
					  
			    	  Element element = document.createElement(elementText.getText());
			    	  element.setAttribute("href", hrefText.getText());
			    	  element.setAttribute("description", descriptionText.getText());
			    	  element.setAttribute("label", labelText.getText());
			    	  insertionElement.appendChild(element);
	  
					  treeViewer.refresh();
				      dialog.close();
			      }
			    });

			 cancel.addListener(SWT.Selection, new Listener() {
			      public void handleEvent(Event event) {
			       dialog.close();
			      }
			    });
			
			dialog.open ();
		
		
		}
}
