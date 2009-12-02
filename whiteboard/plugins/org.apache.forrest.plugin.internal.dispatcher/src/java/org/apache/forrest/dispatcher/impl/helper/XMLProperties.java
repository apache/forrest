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

import java.io.InputStream;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * This class a helper class to extract the properties that
 * are stored in a forrest.properties.xml file.
 * 
 * @version 1.0
 *
 */
public class XMLProperties {
  
 
  private XMLProperties (){
  }

  /**
   * Will extract the properties of the in coming forrest.properties.xml.
   * <p>
   * The extracted key, value pairs will be added to the map that has been
   * passed to the method.
   * @param stream forrest.properties.xml like properties stream.
   * like e.g. 
   * <code><pre>
   * &lt;properties input-module="properties">
   *  &lt;property name="dispatcher.caching" value="on"/>
   *  &lt;-- ... -->
   * &lt;/properties>
   * </pre><code>
   * @param map the map where we want to store the extracted variables.
   * @throws XMLStreamException
   */
  public static void parseProperties(InputStream stream, Map<String, Object> map) throws XMLStreamException{
    XMLStreamReader reader  = XMLInputFactory.newInstance().createXMLStreamReader(stream);
    boolean loop = true;
    String elemName = null, value = "", key = "";
    while (loop) {
      int event = reader.next();

      switch (event) {
      case XMLStreamConstants.END_DOCUMENT:
        loop = false;
        break;

      case XMLStreamConstants.END_ELEMENT:
        elemName = reader.getLocalName();
        if (elemName.equals(Captions.PROPERTY_ELEMENT)) {
          if (!map.containsKey(key)) {
            map.put(key, value);
          }
          key = "";
          value = "";
        }
        break;

      case XMLStreamConstants.START_ELEMENT:
        elemName = reader.getLocalName();
        if (elemName.equals(Captions.PROPERTY_ELEMENT)) {
          for (int i = 0; i < reader.getAttributeCount(); i++) {
            // Get attribute name
            String localName = reader.getAttributeLocalName(i);
            String localValue = reader.getAttributeValue(i);
            if (localName.equals(Captions.NAME_ATT)) {
              key = localValue;
            } else if (localName.equals(Captions.VALUE_ATT)) {
              value = localValue;
            }
          }
        }
        break;

      default:
        break;
      }
    }
  }
}
