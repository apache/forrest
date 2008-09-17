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
package org.apache.forrest.dispatcher.api;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.forrest.dispatcher.exception.DispatcherException;

public interface Contract {

  /**
   * To maintain downwards compatibility with former version of the dispatcher,
   * you can allow xml in the properties. However be aware that setting this
   * properties to true will have negative influence on performance since this
   * feature needs to use DOM parsing!
   * 
   * We strongly recommend to rewrite your contracts and structurer to use
   * simple string for the properties.
   * 
   * @return whether we allow xml in the properties configuration of the
   *         contract.
   */
  boolean isAllowXmlProperties();

  /**
   * To maintain downwards compatibility with former version of the dispatcher,
   * you can allow xml in the properties. However be aware that setting this
   * properties to true will have negative influence on performance since this
   * feature needs to use DOM parsing!
   * 
   * We strongly recommend to rewrite your contracts and structurer to use
   * simple string for the properties and set this properties to false.
   * 
   * @param allowXmlProperties
   *          whether we allow xml in the properties configuration of the
   *          contract.
   */
  void setAllowXmlProperties(boolean allowXmlProperties);

  /**
   * Some contracts are based on text based files. Best known example are the
   * xsl contracts as the first implementation. This method sets the xslSource,
   * name, description and usage information of the contract.
   * 
   * @param stream
   *          that contains all information of the contract as description,
   *          name, usage, ...
   */
  void initializeFromStream(InputStream stream) throws DispatcherException;

  /**
   * Execute the contract with the given DataStream. The dataStream serves as
   * base to do a transformation (in xsl based contracts) or invocation of
   * business logic if used in java based contracts.
   * 
   * @param dataStream
   *          base to do the transformation or invoked business logic
   * @param properties
   *          the parameter that configure the contract (customizing it)
   * @return the resulting transformation of the dataStream with the given
   *         properties.
   * @throws DispatcherException
   */
  InputStream execute(InputStream dataStream, HashMap<String, ?> properties)
      throws DispatcherException;

  /**
   * @return the name of the contract
   */
  String getName();

  /**
   * @param contractName
   *          - the name of the contract
   */
  void setName(String contractName);

  /**
   * @return the description of the contract
   */
  String getDescription();

  /**
   * @param Description
   *          - the description of the contract
   */
  void setDescription(String contractDescription);

  /**
   * @return how to use the contract
   */
  String getUsage();

  /**
   * @param contractUsage
   *          - how to use the contract
   */
  void setUsage(String contractUsage);

}
