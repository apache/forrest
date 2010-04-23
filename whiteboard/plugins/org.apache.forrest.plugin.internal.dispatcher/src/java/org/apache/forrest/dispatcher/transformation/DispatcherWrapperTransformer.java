/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher.transformation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.axiom.attachments.utils.IOUtils;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.cocoon.util.TraxErrorHandler;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.config.WritableDispatcherBean;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.CocoonResolver;
import org.apache.forrest.dispatcher.impl.XMLStructurer;
import org.apache.forrest.dispatcher.impl.XMLStructurerAxiom;
import org.apache.forrest.dispatcher.impl.helper.Captions;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.apache.forrest.dispatcher.impl.helper.XMLProperties;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A cocoon transformer wrapper class for the dispatcher.
 * 
 * The actual work will be done in the core code of the dispatcher which is
 * based on StAX and AXIOM.
 * 
 * <li>In this transformer we doing principal configuration to adopt the
 * dispatcher to the cocoon environment ({@link #configure(Configuration)} and (
 * {@link #setup(SourceResolver, Map, String, Parameters)} <li>We reusing cocoon
 * caching mechanism ({@link #getKey()} and {@link #getValidity()} to cache the
 * dispatcher. <li>We record the SAX events ({@link #startDocument()} to later
 * {@link #endDocument()} passing them as Stream to the Structurer
 * implementation we have chosen.
 * 
 * @version 1.0
 * 
 */
public class DispatcherWrapperTransformer extends AbstractSAXTransformer
    implements Disposable, CacheableProcessingComponent, URIResolver {

  /**
   * The requested format.
   * <p>
   * Used to identify the structure to process
   */
  private String requestedFormat;

  /**
   * The requested id.
   * <p>
   * Used to request the corresponding properties file for the request
   */
  private String requestId;

  /**
   * Caching and validity properties
   */
  private String cacheKey, validityFile;

  private SourceValidity validity;

  private String validityChecking;

  /**
   * Resolver which will be used for uri resolving
   */

  private org.apache.excalibur.source.SourceResolver m_resolver;

  /**
   * The caption for the cache parameter
   */
  public static final String CACHE_PARAMETER = "cacheKey";

  /**
   * The caption for the validity parameter
   */
  public static final String VALIDITY_PARAMETER = "validityFile";

  /**
   * The caption for the validity override parameter
   */
  public static final String VALIDITY_OVERRIDE_PARAMETER = "dispatcher.caching";

  /**
   * The caption for the condition that caching is turned off
   */
  public static final String CACHING_OFF = "off";

  /**
   * The caption for the condition that caching is turned on
   */
  public static final String CACHING_ON = "on";

  /**
   * Main configuration bean of the dispatcher.
   * <p>
   * This config will control things like <li>contract prefixes/suffixes <li>
   * resolver to use
   */
  private WritableDispatcherBean config;

  /**
   * Cocoon 2.2 compatible method. Allow that the ServiceManager is be set via
   * e.g. spring
   * 
   * @param manager
   *          manger to use in the context
   */
  public void setManager(ServiceManager manager) {
    this.manager = manager;
  }

  /**
   * The level of xpath support we need.
   * <p>
   * If you choose enhanced you can inject/create node with enhanced xpath
   * expression like e.g. /root/child[id='test']
   * <p>
   * Supported values are: basic | enhanced
   */
  private String xpathSupport;

  /**
   * @param xpathSupport
   *          the xpathSupport to set
   */
  public synchronized void setXpathSupport(String xpathSupport) {
    this.xpathSupport = xpathSupport;
  }

  /**
   * The prefix that we need to use to resolve a concrete contract.
   */
  private String contractUriPrefix;

  /**
   * The parameter caption use to pass the requestId from the sitemap to the
   * contract
   */
  public static final String DISPATCHER_REQUEST_ATTRIBUTE = "request";

  /**
   * The caption of the key for the default variables
   */
  private static final String DEFAULT_VARIABLES = "defaultVariables";

  /**
   * preparing the parser that will pick up the result and pass it to the
   * consumer again.
   */
  SAXParser parser = null;

  /*
   * @see
   * org.apache.cocoon.transformation.AbstractSAXTransformer#configure(org.apache
   * .avalon.framework.configuration.Configuration)
   */
  public void configure(Configuration configuration)
      throws ConfigurationException {
    // creating a new config and store the general not request specific
    // parameters here
    config = new WritableDispatcherBean();
    config.setStaxHelper(new StAX());
    boolean allowXml = configuration.getChild("allowXml").getValueAsBoolean(
        false);
    config.setAllowXmlProperties(allowXml);
    contractUriPrefix = configuration.getChild("contractUriPrefix").getValue(
        "cocoon://resolve.contract.");
    config.setContractUriSufix(configuration.getChild("contractUriSufix")
        .getValue(""));
    xpathSupport = configuration.getChild("xpathSupport").getValue("basic");
    boolean shrink = configuration.getChild("shrink").getValueAsBoolean(true);
    config.setShrink(shrink);
    setNewTransformerFactory();
  }

  /**
   * Cocoon 2.2 compatible method. Allow that the WritableDispatcherBean is be
   * set via e.g. spring
   * 
   * @param config
   *          the configuration to use.
   */
  public void setConfig(WritableDispatcherBean config) {
    this.config = config;
    if (config.getTransFact() == null) {
      setNewTransformerFactory();
    }
    contractUriPrefix = config.getContractUriPrefix();
  }

  private void setNewTransformerFactory()
      throws TransformerFactoryConfigurationError {
    // FIXME: is this the best way to get an instance in cocoon?
    TransformerFactory tfactory = TransformerFactory.newInstance();
    tfactory.setURIResolver(this);
    tfactory.setErrorListener(new TraxErrorHandler(getLogger()));
    config.setTransFact(tfactory);
  }

  // we do all the heavy stuff later and only prepare the basics here,
  // this enhance the response time while caching.
  public void setup(SourceResolver resolver, Map objectModel, String src,
      Parameters par) throws ProcessingException, SAXException, IOException {
    super.setup(resolver, objectModel, src, par);

    this.requestId = parameters
        .getParameter(DISPATCHER_REQUEST_ATTRIBUTE, null);
    this.cacheKey = parameters.getParameter(CACHE_PARAMETER, null);
    if (null == this.cacheKey)
      getLogger().warn(
          "Caching not activated! Declare the CACHE_KEY_PARAMETER="
              + CACHE_PARAMETER + " in your sitemap.");
    this.validityFile = parameters.getParameter(VALIDITY_PARAMETER, null);
    this.validityChecking = parameters.getParameter(
        VALIDITY_OVERRIDE_PARAMETER, "");
    if (requestId == null) {
      String error = "dispatcherError:\n"
          + "You have to set the \"request\" parameter in the sitemap!";
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    this.requestedFormat = parameters.getParameter(Captions.TYPE_ATT, null);
    /*
     * We need to change now the contract uri prefixes since in cocoon we need
     * to add the current format.
     */
    String loacalPrefix = contractUriPrefix + requestedFormat + ".";
    config.setContractUriPrefix(loacalPrefix);
    if (requestedFormat == null) {
      String error = "dispatcherError:\n"
          + "You have to set the \"type\" parameter in the sitemap!";
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    this.cacheKey += requestedFormat;
    if (null == m_resolver) {
      try {
        m_resolver = (org.apache.excalibur.source.SourceResolver) manager
            .lookup(SourceResolver.ROLE);
        config.setResolver(new CocoonResolver(m_resolver));
      } catch (ServiceException e) {
        throw new ProcessingException(e);
      }
    }
  }

  /*
   * Here we just start recording to use them later on.
   * 
   * In the future we may want to implement a sax based structurer
   * implementation again.
   * 
   * old transformer link @see
   * http://svn.apache.org/viewvc?view=rev&revision=694101
   * 
   * @see
   * org.apache.cocoon.transformation.AbstractSAXTransformer#startDocument()
   */
  public void startDocument() throws SAXException {
    // just start the recording
    startSerializedXMLRecording(null);
  }

  /*
   * Here we pick up the recorder result and start the processing on it.
   * 
   * @see org.apache.cocoon.transformation.AbstractSAXTransformer#endDocument()
   */
  public void endDocument() throws SAXException {

    // start structurer routine
    Structurer structurer = null;
    // need to get the properties for this uri
    HashMap<String, Object> map = new HashMap<String, Object>();
    String propertyURI = "cocoon://" + requestId + ".props";
    try {
      // get the source representation of the propertyURI
      Source propsSource = m_resolver.resolveURI(propertyURI);
      if (propsSource != null) {
        // get the stream
        InputStream stream = new BufferedInputStream(propsSource
            .getInputStream());
        /*
         * we need either just the bytes of the stream (if we allow xml
         * properties) or we need to process it extracting the properties.
         */
        if (config.isAllowXmlProperties()) {
          // get the bytes from the stream
          byte[] properties = IOUtils.getStreamAsByteArray(stream);
          /*
           * add the bytes to the properties map later on they will be picked up
           * and parsed to a dom node.
           */
          map.put(DEFAULT_VARIABLES, properties);
        } else {
          // extract the properties of the in coming stream
          XMLProperties.parseProperties(stream, map);
        }
        // release source - you need to ALWAYS do this!
        release(propsSource);
      }
    } catch (Exception e) {
      throw new SAXException(e);
    }
    // which implementation do we want
    if (xpathSupport.equals("enhanced")) {
      /*
       * The axiom implementation is an object model approach to StAX. It allows
       * you a dom like navigation (allocate xpath nodes), adding of child
       * elements in this xpath statement and many more.
       */
      structurer = new XMLStructurerAxiom((DispatcherBean) config, map);
    } else {
      /*
       * The stax implementationis 100% StAX even the generation of the
       * resulting document is done with StAX. However it does not offer real
       * support of xpath expressions.
       */
      structurer = new XMLStructurer((DispatcherBean) config, map);
    }

    try {
      // the recorded document we just streamed
      String document = null;
      try {
        // request the information from the recorder
        document = super.endSerializedXMLRecording();
      } catch (ProcessingException e) {
        throw new SAXException(e);
      }
      // get the result of the structurer as stream
      InputStream result = structurer.execute(new BufferedInputStream(
          new ByteArrayInputStream(document.getBytes())), requestedFormat);
      // requesting a parser
      parser = (SAXParser) manager.lookup(SAXParser.ROLE);
      // adding the result to the consumer
      parser.parse(new InputSource(result), super.xmlConsumer);
    } catch (DispatcherException e) {
      throw new SAXException(e);
    } catch (Exception e) {
      throw new SAXException(e);
    } finally {
      if (null != parser) {
        // release parser - you need to ALWAYS do this!
        manager.release(parser);
      }
    }
  }

  /**
   * Generate the unique key. This key must be unique inside the space of this
   * component.
   * 
   * @return The generated key hashes the src
   */
  public Serializable getKey() {
    return this.cacheKey;
  }

  /**
   * Generate the validity object.
   * 
   * @return The generated validity object or <code>null</code> if the component
   *         is currently not cacheable.
   */
  public SourceValidity getValidity() {
    // You can either request URL?dispatcher.caching=off
    // or add this property to forrest.properties.xml
    // to force a SourceValidity.INVALID
    if (null != validityFile && !(validityChecking.equals(CACHING_OFF))) {
      Source resolveSource = null;
      try {
        resolveSource = m_resolver.resolveURI(validityFile);
        this.validity = resolveSource.getValidity();
      } catch (Exception e) {
        getLogger().error(e.getMessage());
      } finally {
        release(resolveSource);
      }
    } else
      this.validity = null;
    return this.validity;
  }

  /**
   * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
   */
  public void release(Source source) {
    if (source != null) {
      m_resolver.release(source);
    }
  }

  /**
   * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
   */
  public void service(ServiceManager manager) throws ServiceException {
    super.service(manager);
  }

  /**
   * @see org.apache.avalon.framework.activity.Disposable#dispose()
   */
  public void dispose() {
    if (null != this.manager) {
      if (null != m_resolver)
        this.manager.release(m_resolver);
      this.manager = null;
    }
    m_resolver = null;
    super.dispose();
  }

  /**
   * Recycle the component
   */
  public void recycle() {
    this.validity = null;
    super.recycle();

  }

  /*
   * From URIResolver, copied from TraxProcessor
   * 
   * @see javax.xml.transform.URIResolver#resolve(java.lang.String,
   * java.lang.String)
   */
  public javax.xml.transform.Source resolve(String href, String base)
      throws TransformerException {
    if (getLogger().isDebugEnabled()) {
      getLogger().debug(
          "resolve(href = " + href + ", base = " + base + "); resolver = "
              + m_resolver);
    }

    Source xslSource = null;
    try {
      if (base == null || href.indexOf(":") > 1) {
        // Null base - href must be an absolute URL
        xslSource = m_resolver.resolveURI(href);
      } else if (href.length() == 0) {
        // Empty href resolves to base
        xslSource = m_resolver.resolveURI(base);
      } else {
        // is the base a file or a real m_url
        if (!base.startsWith("file:")) {
          int lastPathElementPos = base.lastIndexOf('/');
          if (lastPathElementPos == -1) {
            // this should never occur as the base should
            // always be protocol:/....
            return null; // we can't resolve this
          } else {
            xslSource = m_resolver.resolveURI(base.substring(0,
                lastPathElementPos)
                + "/" + href);
          }
        } else {
          File parent = new File(base.substring(5));
          File parent2 = new File(parent.getParentFile(), href);

          try {
            xslSource = m_resolver.resolveURI(parent2.toURI().toURL().toExternalForm());
          } catch (IllegalArgumentException e1) {
            getLogger().error(e1.getMessage());

            return null;
          } catch (java.net.MalformedURLException e2) {
            getLogger().error(e2.getMessage());

            return null;
          }
        }
      }

      InputSource is = getInputSource(xslSource);

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            "xslSource = " + xslSource + ", system id = " + xslSource.getURI());
      }

      return new StreamSource(is.getByteStream(), is.getSystemId());
    } catch (SourceException e) {
      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            "Failed to resolve " + href + "(base = " + base + "), return null",
            e);
      }

      // CZ: To obtain the same behaviour as when the resource is
      // transformed by the XSLT Transformer we should return null here.
      return null;
    } catch (java.net.MalformedURLException mue) {
      getLogger().debug(
          "Failed to resolve " + href + "(base = " + base + "), return null",
          mue);

      return null;
    } catch (IOException ioe) {
      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            "Failed to resolve " + href + "(base = " + base + "), return null",
            ioe);
      }

      return null;
    } finally {
      release(xslSource);
    }
  }

  /**
   * Return a new <code>InputSource</code> object that uses the <code>
   * InputStream</code> and the system ID of the <code>Source</code> object.
   * 
   * @throws IOException
   *           if I/O error occured.
   */
  private static InputSource getInputSource(final Source source)
      throws IOException, SourceException {
    final InputSource newObject = new InputSource(source.getInputStream());
    newObject.setSystemId(source.getURI());
    return newObject;
  }
}
