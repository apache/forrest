/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.apache.forrest.dispatcher;



import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.impl.XMLStructurer;


public class TestStructurer extends AbstractStructurer {

  public void testStructurer() throws DispatcherException {
    String format = "html";
    Structurer structurer = getStructurer(false);
    structurer.execute(getStream(), format);
  }
  
  public void testStructurerWithXmlProperties() throws DispatcherException {
    String format = "html";
    Structurer structurer = getStructurer(true);
    structurer.execute(getStream(), format);
  }

  public void testStructurerXmlFormat() throws DispatcherException {
    String format = "xml";
    Structurer structurer = getStructurer(false);
    structurer.execute(getStream(), format);
  }

  @Override
  public String getUrl() {
    return "master.structurer.xml";
  }

  @Override
  protected Structurer getStructurer(boolean allowXml) {
    super.prepareStructurer(allowXml);
    Structurer structurer = new XMLStructurer(config,properties);
    return structurer;
  }
}
