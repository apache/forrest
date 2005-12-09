/*
 * Copyright 1999-2004 The Apache Software Foundation or its licensors,
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
package org.apache.forrest.conf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.DefaultsModule;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.cocoon.xml.XMLConsumer;
import org.apache.commons.lang.SystemUtils;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;
import org.apache.excalibur.source.SourceResolver;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Input module for accessing the base properties used in Forrest. The main
 * values are the locations of the <b>source </b> directories and of the
 * <b>forrest </b> directories. The values are gotten using the ForrestConfUtils
 * class.
 */
public class ForrestConfModule extends DefaultsModule implements InputModule,
		Initializable, ThreadSafe, Serviceable {
	private AntProperties filteringProperties;

	private String forrestHome, projectHome, contextHome;

	private SourceResolver m_resolver;

	private final static String defaultHome = "context:/";

	public Object getAttribute(String name, Configuration modeConf,
			Map objectModel) throws ConfigurationException {
		String original;
		String attributeValue;

		try {
			original = super.getAttributeValues(name, modeConf, objectModel)[0]
					.toString();
			attributeValue = this.getAttributeValues(name, modeConf,
					objectModel)[0].toString();
		} catch (NullPointerException npe) {
			original = "(not defined in forrest.xconf)";
			attributeValue = filteringProperties.getProperty(name);
			if (attributeValue == null) {
				throw new ConfigurationException(
						"Unable to get attribute value for " + name);
			}
		}

		if (debugging()) {
			debug(" - Requested:" + name);
			debug(" - Unfiltered:" + original);
			debug(" - Given:" + attributeValue);
		}

		return attributeValue;
	}

	public Object[] getAttributeValues(String name, Configuration modeConf,
			Map objectModel) throws ConfigurationException {
		Object[] attributeValues = super.getAttributeValues(name, modeConf,
				objectModel);
		for (int i = 0; i < attributeValues.length; i++) {
			attributeValues[i] = filteringProperties.filter(attributeValues[i]
					.toString());
		}

		return attributeValues;
	}

	private final String getSystemProperty(String propertyName) {

		// if the property is not set, default to the webapp context
		String propertyValue = System.getProperty(propertyName, defaultHome);

		if (debugging())
			debug("system property " + propertyName + "=" + propertyValue);

		return propertyValue;
	}

	public void initialize() throws Exception {

		forrestHome = ForrestConfUtils.getForrestHome();
		projectHome = ForrestConfUtils.getProjectHome();
		contextHome = ForrestConfUtils.getContextHome();

		filteringProperties = new AntProperties();

		// add forrest.home and project.home to properties
		filteringProperties.setProperty("forrest.home", forrestHome);
		filteringProperties.setProperty("project.home", projectHome);
		filteringProperties.setProperty("context.home", contextHome);

		// NOTE: the first values set get precedence, as in AntProperties

		String forrestPropertiesStringURI;

		// get the values from forrest.properties.xml
		try {
			forrestPropertiesStringURI = projectHome
					+ SystemUtils.FILE_SEPARATOR + "forrest.properties.xml";

			filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
					forrestPropertiesStringURI);
		} catch (FileNotFoundException e) {
			if (debugging())
				debug("Unable to find forrest.properties.xml, ignoring.");
		}

		// if project.required.plugins is curently set, we can load plugin
		// defaults as we are using the new forrest.properties.xml config system
		String strPluginList = filteringProperties
				.getProperty("project.required.plugins");
		if (strPluginList != null) {
			StringTokenizer st = new StringTokenizer(strPluginList, ",");
			while (st.hasMoreTokens()) {
				forrestPropertiesStringURI = ForrestConfUtils.getPluginDir(st
						.nextToken().trim());
				try {
					forrestPropertiesStringURI = forrestPropertiesStringURI
							+ SystemUtils.FILE_SEPARATOR
							+ "default.plugin.properties.xml";
					filteringProperties = loadXMLPropertiesFromURI(
							filteringProperties, forrestPropertiesStringURI);
				} catch (FileNotFoundException e) {
					if (debugging())
						debug("Unable to load " + forrestPropertiesStringURI
								+ ", ignoring. " + e.getMessage());
				}
			}
		}

		// get the values from default.forrest.properties.xml
		try {
			forrestPropertiesStringURI = contextHome
					+ SystemUtils.FILE_SEPARATOR
					+ "default.forrest.properties.xml";

			filteringProperties = loadXMLPropertiesFromURI(filteringProperties,
					forrestPropertiesStringURI);
		} catch (FileNotFoundException e) {
			if (debugging())
				debug("Unable to find default.forrest.properties.xml, ignoring.");
		}

		// get forrest.properties and load the values
		forrestPropertiesStringURI = projectHome + SystemUtils.FILE_SEPARATOR
				+ "forrest.properties";

		filteringProperties = loadAntPropertiesFromURI(filteringProperties,
				forrestPropertiesStringURI);

		// get default-forrest.properties and load the values
		String defaultForrestPropertiesStringURI = contextHome
				+ SystemUtils.FILE_SEPARATOR + "default-forrest.properties";

		filteringProperties = loadAntPropertiesFromURI(filteringProperties,
				defaultForrestPropertiesStringURI);

		loadSystemProperties(filteringProperties);
		ForrestConfUtils.aliasSkinProperties(filteringProperties);
		if (debugging())
			debug("Loaded project forrest.properties:" + filteringProperties);
	}

	/**
	 * Override any properties for which a system property exists
	 */
	private void loadSystemProperties(AntProperties props) {
		for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
			String propName = (String) e.nextElement();
			String systemPropValue = System.getProperty(propName);
			if (systemPropValue != null) {
				// AntProperties.setProperty doesn't let you override, so we
				// have to remove the property then add it again
				props.remove(propName);
				props.setProperty(propName, systemPropValue);
			}
		}
	}

	/**
	 * @param propertiesStringURI
	 * @throws IOException
	 * @throws MalformedURLException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws SourceNotFoundException
	 */
	private AntProperties loadXMLPropertiesFromURI(
			AntProperties precedingProperties, String propertiesStringURI)
			throws MalformedURLException, IOException,
			ParserConfigurationException, SAXException {

		Source source = null;
		InputStream in = null;
		try {
			if (debugging())
				debug("Searching for forrest.properties.xml in"
						+ source.getURI());

			source = m_resolver.resolveURI(propertiesStringURI);

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(source.getURI());

			NodeList nl = document.getElementsByTagName("property");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element el = (Element) nl.item(i);
					filteringProperties.setProperty(el.getAttribute("name"), el
							.getAttribute("value"));
				}
			}

			if (debugging())
				debug("Loaded:" + propertiesStringURI
						+ filteringProperties.toString());

		} finally {
			if (source != null) {
				m_resolver.release(source);
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return filteringProperties;
	}

	/**
	 * @param antPropertiesStringURI
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SourceNotFoundException
	 */
	private AntProperties loadAntPropertiesFromURI(
			AntProperties precedingProperties, String antPropertiesStringURI)
			throws MalformedURLException, IOException, SourceNotFoundException {

		Source source = null;
		InputStream in = null;
		try {

			source = m_resolver.resolveURI(antPropertiesStringURI);

			if (debugging())
				debug("Searching for forrest.properties in" + source.getURI());
			in = source.getInputStream();
			filteringProperties = new AntProperties(precedingProperties);
			filteringProperties.load(in);

			if (debugging())
				debug("Loaded:" + antPropertiesStringURI
						+ filteringProperties.toString());

		} finally {
			if (source != null) {
				m_resolver.release(source);
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return filteringProperties;
	}

	public void service(ServiceManager manager) throws ServiceException {
		m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
	}

	/**
	 * Rocked science
	 */
	private final boolean debugging() {
		return getLogger().isDebugEnabled();
	}

	/**
	 * Rocked science
	 * 
	 * @param debugString
	 */
	private final void debug(String debugString) {
		getLogger().debug(debugString);
	}

}
