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
import java.util.HashMap;
import java.util.Map;

import org.apache.forrest.dispatcher.api.Resolver;
import org.apache.forrest.dispatcher.api.Structurer;
import org.apache.forrest.dispatcher.config.DispatcherBean;
import org.apache.forrest.dispatcher.exception.DispatcherException;
import org.apache.forrest.dispatcher.factories.ContractFactory;
import org.apache.forrest.dispatcher.impl.helper.Loggable;
import org.apache.forrest.dispatcher.impl.helper.StAX;

public abstract class AbstractXmlStructurer extends Loggable  implements Structurer {

  protected final Resolver resolver;
  protected final StAX stax;
  protected final boolean allowXmlProperties;
  protected final ContractFactory contractRep;
  protected final boolean shrink;
  protected final Map<String, Object> param;

  protected AbstractXmlStructurer(final DispatcherBean config, final Map<String, Object> defaultProperties) {
    this.contractRep = new ContractFactory(config);
    this.stax = config.getStaxHelper();
    this.resolver = config.getResolver();
    this.allowXmlProperties = config.isAllowXmlProperties();
    this.shrink = config.isShrink();
    if (defaultProperties == null){
      param = new HashMap<String, Object>();
    }else{
      param = defaultProperties;
    }
  }

public abstract InputStream execute(InputStream structurerStream, String format)
        throws DispatcherException;

}