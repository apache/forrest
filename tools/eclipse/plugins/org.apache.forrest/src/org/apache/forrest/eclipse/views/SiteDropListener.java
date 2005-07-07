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

import org.apache.forrest.eclipse.actions.Utilities;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;

/**
 * Handles dropping of files onto the siteXMLView.
 *
 */
public class SiteDropListener implements DropTargetListener {


    TreeViewer treeViewer;

    Document document;
    
    String projectName;
    
    final Display display = Display.getDefault();
    
    public SiteDropListener(String projectName, Document document, TreeViewer treeViewer) {
        // TODO Auto-generated constructor stub
        this.treeViewer = treeViewer;
        this.document = document;
        this.projectName = projectName;
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
            String localItemPath;
            String itemPath;
            for (int i = 0; i < files.length; i++) {
                strFilename = new File(files[i]);
                filePath = strFilename.getPath();
                localItemPath = filePath.substring(filePath
                        .indexOf("xdocs")
                        + "xdocs".length() + 1);
                itemPath = filePath.substring(Utilities
                        .getPathToXDocs().length() + 2);
                //TODO: Code to add element from drag and drop goes here
       
                
            }
        }
    }

    public void dropAccept(DropTargetEvent event) {
        // TODO Auto-generated method stub

    }

}
