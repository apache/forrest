package org.apache.forrest.core.document;

import java.io.IOException;
import java.net.URI;

public class InternalErrorDocument extends InternalDocument {

	public InternalErrorDocument(URI requestURI, String message) {
		super (requestURI, message);
	}

	@Override
	public String getContentAsString() throws IOException {
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<html xmlns=\"http://www.w3.org/2002/06/xhtml2\" xml:lang=\"en\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd\">");
		sb.append("<head><title>Internal Forrest Error</title></head>");
		sb.append("<body>");
		sb.append("<h>");
		sb.append("Error processing request for " + getRequestURI());
		sb.append("</h>");
		sb.append("<p>");
		sb.append(super.getContentAsString());
		sb.append("</p>");
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

}
