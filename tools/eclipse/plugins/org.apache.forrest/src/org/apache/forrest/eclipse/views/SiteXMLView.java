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
package org.apache.forrest.eclipse.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import com.sun.org.apache.xml.internal.utils.URI;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SiteXMLView extends ViewPart  implements
			 IMenuListener, ISelectionListener{
	private TreeViewer viewer;
	
	private IProject activeProject;
	
	private Action doubleClickAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */


	class NameSorter extends ViewerSorter {
	}

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
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		getSite().setSelectionProvider(viewer);
		viewer.setContentProvider(new ITreeContentProvider() {
			public Object[] getChildren(Object element) {
				ArrayList ch = new ArrayList();
				NamedNodeMap atrs = ((Node) element).getAttributes();
				if (atrs != null)
					for (int i = 0; i < atrs.getLength(); i++)
						ch.add(atrs.item(i));
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

		viewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof Attr)
					return "@" + ((Attr) element).getNodeName();
				else
					return ((Node) element).getNodeName();
			}
		});
		
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder parser;
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
		viewer.setInput(document);
	}
   
	
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void menuAboutToShow(IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object first = ((IStructuredSelection)selection).getFirstElement();
            IResource resource = (IResource)first;
            if (resource instanceof IProject) {
                activeProject = (IProject)resource;
            }            
		}
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	
}
