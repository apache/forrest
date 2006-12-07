package org.apache.forrest.core.locationMap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.matcher.AbstractMatcher;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;

/**
 * A Source Node represents a single possible source or, in the case of an
 * aggregated node a collection of possible source nodes.
 * 
 */
public abstract class AbstractSourceNode {

	Logger log = Logger.getLogger(AbstractSourceNode.class);

	private boolean isRequired;

	private URI sourceURI;

	/**
	 * Construct a new LocationNode from an XML node.
	 * 
	 * @param element
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public AbstractSourceNode(final Node element) throws URISyntaxException,
			IOException {
		final NamedNodeMap atts = element.getAttributes();
		setSourceURI(new URI(atts.getNamedItem("href").getNodeValue()));
		final Node required = atts.getNamedItem("required");
		if (required != null) {
			setRequired(required.getNodeValue().toLowerCase().equals("true"));
		}
	}

	public AbstractSourceNode() {
	}

	public boolean isRequired() {
		return this.isRequired;
	}

	public void setRequired(final boolean isRequired) {
		this.isRequired = isRequired;
	}

	/**
	 * 
	 * @param sourceURI
	 * @return
	 * @throws ProcessingException -
	 *             if the path to the resource cannot be resolved
	 */
	private URL resolveClasspathURI(final URI sourceURI)
			throws ProcessingException {
		URL resourceURL;
		resourceURL = this.getClass().getResource(sourceURI.getPath());
		if (resourceURL == null)
			throw new ProcessingException(
					"Cannot find the classpath resource: " + sourceURI);
		return resourceURL;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (isRequired()) {
			sb.append("Required ");
		} else {
			sb.append("Optional ");
		}
		sb.append("Source URI: ");
		sb.append(this.getSourceURI());
		return sb.toString();
	}

	/**
	 * Resolve the supplied URI and return a URL that can be used to attempt to
	 * retrieve the resource. A resolved uri has all variables substituted with
	 * their values.
	 * 
	 * @param requestURI -
	 *            the full request URI
	 * @return
	 * @throws MalformedURLException
	 * @throws ProcessingException
	 */
	public URL resolveURL(AbstractMatcher matcher, URI requestURI)
			throws MalformedURLException, ProcessingException {
		URI uri = matcher.substituteVariables(requestURI, getSourceURI());

		URL resolvedURL;
		if (uri.getScheme().equals("classpath")) {
			resolvedURL = resolveClasspathURI(uri);
		} else {
			try {
				String strURI = uri.getSchemeSpecificPart();
				if (strURI.contains(":")) {
					String subProtocol = strURI.substring(0, strURI
							.lastIndexOf(':'));
					uri = new URI(strURI.substring(strURI.lastIndexOf(':') + 1));
					if (subProtocol.equals("classpath")) {
						resolvedURL = resolveClasspathURI(uri);
					} else {
						URI subURI;
						subURI = new URI(subProtocol, uri
								.getSchemeSpecificPart(), null);
						resolvedURL = subURI.toURL();
					}
				} else {
					resolvedURL = uri.toURL();
				}
			} catch (URISyntaxException e) {
				throw new MalformedURLException(
						"Unable to work out sub protocol URI");
			}
		}
		return resolvedURL;

	}

	public URI getSourceURI() {
		return sourceURI;
	}

	public void setSourceURI(URI sourceURI) {
		this.sourceURI = sourceURI;
	}

}
