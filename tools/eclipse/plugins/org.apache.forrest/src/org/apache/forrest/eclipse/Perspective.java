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
package org.apache.forrest.eclipse;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;






public class Perspective implements IPerspectiveFactory  {
	
	

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		IFolderLayout top=layout.createFolder("top",IPageLayout.TOP,0.7f,editorArea);
		top.addView("SiteXML");
		top.addView("TabsXML");
		IFolderLayout bottom=layout.createFolder("bottom",IPageLayout.BOTTOM,0.7f,editorArea);
		bottom.addView("Locationmap");
		//FIXME: The views and menues that are needed for the RCP have to be properly defined
		
		
	}

}
