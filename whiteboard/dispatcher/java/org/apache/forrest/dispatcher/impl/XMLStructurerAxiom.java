package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.api.Resolver;
import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.factories.ContractFactory;
import org.apache.forrest.dispatcher.impl.helper.AXIOMXPathPatched;
import org.apache.forrest.dispatcher.impl.helper.Captions;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.apache.forrest.dispatcher.impl.helper.StreamHelper;
import org.jaxen.JaxenException;

public class XMLStructurerAxiom extends StAX implements Structurer {

  private static final String PATH_PREFIX = "/result";

  private final Resolver resolver;

  private final boolean allowXmlProperties;

  private final ContractFactory contractRep;

  private LinkedHashMap<String, LinkedHashSet<OMNode>> resultTree = new LinkedHashMap<String, LinkedHashSet<OMNode>>();

  private OMFactory factory;

  private OMElement root;

  public XMLStructurerAxiom(DispatcherBean config) {
    this.contractRep = new ContractFactory(config);
    this.resolver = config.getResolver();
    this.allowXmlProperties = config.isAllowXmlProperties();
    this.factory = OMAbstractFactory.getOMFactory();
  }

  /*
   * @see
   * org.apache.forrest.dispatcher.impl.Structurer#execute(java.io.InputStream,
   * java.lang.String)
   */
  public InputStream execute(InputStream structurerStream, String format)
      throws DispatcherException {
    BufferedInputStream stream = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    /*
     * Preparing the model by creating a root document/element. Afterward we
     * need to strip the root element again.
     */
    OMDocument doc = factory.createOMDocument();
    root = factory.createOMElement("result", null);
    doc.addChild(root);
    String currentPath = PATH_PREFIX;
    try {
      /*
       * Preparing the processing.
       */
      XMLStreamReader reader = getReader(structurerStream);
      StAXOMBuilder builder = new StAXOMBuilder(reader);
      OMElement strucuturer = builder.getDocumentElement();
      Iterator<OMNode> structures = strucuturer.getChildrenWithName(qIt(
          Captions.NS, Captions.STRUCTURE_ELEMENT));
      /*
       * process all structure elements within this structurer file.
       */
      while (structures.hasNext()) {
        String m_type = "", path = "";
        OMNode node = (OMNode) structures.next();
        if (isElement(node)) {
          OMElement structure = (OMElement) node;
          m_type = structure.getAttributeValue(qIt(Captions.TYPE_ATT));
          path = structure.getAttributeValue(qIt(Captions.HOOKS_ATT));
          if (format.equals(m_type)) {
            log.debug("matched - need to process");
            // adding the default path
            boolean startsWithRoot = "/".equals(String.valueOf(path.charAt(0)));
            if (!startsWithRoot) {
              path = "/" + path;
            }else if(path.length()==1 && startsWithRoot){
              path="";
            }
            currentPath += path;
            AXIOMXPathPatched xpath = new AXIOMXPathPatched(currentPath);
            Object pathNode = (OMElement) xpath.selectSingleNode(root, true);
            processStructure(structure, (OMElement) pathNode);
          }
        }
      }
      /*
       * In case it strike you odd that we just use the first node this is
       * because the output of a structurer needs to be well-formed and there
       * should only be one child in any circumstance.
       */
      root.getFirstElement().serialize(out);
      // debug hook
      // next line will get the whole result document
      // root.serialize(out);
    } catch (XMLStreamException e) {
      throw new DispatcherException(e);
    } catch (JaxenException e) {
      throw new DispatcherException(e);
    } catch (IOException e) {
      throw new DispatcherException(e);
    } finally {
      if (null != structurerStream) {
        try {
          structurerStream.close();
        } catch (IOException e) {
          throw new DispatcherException(e);
        }
        ;
      }
    }
    stream = StreamHelper.switchStream(out);
    log.debug(out.toString());
    return stream;
  }

  /**
   * @param node
   * @return
   */
  private boolean isElement(OMNode node) {
    return node.getType() == OMNode.ELEMENT_NODE;
  }

  /**
   * @param structure
   * @param pathNode
   * @throws XMLStreamException
   * @throws DispatcherException
   * @throws IOException
   * @throws JaxenException
   */
  private void processStructure(OMElement structure, OMElement pathNode)
      throws XMLStreamException, DispatcherException, IOException,
      JaxenException {
    Iterator<OMNode> strucutrerElements = structure.getChildren();
    while (strucutrerElements.hasNext()) {
      OMNode node = (OMNode) strucutrerElements.next();
      if (isElement(node)) {
        computeNodes((OMElement) node, pathNode);
      }
    }

  }

