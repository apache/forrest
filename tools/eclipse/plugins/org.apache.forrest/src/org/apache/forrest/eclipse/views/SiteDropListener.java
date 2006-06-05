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

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handles dropping of files onto the siteXMLView.
 *
 */
public class SiteDropListener extends SiteXMLView implements DropTargetListener {



    protected NavigationView view;
    protected Document activeDocument;
    final Display display = Display.getDefault();
    
    public SiteDropListener() {
        // TODO Auto-generated constructor stub
    	activeDocument = document; 
    }

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
                strFilename = new File(files[i]);
                filePath = strFilename.getPath();
                Node insertionElement = (Element) event.item.getData();	
		        Element element = activeDocument.createElement("NewElement");
		        relativePath = filePath.substring(xDocPath.length());  
		        element.setAttribute("href", relativePath);
		    	element.setAttribute("description", relativePath);
		    	element.setAttribute("label", relativePath);
		    	insertionElement.appendChild(element);
		    	treeViewer.refresh();
            }
        }
    }

    public void dropAccept(DropTargetEvent event) {
        // TODO Auto-generated method stub

    }

}
