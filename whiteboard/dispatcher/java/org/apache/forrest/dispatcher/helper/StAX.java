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
package org.apache.forrest.dispatcher.helper;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.XMLEventAllocator;

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
}
