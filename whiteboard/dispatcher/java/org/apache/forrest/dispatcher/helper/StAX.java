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

/**
 * Helper class that eases the usage of StAX in your plugins.
 * 
 * @author thorsten
 * @version 1.0
 * 
 */
public class StAX extends Loggable {
  private XMLOutputFactory outputFactory = null;

  private XMLEventFactory eventFactory = null;

  private XMLInputFactory inputFactory = null;

  /**
   * Easy helper to get StAX based parser and writer.
   */
  public StAX() {
    inputFactory = XMLInputFactory.newInstance();
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
   * Get an event Parser based on the incoming stream
   * 
   * @param in
   *                the stream we want to read from
   * @return ready to use event parser
   * @throws XMLStreamException
   */
  public XMLEventReader getEventParser(InputStream in)
      throws XMLStreamException {
    XMLEventReader parser = inputFactory.createXMLEventReader(in);
    return parser;
  }

  /**
   * Get a stream Parser based on the incoming stream
   * 
   * @param in
   *                the stream we want to read from
   * @return ready to use stream parser
   * @throws XMLStreamException
   */
  public XMLStreamReader getParser(InputStream in) throws XMLStreamException {
    XMLStreamReader parser = inputFactory.createXMLStreamReader(in);
    return parser;
  }

  /**
   * Get the ready to used EventFactory
   * 
   * @return ready to used EventFactory
   */
  public XMLEventFactory getEventFactory() {
    return eventFactory;
  }
}
