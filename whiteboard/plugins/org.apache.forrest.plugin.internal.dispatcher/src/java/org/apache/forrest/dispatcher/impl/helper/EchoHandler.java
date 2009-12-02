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
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Manejador que escribe los eventos SAX a un <code>StringBuffer</code> para
 * posteriormente guardarlo en un <code>InputStream</code>.
 * 
 * @author thorsten
 * @version 1.0
 */
public class EchoHandler extends DefaultHandler {
  protected String encoding = null;

  private StringBuffer xmlBuffer = null;

  private ByteArrayInputStream inputStream = null;

  private byte[] bytes;

  /**
   * Establece la codificación que se empleará en el tratamiento de los datos.
   * Si se pasa <code>null</code> o una cadena vacia emplea "UTF-8" por defecto.
   * 
   * @param encoding
   *          La codificación a emplear.
   */
  public EchoHandler(String encoding) {
    if (null != encoding & !" ".equals(encoding)) {
      this.encoding = encoding;
    } else {
      this.encoding = "UTF-8";
    }
  }

  /*
   * Receive notification of the beginning of a document. (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.DefaultHandler#startDocument()
   */
  public void startDocument() throws SAXException {
    xmlBuffer = new StringBuffer();
    emit("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" );
  }

  /*
   * Receive notification of the end of a document. (non-Javadoc)
   * 
   * @see org.xml.sax.helpers.DefaultHandler#endDocument()
   */
  public void endDocument() throws SAXException {
    try {
      setResult();
    } catch (UnsupportedEncodingException e) {
      throw new SAXException(e);
    }
  }

  /*
   * Receive notification of the beginning of an element.
   * 
   * @param uri The Namespace URI, or the empty string if the element has no
   * Namespace URI or if Namespace processing is not being performed. @param loc
   * The local name (without prefix), or the empty string if Namespace
   * processing is not being performed. @param raw The raw XML 1.0 name (with
   * prefix), or the empty string if raw names are not available. @param atts
   * The attributes attached to the element. If there are no attributes, it
   * shall be an empty Attributes object.
   * 
   * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
   * java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  public void startElement(String uri, String loc, String raw, Attributes attrs)
      throws SAXException {
    String prefix = extractPrefix(raw);
    emit("<" + raw);
    if (attrs != null) {
      createAttributeString(attrs);
    }
    if (uri != null && !uri.equals("")) {
      if (prefix != null) {
        emit(" xmlns:" + prefix + "=\"" + uri + "\"");
      } else {
        emit(" xmlns=\"" + uri + "\"");
      }
    }
    emit(">");
  }

  /**
   * @param attrs
   * @throws SAXException
   */
  protected void createAttributeString(Attributes attrs) throws SAXException {
    // walk attributes backward to
    // avoid unnecessary index change
    for (int i = attrs.getLength() - 1; i >= 0; i--) {
      xmlBuffer.append(" ");
      String qName = attrs.getQName(i);
      xmlBuffer.append(qName);
      xmlBuffer.append("=\"");
      char[] value = attrs.getValue(i).toCharArray();
      characters(value, 0, value.length);

      xmlBuffer.append("\"");
    }
  }

  /**
   * @param raw
   * @return
   */
  public static String extractPrefix(String raw) {
    String[] rawArray = raw.split(":");
    String prefix = null;
    if (rawArray.length == 2) {
      prefix = rawArray[0];
    }
    return prefix;
  }

  /**
   * @param raw
   * @return
   */
  public static String extractSufix(String raw) {
    String[] rawArray = raw.split(":");
    String sufix = null;
    if (rawArray.length == 2) {
      sufix = rawArray[1];
    }
    return sufix;
  }

  protected void emit(char c) {
    xmlBuffer.append(c);
  }

  protected void emit(String string) {
    xmlBuffer.append(string);
  }

  /*
   * Receive notification of the end of an element.
   * 
   * @param uri The Namespace URI, or the empty string if the element has no
   * Namespace URI or if Namespace processing is not being performed. @param loc
   * The local name (without prefix), or the empty string if Namespace
   * processing is not being performed. @param raw The raw XML 1.0 name (with
   * prefix), or the empty string if raw names are not available.
   * 
   * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  public void endElement(String uri, String loc, String raw)
      throws SAXException {
    emit("</" + raw + ">" );
  }

  /*
   * Receive notification of character data.
   * 
   * @param ch The characters from the XML document. @param start The start
   * position in the array. @param length The number of characters to read from
   * the array.
   * 
   * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
   */
  public void characters(char ch[], int start, int length) throws SAXException {
    String outPut = new String(ch,start,length);
    //outPut=StringUtils.trimToEmpty(outPut);
    outPut=StringEscapeUtils.escapeXml(outPut);
    if(!StringUtils.isEmpty(outPut)){
      emit(outPut);
    }
  }

  /*
   * Receive notification of ignorable whitespace in element content.
   * 
   * @param ch The characters from the XML document. @param start The start
   * position in the array. @param length The number of characters to read from
   * the array.
   * 
   * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int,
   * int)
   */
  public void ignorableWhitespace(char ch[], int start, int length)
      throws SAXException {
    this.characters(ch, start, length);
  }

  /*
   * Receive notification of a processing instruction.
   * 
   * @param target The processing instruction target. @param data The processing
   * instruction data, or null if none was supplied.
   * 
   * @see
   * org.xml.sax.helpers.DefaultHandler#processingInstruction(java.lang.String,
   * java.lang.String)
   */
  public void processingInstruction(String target, String data)
      throws SAXException {
    emit("<?" + target + " " + data + "?>");
  }

  /*
   * Receive notification of a skipped entity.
   * 
   * @param name The name of the skipped entity. If it is a parameter entity,
   * the name will begin with '%'.
   * 
   * @see org.xml.sax.helpers.DefaultHandler#skippedEntity(java.lang.String)
   */
  public void skippedEntity(String name) throws SAXException {
    emit("&" + name + ";");
  }

  private void setResult() throws UnsupportedEncodingException {
    try {
      this.bytes = xmlBuffer.toString().getBytes(encoding);
      this.inputStream = new ByteArrayInputStream(this.bytes);
    } catch (UnsupportedEncodingException e) {
      throw new UnsupportedEncodingException();
    }
  }

  /**
   * Return the underlying input stream
   * 
   * @return the input stream
   */
  public ByteArrayInputStream getInputStream() {
    return this.inputStream;
  }

  /**
   * Return the underlying input stream
   * 
   * @return the input stream
   */
  public byte[] getBytes() {
    return this.bytes;
  }
}