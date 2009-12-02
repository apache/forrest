package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

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
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.ContractException;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.helper.AXIOMXPathCreate;
import org.apache.forrest.dispatcher.impl.helper.Captions;
import org.apache.forrest.dispatcher.impl.helper.StreamHelper;
import org.jaxen.JaxenException;

public class XMLStructurerAxiom extends AbstractXmlStructurer {

  private static final String PATH_PREFIX = "/result";

  private OMFactory factory;

  private OMElement root;

  /**
   * AXIOM stands for AXis Object Model 
   * (also known as OM - Object Model) and refers to the XML
   * infoset model that was initially developed for Apache Axis2.
   * <p>XML infoset refers to the information included inside the 
   * XML and for programmatical manipulation it is convenient to 
   * have a representation of this XML infoset in a language specific manner. 
   * <p>For an object oriented language the obvious choice is a model 
   * made up of objects.<p>
   * The axiom implementation is an object model
       * approach to StAX. It allows you a dom like navigation
       * (allocate xpath nodes), adding of child elements in
       * this xpath statement and many more.
   * @param config the current dispatcher configuration. 
   * @param defaultProperties the properties that we want to 
   * pass later to the contract transformation.
   */
  public XMLStructurerAxiom(DispatcherBean config, Map<String, Object> defaultProperties) {
    super(config, defaultProperties);
    this.factory = OMAbstractFactory.getOMFactory();
  }

  /*
   * @see
   * org.apache.forrest.dispatcher.impl.Structurer#execute(java.io.InputStream,
   * java.lang.String)
   */
  @SuppressWarnings("unchecked")
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
      XMLStreamReader reader = stax.getReader(structurerStream);
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
            AXIOMXPathCreate xpath = new AXIOMXPathCreate(currentPath);
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
      OMElement firstElement = root.getFirstElement();
      if (null!=firstElement){
        firstElement.serialize(out);
      }else{
        root.serialize(out);
      }
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
  public static boolean isElement(final OMNode node) {
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
  @SuppressWarnings("unchecked")
  private void processStructure(OMElement structure, OMElement pathNode)
      throws XMLStreamException, DispatcherException, IOException,
      JaxenException {
    Iterator<OMNode> strucutrerElements = structure.getChildren();
    while (strucutrerElements.hasNext()) {
      OMNode node = strucutrerElements.next();
      if (isElement(node)) {
        computeNodes((OMElement) node, pathNode);
      }
    }

  }

  /**
   * This is the main iterating process here we process all contracts and hooks.
   * 
   * @param component
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
    } else{
      nextChildren(component, pathNode);
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
  @SuppressWarnings("unchecked")
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
    nextChildren(component, element);
  }

  @SuppressWarnings("unchecked")
private void nextChildren(OMElement component, OMElement element)
      throws DispatcherException, XMLStreamException, IOException,
      JaxenException {
    Iterator<OMNode> childs = component.getChildren();
    while (childs.hasNext()) {
      OMNode node = childs.next();
      if (isElement(node)) {
        computeNodes((OMElement) node, element);
      }
    }
  }

  /**
   * @param component
   * @param pathNode
   * @throws DispatcherException
   * @throws XMLStreamException
   */
  @SuppressWarnings("unchecked")
  private void processContract(OMElement component, OMElement pathNode)
      throws DispatcherException, XMLStreamException {
    String name = "", data = null;
    name = component.getAttributeValue(qIt(Captions.NAME_ATT));
    data = component.getAttributeValue(qIt(Captions.DATA_ATT));
    log.debug("Contract: " + name + "\ndataUri: " + data);
    InputStream dataStream = null;
    if (null != data && !data.equals("")) {
      dataStream = resolver.resolve(data);
    }
    Contract contract = contractRep.resolve(name);
    Iterator <OMNode> properties = component.getChildrenWithName(qIt(Captions.NS,Captions.PROPERTY_ELEMENT));
    Map<String, Object> localParams = new HashMap<String, Object>(param);
    while (properties.hasNext()) {
      OMNode node = properties.next();
      if (isElement(node)){
        processProperty((OMElement) node, localParams);
      }
    }
    try {
      InputStream resultStream = contract.execute(dataStream, localParams);
      StreamHelper.closeStream(dataStream);
      processContractResult(resultStream, pathNode);
      StreamHelper.closeStream(resultStream);
    } catch (Exception e) {
      /*
       *  FOR-1127
       *  Here we can inject custom handler for allowing that contracts can
       *  throw exceptions but the overall structurer will not fail at whole.
       *  
       *  Imaging contract "xyz" will fail. Now we throw an exception and abort 
       *  processing. However it may be desirable that the process continues
       *  since the contract may not be critical for the overall result.
       *  
       */
      throw new ContractException(name,"\n",e);
    }
  }

  /**
   * @param resultStream
   * @param pathNode
   * @throws XMLStreamException
   * @throws JaxenException
   */
  @SuppressWarnings("unchecked")
  private void processContractResult(InputStream resultStream,
      OMElement pathNode) throws XMLStreamException, JaxenException {
    XMLStreamReader contractResultReader = stax.getReader(resultStream);
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
          OMNode child = children.next();
          pathNode.addChild(child);
        }
      } else {
        xpath = PATH_PREFIX + xpath;
        // we need to feed the xpathSelector with the ns we may have
        AXIOMXPathCreate xpathSelector = new AXIOMXPathCreate(xpath);

        for (OMNamespace space : spaces) {
          xpathSelector
              .addNamespace(space.getPrefix(), space.getNamespaceURI());
        }
        OMElement injectionPoint = (OMElement) xpathSelector.selectSingleNode(
            root, true);
        while (children.hasNext()) {
          OMNode child = children.next();
          injectionPoint.addChild(child);
        }

      }
    }
  }

  /**
   * @param name
   * @return
   */
  public static QName qIt(String name) {
    return new QName(name);
  }

  /**
   * @param name
   * @param uri
   * @return
   */
  public static  QName qIt(String uri,String name) {
    return new QName(uri,name);
  }
  /**
   * @param properties
   * @param currentParam
   * @throws XMLStreamException
   */
  private void processProperty(OMElement properties, Map<String, Object> currentParam)
      throws XMLStreamException {
    String propertyName = null, propertyValue = null;
    propertyName = properties.getAttributeValue(qIt(Captions.NAME_ATT));
    propertyValue = properties.getAttributeValue(qIt(Captions.VALUE_ATT));
    stax.addProperties(properties.getXMLStreamReader(), currentParam, propertyName, propertyValue, allowXmlProperties, shrink);
  }

  
}
