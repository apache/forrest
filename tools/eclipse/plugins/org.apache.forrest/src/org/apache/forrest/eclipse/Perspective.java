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
package org.apache.forrest.eclipse;

import org.apache.forrest.eclipse.views.ForrestPropertiesView;
import org.apache.forrest.eclipse.views.LocationmapView;
import org.apache.forrest.eclipse.views.SiteXMLView;
import org.apache.forrest.eclipse.views.TabsXMLView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;






public class Perspective implements IPerspectiveFactory  {
	
	

	private static final String ID_PERSPECTIVE = "org.apache.forrest.perspective.ContentObject";

    public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		
        IFolderLayout topLeft = layout.createFolder("left", IPageLayout.LEFT, 
                0.25f, editorArea);
        topLeft.addView(IPageLayout.ID_RES_NAV);
                
        IFolderLayout bottomLeft = layout.createFolder("topLeft", IPageLayout.BOTTOM, 
                0.5f, "left");
		bottomLeft.addView(SiteXMLView.ID_VIEW);
		bottomLeft.addView(TabsXMLView.ID_VIEW);
        bottomLeft.addView(LocationmapView.ID_VIEW);
        
        IFolderLayout bottom = layout.createFolder("bottom",IPageLayout.BOTTOM,0.7f,editorArea);
        // FIXME: Forrest specific properties editor should be replaced with: bottom.addView(IPageLayout.ID_PROP_SHEET);
        bottom.addView(ForrestPropertiesView.ID_VIEW);
        bottom.addView(IPageLayout.ID_TASK_LIST);
		
        //FIXME: The views and menus that are needed for the RCP have to be properly defined

        layout.addPerspectiveShortcut(ID_PERSPECTIVE);
	}

}
