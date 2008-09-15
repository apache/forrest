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
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import org.apache.forrest.dispatcher.DispatcherException;
import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.helper.StAX;
import org.apache.forrest.dispatcher.utils.CommonString;
import org.xml.sax.InputSource;

public class XMLStructurer extends StAX {

  public static final String NS = "http://apache.org/forrest/templates/2.0";

  public static final String STRUCTURER_ELEMENT = "structurer";

  public static final String STRUCTURE_ELEMENT = "structure";

  public static final String TYPE_ATT = "type";

  public static final String HOOKS_ATT = "hooksXpath";

  public static final String CONTRACT_ELEMENT = "contract";

  public static final String NAME_ATT = "name";

  public static final String DATA_ATT = "dataURI";

  public static final String PROPERTY_ELEMENT = "property";

  public static final String HOOK_ELEMENT = "hook";

  private static final Object VALUE_ATT = "value";

  private static final Object CONTRACT_RESULT_XPATH = "xpath";

  private String format = "";
  private InputStream dataStream = null;

  private String currentPath = "";

  private boolean allowXmlProperties = false;

  private LinkedHashMap<String, LinkedHashSet<XMLEvent>> resultTree = new LinkedHashMap<String, LinkedHashSet<XMLEvent>>();

  public boolean isAllowXmlProperties() {
    return allowXmlProperties;
  }

  public void setAllowXmlProperties(boolean allowXmlProperties) {
    this.allowXmlProperties = allowXmlProperties;
  }

  private String contractUriPrefix = "";
  private String contractUriSufix = ".contract.xml";

  public String getContractUriPrefix() {
    return contractUriPrefix;
  }

  public void setContractUriPrefix(String contractUriPrefix) {
    this.contractUriPrefix = contractUriPrefix;
  }

  public String getContractUriSufix() {
    return contractUriSufix;
  }

  public void setContractUriSufix(String contractUriSufix) {
    this.contractUriSufix = contractUriSufix;
  }

  public XMLStructurer(InputStream dataStream, String format) {
    this.format = format;
    this.dataStream = dataStream;
  }

  public BufferedInputStream execute() throws DispatcherException {
    BufferedInputStream stream = null;
    try {
      XMLStreamReader reader = getReader(dataStream);
      boolean process = true;
      while (process) {
        int event = reader.next();
        switch (event) {
        case XMLStreamConstants.END_DOCUMENT:
          process = false;
          break;

        case XMLStreamConstants.START_ELEMENT:
          String elementName = reader.getLocalName();
          if (elementName.equals(STRUCTURE_ELEMENT)) {
            String m_type = "", path = "";
            // Get attribute names
            for (int i = 0; i < reader.getAttributeCount(); i++) {
              String localName = reader.getAttributeLocalName(i);
              if (localName.equals(TYPE_ATT)) {
                // Return value
                m_type = reader.getAttributeValue(i);
              } else if (localName.equals(HOOKS_ATT)) {
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
    }
    return stream;
  }

  private BufferedInputStream processStructure(XMLStreamReader reader)
      throws XMLStreamException, DispatcherException, IOException {
    boolean process = true;
    String elementName = null;
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLEventWriter writer = getWriter(out);
    while (process) {
      int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(STRUCTURE_ELEMENT)) {
          writer.add(getEventFactory().createStartDocument("UTF-8", "1.0"));
          Iterator<String> iterator = resultTree.keySet().iterator();
          String[] paths = resultTree.keySet().toArray(new String[1]);
          String rootPath = CommonString.common(paths);
          String[] tokenizer = rootPath.split("/");
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
          resultTree.clear();
          process = false;
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(CONTRACT_ELEMENT)) {
          log.debug("Path " + currentPath);
          processContract(reader);
        } else if (elementName.equals(HOOK_ELEMENT)) {
          log.debug("HOOKS " + elementName);
          log.info("HOOKS transformation NOT YET IMPLEMENTED");
        }
        break;

      default:
        break;

      }
    }
    log.debug(out.toString());
    return (out != null) ? new BufferedInputStream(new ByteArrayInputStream(out
        .toByteArray())) : null;
  }

  private void processContract(XMLStreamReader reader)
      throws XMLStreamException, DispatcherException, IOException {
    boolean process = true;
    String elementName = null;
    String name = "", data = "";
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(NAME_ATT)) {
        // Return value
        name = reader.getAttributeValue(i);
      } else if (localName.equals(DATA_ATT)) {
        data = reader.getAttributeValue(i);
      }
    }
    /*
     * FIXME: TEMPORAL HACK ONLY Use source resolver/contract factory when
     * fixing this.
     * 
     * Ignoring dataStream completely for now
     * 
     * THIS ONLY WORKS FOR JUNIT ATM!!!
     */
    dataStream = null;
    Contract contract = new XSLContract(allowXmlProperties);
    InputStream xslStream = this.getClass().getResourceAsStream(
        this.contractUriPrefix + name + this.contractUriSufix);
    contract.initializeFromStream(xslStream);
    // closing stream
    if (xslStream != null) {
      xslStream.close();
    }
    /*
     * HACK END
     */

    HashMap<String, ?> param = new HashMap();
    while (process) {
      int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(CONTRACT_ELEMENT)) {
          InputStream resultStream = contract.execute(dataStream, param);
          if (null != dataStream) {
            dataStream.close();
          }
          processContractResult(resultStream);
          if (null != resultStream) {
            resultStream.close();
          }
          process = false;
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(PROPERTY_ELEMENT)) {
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
            if (localName.equals(CONTRACT_RESULT_XPATH)) {
              // Return value
              xpath = contractResultReader.getAttributeValue(i);
            }
          }
          if (xpath.equals("")) {
            // iterate through the children and add them
            // to the pathElement
            if (resultTree.containsKey(currentPath))
              pathElement = resultTree.get(currentPath);
            else
              pathElement = new LinkedHashSet<XMLEvent>();
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
      if (localName.equals(NAME_ATT)) {
        // Return value
        propertyName = reader.getAttributeValue(i);
      } else if (localName.equals(VALUE_ATT)) {
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
        if (reader.getLocalName().equals(PROPERTY_ELEMENT)) {
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
    InputSource value = new InputSource(new ByteArrayInputStream(out
        .toByteArray()));
    return value;
  }

  private void openPaths(XMLEventWriter writer, String[] tokenizer)
      throws XMLStreamException {
    for (int i = 0; i < tokenizer.length; i++) {
      if (!tokenizer[i].equals("")) {
        StartElement value = getEventFactory().createStartElement("", "",
            tokenizer[i]);
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
