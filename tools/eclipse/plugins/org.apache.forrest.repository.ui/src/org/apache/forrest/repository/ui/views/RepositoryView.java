/*
 * Copyright 2005 The Apache Software Foundation or its licensors,
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
package org.apache.forrest.repository.ui.views;


import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.forrest.repository.daisy.RepositoryInterface;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A tree view for documents avaialable in the Daisy repository. You can drag the document from this
 * view to the locationmap view in the Apache Forrest Plugin
 */
public class RepositoryView extends ViewPart implements IMenuListener,
ISelectionListener {
	protected TreeViewer treeViewer;
	protected Document document;
	private Action action1;
	protected IStructuredSelection treeSelection;
	  
	    
	/**
	 * The constructor.
	 */
	public RepositoryView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			 document = builder.newDocument();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FactoryConfigurationError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       
		
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
	
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
				return ((Element) element).getNodeName() + " : " + ((Element) element).getAttribute("name") + " : " + ((Element) element).getAttribute("location");
			}
		});
		getViewSite().getPage().addSelectionListener(this);
		try {
			document = RepositoryInterface.SearchRepository(document);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					treeSelection = selection;

				}
			}
		});
	 	   
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		
		
	
		treeViewer.addDragSupport (operations, types,new DragSourceListener () {
		
			public void dragStart(DragSourceEvent event) {
			}
			
			public void dragSetData(DragSourceEvent event) {
				Element element = (Element) treeSelection
				.getFirstElement();
				String dataTransfer = "daisy," + element.getAttribute("id") + "," + element.getAttribute("location");
				event.data = dataTransfer;
				
			}
			public void dragFinished(DragSourceEvent event) {
				
				
			}
		});
		
		treeViewer.setInput(document);
		makeActions();
		contributeToActionBars();
		
		
	}

	
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}


	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				try {
					RepositoryInterface.SearchRepository(document);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showMessage("Document List refreshed");
			}
		};
		action1.setText("Refresh Document List");
		action1.setToolTipText("Refresh Document List");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			treeViewer.getControl().getShell(),
			"Repository Browser",
			message);
	}
	
	public void menuAboutToShow(IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}


	
}
