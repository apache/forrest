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
package org.apache.forrest.eclipse.views;

import java.io.File;
import java.util.ArrayList;

import org.apache.forrest.eclipse.actions.Utilities;
import org.apache.forrest.eclipse.wizards.NewSiteElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A tree view for site.xml files. The view handles drag and drop from the
 * navigator and supports a number of context menus for editing.
 */
public class SiteXMLView extends NavigationView {
	public static String ID_VIEW = "org.apache.forrest.view.siteXML";

    private Action AddElement;

	private Action RemoveElement;

	private Action SaveDocument;

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
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] types = new Transfer[] { FileTransfer.getInstance() };

		treeViewer.addDropSupport(operations, types, new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			public void dragLeave(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			public void dragOperationChanged(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			public void dragOver(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

			/**
			 * Handle files that are dropped into the site tree.
			 */
			public void drop(DropTargetEvent event) {
				if (event.data instanceof String[]) {
					String[] files = (String[]) event.data;
					File strFilename;
					String filePath = null;
					String relativePath = null;
					for (int i = 0; i < files.length; i++) {
						strFilename = new File(files[i]);
						filePath = strFilename.getPath();
						File file = new File(filePath);
						Node insertionElement = (Element) event.item.getData();
						Element element = document.createElement(file.getName());
						relativePath = filePath.substring(xDocPath.length());
						element.setAttribute("href", relativePath);
						element.setAttribute("description", file.getName());
						element.setAttribute("label", file.getName());
						insertionElement.appendChild(element);
						treeViewer.refresh();
					}
				}
			}

			public void dropAccept(DropTargetEvent event) {
				// TODO Auto-generated method stub

			}

		});

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
					return "@" + ((Attr) element).getNodeName() + " "
							+ ((Attr) element).getNodeValue();
				else
					return ((Node) element).getNodeName();
			}
		});
		getViewSite().getPage().addSelectionListener(this);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event
							.getSelection();
					treeSelection = selection;
					Element element = (Element) selection.getFirstElement();
					ForrestPropertiesView.setElement(element);
					ForrestPropertiesView.refreshTree();
					

				}
			}
		});

		// System.out.println(document.toString());
		treeViewer.setInput(document);
		makeActions();
		hookContextMenu();
	}

	  /* (non-Javadoc)
     * Method declared on IAdaptable
     */
    public Object getAdapter(Class adapter) {
       
        if (adapter.equals(IPropertySheetPage.class)) {
            return getPropertySheet();
        }
        return super.getAdapter(adapter);
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
					NewSiteElement elementCreation_ = new NewSiteElement(
							treeSelection, document);
					elementCreation_.init(PlatformUI.getWorkbench(), null); // initializes
																			// the
																			// wizard
					WizardDialog dialog = new WizardDialog(treeViewer
							.getControl().getShell(), elementCreation_);
					dialog.open(); // This opens a dialog
					treeViewer.refresh();
				}
			}
		};
		AddElement.setText("Add Element");
		AddElement.setToolTipText("Add Element tooltip");
		AddElement.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		RemoveElement = new Action() {
			public void run() {
				if (treeSelection != null) {
					// TODO: Code to remove Element does here.
					Node deletionElement = (Element) treeSelection
							.getFirstElement();
					Node deletionParent = deletionElement.getParentNode();
					deletionParent.removeChild(deletionElement);
					treeViewer.refresh();

				}
			}
		};
		RemoveElement.setText("DeleteElement");
		RemoveElement.setToolTipText("Delete Element");
		RemoveElement.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		SaveDocument = new Action() {
			public void run() {
				DOMUtilities.SaveDOM(document, path);
			}
		};

		SaveDocument.setText("Save");
		SaveDocument.setToolTipText("Save XML Document");
		SaveDocument.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));
	}

    /**
	 * Get the name of the file this editor view represents. This name does not
	 * include the path. For example. 'site.xml' or 'tabs.xml'
	 * 
	 * @return the name (without pat) of the document to view
	 */

	protected String getFilename() {
		return Utilities.getPathToXDocs() + java.io.File.separator + "site.xml";
	}
	
	protected IPropertySheetPage getPropertySheet() {
	        return new PropertySheetPage();
	    }

}
