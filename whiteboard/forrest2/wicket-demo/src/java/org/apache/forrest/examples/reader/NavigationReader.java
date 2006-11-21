package org.apache.forrest.examples.reader;

import java.net.MalformedURLException;
import java.net.URI;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.XMLSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.Location;
import org.apache.forrest.core.reader.AbstractReader;

public class NavigationReader extends AbstractReader {

	public AbstractSourceDocument read(IController controller, URI requestURI,
			Location location) throws MalformedURLException,
			ProcessingException {
		String content = "<navigation><item label=\"Item 1\"/><item label=\"Item 2\"/><item label=\"Item 3\"/></navigation>";
		String type = "org.apache.forrest.example.Navigation";
		XMLSourceDocument doc = new XMLSourceDocument(content, type);
		return doc;
	}

}
