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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;

import org.xml.sax.InputSource;


import com.ctc.wstx.evt.DefaultEventAllocator;

/**
 * Helper class that eases the usage of StAX in your plugins.
 * 
 * @version 1.0
 * 
 */
public class StAX extends Loggable {
  private XMLOutputFactory outputFactory = null;

  private XMLEventFactory eventFactory = null;

  private XMLInputFactory inputFactory = null;

  /**
   * Easy helper to get StAX based reader and writer.
   */
  public StAX() {
    inputFactory = XMLInputFactory.newInstance();
    inputFactory.setEventAllocator(DefaultEventAllocator.getDefaultInstance());
    outputFactory = XMLOutputFactory.newInstance();
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
   * @param reader
   * @return
   * @throws XMLStreamException
   */
  protected InputSource recordXmlProperies(XMLStreamReader reader) throws XMLStreamException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLEventWriter writerProperty = getWriter(out);
    XMLEventAllocator allocator = getEventAllocator();
    XMLEvent currentEvent = allocator.allocate(reader);
    writerProperty.add(currentEvent);
    boolean process = true;
    boolean hadChilds = false;
    while (process) {
      int event = reader.next();
      currentEvent = allocator.allocate(reader);
      switch (event) {
      case XMLStreamConstants.START_ELEMENT:
        if (!reader.getLocalName().equals(Captions.PROPERTY_ELEMENT)){
          hadChilds=true;
        }
        writerProperty.add(currentEvent);
        break;
      
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
    InputSource value = null;
    if(hadChilds){
      value = new InputSource(new ByteArrayInputStream(out.toByteArray()));
    }
    return value;
  }
  
  /**
   * @param properties
   * @param param
   * @param propertyName
   * @param propertyValue
   * @throws XMLStreamException
   */
  protected void addProperties(XMLStreamReader reader, HashMap param,
      String propertyName, String propertyValue,boolean allowXmlProperties) throws XMLStreamException {
    /*
     *  if we are in allowXmlProperties mode then
     *  we need to see whether we have an input stream meaning child
     *  elements of the property element.
     */
    if (allowXmlProperties ) {
      InputSource recordXmlProperies = recordXmlProperies(reader);
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
}
