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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractSAXTransformer;
import org.apache.cocoon.xml.IncludeXMLConsumer;
import org.apache.cocoon.xml.XMLUtils;
import org.apache.cocoon.xml.dom.DOMBuilder;
import org.apache.cocoon.xml.dom.DOMUtil;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.AggregatedValidity;
import org.apache.excalibur.xml.xpath.XPathProcessor;
import org.apache.forrest.dispatcher.ContractBean;
import org.apache.forrest.dispatcher.ContractBeanDOMImpl;
import org.apache.forrest.dispatcher.DispatcherException;
import org.apache.forrest.dispatcher.DispatcherHelper;
import org.apache.forrest.dispatcher.lenya.xml.NamespaceHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class DispatcherTransformer extends AbstractSAXTransformer implements
    Disposable, CacheableProcessingComponent, URIResolver {

  /* Node and attribute names */
  /**
   * <code>forrest:view</code> element is used to structure contracts and
   * hooks into format specific container. It allows to configure x different
   * format for the request.
   * 
   * <pre>
   *          &lt;&lt;strong&gt;forrest:view&lt;/strong&gt; format=&quot;html&quot;/&gt;
   * </pre>
   */
  public static final String STRUCTURER_ELEMENT = "view";

  /**
   * <code>forrest:view</code> element is used to structure contracts and
   * hooks into format specific container. It allows to configure x different
   * format for the request. The attribute which identify a view format is
   * <strong>format</strong>.
   * 
   * <pre>
   *          &lt;forrest:view &lt;strong&gt;format&lt;/strong&gt;=&quot;html&quot;/&gt;
   * </pre>
   */
  public static final String STRUCTURER_FORMAT_ATTRIBUTE = "type";

  public static final String STRUCTURER_HOOK_XPATH_ATTRIBUTE = "hooksXpath";

  protected String requestedFormat;

  private boolean includeNodes = true;

  /**
   * <code>forrest:hook</code> element is used to structure the elements. It
   * allows to create skeletons that a designer needs to apply a specific layout
   * via e.g. css. In html for example
   * 
   * <pre>
   *          &lt;forrest:hook name=&quot;test&quot;/&gt;
   * </pre>
   * 
   * <p>
   * will be transformed to
   * </p>
   * 
   * <pre>
   *          &lt;div id=&quot;test&quot;/&gt;
   * </pre>
   */
  public static final String DISPATCHER_HOOK_ELEMENT = "hook";

  /**
   * <code>forrest:css</code> element is used to apply a specific layout via
   * css. In html for example
   * 
   * <pre>
   *          &lt;forrest:css url=&quot;pelt.basic.css&quot; media=&quot;screen&quot; theme=&quot;Pelt&quot;/&gt;
   * </pre>
   * 
   * <p>
   * will be transformed to
   * </p>
   * 
   * <pre>
   *          &lt;link media=&quot;screen&quot; href=&quot;../themes/pelt.basic.css&quot; title=&quot;Pelt&quot; rel=&quot;stylesheet&quot; type=&quot;text/css&quot; /&gt;
   * </pre>
   */
  public static final String DISPATCHER_CSS_ELEMENT = "css";

  private String propertyID;

  private boolean insideProperties = false;

  /**
   * Convenience object, so we don't need to create an AttributesImpl for every
   * element.
   */
  protected AttributesImpl attributes;

  protected String currentFormat;

  protected ContractBean contract;

  /**
   * The namespace used by the transformer for the SAX events filtering. This
   * either equals to the {@link #defaultNamespaceURI} or to the value set by
   * the <code>namespaceURI</code> sitemap parameter for the pipeline. Must
   * never be null.
   */
  protected String namespaceURI;

  /**
   * This is the default namespace used by the transformer. Implementations
   * should set its value in the constructor. Must never be null.
   */
  protected String defaultNamespaceURI;

  private DOMBuilder builder, dispatcherBuilder;

  private Transformer structurerTransformer;

  private Element rootNode;

  private XPathProcessor processor;

  private Document document;

  private NamespaceHelper dispatcher;

  private Map storedPrefixMap;

  private String path = null;

  private boolean recording, allowMarkup;

  public static final String DISPATCHER_ALLOW_MARKUP = "allowMarkup";

  private DispatcherHelper dispatcherHelper;

  private boolean insideStructurer = false;

  private String hooksXSL, cacheKey, validityFile;

  private HashMap hooksPosition;

  private HashMap parameterHelper;

  private SourceResolver m_resolver;

  private String requestId;

  private Document defaultProperties;

  private AggregatedValidity validity;

  private String validityOverride;

  public static final String HOOKS_TRANSFORMER_PARAMETER = "hooksTransformer";

  public static final String PATH_PARAMETER = "path";

  static public final String DISPATCHER_REQUEST_ATTRIBUTE = "request";

  public static final String CACHE_PARAMETER = "cacheKey";

  public static final String VALIDITY_PARAMETER = "validityFile";

  private static final String VALIDITY_OVERRIDE_PARAMETER = "dispatcher.caching";

  private static final String CACHING_OFF = "off";

  private static final String CACHING_ON = "on";

  /**
   * Constructor Set the namespace
   */
  public DispatcherTransformer() {
    this.defaultNamespaceURI = DispatcherHelper.DISPATCHER_NAMESPACE_URI;
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
   * @return The generated validity object or <code>null</code> if the
   *         component is currently not cacheable.
   */
  public SourceValidity getValidity() {
    // You can either request URL?dispatcher.caching=off
    // or add this property to forrest.properties.xml
    // to force a SourceValidity.INVALID
    if (null != validityFile & !(validityOverride.equals(CACHING_OFF))) {
      this.validity = new AggregatedValidity();
      try {
        this.validity.add(m_resolver.resolveURI(validityFile).getValidity());
      } catch (Exception e) {
        getLogger().error(e.getMessage());
      }
    } else
      this.validity = null;
    return this.validity;
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
      this.manager.release(m_resolver);
      this.manager = null;
    }
    m_resolver = null;
    insideProperties = false;
    super.dispose();
  }

  /**
   * Recycle the component
   */
  public void recycle() {
    localRecycle();
    this.validity = null;
    super.recycle();

  }

  /**
   * Setup the file generator. Try to get the last modification date of the
   * source for caching.
   */
  // FIXME: See
  // http://cocoon.zones.apache.org/daisy/documentation/writing/690.html
  // Writing Cache Efficient Components
  // we have to do all the heavy stuff later and only prepare the basics here,
  // this will
  // help enhance caching. I mark them with *
  public void setup(SourceResolver resolver, Map objectModel, String src,
      Parameters par) throws ProcessingException, SAXException, IOException {
    super.setup(resolver, objectModel, src, par);
    localRecycle();
    try {
      if (null == this.dispatcherHelper)
        this.dispatcherHelper = new DispatcherHelper(manager);
      if (null == this.processor)
        this.processor = (XPathProcessor) this.manager.lookup(XPathProcessor.ROLE);
      if (null == m_resolver)
        m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
    } catch (Exception e) {
      String error = "dispatcherError:\n Could not set up the dispatcherHelper!\n DispatcherStack: "
          + e;
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    storedPrefixMap = new HashMap();
    this.parameterHelper = new HashMap();

    this.hooksPosition = new HashMap();

    this.allowMarkup = Boolean.getBoolean(parameters.getParameter(
        DISPATCHER_ALLOW_MARKUP, null));
    this.requestId = parameters.getParameter(DISPATCHER_REQUEST_ATTRIBUTE, null);
    this.cacheKey = parameters.getParameter(CACHE_PARAMETER, null);
    if (null == this.cacheKey)
      getLogger().warn(
          "Caching not activated! Declare the CACHE_KEY_PARAMETER="
              + CACHE_PARAMETER + " in your sitemap.");
    this.validityFile = parameters.getParameter(VALIDITY_PARAMETER, null);
    this.validityOverride = parameters
        .getParameter(VALIDITY_OVERRIDE_PARAMETER, "");
    this.cacheKey += validityOverride;

    if (requestId == null) {
      String error = "dispatcherError:\n"
          + "You have to set the \"request\" parameter in the sitemap!";
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    // * just store the $requestId and do the DOM-reading/storing later
    String propertyURI = "cocoon://" + requestId + ".props";
    try {
      this.defaultProperties = org.apache.forrest.dispatcher.util.SourceUtil
          .readDOM(propertyURI, this.manager);
    } catch (Exception e1) {
      String error = "dispatcherError:\n" + "Could not get the properties for "
          + propertyURI + "\n DispatcherStack: " + e1;
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    parameterHelper.put(DISPATCHER_REQUEST_ATTRIBUTE, requestId);
    this.requestedFormat = parameters.getParameter(STRUCTURER_FORMAT_ATTRIBUTE,
        null);
    if (requestedFormat == null) {
      String error = "dispatcherError:\n"
          + "You have to set the \"type\" parameter in the sitemap!";
      getLogger().error(error);
      throw new ProcessingException(error);
    }
    parameterHelper.put(STRUCTURER_FORMAT_ATTRIBUTE, requestedFormat);

    this.hooksXSL = parameters.getParameter(HOOKS_TRANSFORMER_PARAMETER, null);
    try {
      if (this.hooksXSL == null || this.hooksXSL.equals("")) {
        String warning = "dispatcherError:\n"
            + "You did not set up the \"hooksTransformer\" parameter in the sitemap, we are not going to transform forrest:hooks elements."
            + " For text output where you would not have to use hooks as structurer, the way you want it.";
        getLogger().warn(warning);
      } else {
        // * just store the $hooksXSL and do the DOM-reading/storing
        // later
        DOMSource stylesheet = new DOMSource(dispatcherHelper
            .getDocument(this.hooksXSL));
        this.structurerTransformer = dispatcherHelper.createTransformer(stylesheet);
      }
      parameterHelper.put(HOOKS_TRANSFORMER_PARAMETER, hooksXSL);
    } catch (Exception e) {
      String error = "dispatcherError:\n"
          + "Could not set up the \"hooks transformer\".\n\n DispatcherStack:\n "
          + e;
      getLogger().error(error);
      throw new ProcessingException(error);
    }
  }

  /**
   * Cleanup the transformer
   */
  private void localRecycle() {
    this.contract = null;
    this.hooksXSL = null;
    this.structurerTransformer = null;
    this.insideProperties = false;
  }

  public void startElement(String uri, String name, String raw, Attributes attr)
      throws SAXException {
    // Process start element event
    // Are we inside of properties? If so we need to record the elements.
    if (this.insideProperties & this.includeNodes) {
      try {
        this.builder.startElement(uri, name, raw, attr);
      } catch (SAXException e) {
        this.insideProperties = false;
        String error = "dispatcherError: " + DispatcherException.ERROR_500 + "\n"
            + "The contract \"" + contract.getContractName()
            + "\" has thrown in the property with ID \"" + this.propertyID
            + "\" an error.\n\n DispatcherStack:\n " + e;
        getLogger().error(error);
        throw new SAXException(error);
      }
    } else if (DispatcherHelper.DISPATCHER_NAMESPACE_URI.equals(uri)) {
      /*
       * We are in the dispatcher ns.
       */
      if (getLogger().isDebugEnabled()) {
        getLogger().debug("Starting dispatcher element: " + raw);
      }
      if (STRUCTURER_ELEMENT.equals(name))
        structurerProcessingStart(attr);
      else if (DISPATCHER_HOOK_ELEMENT.equals(name) & this.includeNodes)
        hookProcessingStart(name, raw, attr);
      else if (ContractBean.CONTRACT_ELEMENT.equals(name) & this.includeNodes)
        contractProcessingStart(attr);
      else if (ContractBean.PROPERTY_ELEMENT.equals(name) & this.includeNodes) {
        this.insideProperties = true;
        propertyProcessingStart(uri, name, raw, attr);
      }
    } else {
      if (!this.insideProperties & this.includeNodes & this.insideStructurer
          & this.allowMarkup)
        super.startElement(uri, name, raw, attr);
      if (!this.insideProperties & this.includeNodes & !this.insideStructurer)
        super.startElement(uri, name, raw, attr);
    }
  }

  /**
   * @param name
   * @param raw
   * @param attr
   * @throws DOMException
   * @throws SAXException
   */
  private void hookProcessingStart(String name, String raw, Attributes attr)
      throws DOMException, SAXException {
    /* create a DOM node from the current sax event */
    Element currentElement = dispatcher.getDocument().createElement(name);
    dispatcherHelper.setAttributesDOM(attr, currentElement);
    String tempPathWithoutAttr = path + "/" + name;
    String tempPath = dispatcherHelper
        .setAttributesXPath(attr, tempPathWithoutAttr);
    // If the hook has already been met, we use the position in the XPath to
    // avoid confusion ...
    if (hooksPosition.containsKey(tempPath)) {
      int pos = ((Integer) hooksPosition.get(tempPath)).intValue() + 1;
      hooksPosition.put(tempPath, new Integer(pos));
      tempPath = dispatcherHelper.setAttributesXPathWithPosition(attr,
          tempPathWithoutAttr, pos);
    } else {
      hooksPosition.put(tempPath, new Integer(1));
    }
    if (path == null || path.equals("")) {
      path = name;
      this.rootNode.appendChild(currentElement);
    } else {
      /* calculate, prepare and add node to the dispatcher */
      try {
        Node xpathNode;
        xpathNode = DOMUtil.getSingleNode(rootNode, path, this.processor);
        if (xpathNode == null)
          createXpathNode(attr, tempPath);
        else
          xpathNode.appendChild(currentElement);
      } catch (Exception e) {
        String error = "dispatcherError: " + DispatcherException.ERROR_500 + "\n"
            + "Could not set up xpath!\n\n DispatcherStack:\n " + e;
        getLogger().error(error);
        throw new SAXException(error);
      }
      path = tempPath;
    }
  }

  /**
   * @param attr
   * @param tempPath
   * @throws ProcessingException
   * @throws DOMException
   */
  private void createXpathNode(Attributes attr, String tempPath)
      throws ProcessingException, DOMException {
    Node xpathNode;
    xpathNode = DOMUtil.selectSingleNode(rootNode, tempPath, this.processor);
    if (attr != null)
      dispatcherHelper.setAttributesDOM(attr, xpathNode);
  }

  /**
   * @param attr
   * @param tempPath
   * @throws ProcessingException
   * @throws DOMException
   */
  private Node createXpathNode(String tempPath) throws ProcessingException,
      DOMException {
    Node xpathNode;
    xpathNode = DOMUtil.selectSingleNode(rootNode, tempPath, this.processor);
    return xpathNode;
  }

  public void endElement(String uri, String name, String raw) throws SAXException {
    if (getLogger().isDebugEnabled()) {
      getLogger().debug("Ending element: " + raw);
    }
    // Are we inside of properties? If so we need to record the elements.
    if (this.insideProperties) {
      propertyProcessingEnd(uri, name, raw);
    } else if (DispatcherHelper.DISPATCHER_NAMESPACE_URI.equals(uri)) {
      if (STRUCTURER_ELEMENT.equals(name))
        structurerProcessingEnd(raw);
      else if (ContractBean.CONTRACT_ELEMENT.equals(name) & this.includeNodes)
        contractProcessingEnd();
      else if (DISPATCHER_HOOK_ELEMENT.equals(name) & this.includeNodes)
        if (path.lastIndexOf("/") > -1)
          path = path.substring(0, path.lastIndexOf("/"));
        else
          path = null;
    } else {
      if (!this.insideProperties & this.includeNodes & this.insideStructurer
          & this.allowMarkup)
        super.endElement(uri, name, raw);
      if (!this.insideProperties & this.includeNodes & !this.insideStructurer)
        super.endElement(uri, name, raw);
    }
  }

  /**
   * view type="?" Here we check which format we have in the view. That should
   * be extended to matching the requested format
   * 
   * @param attr
   * @throws SAXException
   */
  private void structurerProcessingStart(Attributes attr) throws SAXException {
    this.insideStructurer = true;
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String value = attr.getValue(i);
      if (localName.equals(STRUCTURER_FORMAT_ATTRIBUTE)) {
        currentFormat = value;
      }
      if (localName.equals(STRUCTURER_HOOK_XPATH_ATTRIBUTE)) {
        if ("/".equals(String.valueOf(value.charAt(0))) & value.length() != 1) {
          path = "result" + value;
        } else if ("/".equals(String.valueOf(value.charAt(0)))
            & value.length() == 1) {
          path = "result";
        } else {
          path = "result/" + value;
        }
      }
    }
    if (requestedFormat.equals(currentFormat)) {
      if (path == null)
        path = "result";
      this.includeNodes = true;
      this.recording = true;
      try {
        dispatcherHelper.setNamespaceHelper(
            DispatcherHelper.DISPATCHER_NAMESPACE_URI,
            DispatcherHelper.DISPATCHER_PREFIX, STRUCTURER_ELEMENT);
        this.dispatcher = dispatcherHelper.getNamespaceHelper();
        this.document = dispatcher.getDocument();
        this.rootNode = document.getDocumentElement();
        this.rootNode.setAttribute(STRUCTURER_FORMAT_ATTRIBUTE, currentFormat);
        this.rootNode.setAttribute(STRUCTURER_HOOK_XPATH_ATTRIBUTE, path);
        // we create the path node for the result node
        DOMUtil.selectSingleNode(rootNode, path, this.processor);
      } catch (Exception e) {
        String error = "dispatcherError: " + DispatcherException.ERROR_500 + "\n"
            + "Could not setup dispatcherHelper!\n\n DispatcherStack:\n " + e;
        getLogger().error(error);
        this.recording = false;
        throw new SAXException(error);
      }

    } else {
      path = null; // unset path because we do not process the node
      this.includeNodes = false;
      this.recording = false;
    }
  }

  /**
   * @param raw
   * @throws SAXException
   */
  private void structurerProcessingEnd(String raw) throws SAXException {
    if (this.recording) {
      try {
        NodeList resultList = this.rootNode.getFirstChild().getChildNodes();
        for (int i = 0; i < resultList.getLength(); i++) {
          Node array_element = resultList.item(i);
          if (this.structurerTransformer == null) {
            XMLUtils.valueOf(new IncludeXMLConsumer(super.xmlConsumer),
                array_element);
          } else {
            DOMSource source = new DOMSource(array_element);
            DOMResult result = new DOMResult();
            this.structurerTransformer.transform(source, result);
            XMLUtils.valueOf(new IncludeXMLConsumer(super.xmlConsumer), result
                .getNode());
          }
        }

      } catch (Exception e) {
        String error = "dispatcherError: " + DispatcherException.ERROR_500 + "\n"
            + "Could not setup final transformer!\n\n DispatcherStack:\n " + e;
        getLogger().error(error);
        this.recording = false;
        throw new SAXException(error);
      } finally {
        this.recording = false;
        this.insideStructurer = false;
      }
    }
    if (getLogger().isDebugEnabled()) {
      getLogger().debug("Ending \"" + STRUCTURER_ELEMENT + "\" element: " + raw);
    }
  }

  /**
   * We have found a contract. Now we have to prepare a bean to later add it to
   * the output stream in the endElement.
   * 
   * @param attr
   * @throws SAXException
   */
  private void contractProcessingStart(Attributes attr) throws SAXException {
    try {
      if (contract == null)
        contract = new ContractBeanDOMImpl(this.manager, parameterHelper,
            defaultProperties, (URIResolver) this);
      // This is not needed since the manager did not change.
      // else
      // contract.initialize();
    } catch (Exception e) {
      String error = DispatcherException.ERROR_500 + "\n"
          + "component: ContractBean" + "\n"
          + "message: Could not setup contractBean." + "\n" + "\n\n"
          + "dispatcherErrorStack:\n" + e;
      getLogger().error(error);
      throw new SAXException(error);
    }
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String value = attr.getValue(i);
      if (ContractBean.CONTRACT_ID_ATTRIBUTE.equals(localName)) {
        // getting the contract name
        contract.setContractName(value);
        String contractUri = ContractBean.CONTRACT_RESOLVE_PREFIX + "."
            + currentFormat + "." + value;
        try {
          // Adding the contract to the validity object.
          // As soon the contract changes we want a rebuild of
          // the page and not the cached object.
          if (!validityOverride.equals(CACHING_OFF) & null != this.validity) {
            SourceValidity contractValidityId = m_resolver.resolveURI(contractUri)
                .getValidity();
            // we cannot allow null in an AggregatedValidity
            if (null != contractValidityId)
              this.validity.add(contractValidityId);
          }
          Document doc = org.apache.forrest.dispatcher.util.SourceUtil.readDOM(
              contractUri, this.manager);
          contract.setContractImpl(doc, contractUri);
          // contract.setContractImpl(contractUri);
        } catch (Exception e) {
          String error = "dispatcherError: "
              + DispatcherException.ERROR_500
              + "\n"
              + "The contract \""
              + contract.getContractName()
              + "\" has thrown an exception by resolving the implementation from \""
              + contractUri + "\".\n\n" + "dispatcherErrorStack:\n" + e;
          getLogger().error(error);
          throw new SAXException(error);
        }
        if (getLogger().isDebugEnabled()) {
          getLogger().debug(
              "DISPATCHER_CONTRACT_ID_ATTRIBUTE" + "-->" + localName + "\n"
                  + "value" + "-->" + value);
        }
      } else if (ContractBean.CONTRACT_NUGGET_ATTRIBUTE.equals(localName)) {
        // contract is a nugget-contract
        contract.setNugget(true);
        try {
          // Adding the raw data to the validity object.
          // As soon the raw data changes we want a rebuild of
          // the page and not the cached object.
          if (!validityOverride.equals(CACHING_OFF) & null != this.validity) {
            SourceValidity contractValidityRaw = m_resolver.resolveURI(value)
                .getValidity();
            // we cannot allow null in an AggregatedValidity
            if (null != contractValidityRaw)
              this.validity.add(contractValidityRaw);
          }
          Document doc = org.apache.forrest.dispatcher.util.SourceUtil.readDOM(
              value, this.manager);
          contract.setContractRawData(doc);
          // contract.setNuggetUri(value);
        } catch (Exception e) {
          String error = "dispatcherError: " + DispatcherException.ERROR_500 + "\n"
              + "The contract \"" + contract.getContractName()
              + "\" has thrown thrown an exception by resolving raw data from \""
              + value + "\".\n\n" + "dispatcherErrorStack:\n " + e;
          getLogger().error(error);
          throw new SAXException(error);
        }
        if (getLogger().isDebugEnabled()) {
          getLogger().debug(
              "ContractBean.CONTRACT_NUGGET_ATTRIBUTE" + "-->" + localName + "\n"
                  + "value" + "-->" + value);
        }
      } else {
        if (getLogger().isDebugEnabled()) {
          getLogger().debug("localName" + "-->" + localName + " not implemented");
        }
      }
    }
    /*
     * if we do not have a nugget add a foo element to the raw data to invoke
     * the transformation.
     */
    if (!contract.isNugget()) {
      Document foo;
      try {
        foo = dispatcherHelper.createDocument("foo");
        contract.setContractRawData(foo);
      } catch (Exception e) {
        String error = "dispatcherError: "
            + DispatcherException.ERROR_500
            + "\n"
            + "The contract \""
            + contract.getContractName()
            + "\" has thrown thrown an exception by creating the dummy foo document."
            + ".\n\n" + "dispatcherErrorStack:\n " + e;
        getLogger().error(error);
        throw new SAXException(error);
      }
    }
  }

  /**
   * @throws SAXException
   */
  private void contractProcessingEnd() throws SAXException {
    try {
      contract.setContractResultData();
      Document node = (Document) contract.getContractResultData().getNode();
      Document root = this.rootNode.getOwnerDocument();
      /*
       * debug code - uncomment it if you need it! will output the contract
       * resulting data to sysout
       */
      // DOMSource source = new DOMSource(node);
      // StreamResult result = new StreamResult(System.out);
      // contract.getContractTransformer().transform(source, result);
      /*
       * append this node to the current path after testing where there is a
       * fixed location for the contract content. If so then add it there.
       */
      NodeList contentChildren = node.getElementsByTagNameNS(
          DispatcherHelper.DISPATCHER_NAMESPACE_URI, "part");
      for (int i = 0; i < contentChildren.getLength(); i++) {
        Element contentChild = (Element) contentChildren.item(i);
        NodeList finalContent = contentChild.getChildNodes();
        if (contentChild != null) {
          String location = contentChild.getAttribute("xpath");
          if (location.equals("") | location == null) {
            Node xpathNode = DOMUtil.getSingleNode(rootNode, path, this.processor);
            if (xpathNode != null) {
              // add everything *within* the forrest:part element
              appendChildToResultIterator(root, finalContent, xpathNode);
            }
          } else {
            if (location.charAt(0) == '/')
              location = "result" + location;
            else
              location = "result/" + location;
            Node xpathNode = DOMUtil.getSingleNode(rootNode, location,
                this.processor);
            if (xpathNode != null) {
              // add everything *within* the forrest:part element
              appendChildToResultIterator(root, finalContent, xpathNode);
            } else {
              xpathNode = createXpathNode(location);
              // add everything *within* the forrest:part element
              appendChildToResultIterator(root, finalContent, xpathNode);
            }
          }
        }
      }
    } catch (Exception e) {
      String error = "dispatcherError: "
          + DispatcherException.ERROR_500
          + "\n"
          + "The contract \""
          + contract.getContractName()
          + "\" has thrown thrown an exception while trying to transform the final markup. \n\n"
          + "dispatcherErrorStack:\n " + e;
      getLogger().error(error);
      throw new SAXException(error);
    } finally {
      this.contract.recycle();
    }
  }

  /**
   * @param root
   * @param finalContent
   * @param xpathNode
   * @throws DOMException
   */
  private void appendChildToResultIterator(Document root, NodeList finalContent,
      Node xpathNode) throws DOMException {
    for (int j = 0; j < finalContent.getLength(); j++) {
      appendChildToResult(root, finalContent, xpathNode, j);
    }
  }

  /**
   * @param root
   * @param finalContent
   * @param xpathNode
   * @param j
   * @throws DOMException
   */
  private void appendChildToResult(Document root, NodeList finalContent,
      Node xpathNode, int j) throws DOMException {
    Node contractData = finalContent.item(j);
    Node toMove = root.importNode(contractData, true);
    xpathNode.appendChild(toMove);
  }

  /**
   * @param uri
   * @param name
   * @param raw
   * @param attr
   * @throws SAXException
   */
  private void propertyProcessingStart(String uri, String name, String raw,
      Attributes attr) throws SAXException {
    for (int i = 0; i < attr.getLength(); i++) {
      String localName = attr.getLocalName(i);
      String value = attr.getValue(i);
      if (ContractBean.PROPERTY_ID_ATTRIBUTE.equals(localName))
        this.propertyID = value;
    }
    if (this.propertyID.equals("") | this.propertyID == null) {
      String error = "dispatcherError: " + DispatcherException.ERROR_500 + "\n"
          + "The contract \"" + contract.getContractName()
          + "\" has no identifier attribute \""
          + ContractBean.PROPERTY_ID_ATTRIBUTE + "\" in the " + raw;
      getLogger().error(error);
      throw new SAXException(error);
    }
    this.builder = new DOMBuilder();
    this.builder.startDocument();
    launchStoredMappings();
    this.builder.startElement(uri, name, raw, attr);
  }

  /**
   * @param uri
   * @param name
   * @param raw
   * @throws SAXException
   */
  private void propertyProcessingEnd(String uri, String name, String raw)
      throws SAXException {
    if (ContractBean.PROPERTY_ELEMENT.equals(name)) {
      this.insideProperties = false;
      if (this.includeNodes) {
        this.builder.endElement(uri, name, raw);
        this.builder.endDocument();
        if (getLogger().isDebugEnabled()) {
          getLogger()
              .debug(
                  "DispatcherTransformer: putting DOM tree into the contract transformer");
        }
        Transformer transformer = contract.getContractTransformer();
        transformer.setParameter(this.propertyID, this.builder.getDocument()
            .getFirstChild());
        contract.setContractTransformer(transformer);
        this.propertyID = "";
        contract.setHasProperties(true);
        if (getLogger().isDebugEnabled()) {
          getLogger().debug(
              "DispatcherTransformer: DOM tree is in the contract transformer");
        }
        this.builder = null;
      }
    } else {
      if (this.includeNodes)
        this.builder.endElement(uri, name, raw);
    }
  }

  public void characters(char c[], int start, int len) throws SAXException {
    if (this.insideProperties & this.includeNodes)
      this.builder.characters(c, start, len);
    else if (!this.insideProperties & this.includeNodes & !this.insideStructurer)
      super.contentHandler.characters(c, start, len);
  }

  public void startCDATA() throws SAXException {
    if (this.insideProperties & this.includeNodes)
      this.builder.startCDATA();
    else if (!this.insideProperties & this.includeNodes & !this.insideStructurer)
      super.lexicalHandler.startCDATA();
  }

  public void endCDATA() throws SAXException {
    if (this.insideProperties & this.includeNodes)
      this.builder.endCDATA();
    else if (!this.insideProperties & this.includeNodes & !this.insideStructurer)
      super.lexicalHandler.endCDATA();
  }

  /** BEGIN SAX ContentHandler handlers * */

  public void startPrefixMapping(String prefix, String uri) throws SAXException {
    super.startPrefixMapping(prefix, uri);
    if (this.insideProperties & this.includeNodes) {
      this.builder.startPrefixMapping(prefix, uri);
    } else {
      storePrefixMapping(prefix, uri);
    }
  }

  /** END SAX ContentHandler handlers * */

  protected void storePrefixMapping(String prefix, String uri) {
    storedPrefixMap.put(prefix, uri);
  }

  protected void launchStoredMappings() throws SAXException {
    Iterator it = storedPrefixMap.keySet().iterator();
    while (it.hasNext()) {
      String pre = (String) it.next();
      String uri = (String) storedPrefixMap.get(pre);
      getLogger().debug(
          "WriteSessionTransformer: launching prefix mapping[ pre: " + pre
              + " uri: " + uri + " ]");
      this.builder.startPrefixMapping(pre, uri);
    }
  }

  protected void launchStoredMappingsDispatcher() throws SAXException {
    Iterator it = storedPrefixMap.keySet().iterator();
    while (it.hasNext()) {
      String pre = (String) it.next();
      String uri = (String) storedPrefixMap.get(pre);
      getLogger().debug(
          "WriteSessionTransformer: launching prefix mapping[ pre: " + pre
              + " uri: " + uri + " ]");
      this.dispatcherBuilder.startPrefixMapping(pre, uri);
    }
  }

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
            xslSource = m_resolver.resolveURI(base.substring(0, lastPathElementPos)
                + "/" + href);
          }
        } else {
          File parent = new File(base.substring(5));
          File parent2 = new File(parent.getParentFile(), href);
          xslSource = m_resolver.resolveURI(parent2.toURL().toExternalForm());
        }
      }

      InputSource is = getInputSource(xslSource);

      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            "xslSource = " + xslSource + ", system id = " + xslSource.getURI());
      }

      // if (m_checkIncludes) {
      // // Populate included validities
      // List includes = (List) m_includesMap.get(base);
      // if (includes != null) {
      // SourceValidity included = xslSource.getValidity();
      // if (included != null) {
      // includes.add(new Object[] { xslSource.getURI(),
      // xslSource.getValidity() });
      // } else {
      // // One of the included stylesheets is not cacheable
      // m_includesMap.remove(base);
      // }
      // }
      // }

      return new StreamSource(is.getByteStream(), is.getSystemId());
    } catch (SourceException e) {
      if (getLogger().isDebugEnabled()) {
        getLogger().debug(
            "Failed to resolve " + href + "(base = " + base + "), return null", e);
      }

      // CZ: To obtain the same behaviour as when the resource is
      // transformed by the XSLT Transformer we should return null here.
      return null;
    } catch (java.net.MalformedURLException mue) {
      if (getLogger().isDebugEnabled()) {
        getLogger()
            .debug(
                "Failed to resolve " + href + "(base = " + base + "), return null",
                mue);
      }

      return null;
    } catch (IOException ioe) {
      if (getLogger().isDebugEnabled()) {
        getLogger()
            .debug(
                "Failed to resolve " + href + "(base = " + base + "), return null",
                ioe);
      }

      return null;
    } finally {
      m_resolver.release(xslSource);
    }
  }

  /**
   * Return a new <code>InputSource</code> object that uses the
   * <code>InputStream</code> and the system ID of the <code>Source</code>
   * object.
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
