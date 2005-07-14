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



import java.util.ArrayList;

import org.apache.forrest.eclipse.actions.Utilities;
import org.apache.forrest.eclipse.wizards.NewTabElement;
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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
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

/**
 * A tree view for tabs.xml files. The view handles drag and
 * drop from the navigator and supports a number of context 
 * menus for editing. 
 */
public class TabsXMLView extends ViewPart implements IMenuListener,
		ISelectionListener {
	private TreeViewer tree;
	private Document tabsDocument;
	private String projectName;
	private String path;
	private IStructuredSelection treeSelection;
	private Action AddElement;
	private Action RemoveElement;
	private Action SaveDocument;
	
	protected IProject activeProject;
	/**
	 * The constructor.
	 */
	public TabsXMLView() {
	}
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		tree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		getSite().setSelectionProvider(tree);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] types = new Transfer[] { FileTransfer.getInstance()};
		//tree.addDropSupport(operations, types, new SiteDropListener(projectName ,tabsDocument, tree));
		tree.setContentProvider(new ITreeContentProvider() {
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
		tree.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof Attr)
					return "@" + ((Attr) element).getNodeName() + " " +((Attr) element).getNodeValue();
				else
					return ((Node) element).getNodeName();
			}
		});
		getViewSite().getPage().addSelectionListener(this);
		tree.addSelectionChangedListener(new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
					if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					treeSelection = selection;
				}
			}
		});
	
		//System.out.println(document.toString());
		if (path != null) { tabsDocument = DOMUtilities.loadDOM(path);}
		tree.setInput(tabsDocument);
		makeActions();
		hookContextMenu();
	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void menuAboutToShow(IMenuManager manager) {
		// TODO Auto-generated method stub

	}

	/**
     * When the selection in the navigator view is changed 
     * we look to see if the new selection is an IProject.
     * If it is then we load the tabs.xml file into this
     * TabsXMLView.
	 */
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
						+ java.io.File.separator + "tabs.xml");
				
				tabsDocument = DOMUtilities.loadDOM(path);
				tree.setInput(tabsDocument);
				
	
			}
		}

	}

    private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
            TabsXMLView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tree.getControl());
		tree.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, tree);
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
					NewTabElement elementCreation_ = new NewTabElement(treeSelection,tabsDocument);
					elementCreation_.init(PlatformUI.getWorkbench(), null); // initializes the wizard
					WizardDialog dialog = new WizardDialog(tree.getControl().getShell(), elementCreation_);
					dialog.open();
				tree.refresh();
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
					tree.refresh();
					
				}
			}
		};
		RemoveElement.setText("DeleteElement");
		RemoveElement.setToolTipText("Delete Element");
		RemoveElement.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		SaveDocument = new Action() {
			public void run() {
				DOMUtilities.SaveDOM(tabsDocument,path);
			}
		};
		
		SaveDocument.setText("Save");
		SaveDocument.setToolTipText("Save XML Document");
		SaveDocument.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			tree.getControl().getShell(),
			"Sample View",
			message);
	}

   
	
}
