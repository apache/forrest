/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher.api;

import java.io.File;
import java.util.HashMap;

/**
 *
 */
public interface ContractOld {
    
    /**
     * @return the name of the contract
     */
    public abstract String getName();
    
    /**
     * @param contractName - the name of the contract
     */
    public abstract void setName(String contractName);
    
    /**
     * @return the description of the contract
     */
    public abstract String getDescription();
    
    /**
     * @param Description - the description of the contract
     */
    public abstract void setDescription(String contractDescription);
    
    /**
     * @return how to use the contract
     */
    public abstract String getUsage();

    /**
     * @param contractUsage - how to use the contract
     */
    public abstract void setUsage(String contractUsage);
    
    public abstract void initialize(String sourceUrl,String destination);
    
    public abstract File getTemplate();
    
    public abstract File execute(String dataUrl, HashMap param,String destination);
    
}
