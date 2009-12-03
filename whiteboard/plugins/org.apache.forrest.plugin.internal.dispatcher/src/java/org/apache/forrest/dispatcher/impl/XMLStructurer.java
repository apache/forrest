/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.ContractException;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.helper.Captions;
import org.apache.forrest.dispatcher.impl.helper.CommonString;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.apache.forrest.dispatcher.impl.helper.StreamHelper;

/**
 * @author thorsten
 * @version 1.0
 */
public class XMLStructurer extends AbstractXmlStructurer {

  private final LinkedHashMap<String, LinkedHashSet<XMLEvent>> resultTree = new LinkedHashMap<String, LinkedHashSet<XMLEvent>>();

  private String currentPath = "";
  
  /**
   * The Streaming API for XML (StAX) is a Java based API for pull-parsing XML.
   *<p>
   * This implementation is 100% StAX even the generation of the resulting document
   * is done with StAX. This has some limitation in comparison with the AXIOM implementation.
   * <p><strong>No real support of xpath expressions.</strong> You can not freely allocate a node and append the
   * result of a contract. We will generate the path but not always as child.
   *  
   * @param config
   * @param defaultProperties
   */
  public XMLStructurer(final DispatcherBean config, final Map<String, Object> defaultProperties) {
    super(config, defaultProperties);
  }

