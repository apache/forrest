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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;


import org.apache.forrest.dispatcher.api.Contract;

import org.apache.forrest.dispatcher.helper.Loggable;
import org.apache.forrest.dispatcher.helper.XSLContractHelper;

public class XSLContract extends Loggable implements Contract {

  private String name="", usage="", description="";
  private XSLContractHelper helper=null;

  public OutputStream execute(InputStream dataStream, HashMap properties) {
    /* 
     * get a new instance of the corresponding helper class since the helper
     * is doing the actual work
     */
     helper = new XSLContractHelper();
    /*
     *  If no dataStream is present we need to create an empty xml doc
     *  to be able to invoke the xsl transformation.
     */
    if (null == dataStream) {
      try {
        dataStream = helper.createEmptyXml();
      } catch (XMLStreamException e) {
        log.fatal(e);
      }
    }

    return null;
  }

  public void initializeFromStream(InputStream stream) {
    // TODO Auto-generated method stub

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
}
