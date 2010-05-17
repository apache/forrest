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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.forrest.dispatcher.exception.ContractException;
import org.apache.forrest.dispatcher.impl.XSLContract;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XSLContractHelper extends Loggable {

  private Transformer transformer = null;
  private TransformerFactory transFact = null;
  private SAXParser parser;
  private DocumentBuilder builder;
  private String name;
  private ContractHandler handler;
  private LoggingErrorListener listener;
  private XMLReader xmlReader;

  /**
   * @return the name of the contract
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set of the contract
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Create a new instance of the contract helper
   * with the transformer factory we want to use.
   * <p>
   * We are using the parameter approach to optimize performance. 
   * @param transformerFactory the transformer factory that we should use for this helper class
   * @throws ContractException
   * @throws ParserConfigurationException 
   * @throws SAXNotSupportedException 
   * @throws SAXNotRecognizedException 
   */
  public XSLContractHelper(TransformerFactory transformerFactory, EntityResolver entityResolver)
      throws ContractException, SAXNotRecognizedException, SAXNotSupportedException, ParserConfigurationException {
    this.transFact = transformerFactory;
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
    // FIXME: Make encoding configurable
    handler = new ContractHandler("UTF-8");
    listener = new LoggingErrorListener(log);
    try {
      parser = factory.newSAXParser();
      builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      xmlReader=XMLReaderFactory.createXMLReader();
      xmlReader.setEntityResolver(entityResolver);
    } catch (Exception e) {
      throw new ContractException(e);
    }
  }

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
  public void prepareTransformation(Source xslSource,
      boolean allowXmlProperties, Map<String, Object> params)
      throws TransformerConfigurationException, ParserConfigurationException,
      SAXException, IOException {
    // prepare transformation
    transformer = transFact.newTransformer(xslSource);
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    // set errorListener
    transformer.setErrorListener(listener);
    // do we allow xml properties?
    if (allowXmlProperties) {
      for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
        String key = iter.next();
        Class<String> string = String.class;
        Object value = params.get(key);
        if (!string.isInstance(value)) {
          if (log.isDebugEnabled()) {
            log.debug(new String((byte[]) value));
          }
          BufferedInputStream valueStream = new BufferedInputStream(
              new ByteArrayInputStream((byte[]) value));
          transformer.setParameter(key, builder.parse(valueStream)
              .getFirstChild());
        } else {
          transformer.setParameter(key, value);
        }
      }
    } else {
      for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
        String key = iter.next();
        String value = (String) params.get(key);
        transformer.setParameter(key, value);
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
    PrintWriter writer = new PrintWriter (out);
    String doc ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<foo/>";
    writer.write(doc);
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
    return (out != null) ? StreamHelper.switchStream(out) : null;
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
   * @throws ContractException
   */
  public void setTemplate(InputStream stream, XSLContract contract)
      throws XMLStreamException, ContractException {

    try {
      /* DEBUG_CODE: 
       * The following is useful to see what is going on
       * 
       * It will output the contract descriptor as is to 
       * the system out.
       */
      /*
       * byte[] debug  = IOUtils.toByteArray(stream);
       * System.out.println(new String(debug));
       * parser.parse(new ByteArrayInputStream(debug), handler);
       */
      // Process the stream by parsing it with the contractHandler
      parser.parse(stream, handler);
    } catch (Exception e) {
      throw new ContractException(e);
    }
    // Setting the necessary attributes of the contract 
    // xsl
    byte[] bytes = handler.getBytes();
    contract.setXslSource(new StreamSource(new ByteArrayInputStream(bytes)));
    /* DEBUG_CODE: 
     * The following is useful to see what is going on
     * 
     * It will output the xsl stylesheet of the contract descriptor
     * to the system out.
     */
   /* System.out.println(new String(handler
        .getBytes()));*/
    // description - what does the contract?
    contract.setDescription(handler.getDescription());
    // name - how is the contract called?
    name = handler.getName();
    contract.setName(name);
    // usage - how can the contract be used?
    contract.setUsage(handler.getUsage());
  }

  

  /**
   * Is wrapping {@link javax.xml.transform.Transformer.transform(Source xmlSource, Result outputTarget)}
   * to enhancing the exceptions that may have been thrown.
   * @param dataStream the input stream that we want to transform
   * @param streamResult the result object where we want to store the outcome of the 
   * transformation. 
   * @throws ContractException enhanced error messages for sax and FAQ of the most 
   * common problems and solutions 
   */
  public void transform(InputStream dataStream, Result streamResult)
      throws ContractException {
    //Source dataSource = new StreamSource(dataStream);
    try {
      InputSource inputSource = new InputSource(new InputStreamReader(dataStream, "UTF-8"));
      inputSource.setEncoding("UTF-8");
      SAXSource saxSource = new SAXSource(xmlReader,inputSource);
      transformer.transform(saxSource, streamResult);
    } catch (Exception e) {
      String message = "The xsl transformation has thrown an exception. for "
            + "the contract \""+name+"\".\nPlease see some FAQ:"
          + "\n"
          + e
          + "\n\nproblem->solution:\n"
          + "- org.apache.xpath.XPathException: Can not convert #STRING to a NodeList!\n"
          + "-> Try to activate \"allowXml\" and/or \"shrink\". If this is not working try the contract "
          + "xsl standalone and make sure it is not a xsl specific problem.";
      throw new ContractException(message);
    }
  }
}
