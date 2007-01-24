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
package org.apache.forrest.dispatcher.helper;

public class DispatcherPropertiesHelper {
    // home
    private String dispatcherHome;

    // reps
    private String contractRep;

    private String structurerRep ;

    private String contractSuffix = ".contract.xml";

    private String structurerSuffix = ".structurer.xml";

    // caching
    private String cacheHome;

    private String cacheContracts ;

    private String cacheStructure;

    public final String MASTER_CONTRACT = "master.contract.xml";

    public String getMasterContractUrl() {
        String url = "file://" + contractRep + MASTER_CONTRACT;
        return url;
    }

    public final String MASTER_STRUCTURER = "master.structurer.xml";

    public String getMasterStructurerUrl() {
        String url = "file://" + structurerRep + MASTER_STRUCTURER;
        return url;
    }

    public final String MASTER_FOO = "foo.xml";

    public String getMasterFooUrl() {
        String url = "file://" + cacheHome + MASTER_FOO;
        return url;
    }

    public DispatcherPropertiesHelper(String string) {
        dispatcherHome = string;
        contractRep = dispatcherHome + "/themes/contracts/";
        structurerRep = dispatcherHome + "/themes/structurer/url/";
        cacheHome = dispatcherHome + "/cache/";
        cacheContracts = cacheHome + "/themes/contracts/";
        cacheStructure = cacheHome + "/themes/structurer/url/";
    }

    public String getCacheContracts() {
        return cacheContracts;
    }

    public void setCacheContracts(String cacheContracts) {
        this.cacheContracts = cacheContracts;
    }

    public String getCacheHome() {
        return cacheHome;
    }

    public void setCacheHome(String cacheHome) {
        this.cacheHome = cacheHome;
    }

    public String getContractRep() {
        return contractRep;
    }

    public void setContractRep(String contractRep) {
        this.contractRep = contractRep;
    }

    public String getDispatcherHome() {
        return dispatcherHome;
    }

    public void setDispatcherHome(String dispatcherHome) {
        this.dispatcherHome = dispatcherHome;
    }

    public String getStructurerRep() {
        return structurerRep;
    }

    public void setStructurerRep(String structurerRep) {
        this.structurerRep = structurerRep;
    }

    public String getContractSuffix() {
        return contractSuffix;
    }

    public void setContractSuffix(String contractSuffix) {
        this.contractSuffix = contractSuffix;
    }

    public String getStructurerSuffix() {
        return structurerSuffix;
    }

    public void setStructurerSuffix(String structurerSuffix) {
        this.structurerSuffix = structurerSuffix;
    }

    public String getCacheStructure() {
        return cacheStructure;
    }

    public void setCacheStructure(String cacheStructure) {
        this.cacheStructure = cacheStructure;
    }

}
