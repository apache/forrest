package org.apache.forrest.core.matcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.forrest.core.exception.ProcessingException;
import org.apache.log4j.Logger;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;

/**
 * An REMatcher uses a regular expression to define a match.
 * 
 */
public class REMatcher extends AbstractMatcher {

	Logger log = Logger.getLogger(REMatcher.class);

	/**
	 * Create an REMatcher using the supplied Regular Expression.
	 * 
	 * @param pattern
	 */
	public REMatcher(String re) {
		this.setPattern(re);
	}

	@Override
	public URI substituteVariables(URI requestURI, URI sourceURI)
			throws ProcessingException {
		URL url;
		RE r;

		try {
			r = new RE(getPattern());
		} catch (RESyntaxException re) {
			throw new ProcessingException(
					"Unable to extract variable values from request: "
							+ re.getMessage(), re);
		}

		String urlString = requestURI.getPath();

		String sourceSSP = sourceURI.getSchemeSpecificPart();

		if (r.match(urlString)) {
			String variable;
			String value;
			for (int i = 0; i < r.getParenCount(); i++) {
				variable = "$(" + i + ")";
				value = r.getParen(i);
				if (value != null) {
					sourceSSP = sourceSSP.replace(variable, value);
				}
			}
			log.debug("After variable substitution a potential source path is "
					+ sourceSSP);
		} else {
			throw new ProcessingException(
					"Unable to extract variable values from '" + urlString + "' using Regular Expression: " + getPattern());
		}

		URI newURI;
		try {
			newURI = new URI(sourceURI.getScheme(), sourceSSP, null);
		} catch (URISyntaxException e) {
			throw new ProcessingException(
					"Unable to perform variable substitution on the source uri "
							+ sourceURI + " cuased by " + e.getMessage(), e);
		}
		return newURI;
	}

}
