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
package org.apache.forrest.dispatcher;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.XSLContract;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class TestContract extends TestCase {

  private static final String CONTRACT_XML = "master.contract.xml";
  public void testContractWithoutParameter() throws DispatcherException, FileNotFoundException {
    Contract contract = new XSLContract(false, null, new StAX());
    InputStream xslStream = this.getClass().getResourceAsStream(CONTRACT_XML);
    contract.initializeFromStream(xslStream);
    // testing the transformation without parameters
    Map<String, Object> properties = new HashMap<String, Object>();
    contract.execute(null, properties);
  }
  public void testContractWithParameter() throws DispatcherException, FileNotFoundException {
    Contract contract = new XSLContract(false, null, new StAX());
    InputStream xslStream = this.getClass().getResourceAsStream(CONTRACT_XML);
    contract.initializeFromStream(xslStream);
    Map<String, Object> properties = new HashMap<String, Object>();
    // testing the transformation with parameters
    properties.put("test-inline", this.getClass().getCanonicalName());
    contract.execute(null, properties);
  }
  public void testContractWithXMLParameter() throws DispatcherException, FileNotFoundException {
    Contract contract = new XSLContract(true, null, new StAX());
    InputStream xslStream = this.getClass().getResourceAsStream(CONTRACT_XML);
    contract.initializeFromStream(xslStream);
    Map<String, Object> properties = new HashMap<String, Object>();
    // testing the transformation with parameters
    String value = "<class>"+this.getClass().getCanonicalName()+"</class>";
    properties.put("test-inline-xml", value.getBytes());
    contract.execute(null, properties);
  }
  
}