  /**
   * This is the main iterating process here we process all contracts and hooks.
   * 
   * @param node
   *          current node
   * @param pathNode
   *          the model representing the current path
   * @throws DispatcherException
   * @throws XMLStreamException
   * @throws IOException
   * @throws JaxenException
   */
  private void computeNodes(OMElement component, OMElement pathNode)
      throws DispatcherException, XMLStreamException, IOException,
      JaxenException {
    if (component.getQName().equals(
        qIt(Captions.NS, Captions.CONTRACT_ELEMENT))) {
      processContract(component, pathNode);
    } else if (component.getQName().equals(
        qIt(Captions.NS, Captions.HOOK_ELEMENT))) {
      processHook(component, pathNode);
    }
  }

  /**
   * @param component
   * @param pathNode
   * @throws DispatcherException
   * @throws XMLStreamException
   * @throws IOException
   * @throws JaxenException
   */
  private void processHook(OMElement component, OMElement pathNode)
      throws DispatcherException, XMLStreamException, IOException,
      JaxenException {
    OMElement element = factory.createOMElement(component.getLocalName(),
        component.getNamespace().getNamespaceURI(), "forrest");
    Iterator<OMAttribute> attributes = component.getAllAttributes();
    while (attributes.hasNext()) {
      OMAttribute attribute = attributes.next();
      element.addAttribute(attribute);

    }
    pathNode.addChild(element);
    Iterator<OMNode> childs = component.getChildren();
    while (childs.hasNext()) {
      OMNode node = (OMNode) childs.next();
      if (isElement(node)) {
        computeNodes((OMElement) node, element);
      }
    }
  }

  /**
   * @param component
   * @throws DispatcherException
   * @throws XMLStreamException
   * @throws IOException
   * @throws JaxenException
   */
  private void processContract(OMElement component, OMElement pathNode)
      throws DispatcherException, XMLStreamException, IOException,
      JaxenException {
    String name = "", data = null;
    name = component.getAttributeValue(qIt(Captions.NAME_ATT));
    data = component.getAttributeValue(qIt(Captions.DATA_ATT));
    log.debug("Contract: " + name + "\ndataUri: " + data);
    InputStream dataStream = null;
    if (null != data && !data.equals("")) {
      dataStream = resolver.resolve(data);
    }
    Contract contract = contractRep.resolve(name);
    HashMap<String, ?> param = new HashMap();
    Iterator <OMNode> properties = component.getChildrenWithName(qIt(Captions.NS,Captions.PROPERTY_ELEMENT));
    while (properties.hasNext()) {
      OMNode node = (OMNode) properties.next();
      if (isElement(node)){
        processProperty((OMElement) node, param);
      }
    }
    InputStream resultStream = contract.execute(dataStream, param);
    StreamHelper.closeStream(dataStream);
    processContractResult(resultStream, pathNode);
    StreamHelper.closeStream(resultStream);
  }

  /**
   * @param resultStream
   * @param pathNode
   * @throws XMLStreamException
   * @throws JaxenException
   */
  private void processContractResult(InputStream resultStream,
      OMElement pathNode) throws XMLStreamException, JaxenException {
    XMLStreamReader contractResultReader = getReader(resultStream);
    StAXOMBuilder builder = new StAXOMBuilder(contractResultReader);
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
    Iterator<OMElement> parts = content.getChildrenWithName(qIt(
        Captions.NS_CONTRACTS, Captions.PART_ELEMENT));
    while (parts.hasNext()) {
      OMElement part = parts.next();
      String xpath = part.getAttributeValue(qIt(Captions.RESULT_XPATH));
      Iterator<OMNode> children = part.getChildren();
      if (xpath == null) {
        while (children.hasNext()) {
          OMNode child = (OMNode) children.next();
          pathNode.addChild(child);
        }
      } else {
        xpath = PATH_PREFIX + xpath;
        // we need to feed the xpathSelector with the ns we may have
        AXIOMXPathPatched xpathSelector = new AXIOMXPathPatched(xpath);

        for (OMNamespace space : spaces) {
          xpathSelector
              .addNamespace(space.getPrefix(), space.getNamespaceURI());
        }
        OMElement injectionPoint = (OMElement) xpathSelector.selectSingleNode(
            root, true);
        while (children.hasNext()) {
          OMNode child = (OMNode) children.next();
          injectionPoint.addChild(child);
        }

      }
    }
  }

  /**
   * @param name
   * @return
   */
  private QName qIt(String name) {
    return new QName(name);
  }

  /**
   * @param name
   * @param uri
   * @return
   */
  private QName qIt(String uri,String name) {
    return new QName(uri,name);
  }
  /**
   * @param properties
   * @param param
   * @throws XMLStreamException
   */
  private void processProperty(OMElement properties, HashMap param)
      throws XMLStreamException {
    String propertyName = null, propertyValue = null;
    propertyName = properties.getAttributeValue(qIt(Captions.NAME_ATT));
    propertyValue = properties.getAttributeValue(qIt(Captions.VALUE_ATT));
    addProperties(properties.getXMLStreamReader(), param, propertyName, propertyValue, allowXmlProperties);
  }

  
}
