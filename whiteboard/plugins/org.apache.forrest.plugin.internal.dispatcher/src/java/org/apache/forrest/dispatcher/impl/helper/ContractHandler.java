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
package org.apache.forrest.dispatcher.impl.helper;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ContractHandler extends EchoHandler {

  private boolean recording = false;
  private HashMap<String, String> map;
  private HashSet<String> mapTrigger;
  private String name = "", usage = "", description = "";
  private boolean descriptionRecord;
  private boolean usageRecord;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsage() {
    return usage;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ContractHandler(String encoding) {
    super(encoding);
    map = new HashMap<String, String>();
    mapTrigger = new HashSet<String>();
    descriptionRecord = false;
    usageRecord = false;
  }

  public void startElement(String uri, String loc, String raw, Attributes attrs)
      throws SAXException {
    if (uri.equals(Captions.NS) && loc.equals(Captions.CONTRACT_ELEMENT)){
      for (int i = 0; i < attrs.getLength(); i++) {
        String aName = attrs.getLocalName(i); // Attr name
        if (aName.equals(Captions.CONTRACT_NAME_ATT)) {
          name = attrs.getValue(i);
        }
      }
    } else if (raw.equals(Captions.DESCRIPTION_ELEMENT)){
      descriptionRecord = true;
    } else if (raw.equals(Captions.USAGE_ELEMENT)){
      usageRecord = true;
    }else if (uri.equals(Captions.NS_XSL) && loc.equals("stylesheet")) {
      recording = true;
    }

    if (recording) {
      String prefix = extractPrefix(raw);
      emit( "<" + raw);
      if (uri != null && !uri.equals("")) {
        if (prefix != null) {
          if (!map.containsKey(prefix)) {
            emit(" xmlns:" + prefix + "=\"" + uri + "\"");
            map.put(prefix, uri);
            mapTrigger.add(raw);
          }
        } else {
          emit(" xmlns=\"" + uri + "\"");
        }
      }
      if (attrs != null) {
        for(int i=attrs.getLength()-1;i>=0;i--){
          String qName = attrs.getQName(i);
          String localName = extractSufix(qName);
          prefix = extractPrefix(qName);
          if ( null == prefix || (null != prefix && !prefix.equals("xmlns"))) {
            writeAttribute(attrs.getValue(i), qName);
          }else if (null != prefix && prefix.equals("xmlns") && !map.containsKey(localName)){
            writeAttribute(attrs.getValue(i), qName);
          }
        }
      }
        emit(">");
    }
  }

  private void writeAttribute(String localValue, String qName)
      throws SAXException {
    emit(" ");
    emit(qName);
    emit("=\"");
    char[] value = localValue.toCharArray();
    characters(value, 0, value.length);
    emit("\"");
  }

  public void endElement(String uri, String loc, String raw)
      throws SAXException {
    if (raw.equals(Captions.DESCRIPTION_ELEMENT)){
      descriptionRecord = false;
    }else if (raw.equals(Captions.USAGE_ELEMENT)){
      usageRecord = false;
    }
    if (recording) {
      String prefix = extractPrefix(raw);
      if (prefix != null && map.containsKey(prefix) && mapTrigger.contains(raw)) {
        map.remove(prefix);
        mapTrigger.remove(raw);
      }
        super.endElement(uri, loc, raw);
    }
    if (uri.equals("http://www.w3.org/1999/XSL/Transform")
        && loc.equals("stylesheet")) {
      endPrefixMapping(extractPrefix(raw));
      recording = false;
    }
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    if (recording) {
      super.characters(ch, start, length);
    }else if (descriptionRecord){
      description += cleanCharacters(ch, start, length);
    }else if (usageRecord){
      usage += cleanCharacters(ch, start, length);
    }
  }

  /**
   * @param ch
   * @param start
   * @param length
   */
  private String cleanCharacters(char[] ch, int start, int length) {
    return StringEscapeUtils.escapeXml(new String(ch,start,length));
  }

}
