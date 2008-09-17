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
package org.apache.forrest.dispatcher.config;

import org.apache.forrest.dispatcher.api.Resolver;

/**
 * The dispatcherBean is holding all configuration information 
 * that are needed for the different dispatcher components. 
 * 
 * @version 1.0
 *
 */
public class DispatcherBean {
  
  private boolean allowXmlProperties = false;
  private Resolver resolver = null;

  private String contractUriPrefix = "";
  private String contractUriSufix = ".contract.xml";
  
  public boolean isAllowXmlProperties() {
    return allowXmlProperties;
  }

  public void setAllowXmlProperties(boolean allowXmlProperties) {
    this.allowXmlProperties = allowXmlProperties;
  }

  public Resolver getResolver() {
    return resolver;
  }

  public void setResolver(Resolver resolver) {
    this.resolver = resolver;
  }

  public String getContractUriPrefix() {
    return contractUriPrefix;
  }

  public void setContractUriPrefix(String contractUriPrefix) {
    this.contractUriPrefix = contractUriPrefix;
  }

  public String getContractUriSufix() {
    return contractUriSufix;
  }

  public void setContractUriSufix(String contractUriSufix) {
    this.contractUriSufix = contractUriSufix;
  }

}