  /*
   * @see
   * org.apache.forrest.dispatcher.impl.Structurer#execute(java.io.InputStream,
   * java.lang.String)
   */
  public InputStream execute(final InputStream structurerStream, final String format)
      throws DispatcherException {
    BufferedInputStream stream = null;
    try {
      final XMLStreamReader reader = stax.getReader(structurerStream);
      boolean process = true;
      while (process) {
        final int event = reader.next();
        switch (event) {
        case XMLStreamConstants.END_DOCUMENT:
          process = false;
          break;

        case XMLStreamConstants.START_ELEMENT:
          final String elementName = reader.getLocalName();
          if (elementName.equals(Captions.STRUCTURE_ELEMENT)) {
            String m_type = "", path = "";
            // Get attribute names
            for (int i = 0; i < reader.getAttributeCount(); i++) {
              final String localName = reader.getAttributeLocalName(i);
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
    } catch (final XMLStreamException e) {
      throw new DispatcherException(e);
    } finally {
      try {
        StreamHelper.closeStream(structurerStream);
      } catch (final IOException e) {
        throw new DispatcherException(e);
      }
    }
    return stream;
  }

  /**
 * @param reader
 * @return
 * @throws XMLStreamException
 * @throws DispatcherException
 */
private BufferedInputStream processStructure(final XMLStreamReader reader)
      throws XMLStreamException, DispatcherException{
    boolean process = true;
    String elementName = null;
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    while (process) {
      final int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.STRUCTURE_ELEMENT)) {
          final XMLEventWriter writer = stax.getWriter(out);
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
          log.debug("CONTRACT " + elementName);
          processContract(reader);
        } else if (elementName.equals(Captions.HOOK_ELEMENT)) {
          log.debug("HOOKS " + elementName);
          processHook(reader, true);
        }
        break;

      default:
        break;

      }
    }
    log.debug(out.toString());
    return StreamHelper.switchStream(out);
  }

  /**
   * Create the outcome of the hooks and contracts. Here we need to find the
   * injectionPoints that can be defined in the different contracts.
   * 
   * This injectionPoints can be within or extending other.
 * @param writer
 * @throws XMLStreamException
 */
private void createResultStax(final XMLEventWriter writer)
      throws XMLStreamException {
    // We start with creating a new result document
    writer.add(stax.getEventFactory().createStartDocument("UTF-8", "1.0"));
    // get a iterator about the injectionPoints we use
    final Iterator<String> iterator = resultTree.keySet().iterator();
    // create an path array
    final String[] paths = resultTree.keySet().toArray(new String[1]);
    // determine the common root path for all paths
    final String rootPath = CommonString.common(paths);
    // Prepare the creation of the root path
    final String[] tokenizer = rootPath.split("/");
    // create the events related to the root path
    openPaths(writer, tokenizer);
    while (iterator.hasNext()) {
      final String element = iterator.next();
      final String replaceFirst = element.replaceFirst(rootPath, "");
      final String[] split = replaceFirst.split("/");
      if (split.length > 1) {
        openPaths(writer, split);
        injectResult(writer, element);
        closingPaths(writer, split);
      } else {
        if (null != replaceFirst && !replaceFirst.equals("")) {
          final StartElement start = stax.getEventFactory().createStartElement("", "",
              replaceFirst);
          writer.add((XMLEvent) start);
        }else if(element.equals("/")){
          final StartElement start = stax.getEventFactory().createStartElement("", "",
              "result");
          writer.add((XMLEvent) start);
        }

        injectResult(writer, element);
        if (replaceFirst != null && !replaceFirst.equals("")) {
          final EndElement end = stax.getEventFactory().createEndElement("", "",
              replaceFirst);
          writer.add((XMLEvent) end);
        }else if(element.equals("/")){
          final EndElement end = stax.getEventFactory().createEndElement("", "",
              "result");
          writer.add((XMLEvent) end);
    }

      }

    }
    closingPaths(writer, tokenizer);
    writer.add(stax.getEventFactory().createEndDocument());
  }

  /**
 * @param reader
 * @throws DispatcherException
 * @throws XMLStreamException
 */
private void processContract(final XMLStreamReader reader)
      throws DispatcherException, XMLStreamException{
    boolean process = true;
    String elementName = null;
    String name = "", data = null;
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      final String localName = reader.getAttributeLocalName(i);
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
    final Contract contract = contractRep.resolve(name);
    final Map<String, Object> localParams = new HashMap<String, Object>(param);
    while (process) {
      final int event = reader.next();
      switch (event) {
      case XMLStreamConstants.END_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.CONTRACT_ELEMENT)) {
          try {
            final InputStream resultStream = contract.execute(dataStream, localParams);
            StreamHelper.closeStream(dataStream);
            processContractResult(resultStream);
            StreamHelper.closeStream(resultStream);
          } catch (final Exception e) {
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
          process = false;
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elementName = reader.getLocalName();
        if (elementName.equals(Captions.PROPERTY_ELEMENT)) {
          processProperty(reader, localParams);
        }
        break;

      default:
        break;

      }
    }
  }

  /**
 * @param resultStream
 * @throws XMLStreamException
 */
private void processContractResult(final InputStream resultStream)
      throws XMLStreamException {
    final XMLStreamReader contractResultReader = stax.getReader(resultStream);
    String xpath = "", injectionPoint = "";
    LinkedHashSet<XMLEvent> pathElement = null;
    boolean process = true;
    while (process) {
      final int resultEvent = contractResultReader.next();
      switch (resultEvent) {
      case XMLStreamConstants.START_ELEMENT:
        if (contractResultReader.getLocalName().equals(Captions.PART_ELEMENT)) {
          // Get attribute names
          for (int i = 0; i < contractResultReader.getAttributeCount(); i++) {
            final String localName = contractResultReader.getAttributeLocalName(i);
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
            // as soon as you find the end element add
            // it back to the resultTree
          } else {
            // iterate through the children and add them
            // to the xpath defined
            if (resultTree.containsKey(xpath)){
              pathElement = resultTree.get(xpath);
            }else{
              pathElement = new LinkedHashSet<XMLEvent>();
            }
            injectionPoint = xpath;
          }
          inject(pathElement, contractResultReader, injectionPoint);
          // cleaning path again
          xpath="";
          }
        break;
      case XMLStreamConstants.END_DOCUMENT:
        process = false;
        break;

      default:
        break;

      }
    }
  }

  /**
 * @param reader
 * @param start
 * @throws XMLStreamException
 */
private void processHook(final XMLStreamReader reader, final boolean start)
      throws XMLStreamException {

    LinkedHashSet<XMLEvent> pathElement = null;
    if (resultTree.containsKey(currentPath)) {
      pathElement = resultTree.get(currentPath);
    } else {
      pathElement = new LinkedHashSet<XMLEvent>();
    }
    final XMLEventAllocator allocator = stax.getEventAllocator();
    XMLEvent currentEvent = allocator.allocate(reader);
    if (start){
      currentEvent = StAX.createStartElementNS(reader, currentEvent);
    }
    pathElement.add(currentEvent);
    resultTree.put(currentPath, pathElement);
  }

  /**
 * @param pathElement
 * @param parser
 * @param injectionPoint
 * @throws XMLStreamException
 */
private void inject(final LinkedHashSet<XMLEvent> pathElement,
      final XMLStreamReader parser, final String injectionPoint) throws XMLStreamException {
    log.debug("injectionPoint " + injectionPoint);
    final XMLEventAllocator allocator = stax.getEventAllocator();
    boolean process = true;
    while (process) {
      final int event = parser.next();

      final XMLEvent currentEvent = allocator.allocate(parser);
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

  /**
 * @param reader
 * @param localParam
 * @throws XMLStreamException
 */
private void processProperty(final XMLStreamReader reader, final Map<String, Object> localParam)
      throws XMLStreamException {
    String propertyName = null, propertyValue = null;
    // Get attribute names
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      final String localName = reader.getAttributeLocalName(i);
      if (localName.equals(Captions.NAME_ATT)) {
        // Return value
        propertyName = reader.getAttributeValue(i);
      } else if (localName.equals(Captions.VALUE_ATT)) {
        propertyValue = reader.getAttributeValue(i);
      }
    }
    stax.addProperties(reader, localParam, propertyName, propertyValue,allowXmlProperties, shrink);
  }

  /**
 * @param writer
 * @param tokenizer
 * @throws XMLStreamException
 */
private void openPaths(final XMLEventWriter writer, final String[] tokenizer)
      throws XMLStreamException {
    for (final String string : tokenizer) {
      if (!string.equals("")) {
        final StartElement value = stax.getEventFactory().createStartElement("", "",
            string);
        writer.add((XMLEvent) value);
      }
    }
  }

  /**
 * @param writer
 * @param tokenizer
 * @throws XMLStreamException
 */
private void closingPaths(final XMLEventWriter writer, final String[] tokenizer)
      throws XMLStreamException {
    // closing the initial paths again
    for (int j = tokenizer.length - 1; j >= 0; j--) {
      if (!tokenizer[j].equals("")) {
        final EndElement value = stax.getEventFactory().createEndElement("", "",
            tokenizer[j]);
        writer.add((XMLEvent) value);
      }
    }
  }

  /**
 * @param writer
 * @param element
 * @throws XMLStreamException
 */
private void injectResult(final XMLEventWriter writer, final String element)
      throws XMLStreamException {
    final LinkedHashSet<XMLEvent> part = resultTree.get(element);
    final Object[] partResult = part.toArray();
    for (int i = 0; i < partResult.length; i++) {
      writer.add((XMLEvent) partResult[i]);
    }
  }
}
