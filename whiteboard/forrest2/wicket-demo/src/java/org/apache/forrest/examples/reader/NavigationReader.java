package org.apache.forrest.examples.reader;

import java.net.URI;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.matcher.AbstractMatcher;
import org.apache.forrest.core.reader.ChainedReader;

public class NavigationReader extends ChainedReader {

	public AbstractSourceDocument read(IController controller, URI requestURI,
			AbstractSourceNode location, AbstractMatcher matcher) throws ProcessingException {
		return super.read(controller, requestURI, location, matcher);
	}
	
	public String getDocType() {
		return "org.apache.forrest.example.Navigation";
	}

}
