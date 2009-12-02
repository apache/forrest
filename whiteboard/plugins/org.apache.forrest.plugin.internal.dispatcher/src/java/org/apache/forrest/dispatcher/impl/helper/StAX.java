/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher.impl.helper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import com.ctc.wstx.api.WstxOutputProperties;
import com.ctc.wstx.evt.DefaultEventAllocator;

/**
 * Helper class that eases the usage of StAX in your plugins.
 * 
 * @version 1.0
 * 
 */
public class StAX extends Loggable {
  private static XMLOutputFactory outputFactory = null;

  private static XMLEventFactory eventFactory;

  private static XMLInputFactory inputFactory = null;

  private static XMLInputFactory nsInputFactory= null;

  /**
   * Easy helper to get StAX based reader and writer.
   */
  public StAX() {
    inputFactory = XMLInputFactory.newInstance();
    inputFactory.setEventAllocator(DefaultEventAllocator.getDefaultInstance());
    outputFactory = XMLOutputFactory.newInstance();
    outputFactory.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_STRUCTURE, Boolean.TRUE);
    outputFactory.setProperty(WstxOutputProperties.P_OUTPUT_VALIDATE_NAMES, Boolean.TRUE);
    eventFactory = XMLEventFactory.newInstance();
  }

  /**
   * Get an event writer based on the incoming stream
   * 
   * @param stream
   *                the stream we want to write to
   * @return ready to use event writer
   * @throws XMLStreamException
   */
  public XMLEventWriter getWriter(OutputStream stream)
      throws XMLStreamException {
    XMLEventWriter writer = outputFactory.createXMLEventWriter(stream);
    return writer;
  }

  /**
   * Get a stream writer based on the incoming stream
   * 
   * @param stream
   *                the stream we want to write to
   * @return ready to use stream writer
   * @throws XMLStreamException
   */
  public XMLStreamWriter getStreamWriter(OutputStream stream)
      throws XMLStreamException {
    XMLStreamWriter writer = outputFactory.createXMLStreamWriter(stream);
    return writer;
  }

  /**
   * Get an event Reader based on the incoming stream
   * 
   * @param in
   *                the stream we want to read from
   * @return ready to use event reader
   * @throws XMLStreamException
   */
  public XMLEventReader getEventReader(InputStream in)
      throws XMLStreamException {
    XMLEventReader reader = inputFactory.createXMLEventReader(in);
    return reader;
  }

  /**
   * Get a stream reader based on the incoming stream
   * 
   * @param in
   *                the stream we want to read from
   * @return ready to use stream reader
   * @throws XMLStreamException
   */
  public XMLStreamReader getReader(InputStream in) throws XMLStreamException {
    XMLStreamReader reader = inputFactory.createXMLStreamReader(in);
    return reader;
  }

  /**
   * Get a stream reader based on the incoming stream (ns enhanced)
   * 
   * @param in
   *                the stream we want to read from
   * @return ready to use stream reader
   * @throws XMLStreamException
   */
  public XMLStreamReader getNSReader(InputStream in) throws XMLStreamException {
    initNs();
    XMLStreamReader reader = nsInputFactory.createXMLStreamReader(in);
    return reader;
  }
  
  private void initNs() {
    if(null==nsInputFactory)
      nsInputFactory = XMLInputFactory.newInstance();
      nsInputFactory.setEventAllocator(new NSAllocator(true));
  }

  /**
   * Get the ready to use EventFactory
   * 
   * @return ready to use EventFactory
   */
  public XMLEventFactory getEventFactory() {
    return eventFactory;
  }
  
  /**
   *  Get the ready to use Event Allocator
   * @return ready to use Event Allocator
   */
  public XMLEventAllocator getEventAllocator() {
    return  inputFactory.getEventAllocator();
  }
  
  /**
   *  Get the ready to use NS enhanced Event Allocator
   * @return ready to use Event Allocator
   */
  public XMLEventAllocator getNSEventAllocator() {
    initNs();
    return  nsInputFactory.getEventAllocator();
  }
  
  public static XMLEvent createStartElement(final QName name, final Iterator attributes, final Iterator namespaces){
    XMLEvent startElement = eventFactory.createStartElement(name,
        attributes, namespaces);
    return startElement;
  }

  /**
   * Here we calculate the namespaces. If we do not find a declaration for a
   * prefixed node we will see if it is in the NameSpaceContext and create it.
   * 
   * @param name qname of the node that we are working with
   * @param reader the underlying stream reader.
   * @return iterator of the calculated namespaces. 
   */
  public static Iterator<Namespace> allocateNamespacesForced(QName name,
      XMLStreamReader reader) {
    ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
    Set<String> set = new HashSet<String>();
    Namespace namespace;
    for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
      String prefix = reader.getNamespacePrefix(i);
      String uri = reader.getNamespaceURI(i);
      namespace = eventFactory.createNamespace(prefix, uri);
      namespaces.add(namespace);
      set.add(prefix);
    }
    String prefix = name.getPrefix();
    if (null != prefix && !prefix.equals("") && !set.contains(prefix)) {
      NamespaceContext context = reader.getNamespaceContext();
      String uri = context.getNamespaceURI(prefix);
      namespace = eventFactory.createNamespace(prefix, uri);
      namespaces.add(namespace);
    }
    return namespaces.iterator();
  }

  /**
   * Handy method to create an attribute iterator of the current event.
   * @param reader the underlying XMLStreamReader.
   * @return an attribute iterator.
   */
  public static Iterator<Attribute> allocateAttributes(final XMLStreamReader reader) {
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
      attributes.add(eventFactory.createAttribute(reader.getAttributeName(i),
          reader.getAttributeValue(i)));
    }
    return attributes.iterator();
  }
  /**
   * @param reader
   * @param shrink 
   * @return
   * @throws XMLStreamException
   */
  protected byte[] recordXmlProperies(XMLStreamReader reader, boolean shrink) throws XMLStreamException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLEventWriter writerProperty = getWriter(out);
    XMLEventAllocator allocator = getEventAllocator();
    writerProperty.add(eventFactory.createStartDocument());
    XMLEvent currentEvent = allocator.allocate(reader);
    if (currentEvent.isStartElement()){
      XMLEvent startElement = StAX.createStartElementNS(reader, currentEvent);
      writerProperty.add(startElement);
    }
    boolean process = true;
    boolean hadChilds = false;
    while (process) {
      int event = reader.next();
      currentEvent = allocator.allocate(reader);
      switch (event) {
      case XMLStreamConstants.START_ELEMENT:
        if (!reader.getLocalName().equals(Captions.PROPERTY_ELEMENT)){
          hadChilds=true;
          writerProperty.add(currentEvent);
        }else{
          XMLEvent startElement = StAX.createStartElementNS(reader, currentEvent);
          writerProperty.add(startElement);
        }
        break;
      
      case XMLStreamConstants.END_ELEMENT:
        if (reader.getLocalName().equals(Captions.PROPERTY_ELEMENT)) {
          writerProperty.add(currentEvent);
          writerProperty.add(eventFactory.createEndDocument());
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
    log.debug(out.toString());

    byte[] value = null;
    if(!shrink || hadChilds){
      value = out.toByteArray();
    }
    return value;
  }
  
  /**
   * @param properties
   * @param param
   * @param propertyName
   * @param propertyValue
   * @param shrink 
   * @throws XMLStreamException
   */
  public void addProperties(XMLStreamReader reader, Map<String, Object> param,
      String propertyName, String propertyValue,boolean allowXmlProperties, boolean shrink) throws XMLStreamException {
    /*
     *  if we are in allowXmlProperties mode then
     *  we need to see whether we have an input stream meaning child
     *  elements of the property element.
     */
    if (allowXmlProperties ) {
      byte[] recordXmlProperies = recordXmlProperies(reader, shrink);
      if(null != recordXmlProperies){
        param.put(propertyName, recordXmlProperies);
        return;
      }
    }
    /*
     *  Add resolved properties that are not null.
     *  If we are in allowXmlProperties and come up to here
     *  this means the property had no child elements. We
     *  assume then that it had a @value attribute.
     */
    if (null != propertyValue && null != propertyName){
      param.put(propertyName, new String(propertyValue));    
    }
  }

  /**
   * @param reader
   * @param currentEvent
   * @return
   */
  public static XMLEvent createStartElementNS(final XMLStreamReader reader, final XMLEvent currentEvent) {
    QName name = currentEvent.asStartElement().getName();
    Iterator namespaces = null;
    /*
     * Workaround to a xalan bug see thread
     * http://markmail.org/message/5bekq5goyeik7q7l 
     * and XALANJ-2301
     * 
     * We will only append ns declaration for xsl:stylesheet any other xsl element needs to be
     * child of this element in any form so we are save to do so.
     */
    if (!(name.getNamespaceURI().equals(Captions.NS_XSL) && !name.getLocalPart().equals("stylesheet"))){
      namespaces = allocateNamespacesForced(name, reader);
    }
    Iterator attributes = allocateAttributes(reader);
    return createStartElement(name, attributes, namespaces);
  }
}
