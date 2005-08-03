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
