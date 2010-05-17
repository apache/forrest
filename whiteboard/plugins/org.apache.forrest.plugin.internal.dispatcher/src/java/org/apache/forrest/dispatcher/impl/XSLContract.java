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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.exception.ContractException;
import org.apache.forrest.dispatcher.impl.helper.Loggable;
import org.apache.forrest.dispatcher.impl.helper.XSLContractHelper;
import org.xml.sax.EntityResolver;

public class XSLContract extends Loggable implements Contract {

  private String name = "", usage = "", description = "";
  private final XSLContractHelper helper;
  private Source xslSource = null;

  private boolean allowXmlProperties = false;

  /**
   * This contract implementation is assuming xsl based contracts
   * and their corresponding descriptors. A contract descriptor is
   * providing information about name, description, usage and xsl stylesheet.
   * <p>
   * 
   * @param allowXmlProperties are we allowing xml based properties. This are the ones where you 
   * have you own set of xml within the <code>&lt;forrest:property></code> element.
   * @param transformerFactory the transformer factory that we should use
   * @param entityResolver 
   * @throws ContractException
   */
  public XSLContract(boolean allowXmlProperties, TransformerFactory transformerFactory, EntityResolver entityResolver) throws ContractException {
    this.allowXmlProperties = allowXmlProperties;
    /*
     * get a new instance of the corresponding helper class since the helper is
     * doing the actual work
     */
    try {
      helper = new XSLContractHelper(transformerFactory,entityResolver);
    } catch (Exception e) {
      throw new ContractException(e);
    }
  }

  /* (non-Javadoc)
   * @see org.apache.forrest.dispatcher.api.Contract#execute(java.io.InputStream, java.util.Map)
   */
  public BufferedInputStream execute(InputStream dataStream,
      Map<String, Object> properties) throws ContractException {
    if (xslSource == null || helper == null) {
      String message = "The xsl source have not been initialize from stream "
          + "for the contract \""+name+"\".";
      throw new ContractException(message);
    }
    try {
      /*
       * prepare the transformation in the helper class
       */
      helper.prepareTransformation(xslSource, allowXmlProperties, properties);
    } catch (TransformerConfigurationException e) {
      String message =  "Could not setup the transformer for "
                + "the contract \""+name+"\".\n"+ e;
      throw new ContractException(message );
    } catch (Exception e) {
      throw new ContractException(e);
    }
    /*
     * If no dataStream is present we need to create an empty xml doc to be able
     * to invoke the xsl transformation.
     */
    if (null == dataStream) {
      try {
        dataStream = helper.createEmptyXml();
      } catch (XMLStreamException e) {
        String message = "Could not create an empty xml document for "
            + "the contract \""+name+"\".\n"+ e;
        throw new ContractException(message);
      }
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] bytes = null;
    String utf8 = "";
    // create a StreamResult and use it for the transformation
    try {
        OutputStreamWriter writer = new OutputStreamWriter(out,"UTF-8");
        Result streamResult = new StreamResult(writer);
        helper.transform(dataStream,streamResult);
        utf8 = out.toString("UTF-8");
        log.debug(utf8);
        bytes = utf8.getBytes("utf-8");
    } catch (Exception e) {
      String message = "Could not invoke the transformation for "
          + "the contract \""+name+"\". "+"\n"+ e;
      throw new ContractException(message);
    }
    
    return new BufferedInputStream(new ByteArrayInputStream(bytes));
  }

  /* (non-Javadoc)
   * @see org.apache.forrest.dispatcher.api.Contract#initializeFromStream(java.io.InputStream)
   */
  public void initializeFromStream(InputStream stream)
      throws ContractException {
    // invoke the extraction
    try {
      helper.setTemplate(stream, this);
    } catch (XMLStreamException e) {
      throw new ContractException(e);
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
