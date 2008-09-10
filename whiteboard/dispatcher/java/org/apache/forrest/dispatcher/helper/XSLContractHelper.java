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
import java.io.InputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.apache.forrest.dispatcher.helper.StAX;
import org.apache.forrest.dispatcher.impl.XSLContract;

public class XSLContractHelper extends StAX {

  private Transformer transformer = null;

  private boolean allowXmlProperties = false;

  public static final String NS = "http://apache.org/forrest/templates/1.0";

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
   */
  public void prepareTransformation(Source xslSource, boolean allowXmlProperties)
      throws TransformerConfigurationException {
    TransformerFactory transFact = TransformerFactory.newInstance();
    // prepare transformation
    transformer = transFact.newTransformer(xslSource);
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    // do we allow xml properties?
    this.allowXmlProperties = allowXmlProperties;
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
    XMLStreamReader parser = getParser(stream);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    XMLStreamWriter writer = getStreamWriter(out);
    boolean process = true;
    while (process) {
      int event = parser.next();
      switch (event) {
      case XMLStreamConstants.END_DOCUMENT:
        process = false;
        break;

      case XMLStreamConstants.START_ELEMENT:
        String localName = parser.getLocalName();
        if (localName.equals(CONTRACT_ELEMENT)) {
          contract.setName(processContract(parser));
        }
      }
    }
  }

  private String processContract(XMLStreamReader parser) {
    String contractName = "";
    for (int i = 0; i < parser.getAttributeCount(); i++) {
      // Get attribute name
      String localName = parser.getAttributeLocalName(i);
      if (localName.equals(CONTRACT_NAME_ATT)) {
        // Return value
        contractName = parser.getAttributeValue(i);
        return contractName;
      }
    }
    return contractName;
  }
}
