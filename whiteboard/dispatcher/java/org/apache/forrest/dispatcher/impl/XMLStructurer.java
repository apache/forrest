package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.api.Resolver;
import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.factories.ContractFactory;
import org.apache.forrest.dispatcher.impl.helper.Captions;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.apache.forrest.dispatcher.impl.helper.StreamHelper;
import org.apache.forrest.dispatcher.utils.CommonString;
import org.xml.sax.InputSource;

public class XMLStructurer extends StAX implements Structurer {

  private final Resolver resolver;

  private final boolean allowXmlProperties;

  private final ContractFactory contractRep;

  private LinkedHashMap<String, LinkedHashSet<XMLEvent>> resultTree = new LinkedHashMap<String, LinkedHashSet<XMLEvent>>();

  private String currentPath = "";

  public XMLStructurer(DispatcherBean config) {
    this.contractRep = new ContractFactory(config);
    this.resolver = config.getResolver();
    this.allowXmlProperties = config.isAllowXmlProperties();
  }

  /*
   * @see
   * org.apache.forrest.dispatcher.impl.Structurer#execute(java.io.InputStream,
   * java.lang.String)
   */
  public InputStream execute(InputStream structurerStream, String format)
      throws DispatcherException {
    BufferedInputStream stream = null;
    try {
      XMLStreamReader reader = getReader(structurerStream);
      boolean process = true;
      while (process) {
        int event = reader.next();
        switch (event) {
        case XMLStreamConstants.END_DOCUMENT:
          process = false;
          break;

        case XMLStreamConstants.START_ELEMENT:
          String elementName = reader.getLocalName();
          if (elementName.equals(Captions.STRUCTURE_ELEMENT)) {
            String m_type = "", path = "";
            // Get attribute names
            for (int i = 0; i < reader.getAttributeCount(); i++) {
              String localName = reader.getAttributeLocalName(i);
              if (localName.equals(Captions.TYPE_ATT)) {
                // Return value
                m_type = reader.getAttributeValue(i);
              } else if (localName.equals(Captions.HOOKS_ATT)) {
                path = reader.getAttributeValue(i);
              }
            }
            if (m_type.equals(format)) {
              log.debug("matched - need to process");
              // adding the default path
              if (!"/".equals(String.valueOf(path.charAt(0)))) {
                path = "/" + path;
              }
              currentPath = path;
              stream = processStructure(reader);
            } else {
              log.debug("no-matched");
            }
          }
          break;

        default:
          break;
        }
      }
    } catch (XMLStreamException e) {
      throw new DispatcherException(e);
    } catch (IOException e) {
      throw new DispatcherException(e);
    } finally {
        try {
          StreamHelper.closeStream(structurerStream);
        } catch (IOException e) {
          throw new DispatcherException(e);
      }
    }
    return stream;
  }

