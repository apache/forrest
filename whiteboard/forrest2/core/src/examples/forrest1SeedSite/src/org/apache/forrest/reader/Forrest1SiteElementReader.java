package org.apache.forrest.reader;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.forrest.core.IController;
import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.AbstractSourceNode;
import org.apache.forrest.core.matcher.AbstractMatcher;
import org.apache.forrest.core.reader.AbstractReader;

/**
 * Reads a document using the "site:" psuedo protocol
 * from Forrest 1.
 *
 */
public class Forrest1SiteElementReader extends AbstractReader {

	public AbstractSourceDocument read(IController controller, URI requestURI, AbstractSourceNode sourceNode, AbstractMatcher matcher) throws ProcessingException {
		URI siteURI;
		AbstractOutputDocument siteDoc;
		try {
			siteURI = new URI("classpath:/xdocs/site.xml");
			siteDoc = controller.getOutputDocument(siteURI);
			String href = siteDoc.getValue(requestURI.getSchemeSpecificPart() + "@href");
			return controller.getSourceDocuments(new URI(href));
		} catch (URISyntaxException e) {
			// Should never be thrown
			throw new ProcessingException("Unable create URI for site.xml document", e);
		} catch (MalformedURLException e) {
			throw new ProcessingException("Unable to get site.xml document", e);
		}
	}

}
