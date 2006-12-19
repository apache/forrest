package org.apache.forrest.core.matcher;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.forrest.core.exception.ProcessingException;
import org.apache.log4j.Logger;

import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.regexp.internal.RESyntaxException;

/**
 * A Wildcard Matcher is based on Cocoons Widcard Matcher.
 * 
 * @TODO actual implement something useful rather than always
 * return true
 */
public class WildcardMatcher extends AbstractMatcher {

	Logger log = Logger.getLogger(WildcardMatcher.class);

	/**
	 * Create an REMatcher using the supplied Regular Expression.
	 * 
	 * @param pattern
	 */
	public WildcardMatcher(String pattern) {
		this.setPattern(pattern);
	}

	@Override
	public URI substituteVariables(URI requestURI, URI sourceURI)
			throws ProcessingException {
		return sourceURI;
	}

}
