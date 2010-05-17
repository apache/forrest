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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
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
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.cocoon.util.TraxErrorHandler;
import org.apache.cocoon.xml.RedundantNamespacesFilter;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.api.Resolver;
import org.apache.forrest.dispatcher.config.WritableDispatcherBean;
import org.apache.forrest.dispatcher.exception.ContractException;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.factories.ContractFactory;
import org.apache.forrest.dispatcher.impl.CocoonResolver;
import org.apache.forrest.dispatcher.impl.helper.AXIOMXPathCreate;
import org.apache.forrest.dispatcher.impl.helper.Captions;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.apache.forrest.dispatcher.impl.helper.StreamHelper;
import org.apache.forrest.dispatcher.impl.helper.XMLProperties;
import org.jaxen.JaxenException;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A cocoon transformer class for the dispatcher.
 * 
 * Unlike the wrapper this class will do the actual work and only reuse the
 * contract handling from the core. This class is nearly the same as we had
 * before just switching DOM for StAX/AXIOM.
 * 
 * @version 1.0
 * 
 */
public class DispatcherTransformer extends AbstractSAXTransformer implements
    Disposable, CacheableProcessingComponent, URIResolver {

  private static final String PATH_PREFIX = "result";

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
   * This config will control things like
   * <li>contract prefixes/suffixes
   * <li>
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
  private SAXParser parser = null;
  private OMFactory factory;
  private ContractFactory contractRep;

  /*
   * Housekeeping variables to indicate the state of processing. Remember SAX is
   * a stream.
   */
  /**
   * whether or not we need to process the properties
   */
  private boolean insideProperties = false;
  /**
   * whether or not we need are in the requested structurer
   */
  private boolean includeNodes = true;
  private OMElement root;

  private OMElement pathNode;

  private Resolver resolverDispatcher;

  private Contract contract;

  private HashMap<String, Object> map;

  private HashMap<String, Object> localParams;

  private String currentProperty;

  private InputStream dataStream;

  private String prefixString;

  private TransformerFactory tfactory = TransformerFactory.newInstance();

  private EntityResolver entityResolver;

  private boolean multipleRoot;

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
    // are we allowing xml properties?
    boolean allowXml = configuration.getChild("allowXml").getValueAsBoolean(
        false);
    config.setAllowXmlProperties(allowXml);
    // set the prefix for the contract resolving
    contractUriPrefix = configuration.getChild("contractUriPrefix").getValue(
        "cocoon://resolve.contract.");
    // set the suffix for the contract
    config.setContractUriSufix(configuration.getChild("contractUriSufix")
        .getValue(""));
    // what level of xpath support do we want to allow?
    xpathSupport = configuration.getChild("xpathSupport").getValue("basic");
    // Do we want to shrink xml properties that have only a @value?
    boolean shrink = configuration.getChild("shrink").getValueAsBoolean(true);
    config.setShrink(shrink);
    config.setEntityResolver(this.entityResolver);
    // request all factories to be created at this point since it is better to
    // create them only once
    try {
      setNewTransformerFactory();
    } catch (ProcessingException e) {
      throw new ConfigurationException(e.getLocalizedMessage(), e);
    } catch (TransformerFactoryConfigurationError e) {
      throw new ConfigurationException(e.getLocalizedMessage(), e);
    }
  }

  /**
   * Cocoon 2.2 compatible method. Allow that the WritableDispatcherBean is be
   * set via e.g. spring
   * 
   * @param config
   *          the configuration to use.
   * @throws TransformerFactoryConfigurationError
   * @throws ProcessingException
   */
  public void setConfig(WritableDispatcherBean config)
      throws ProcessingException, TransformerFactoryConfigurationError {
    this.config = config;
    if (config.getTransFact() == null) {
      setNewTransformerFactory();
    }
    contractUriPrefix = config.getContractUriPrefix();
  }

  /**
   * Will prepare the factories that we need in further processing
   * 
   * @throws TransformerFactoryConfigurationError
   * @throws ProcessingException
   */
  private void setNewTransformerFactory()
      throws TransformerFactoryConfigurationError, ProcessingException {
    // set the uri resolver the same as this class
    tfactory.setURIResolver(this);
    // we want to set the error handler here to make sure it is intitialized
    tfactory.setErrorListener(new TraxErrorHandler(getLogger()));
    // add the factory to the config
    config.setTransFact(tfactory);
    // get the OM factory
    this.factory = OMAbstractFactory.getOMFactory();
    // get the contract factory
    this.contractRep = new ContractFactory(config);
    try {
      parser = (SAXParser) manager.lookup(SAXParser.ROLE);
    } catch (ServiceException e) {
      String error = "dispatcherError:\n"
          + "SAXParser could not be setup! Abort";
      getLogger().error(error);
      throw new ProcessingException(error);
    }
  }

  /*
   * @see
   * org.apache.cocoon.transformation.AbstractSAXTransformer#setup(org.apache
   * .cocoon.environment.SourceResolver, java.util.Map, java.lang.String,
   * org.apache.avalon.framework.parameters.Parameters)
   */
  public void setup(SourceResolver resolver, Map objectModel, String src,
      Parameters par) throws ProcessingException, SAXException, IOException {
    /*
     * we do all the heavy stuff later and only prepare the basics here, this
     * enhance the response time while caching.
     */
    // setup our super class
    super.setup(resolver, objectModel, src, par);

    // get the id of this request
    this.requestId = parameters
        .getParameter(DISPATCHER_REQUEST_ATTRIBUTE, null);
    // get the external cache key
    this.cacheKey = parameters.getParameter(CACHE_PARAMETER, null);
    if (null == this.cacheKey)
      getLogger().warn(
          "Caching not activated! Declare the CACHE_KEY_PARAMETER="
              + CACHE_PARAMETER + " in your sitemap.");
    /*
     * which is the file use to watch for changes (normally the structurer of
     * the request). As long the file does not change we are using the cache
     * object.
     */
    this.validityFile = parameters.getParameter(VALIDITY_PARAMETER, null);
    // do we want to force that is caching is turned off?
    this.validityChecking = parameters.getParameter(
        VALIDITY_OVERRIDE_PARAMETER, "");
    if (requestId == null) {
      String error = "dispatcherError:\n"
          + "You have to set the \"request\" parameter in the sitemap!";
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    // which format we want to process?
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
    multipleRoot = parameters.getParameterAsBoolean("multipleRoot", false);
    // add the format to the cache key
    this.cacheKey += requestedFormat;
    if (null == m_resolver) {
      try {
        m_resolver = (org.apache.excalibur.source.SourceResolver) manager
            .lookup(SourceResolver.ROLE);
        // add the resolver we use to the dispatcher
        resolverDispatcher = new CocoonResolver(m_resolver);
        config.setResolver(resolverDispatcher);
      } catch (ServiceException e) {
        throw new ProcessingException(e);
      }
    }
  }

  /*
   * old transformer link @see
   * http://svn.apache.org/viewvc?view=rev&revision=694101
   * 
   * @see
   * org.apache.cocoon.transformation.AbstractSAXTransformer#startElement(String
   * uri, String name, String raw, Attributes attr)
   */
  public void startElement(String uri, String name, String raw, Attributes attr)
      throws SAXException {
    // Process start element event
    // Are we inside of properties? If so we need to record the elements.
    if (this.insideProperties && this.includeNodes) {
      // just start the recording
      super.startElement(uri, name, raw, attr);
      // startSerializedXMLRecording(null);
    } else if (Captions.NS.equals(uri)) {
      // we are in the dispatcher ns
      try {
        // We are in the dispatcher ns.
        getLogger().debug("Starting dispatcher element: " + raw);
        if (Captions.STRUCTURE_ELEMENT.equals(name)) {
          // we are in a structurer definition
          structurerProcessingStart(attr);
        }
        if (this.includeNodes) {
          if (Captions.HOOK_ELEMENT.equals(name)) {
            // we are inside a hook element
            hookProcessingStart(name, raw, attr);
          } else if (Captions.CONTRACT_ELEMENT.equals(name)) {
            // we are inside a contract element
            contractProcessingStart(attr);
          } else if (Captions.PROPERTY_ELEMENT.equals(name)) {
            // we are inside a property element
            // this.insideProperties = true;
            propertyProcessingStart(uri, name, raw, attr);
          }
        }
      } catch (Exception e) {
        throw new SAXException(e);
      }
    } else {
      // Do we want to allow to have structurer definitions as nested elements?
      if (!this.insideProperties && this.includeNodes) {
        // super.startElement(uri, name, raw, attr);
      }
    }

  }

  /*
   * @see
   * org.apache.cocoon.transformation.AbstractSAXTransformer#endElement(java
   * .lang.String, java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String name, String raw)
      throws SAXException {
    getLogger().debug("Ending element: " + raw);
    if (Captions.NS.equals(uri)) {
      // we are in the dispatcher ns
      try {
        if (Captions.STRUCTURE_ELEMENT.equals(name)) {
          // we are in a structurer end element
          if (includeNodes) {
            includeNodes = false;
          }
        } else if (Captions.HOOK_ELEMENT.equals(name) && this.includeNodes) {
          // we are inside a hook end element
          pathNode = (OMElement) pathNode.getParent();
        } else if (Captions.CONTRACT_ELEMENT.equals(name) && this.includeNodes) {
          // we are inside a contract end element
          contractProcessingEnd();
        } else if (Captions.PROPERTY_ELEMENT.equals(name) && this.includeNodes) {
          // we are inside a property end element
          if (config.isAllowXmlProperties()) {
            String property = null;
            try {
              // XMLizable endSAXRecording = super.endSAXRecording();
              property = prefixString + endSerializedXMLRecording()
                  + "</forrest:property>";
              insideProperties = false;
              getLogger().debug(
                  "super.endSerializedXMLRecording(): " + property);
            } catch (Exception e) {
              throw new SAXException(e);
            }
            if (null != property) {
              localParams.put(currentProperty, property.getBytes("UTF-8"));
            }
          }
        }
      } catch (Exception e) {
        throw new SAXException(e);
      }
    } else if (this.insideProperties && this.includeNodes) {
      super.endElement(uri, name, raw);
    }
  }

  public void characters(char c[], int start, int len) throws SAXException {
    /*
     * only if we in properties mode we want to record the characters.
     */
    if (includeNodes && insideProperties) {
      super.characters(c, start, len);
    }
  }

  public void startDocument() throws SAXException {
    // Add the namespace filter to our own output.
    RedundantNamespacesFilter nsPipe = new RedundantNamespacesFilter();
    if (this.xmlConsumer != null) {
      nsPipe.setConsumer(this.xmlConsumer);
    } else {
      nsPipe.setContentHandler(this.contentHandler);
    }
    setConsumer(nsPipe);
  }

  public void endDocument() throws SAXException {
    structurerProcessingEnd();
  }

  /*
   * do nothing on the following methods, since we do not use them
   */
  public void ignorableWhitespace(char c[], int start, int len)
      throws SAXException {
  }

  public void startCDATA() throws SAXException {
  }

  public void endCDATA() throws SAXException {
  }

  public void comment(char[] ary, int start, int length) throws SAXException {
  }

  /**
   * Will execute the contract and process the result.
   * 
   * @throws ContractException
   * @throws IOException
   * @throws JaxenException
   * @throws XMLStreamException
   */
  private void contractProcessingEnd() throws ContractException, IOException,
      JaxenException, XMLStreamException {
    // get the result stream from contract by executing it
    InputStream resultStream = contract.execute(dataStream, localParams);
    // close data stream
    StreamHelper.closeStream(dataStream);
    // add the result of the contract to the overall result set
    try {
      processContractResult(resultStream, pathNode);
    } catch (OMException e) {
      String error = DispatcherException.ERROR_500 + "\n"
      + "component: ContractBean" + "\n"
      + "message: Could not setup contractBean \"" + contract.getName() + "\". It seems that the result of the contract " +
      		"is returning an empty document. This normally happens when no forrest:content container is returned or" +
      		" the container does not contains any forrest:part.\n\n"
      + "dispatcherErrorStack:\n" + e;
  getLogger().error(error);
  throw new ContractException(error);
    }
    // close the result Stream
    StreamHelper.closeStream(resultStream);
  }

  /**
   * Process the contract result to add it to the overall result set.
   * 
   * @param resultStream
   * @param pathNode
   * @throws XMLStreamException
   * @throws JaxenException
   */
  @SuppressWarnings("unchecked")
  private void processContractResult(InputStream resultStream,
      OMElement pathNode) throws XMLStreamException, JaxenException {
    // get xml stream reader
    XMLStreamReader contractResultReader = config.getStaxHelper().getReader(
        resultStream);
    // get Axiom builder
    StAXOMBuilder builder = new StAXOMBuilder(contractResultReader);
    // get the document element
    OMElement content = builder.getDocumentElement();
    /*
     * For full blown ns support we need to get all ns from the result. This
     * will be passed later to the XPath processor.
     */
    Iterator<OMNamespace> ns = content.getAllDeclaredNamespaces();
    HashSet<OMNamespace> spaces = new HashSet<OMNamespace>();
    while (ns.hasNext()) {
      OMNamespace space = ns.next();
      spaces.add(space);
    }
    // get the different parts of the contract
    Iterator<OMElement> parts = content.getChildrenWithName(qIt(
        Captions.NS_CONTRACTS, Captions.PART_ELEMENT));
    while (parts.hasNext()) {
      // while we have more parts, get the next part
      OMElement part = parts.next();
      // where do we need to inject it
      String xpath = part.getAttributeValue(qIt(Captions.RESULT_XPATH));
      // get the children of a part. This children needs to be injected in
      // the result
      Iterator<OMNode> children = part.getChildren();

      if (xpath == null) {
        // when we do not have a xpath to inject we will inject it in the
        // current loaction
        while (children.hasNext()) {
          OMNode child = (OMNode) children.next();
          pathNode.addChild(child);
        }
      } else {
        // make sure the xpath is starting with "/"
        if (!xpath.startsWith("/")) {
          String message = contract.getName()
              + " is using relative injection points which is not permited. "
              + "Please fix this ASAP, for now we will do it for you.";
          getLogger().warn(message);
          xpath = "/" + xpath;
        }
        // lookup the node where we want to inject the result part
        xpath = "/" + PATH_PREFIX + xpath;
        // we need to feed the xpathSelector with the ns we may have
        AXIOMXPathCreate xpathSelector = new AXIOMXPathCreate(xpath);
        // add all namespaces that are known to the selector
        for (OMNamespace space : spaces) {
          xpathSelector
              .addNamespace(space.getPrefix(), space.getNamespaceURI());
        }
        // request the final inject point (node which will be used to inject the
        // result)
        OMElement injectionPoint = (OMElement) xpathSelector.selectSingleNode(
            root, true);
        // now iterate through the children of the result part and append the
        // content to the injection point
        while (children.hasNext()) {
          OMNode child = (OMNode) children.next();
          injectionPoint.addChild(child);
        }

      }
    }
  }

  /**
   * Helper method to create a qname
   * 
   * @param name
   *          the element name
   * @return a qualified qname
   */
  private QName qIt(String name) {
    return new QName(name);
  }

  /**
   * Helper method to create a qname
   * 
   * @param name
   *          the element name
   * @param uri
   *          the ns
   * @return a qualified qname
   */
  private QName qIt(String uri, String name) {
    return new QName(uri, name);
  }

  /**
   * Finish the processing of the structurer
   * 
   * @param raw
   * @throws SAXException
   */
  private void structurerProcessingEnd() throws SAXException {
    try {
      // get the result of the structurer as stream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      OMElement firstElement = root.getFirstElement();

      if (null != firstElement & !multipleRoot) {
        firstElement.serialize(out);
      } else {
        root.serialize(out);
      }

      InputSource is = new InputSource(new StringReader(out.toString("UTF-8")));
      // adding the result to the consumer
      parser.parse(is, super.xmlConsumer);
    } catch (Exception e) {
      throw new SAXException(e);
    }

  }

  /**
   * Start the processing of the properties including setting the correct state
   * for the next event round
   * 
   * @param uri
   * @param name
   * @param raw
   * @param attr
   * @throws SAXException
   */
  private void propertyProcessingStart(String uri, String name, String raw,
      Attributes attr) throws SAXException {
    // determine the name and a possible value
    String id = null, value = null;
    String attributesString = "";
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String localValue = attr.getValue(i);
      attributesString += " " + localName + "=\"" + localValue + "\"";
      if (Captions.NAME_ATT.equals(localName)) {
        id = localValue;
      } else if (Captions.VALUE_ATT.equals(localName)) {
        value = localValue;
      }
    }
    currentProperty = id;
    // if we allow xml properties we will just record them
    if (config.isAllowXmlProperties()) {
      // just start the recording
      prefixString = "<" + raw + attributesString + ">";
      startSerializedXMLRecording((XMLUtils.createPropertiesForXML(true)));
      insideProperties = true;
    } else {
      // if we do not allow xml we will use a simple key/value approach
      if (id != null && value != null) {
        localParams.put(id, new String(value));
      }
    }

  }

  /**
   * Start the processing of the contract this includes to resolve the data
   * stream and the actual contract.
   * 
   * @param attr
   * @throws SAXException
   */
  private void contractProcessingStart(Attributes attr) throws SAXException {
    String name = "", data = null;
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String value = attr.getValue(i);
      if (Captions.NAME_ATT.equals(localName)) {
        // get the name of the contract
        name = value;
      } else if (Captions.DATA_ATT.equals(localName)) {
        // see whether we have a defined dataUri
        data = value;
      }
    }
    dataStream = null;
    if (null != data && !data.equals("")) {
      // try resolving the dataUri
      try {
        dataStream = resolverDispatcher.resolve(data);
      } catch (Exception e) {
        String error = DispatcherException.ERROR_500 + "\n"
            + "component: ContractBean" + "\n" + "message: ContractBean \""
            + name + "\" has thrown an exception " + "resolving the dataUri \""
            + data + "\".\n\n" + "dispatcherErrorStack:\n" + e;
        getLogger().error(error);
        throw new SAXException(error);
      }
    }
    try {

      // get the contract
      contract = contractRep.resolve(name);
      // prepare empty properties map
      localParams = new HashMap<String, Object>(map);
      localParams.put("contract.name", name);
    } catch (Exception e) {
      String error = DispatcherException.ERROR_500 + "\n"
          + "component: ContractBean" + "\n"
          + "message: Could not setup contractBean \"" + name + "\".\n\n"
          + "dispatcherErrorStack:\n" + e;
      getLogger().error(error);
      throw new SAXException(error);
    }

  }

  /**
   * Start the processing of the hooks. Actually we just create a element in the
   * result model with the same name and attributes as the incoming event.
   * 
   * @param name
   * @param raw
   * @param attr
   */
  private void hookProcessingStart(String name, String raw, Attributes attr) {
    OMElement element = factory.createOMElement(name, Captions.NS, "forrest");
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String value = attr.getValue(i);
      OMAttribute attribute = factory.createOMAttribute(localName, null, value);
      element.addAttribute(attribute);
    }
    pathNode.addChild(element);
    pathNode = element;
  }

  /**
   * view type="?" Here we check which format we have in the view. That should
   * be extended to matching the requested format
   * 
   * @param attr
   * @throws JaxenException
   * @throws SAXException
   */
  private void structurerProcessingStart(Attributes attr)
      throws DispatcherException {
    String m_type = "", path = "";
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String value = attr.getValue(i);
      // determine what type (format) we have on the structurer
      if (localName.equals(Captions.TYPE_ATT)) {
        m_type = value;
      }
      /*
       * Determine whether we have a hooks path attribute. If so it determines
       * the smallest common path (e.g. <code>/html/body</code>)
       */
      if (localName.equals(Captions.HOOKS_ATT)) {
        // adding the default path
        boolean startsWithRoot = "/".equals(String.valueOf(value.charAt(0)));
        if (!startsWithRoot) {
          value = "/" + value;
        } else if (value.length() == 1 && startsWithRoot) {
          value = "";
        }
        path += value;
      }
    }
    // are we in the format that have been requested?
    if (requestedFormat.equals(m_type)) {
      try {
        // need to get the properties for this uri
        map = new HashMap<String, Object>();
        /*
         * the properties are provided by a match in cocoon and are generated
         * individually for each request
         */
        String propertyURI = "cocoon://" + requestId + ".props";
        // get the source representation of the propertyURI
        Source propsSource = m_resolver.resolveURI(propertyURI);
        if (propsSource != null) {
          // get an input stream from the properties
          InputStream stream = new BufferedInputStream(propsSource
              .getInputStream());
          /*
           * we need either just the bytes of the stream (if we allow xml
           * properties) or we need to process it: extracting the properties.
           */
          if (config.isAllowXmlProperties()) {
            // get the bytes from the stream
            byte[] properties = IOUtils.getStreamAsByteArray(stream);
            /*
             * add the bytes to the properties map later on they will be picked
             * up and parsed to a dom node.
             */
            map.put(DEFAULT_VARIABLES, properties);
          } else {
            // extract the properties of the incoming stream
            XMLProperties.parseProperties(stream, map);
          }
          // release source - you need to ALWAYS do this!
          release(propsSource);
        }
        /*
         * Preparing the model by creating a root document/element. Afterward we
         * need to strip the root element again.
         */
        OMDocument doc = factory.createOMDocument();
        // creating the root node for the xml document
        root = factory.createOMElement(PATH_PREFIX, null);
        // adding root element to the document (much like in DOM)
        doc.addChild(root);
        /*
         * request a axiom xpath resolver that will return a axiom element for a
         * given xpath expression. If the node does not exist, it is created and
         * then returned.
         */
        AXIOMXPathCreate xpath = new AXIOMXPathCreate("/" + PATH_PREFIX + path);
        // get the node (if it does not exist, create it)
        pathNode = (OMElement) xpath.selectSingleNode(root, true);
        // we need to set the status variable, indicating we need process the
        // children.
        this.includeNodes = true;
      } catch (Exception e) {
        throw new DispatcherException(e);
      }
    } else {
      pathNode = null; // unset path because we do not process the node
      this.includeNodes = false;
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
    this.entityResolver = (EntityResolver) this.manager
        .lookup(org.apache.excalibur.xml.EntityResolver.ROLE);
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
    this.includeNodes = false;
    this.insideProperties = false;
    this.requestId = "";
    this.validity = null;
    this.root = null;
    this.pathNode = null;
    this.contract = null;
    this.map = null;
    this.localParams = null;
    this.currentProperty = null;
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
   * InputStream</code>
   * and the system ID of the <code>Source</code> object.
   * 
   * @throws IOException
   *           if I/O error occured.
   */
  private static InputSource getInputSource(final Source source)
      throws IOException, SourceException {
    final InputSource inputSource = new InputSource(new InputStreamReader(source.getInputStream(), "UTF-8"));
    inputSource.setEncoding("UTF-8");
    inputSource.setSystemId(source.getURI());
    return inputSource;
  }
}
