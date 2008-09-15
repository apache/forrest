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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.forrest.dispatcher.helper.StAX;
import org.apache.forrest.dispatcher.impl.XSLContract;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XSLContractHelper extends StAX {

  private Transformer transformer = null;

  public static final String NS = "http://apache.org/forrest/templates/2.0";

  public static final String CONTRACT_ELEMENT = "contract";

  public static final String CONTRACT_NAME_ATT = "name";

  public static final String DESCRIPTION_ELEMENT = "description";

  public static final String TEMPLATE_ELEMENT = "template";

  public static final String TEMPLATE_FORMAT_ATT = "inputFormat";

  public static final String USAGE_ELEMENT = "usage";

  public static final String RESULT_XPATH = "xpath";

  /**
   * This method will prepare the transformation from the data and the xsl
   * stylesheet from the contract.
   * 
   * @param xslSource
   *          the xsl stylesheet
   * @param allowXmlProperties
   *          whether or not we want to allow xml properties
   * @throws TransformerConfigurationException
   * @throws ParserConfigurationException 
   * @throws IOException 
   * @throws SAXException 
   */
  public void prepareTransformation(Source xslSource, boolean allowXmlProperties, HashMap<String, ?> params)
      throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
    TransformerFactory transFact = TransformerFactory.newInstance();
    // prepare transformation
    transformer = transFact.newTransformer(xslSource);
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    // do we allow xml properties?
    if(!allowXmlProperties){
      for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
        String key = iter.next();
        String value = (String) params.get(key);
        transformer.setParameter(key,value);
      }
    }else{
      DocumentBuilder builder = DocumentBuilderFactory.newInstance()
      .newDocumentBuilder();
      for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
        String key = iter.next();
        InputSource source = (InputSource) params.get(key);
        transformer.setParameter(key, builder.parse(source));
      }
    }
  }

  /**
   * Will create a dummy xml document with only one element.
   * 
   * @return ByteArrayOutputStream of the dummy xml document.
   * @throws XMLStreamException
   */
  public ByteArrayOutputStream createEmptyXmlOutput() throws XMLStreamException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = getStreamWriter(out);
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeStartElement("foo");
    writer.writeEndDocument();
    writer.flush();
    writer.close();
    return out;
  }

  /**
   * Will create a dummy xml document with only one element contacting
   * {@link #createEmptyXmlOutput()} and then switching the outputStream to an
   * inputStream
   * 
   * @return InputStream of the dummy xml document.
   * @throws XMLStreamException
   */
  public InputStream createEmptyXml() throws XMLStreamException {
    ByteArrayOutputStream out = createEmptyXmlOutput();
    return new BufferedInputStream(new ByteArrayInputStream(out.toByteArray()));
  }

  /**
   * This method sets the xslSource, name, description and usage information of
   * the contract.
   * 
   * This is been done by parsing the contract and extracting the corresponding
   * information.
   * 
   * @param stream
   * @param contract
   * @throws XMLStreamException
   */
  public void setTemplate(InputStream stream, XSLContract contract)
      throws XMLStreamException {
    XMLStreamReader reader = getReader(stream);
    boolean process = true;
    while (process) {
      int event = reader.next();
      switch (event) {

      case XMLStreamConstants.END_DOCUMENT:
        process = false;
        break;

      case XMLStreamConstants.START_ELEMENT:
        String localName = reader.getLocalName();
        if (localName.equals(CONTRACT_ELEMENT)) {
          contract.setName(processContract(reader));
        }
        if (localName.equals(DESCRIPTION_ELEMENT)) {
          contract.setDescription(processDescription(reader));
        }
        if (localName.equals(USAGE_ELEMENT)) {
          contract.setUsage(processUsage(reader));
        }

        if (localName.equals(TEMPLATE_ELEMENT)) {
          contract.setXslSource(processTemplate(reader));
        }

      default:
        break;
      }
    }
  }

  private Source processTemplate(XMLStreamReader reader)
      throws XMLStreamException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLEventWriter writer = getWriter(out);
    XMLEventAllocator allocator = getEventAllocator();
    String role = "";
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      // Get attribute name
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(TEMPLATE_FORMAT_ATT)) {
        // Return value
        role = reader.getAttributeValue(i);
      }
    }
    boolean process = true;
    if (role.equals("xsl")) {
      while (process) {
        int event = reader.next();
        switch (event) {

        case XMLStreamConstants.END_ELEMENT:
          if (reader.getNamespaceURI() != null) {
            if (reader.getNamespaceURI().equals(NS)
                & reader.getLocalName().equals(TEMPLATE_ELEMENT)) {
              process = false;
            } else {
              allocator.allocate(reader,writer);
            }
          } else {
            allocator.allocate(reader,writer);
          }
          break;

        default:
          allocator.allocate(reader,writer);
          break;
        }
      }
    }
    writer.flush();
    log.debug(out.toString());
    Source source = new StreamSource(new BufferedInputStream(
        new ByteArrayInputStream(out.toByteArray())));
    return source;
  }

  private String processUsage(XMLStreamReader reader) throws XMLStreamException {
    boolean process = true;
    String usage = "";
    while (process) {
      int event = reader.next();
      switch (event) {

      case XMLStreamConstants.CHARACTERS:
        if (reader.getText().replace(" ", "").length() > 1) {
          usage = reader.getText().trim();
        }
        break;

      case XMLStreamConstants.END_ELEMENT:
        if (reader.getLocalName().equals(USAGE_ELEMENT)) {
          process = false;
        }
        break;

      default:
        break;
      }
    }
    return usage;
  }

  private String processDescription(XMLStreamReader reader)
      throws XMLStreamException {
    boolean process = true;
    String description = "";
    while (process) {
      int event = reader.next();
      switch (event) {

      case XMLStreamConstants.CHARACTERS:
        if (reader.getText().replace(" ", "").length() > 1) {
          description = reader.getText().trim();
        }
        break;

      case XMLStreamConstants.END_ELEMENT:
        if (reader.getLocalName().equals(DESCRIPTION_ELEMENT)) {
          process = false;
        }
        break;

      default:
        break;
      }
    }

    return description;
  }

  private String processContract(XMLStreamReader reader) {
    String contractName = "";
    for (int i = 0; i < reader.getAttributeCount(); i++) {
      // Get attribute name
      String localName = reader.getAttributeLocalName(i);
      if (localName.equals(CONTRACT_NAME_ATT)) {
        // Return value
        contractName = reader.getAttributeValue(i);
        return contractName;
      }
    }
    return contractName;
  }

  public void transform(InputStream dataStream, Result streamResult) throws TransformerException {
    Source dataSource = new StreamSource(dataStream);
    transformer.transform(dataSource, streamResult);
  }
}
