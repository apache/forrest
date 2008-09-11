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
package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.forrest.dispatcher.DispatcherException;
import org.apache.forrest.dispatcher.api.Contract;

import org.apache.forrest.dispatcher.helper.Loggable;
import org.apache.forrest.dispatcher.helper.XSLContractHelper;

public class XSLContract extends Loggable implements Contract {

  private String name = "", usage = "", description = "";
  private XSLContractHelper helper = null;
  private Source xslSource = null;

  private boolean allowXmlProperties = false;

  public XSLContract(boolean allowXmlProperties) throws DispatcherException {
    this.allowXmlProperties = allowXmlProperties;
  }

  public BufferedInputStream execute(InputStream dataStream,
      HashMap<String, ?> properties) throws DispatcherException {
    if (xslSource == null || helper == null) {
      throw new DispatcherException("Contract \"" + name
          + "\" has produced an exception"
          + "The xsl source have not been initialize from stream"
          + "the contract. We cannot proceed without this.");
    }
    try {
      /*
       * prepare the transformation in the helper class
       */
      helper.prepareTransformation(xslSource, allowXmlProperties, properties);
    } catch (TransformerConfigurationException e) {
      throw new DispatcherException("Contract \"" + name
          + "\" has produced an exception"
          + "Could not setup the transformer for"
          + "the contract. We cannot proceed without this.", e);
    } catch (Exception e) {
      throw new DispatcherException("Contract \"" + name
          + "\" has produced an exception"
          + "Could not setup the DOM Parser for"
          + "the contract. We cannot proceed without this.", e);
    }
    /*
     * If no dataStream is present we need to create an empty xml doc to be able
     * to invoke the xsl transformation.
     */
    if (null == dataStream) {
      try {
        dataStream = helper.createEmptyXml();
      } catch (XMLStreamException e) {
        throw new DispatcherException("Contract \"" + name
            + "\" has produced an exception"
            + "Could not create an empty xml document for"
            + "the contract. We cannot proceed without this.", e);
      }
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    // create a StreamResult and use it for the transformation
    Result streamResult = new StreamResult(new BufferedOutputStream(out));
    try {
      helper.transform(dataStream,streamResult);
    } catch (TransformerException e) {
      throw new DispatcherException("Contract \"" + name
          + "\" has produced an exception"
          + "Could not invoke the transformation for"
          + "the contract. We cannot proceed without this.", e);
    }
    log.debug(out.toString());
    return new BufferedInputStream(new ByteArrayInputStream(out.toByteArray()));
  }

  public void initializeFromStream(InputStream stream)
      throws DispatcherException {
    /*
     * get a new instance of the corresponding helper class since the helper is
     * doing the actual work
     */
    helper = new XSLContractHelper();
    // invoke the extraction
    try {
      helper.setTemplate(stream, this);
    } catch (XMLStreamException e) {
      throw new DispatcherException(e);
    }

  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

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

  public boolean isAllowXmlProperties() {
    return allowXmlProperties;
  }

  public void setAllowXmlProperties(boolean allowXmlProperties) {
    this.allowXmlProperties = allowXmlProperties;
  }

  public Source getXslSource() {
    return xslSource;
  }

  public void setXslSource(Source xslSource) {
    this.xslSource = xslSource;
  }
}
