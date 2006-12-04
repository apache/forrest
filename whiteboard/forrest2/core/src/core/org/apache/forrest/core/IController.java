package org.apache.forrest.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import org.apache.forrest.core.document.AbstractOutputDocument;
import org.apache.forrest.core.document.AbstractSourceDocument;
import org.apache.forrest.core.document.InternalDocument;
import org.apache.forrest.core.exception.LocationmapException;
import org.apache.forrest.core.exception.ProcessingException;
import org.apache.forrest.core.locationMap.Location;
import org.apache.forrest.core.plugin.AbstractInputPlugin;
import org.apache.forrest.core.plugin.BaseOutputPlugin;
import org.apache.forrest.core.reader.IReader;

public interface IController {
	final String sourceURLExtension = ".forrestSource";

	final String internalURLExtension = ".forrestInternal";
	
	public abstract AbstractInputPlugin getInputPlugin(
			final AbstractSourceDocument doc);

	public abstract BaseOutputPlugin getOutputPlugin(final URI requestURI);

	/**
	 * Get a reader that can be used for retrieving a resource
	 * from a given URI.
	 * 
	 * @param sourceURI
	 * @return
	 * @throws ProcessingException
	 */
	public abstract IReader getReader(final URI sourceURI) throws ProcessingException;

	/**
	 * Get the source URLs for a given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws IOException
	 * @throws LocationmapException
	 * @throws ProcessingException
	 */
	public abstract List<Location> getSourceLocations(final URI requestURI)
			throws IOException, LocationmapException, ProcessingException;

	/**
	 * Get the source documents for a given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws ProcessingException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public abstract AbstractSourceDocument getSourceDocuments(
			final URI requestURI) throws MalformedURLException,
			ProcessingException;

	/**
	 * Get the internal format documents for a given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws ProcessingException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public abstract InternalDocument getInternalDocument(
			final URI requestURI) throws ProcessingException;

	/**
	 * Get the output format documents for a given request URI.
	 * 
	 * @param requestURI
	 * @return
	 * @throws ProcessingException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public abstract AbstractOutputDocument getOutputDocument(
			final URI requestURI) throws MalformedURLException,
			ProcessingException;

}