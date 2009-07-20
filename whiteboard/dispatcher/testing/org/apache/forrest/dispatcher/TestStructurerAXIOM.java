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

import java.io.InputStream;

import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.ClassPathResolver;
import org.apache.forrest.dispatcher.impl.XMLStructurerAxiom;

import junit.framework.TestCase;

public class TestStructurerAXIOM extends TestCase {
  private static final String STRUCTURER_XML = "master.advanced.structurer.xml";

  public void testStructurer() throws DispatcherException {
    String format = "html";
    Structurer structurer = prepareStructurer(false);
    structurer.execute(getStream(), format);
  }
  
  public void testStructurerWithXmlProperties() throws DispatcherException {
    String format = "html";
    Structurer structurer = prepareStructurer(true);
    structurer.execute(getStream(), format);
  }

  public void testStructurerFo() throws DispatcherException {
    String format = "fo";
    Structurer structurer = prepareStructurer(false);
    structurer.execute(getStream(), format);
  }
  
  public void testStructurerXmlFormat() throws DispatcherException {
    String format = "xml";
    Structurer structurer = prepareStructurer(false);
    structurer.execute(getStream(), format);
  }

  private Structurer prepareStructurer(boolean allowXml) {
    DispatcherBean config = new DispatcherBean();
    config.setAllowXmlProperties(allowXml);
    config.setResolver(new ClassPathResolver());
    config.setContractUriPrefix("/org/apache/forrest/dispatcher/");
    Structurer structurer = new XMLStructurerAxiom(config);
    return structurer;
  }

  private InputStream getStream() {
    InputStream dataStream = this.getClass()
        .getResourceAsStream(STRUCTURER_XML);
    return dataStream;
  }
}
