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
package org.apache.forrest.repository.daisy;


import java.util.Locale;
import java.util.Vector;

import org.apache.forrest.repository.daisy.preferences.DaisyPreferences;
import org.outerj.daisy.repository.Credentials;
import org.outerj.daisy.repository.Repository;
import org.outerj.daisy.repository.RepositoryManager;
import org.outerj.daisy.repository.clientimpl.RemoteRepositoryManager;
import org.outerj.daisy.repository.query.QueryManager;
import org.outerx.daisy.x10.SearchResultDocument;
import org.outerx.daisy.x10.SearchResultDocument.SearchResult.Rows.Row;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The main plugin class to be used to interface withthe Daisy repository. It will allow
 * you to search the repository and retrieve a list of documents avaiable. 
 */

public class RepositoryInterface {
	
	/**
	 * This searches the repository for all documents and returns a list of them as DOM document 
	 * @param document 
	 */
	public static Document SearchRepository(Document document) throws Exception { 
		
        Element element = document.createElement("daisy");
        document.appendChild(element);
        
		String repositoryLocation = org.apache.forrest.repository.daisy.DaisyPlugin.getDefault().getPluginPreferences()
        .getString(DaisyPreferences.REPOSITORY_URL);
		String repositoryPort = org.apache.forrest.repository.daisy.DaisyPlugin.getDefault().getPluginPreferences()
        .getString(DaisyPreferences.REPOSITORY_PORT);
		String repositoryUsername = org.apache.forrest.repository.daisy.DaisyPlugin.getDefault().getPluginPreferences()
        .getString(DaisyPreferences.REPOSITORY_USERNAME);
		String repositoryPassword = org.apache.forrest.repository.daisy.DaisyPlugin.getDefault().getPluginPreferences()
        .getString(DaisyPreferences.REPOSITORY_PASSWORD);
		RepositoryManager repositoryManager = new RemoteRepositoryManager(
            "http://"+repositoryLocation+":"+repositoryPort, new Credentials(repositoryUsername, repositoryPassword));
		 Repository repository =
	            repositoryManager.getRepository(new Credentials(repositoryUsername, repositoryPassword));
	        QueryManager queryManager = repository.getQueryManager();
	        Object searchresults = queryManager.performQuery("select id, name where true", Locale.getDefault());
	        Row[] rows = ((SearchResultDocument) searchresults).getSearchResult().getRows().getRowArray();
	        Vector documentList = new Vector();
			for (int i = 0; i < rows.length; i++) {
	            String id = rows[i].getValueArray(0);
	            String documentName = rows[i].getValueArray(1);
	            String name = "http://" + repositoryUsername +":"+repositoryPassword+"@"+repositoryLocation+":"+repositoryPort;
	            name = name + "/publisher/documentPage?documentId=";
	            name = name + id;
	            name = name + "&includeNavigation=false&locale=en_US;version=live";
	            Element element2 = document.createElement("item");
	            element.appendChild(element2);
	            element2.setAttribute("id",id);
	            element2.setAttribute("name",documentName);
	            element2.setAttribute("location",name);
	            documentList.add(name);
	        }
			return document;
			
}

	
	
	
}
