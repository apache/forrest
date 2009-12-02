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
import java.util.HashMap;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerFactory;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.WritableDispatcherBean;
import org.apache.forrest.dispatcher.impl.ClassPathResolver;
import org.apache.forrest.dispatcher.impl.helper.LoggingErrorListener;
import org.apache.forrest.dispatcher.impl.helper.StAX;

public abstract class AbstractStructurer extends TestCase {

  protected final Log log = LogFactory.getLog(this.getClass()
        .getCanonicalName());
  protected HashMap<String, Object> properties;
  protected WritableDispatcherBean config;

  public AbstractStructurer() {
    super();
  }

  public AbstractStructurer(String name) {
    super(name);
  }
  
  protected void prepareStructurer(boolean allowXml){
    TransformerFactory transFact = TransformerFactory.newInstance();
    ErrorListener listener = new LoggingErrorListener(log);
    transFact.setErrorListener(listener);
    config = new WritableDispatcherBean();
    config.setTransFact(transFact);
    config.setStaxHelper(new StAX());
    config.setAllowXmlProperties(allowXml);
    config.setResolver(new ClassPathResolver());
    config.setContractUriPrefix("/org/apache/forrest/dispatcher/");
    properties = new HashMap<String, Object>();
  }

  protected abstract Structurer getStructurer(boolean allowXml);

  protected InputStream getStream() {
    InputStream dataStream = this.getClass()
        .getResourceAsStream(getUrl());
    return dataStream;
  }

  public abstract String getUrl();
}