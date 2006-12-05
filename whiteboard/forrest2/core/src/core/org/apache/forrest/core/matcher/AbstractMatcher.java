package org.apache.forrest.core.matcher;

import java.net.URI;
import java.net.URL;

import org.apache.forrest.core.exception.ProcessingException;


/**
 * A matcher is used to match a pattern against a given 
 * string. 
 *
 */
public abstract class AbstractMatcher {
	
	String pattern;

	/**
	 * Get the pattern used by this mathcer.
	 * The syntax of the pattern is dependant on the
	 * implementation of the matcher.
	 * @return
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Set the pattern used by this mathcer.
	 * The syntax of the pattern is dependant on the
	 * implementation of the matcher.
	 * @param pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * Perform any necessary variable substitution in the
	 * supplied sourceURI using the given requestURI to find
	 * values for variables.
	 * @throws ProcessingException 
	 * 
	 */
	public abstract URI substituteVariables(URI requestURI, URI sourceURI) throws ProcessingException;
}
