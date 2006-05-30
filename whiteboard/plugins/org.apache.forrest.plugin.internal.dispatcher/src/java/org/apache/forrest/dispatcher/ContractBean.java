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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface ContractBean {

    /**
     * The resolver prefix
     */
    public static final String CONTRACT_RESOLVE_PREFIX = "cocoon://resolve.contract";

    /**
     * <code>forrest:contract</code> element is used to include extra content
     * and/or functionality.
     * 
     * <p>
     * extra functionality requested from a uri via CONTRACT_NUGGET_ATTRIBUTE
     * attribute - example:
     * </p>
     * 
     * <pre>
     *  &lt;forrest:contract name=&quot;nav-section&quot; dataURI=&quot;cocoon://index.navigation.xml&quot;/&gt;
     * </pre>
     */
    public static final String CONTRACT_ELEMENT = "contract";

    /**
     * Each contract can contact external/internal business services to get the
     * data model. The attribute "CONTRACT_NUGGET_ATTRIBUTE" is the identifier
     * for the uri of the business data
     */
    public static final String CONTRACT_NUGGET_ATTRIBUTE = "dataURI";

    /**
     * Each contract must have an unique identifier. The attribute
     * "CONTRACT_ID_ATTRIBUTE" stands for this id
     */
    public static final String CONTRACT_ID_ATTRIBUTE = "name";

    /**
     * Each contract implementation needs to define the input format for the
     * transformation. ATM we only support xsl. The attribute
     * "CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE" defines the input format for
     * transformation
     */
    public static final String CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE = "inputFormat";

    /**
     * Each contract implementation needs to store the input format within a
     * root element
     */
    public static final String CONTRACT_IMPL_ROOT_ELEMENT = "forrest:template";

    /**
     * Each contract can have properties, which are definite e.g. in the
     * structurer index.fv and used in the contract.
     * 
     * <pre>
     *  &lt;forrest:contract name=&quot;nav-main-testing&quot; nugget=&quot;cocoon://index.navigation.xml&quot;&gt;
     *   &lt;forrest:property name=&quot;nav-main-testing-test1&quot; &gt;Just a test&lt;/forrest:property&gt;
     *  &lt;/forrest:contract&gt;
     * </pre>
     */
    public static final String PROPERTY_ELEMENT = "property";

    /**
     * Each property must have an unique identifier. The attribute
     * "PROPERTY_ID_ATTRIBUTE" stands for this id
     */
    static public final String PROPERTY_ID_ATTRIBUTE = "name";

    /**
     * Recycle the component
     */
    public abstract void recycle();

    /**
     * initialize the contract (normally done after recycle) Do not use to
     * create a new instance!!!
     * 
     * @throws ParserConfigurationException
     */
    public abstract void initialize() throws ParserConfigurationException;

    /**
     * setContractImpl(String contractUri)
     * 
     * This method invokes the setting of the actual contract implementation via
     * an URI.
     * 
     * @param contractUri
     * @throws Exception
     */
    public abstract void setContractImpl(String contractUri) throws Exception;

    /**
     * setContractImpl(Document _contractImpl,systemID)
     * 
     * This method invokes the setting of the actual contract implementation via
     * a document.
     * 
     * @param _contractImpl 
     * @param systemID
     * @throws Exception
     */
    public abstract void setContractImpl(Document _contractImpl, String systemID)
            throws Exception;

    /**
     * The presentation model (contractRawData) has to be requested by the
     * contract if it needs it. This is be done by setting the
     * CONTRACT_NUGGET_ATTRIBUTE attribute in the structurer which then use this
     * method to set the contractRawData.
     * <p>
     * Extra functionality requested from a uri via CONTRACT_NUGGET_ATTRIBUTE
     * attribute - example:
     * </p>
     * 
     * <pre>
     * &lt;forrest:contract name=&quot;nav-section&quot; dataURI=&quot;cocoon://index.navigation.xml&quot;/&gt;
     * </pre>
     * 
     * @param nuggetUri
     * @throws Exception
     */
    public abstract void setNuggetUri(String nuggetUri) throws Exception;

    /**
     * This method invokes the transformation of the this.contractRawData with
     * the this.contractTransformer (make sure you set them before). The result
     * is set to this.contractResultData.
     * 
     * @throws TransformerException
     */
    public abstract void setContractResultData() throws DispatcherException;

    /*
     * Getter and setter methods
     */
    public abstract String getContractName();

    public abstract void setContractName(String contractName);

    public abstract boolean isHasProperties();

    public abstract void setHasProperties(boolean hasProperties);

    public abstract boolean isNugget();

    public abstract void setNugget(boolean isNugget);

    public abstract String getNuggetUri();

    public abstract Node getContractRawData();

    public abstract void setContractRawData(Document contractRawData);

    public abstract Transformer getContractTransformer();

    public abstract void setContractTransformer(Transformer contractTransformer);

    public abstract Document getContractImpl();

    public abstract Element getContractDescription();

    public abstract void setContractDescription(Element contractDescription);

    public abstract Element getContractUsage();

    public abstract void setContractUsage(Element contractUsage);

    public abstract Element[] getPropertyList();

    public abstract void setPropertyList(Element[] propertyList);

    public abstract DOMResult getContractResultData();

    public abstract void setContractResultData(DOMResult contractResultData);

}