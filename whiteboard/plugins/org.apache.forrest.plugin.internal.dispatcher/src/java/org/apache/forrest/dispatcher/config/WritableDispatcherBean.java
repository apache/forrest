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

import javax.xml.transform.TransformerFactory;

import org.apache.forrest.dispatcher.api.Resolver;
import org.apache.forrest.dispatcher.impl.helper.StAX;
import org.xml.sax.EntityResolver;

public class WritableDispatcherBean extends DispatcherBean {

  /**
   * If you use properties like
   * <code>&lt;forrest:property name="theme" value="pelt"/></code>
   * <p>
   * There is no real need to pass the element as whole it is sufficient to pass
   * a simple string (@value).
   * <p>
   * If you set it to true we will pass a simple string to the transformation in
   * the form of key=@name, value=@value.
   * <p>
   * If set to false then we pass the whole element.
   * 
   * @param shrink
   *          whether we pass empty properties element as simple string (true)
   *          or as w3c element (false)
   */
  public void setShrink(boolean shrink) {
    this.shrink = shrink;
  }

  /**
   * If you want to use inline xml for contract properties be aware that setting
   * this properties to true will have negative influence on performance since
   * this feature needs to use DOM parsing!
   * <p>
   * 
   * We strongly recommend to rewrite your contracts and structurer to use
   * simple string for the properties.
   * <p>
   * Whether we allow that
   * <code>&lt;forrest:property> elements can have children or not.
   * 
   * @param Whether
   *          we allow that <code>&lt;forrest:property> elements can have
   *          children or not.
   */
  public void setAllowXmlProperties(boolean allowXmlProperties) {
    this.allowXmlProperties = allowXmlProperties;
  }

  /**
   * Wrapper interface to enable multible resolver implementation this makes it
   * possible to use the dispatcher outside of cocoon as standalone application
   * without any mayor rewrites.
   * 
   * @param the
   *          currently configured resolver
   */
  public void setResolver(Resolver resolver) {
    this.resolver = resolver;
  }

  /**
   * If you want to change the uri prefix of the contracts. This may be
   * interesting if you work with a contract repository rather then with the
   * ones from the themer plugin.
   * 
   * @param prefix
   *          of the contracts
   */
  public void setContractUriPrefix(String contractUriPrefix) {
    this.contractUriPrefix = contractUriPrefix;
  }
  /**
   * If you want to change the uri suffix of the contracts. This may be
   * interesting if you work with a contract repository rather then with the
   * ones from the themer plugin.
   * 
   * @param suffix
   *          of the contracts
   */
  public void setContractUriSufix(String contractUriSufix) {
    this.contractUriSufix = contractUriSufix;
  }

  public void setStaxHelper(StAX staxHelper) {
    this.staxHelper = staxHelper;
  }
  
  /**
   *  <p>A TransformerFactory instance can be used to create
 * {@link javax.xml.transform.Transformer} and
 * {@link javax.xml.transform.Templates} objects.</p>
   * 
   * @param transFact
   *          the TransformerFactory currently configured
   */  
  public void setTransFact(TransformerFactory transFact) {
    this.transFact = transFact;
  }

  public void setEntityResolver(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

}
