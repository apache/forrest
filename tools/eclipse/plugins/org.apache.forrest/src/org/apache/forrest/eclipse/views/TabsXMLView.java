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

import org.apache.forrest.eclipse.wizards.NewTabElement;
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
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A tree view for tabs.xml files. The view handles drag and
 * drop from the navigator and supports a number of context 
 * menus for editing. 
 */
public class TabsXMLView extends NavigationView {
	private Action AddElement;
	private Action RemoveElement;
	private Action SaveDocument;
    
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
		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] types = new Transfer[] { FileTransfer.getInstance()};
		//tree.addDropSupport(operations, types, new SiteDropListener(projectName ,tabsDocument, tree));
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
		if (path != null) { document = DOMUtilities.loadDOM(path);}
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

    private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
            TabsXMLView.this.fillContextMenu(manager);
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
					NewTabElement elementCreation_ = new NewTabElement(treeSelection,document);
					elementCreation_.init(PlatformUI.getWorkbench(), null); // initializes the wizard
					WizardDialog dialog = new WizardDialog(treeViewer.getControl().getShell(), elementCreation_);
					dialog.open();
				treeViewer.refresh();
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
				DOMUtilities.SaveDOM(document,path);
			}
		};
		
		SaveDocument.setText("Save");
		SaveDocument.setToolTipText("Save XML Document");
		SaveDocument.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(
			treeViewer.getControl().getShell(),
			"Sample View",
			message);
	}

    /**
     * Get the name of the file this editor view represents.
     * This name does not include the path. For example.
     * 'site.xml' or 'tabs.xml'
     * @return the name (without pat) of the document to view
     */
    protected String getFilename() {
        return "tabs.xml";
    }
	
}
