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

import org.apache.forrest.dispatcher.api.Contract;
import org.apache.forrest.dispatcher.impl.XSLContract;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class TestContract extends TestCase {

  public void testContractWithoutParameter() throws DispatcherException, FileNotFoundException {
    Contract contract = new XSLContract(false);
    String rawContract = "master.contract.xml";
    InputStream xslStream = this.getClass().getResourceAsStream(rawContract);
    contract.initializeFromStream(xslStream);
    // testing the transformation without parameters
    HashMap<String, String> properties = new HashMap<String, String>();
    contract.execute(null, properties);
  }
  public void testContractWithParameter() throws DispatcherException, FileNotFoundException {
    Contract contract = new XSLContract(false);
    String rawContract = "master.contract.xml";
    InputStream xslStream = this.getClass().getResourceAsStream(rawContract);
    contract.initializeFromStream(xslStream);
    HashMap<String, String> properties = new HashMap<String, String>();
    // testing the transformation with parameters
    properties.put("test-inline", this.getClass().getCanonicalName());
    contract.execute(null, properties);
  }
  public void testContractWithXMLParameter() throws DispatcherException, FileNotFoundException {
    Contract contract = new XSLContract(true);
    String rawContract = "master.contract.xml";
    InputStream xslStream = this.getClass().getResourceAsStream(rawContract);
    contract.initializeFromStream(xslStream);
    HashMap<String, InputSource> properties = new HashMap<String, InputSource>();
    // testing the transformation with parameters
    String valueString = "<class>"+this.getClass().getCanonicalName()+"</class>";
    InputSource value = new InputSource(new StringReader(valueString));
    properties.put("test-inline-xml", value);
    contract.execute(null, properties);
  }
  
}