  private BufferedInputStream processStructure(XMLStreamReader reader)
      throws XMLStreamException, DispatcherException, IOException {
    boolean process = true;
    String elementName = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    while (process) {
      int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.STRUCTURE_ELEMENT)) {
          XMLEventWriter writer = getWriter(out);
          createResultStax(writer);
          resultTree.clear();
          process = false;
        } else if (elementName.equals(Captions.HOOK_ELEMENT)) {
          processHook(reader, false);
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.CONTRACT_ELEMENT)) {
          log.debug("Path " + currentPath);
          processContract(reader);
        } else if (elementName.equals(Captions.HOOK_ELEMENT)) {
          log.debug("HOOKS " + elementName);
          processHook(reader, true);
          log.info("HOOKS transformation NOT YET IMPLEMENTED");
        }
        break;

      default:
        break;

      }
    }
    log.debug(out.toString());
    return (out != null) ? StreamHelper.switchStream(out) : null;
  }

  /**
   * Create the outcome of the hooks and contracts. Here we need to find the
   * injectionPoints that can be defined in the different contracts.
   * 
   * This injectionPoints can be within or extending other.
   */

  private void createResultStax(XMLEventWriter writer)
      throws XMLStreamException {
    // We start with creating a new result document
    writer.add(getEventFactory().createStartDocument("UTF-8", "1.0"));
    // get a iterator about the injectionPoints we use
    Iterator<String> iterator = resultTree.keySet().iterator();
    // create an path array
    String[] paths = resultTree.keySet().toArray(new String[1]);
    // determine the common root path for all paths
    String rootPath = CommonString.common(paths);
    // Prepare the creation of the root path
    String[] tokenizer = rootPath.split("/");
    // create the events related to the root path
    openPaths(writer, tokenizer);
    while (iterator.hasNext()) {
      String element = iterator.next();
      final String replaceFirst = element.replaceFirst(rootPath, "");
      final String[] split = replaceFirst.split("/");
      if (split.length > 1) {
        openPaths(writer, split);
        injectResult(writer, element);
        closingPaths(writer, split);
      } else {
        StartElement start = getEventFactory().createStartElement("", "",
            replaceFirst);
        writer.add((XMLEvent) start);

        injectResult(writer, element);
        EndElement end = getEventFactory().createEndElement("", "",
            replaceFirst);
        writer.add((XMLEvent) end);
      }

    }
    closingPaths(writer, tokenizer);
    writer.add(getEventFactory().createEndDocument());
  }

  private void processContract(XMLStreamReader reader)
      throws XMLStreamException, DispatcherException, IOException {
    boolean process = true;
    String elementName = null;
    String name = "", data = null;
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(Captions.NAME_ATT)) {
        // Return value
        name = reader.getAttributeValue(i);
      } else if (localName.equals(Captions.DATA_ATT)) {
        data = reader.getAttributeValue(i);
      }
    }
    log.debug("data " + data);
    InputStream dataStream = null;
    if (null != data && !data.equals("")) {
      dataStream = resolver.resolve(data);
    }
    Contract contract = contractRep.resolve(name);

    HashMap<String, ?> param = new HashMap();
    while (process) {
      int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.CONTRACT_ELEMENT)) {
          InputStream resultStream = contract.execute(dataStream, param);
          StreamHelper.closeStream(dataStream);
          processContractResult(resultStream);
          StreamHelper.closeStream(resultStream);
          process = false;
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.PROPERTY_ELEMENT)) {
          processProperty(reader, param);
        }
        break;

      default:
        break;

      }
    }
  }

  private void processContractResult(InputStream resultStream)
      throws XMLStreamException {
    XMLStreamReader contractResultReader = getReader(resultStream);
    String xpath = "", injectionPoint = "";
    LinkedHashSet<XMLEvent> pathElement = null;
    boolean process = true;
    while (process) {
      int resultEvent = contractResultReader.next();
      switch (resultEvent) {
      case XMLStreamConstants.START_ELEMENT:
        if (contractResultReader.getLocalName().equals("part")) {
          // Get attribute names
          for (int i = 0; i < contractResultReader.getAttributeCount(); i++) {
            String localName = contractResultReader.getAttributeLocalName(i);
            if (localName.equals(Captions.CONTRACT_RESULT_XPATH)) {
              // Return value
              xpath = contractResultReader.getAttributeValue(i);
            }
          }
          if (xpath.equals("")) {
            // iterate through the children and add them
            // to the pathElement
            if (resultTree.containsKey(currentPath)) {
              pathElement = resultTree.get(currentPath);
            } else {
              pathElement = new LinkedHashSet<XMLEvent>();
            }
            injectionPoint = currentPath;
            inject(pathElement, contractResultReader, injectionPoint);
            // as soon as you find the end element add
            // it back to the resultTree
          } else {
            // iterate through the children and add them
            // to the xpath defined
            if (resultTree.containsKey(xpath))
              pathElement = resultTree.get(xpath);
            else
              pathElement = new LinkedHashSet<XMLEvent>();
            injectionPoint = xpath;
            inject(pathElement, contractResultReader, injectionPoint);
          }
        }
        break;
      case XMLStreamConstants.END_DOCUMENT:
        process = false;

      default:
        break;

      }
    }
  }

  private void processHook(XMLStreamReader reader, boolean start)
      throws XMLStreamException {

    LinkedHashSet<XMLEvent> pathElement;
    if (resultTree.containsKey(currentPath)) {
      pathElement = resultTree.get(currentPath);
    } else {
      pathElement = new LinkedHashSet<XMLEvent>();
    }
    XMLEventAllocator allocator = getEventAllocator();
    XMLEvent currentEvent = allocator.allocate(reader);
    pathElement.add(currentEvent);
    resultTree.put(currentPath, pathElement);
  }

  private void inject(LinkedHashSet<XMLEvent> pathElement,
      XMLStreamReader parser, String injectionPoint) throws XMLStreamException {
    log.debug("injectionPoint " + injectionPoint);
    XMLEventAllocator allocator = getEventAllocator();
    boolean process = true;
    while (process) {
      int event = parser.next();

      XMLEvent currentEvent = allocator.allocate(parser);
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        if (parser.getLocalName().equals("part")) {
          log.debug("Trying to add to hash " + injectionPoint);
          resultTree.put(injectionPoint, pathElement);
          process = false;
        } else {
          pathElement.add(currentEvent);
        }
        break;

      default:
        pathElement.add(currentEvent);
        break;
      }
    }
  }

  private void processProperty(XMLStreamReader reader, HashMap param)
      throws XMLStreamException {
    String propertyName = null, propertyValue = null;
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(Captions.NAME_ATT)) {
        // Return value
        propertyName = reader.getAttributeValue(i);
      } else if (localName.equals(Captions.VALUE_ATT)) {
        propertyValue = reader.getAttributeValue(i);
      }
    }
    if (allowXmlProperties) {
      param.put(propertyName, recordXmlProperies(reader));
    } else if (null != propertyValue && null != propertyName) {
      param.put(propertyName, propertyValue);
    }

  }

  private InputSource recordXmlProperies(XMLStreamReader reader)
      throws XMLStreamException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLEventWriter writerProperty = getWriter(out);
    XMLEventAllocator allocator = getEventAllocator();
    XMLEvent currentEvent = allocator.allocate(reader);
    writerProperty.add(currentEvent);
    boolean process = true;
    while (process) {
      int event = reader.next();
      currentEvent = allocator.allocate(reader);
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        if (reader.getLocalName().equals(Captions.PROPERTY_ELEMENT)) {
          writerProperty.add(currentEvent);
          writerProperty.flush();
          writerProperty.close();
          process = false;
        } else {
          writerProperty.add(currentEvent);
        }
        break;

      default:
        writerProperty.add(currentEvent);
        break;
      }
    }
    InputSource value = new InputSource(StreamHelper.switchStream(out));
    return value;
  }

  private void openPaths(XMLEventWriter writer, String[] tokenizer)
      throws XMLStreamException {
    for (String string : tokenizer) {
      if (!string.equals("")) {
        StartElement value = getEventFactory().createStartElement("", "",
            string);
        writer.add((XMLEvent) value);
      }
    }
  }

  private void closingPaths(XMLEventWriter writer, String[] tokenizer)
      throws XMLStreamException {
    // closing the initial paths again
    for (int j = tokenizer.length - 1; j >= 0; j--) {
      if (!tokenizer[j].equals("")) {
        EndElement value = getEventFactory().createEndElement("", "",
            tokenizer[j]);
        writer.add((XMLEvent) value);
      }
    }
  }

  private void injectResult(XMLEventWriter writer, String element)
      throws XMLStreamException {
    LinkedHashSet<XMLEvent> part = resultTree.get(element);
    Object[] partResult = part.toArray();
    for (int i = 0; i < partResult.length; i++) {
      writer.add((XMLEvent) partResult[i]);
    }
  }
}
