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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;

import com.ctc.wstx.evt.DefaultEventAllocator;

/**
 * This allocator will add always the xmlns declaration for the prefix used in
 * the start element. Be aware that even if a parent element already declared the
 * same namespace we will declare it again and again. Meaning the allocator is
 * not aware of heredity of namespaces.
 * 
 * @version 1.0
 */
public class NSAllocator extends DefaultEventAllocator implements
    XMLEventAllocator {

  public NSAllocator(boolean accurateLocation) {
    super(accurateLocation);
  }

  /*
   * @see com.ctc.wstx.evt.DefaultEventAllocator#allocate(javax.xml.stream.
   * XMLStreamReader, javax.xml.stream.util.XMLEventConsumer)
   */
  @SuppressWarnings("unchecked")
  public void allocate(XMLStreamReader reader, XMLEventConsumer consumer)
      throws XMLStreamException {
    XMLEvent event = allocate(reader);
    if (event != null) {
      if (event.isStartElement()) {
        XMLEvent startElement = StAX.createStartElementNS(reader, event);
        consumer.add(startElement);
      } else {
        consumer.add(event);
      }
    }
  }

}
