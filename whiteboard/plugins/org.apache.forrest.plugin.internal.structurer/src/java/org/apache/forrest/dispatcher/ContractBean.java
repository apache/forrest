/*
 * Copyright 1999-2005 The Apache Software Foundation or its licensors,
 * as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher;

import java.beans.Beans;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.lenya.xml.NamespaceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author thorsten
 * 
 */
public class ContractBean extends Beans {

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
     *                       &lt;forrest:contract name=&quot;nav-section&quot; dataURI=&quot;cocoon://index.navigation.xml&quot;/&gt;
     * </pre>
     */
    public static final String CONTRACT_ELEMENT = "contract";

    /**
     * Each contract can contact business services to get the data model. The
     * attribute "CONTRACT_NUGGET_ATTRIBUTE" contains the uri to contact for
     * business data
     */
    public static final String CONTRACT_NUGGET_ATTRIBUTE = "dataURI";

    /**
     * Each contract must have an unique identifier. The attribute "CONTRACT_ID_ATTRIBUTE"
     * contains this id
     */
    public static final String CONTRACT_ID_ATTRIBUTE = "name";
    
    /**
     * Each contract implementation needs to define the input format for the transformation. 
     * ATM we only support xsl. The attribute "CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE"
     * defines the input format for transformation
     */
    public static final String CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE = "inputFormat";

    /**
     * Each contract implementation needs to define the input format for the transformation. 
     * ATM we only support xsl. The attribute "CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE"
     * defines the input format for transformation
     */
    public static final String CONTRACT_IMPL_ROOT_ELEMENT = "forrest:template";
    
    private static final String PROPERTY_ID_ATTRIBUTE = "name";

    private HashMap contract;

    private Element structurerContract;

    private Element[] propertyList;

    private Document contractImpl;

    private Element contractDescription;

    private Element contractUsage;

    private Document contractRawData;

    private Transformer contractTransformer;

    private DOMResult contractResultData;

    private boolean isNugget = false;

    private boolean hasProperties = false;

    private String contractName;

    private String nuggetUri;

    protected NamespaceHelper namespaceHelper;

    protected DispatcherHelper dispatcherHelper;

    protected ServiceManager manager;

    /**
     * @param element
     * @param namespaceHelper
     * @deprecated
     */
    public ContractBean(Element element, NamespaceHelper namespaceHelper) {
        this.contract = new HashMap();
        this.namespaceHelper = namespaceHelper;
        this.loadStructurer(element);
    }

    /**
     * The ContractBean contains all fields to work with contracts. It is a
     * helper bean.
     * 
     * Helpers (forrest:contracts) mainly adapt and transform the presentation
     * model (pm), but also help with any limited business processing that is
     * initiated from the structurer by forrest:properties by passing this
     * information to the transfomer.
     * 
     * @param manager
     * @throws ParserConfigurationException
     */
    public ContractBean(ServiceManager manager)
            throws ParserConfigurationException {
        this.manager = manager;
        this.contract = new HashMap();
        dispatcherHelper = new DispatcherHelper(manager);
    }

    /**
     * The ContractBean contains all fields to work with contracts. It is a
     * helper bean.
     * 
     * Helpers (forrest:contracts) mainly adapt and transform the presentation
     * model (pm), but also help with any limited business processing that is
     * initiated from the structurer by forrest:properties by passing this
     * information to the transfomer.
     * 
     * @throws ParserConfigurationException
     */
    public ContractBean() {}

    /**
     * @param structurer
     * @deprecated
     */
    private void loadStructurer(Element structurer) {
        this.setStructurerContract(structurer);
        this.setNugget(structurer.hasAttribute(CONTRACT_NUGGET_ATTRIBUTE));
        this.setHasProperties(structurer.hasChildNodes());
        if (hasProperties) {
            this.propertyList = namespaceHelper.getChildren(structurer);
        }
        this.setContractName(structurer.getAttribute(CONTRACT_ID_ATTRIBUTE));
        /*
         * if (this.isNugget) { this.setNuggetUri(structurer
         * .getAttribute(CONTRACT_NUGGET_ATTRIBUTE)); }
         */
    }

    /**
     * setContractImpl(String contractUri)
     * 
     * This method set the actual contract implementation via a URI.
     * 
     * Here we set the description and the usage instruction of the contract
     * implementation. As well we prepare the transformer. The simplest form is
     * a contract that does not need a pm. It can provide all data through the
     * transformation. A more dynamic contract would provide properties to the
     * transformation (contractImpl) to apply limited business logic.
     * 
     * @param contractUri
     * @throws Exception
     */
    public void setContractImpl(String contractUri) throws Exception {
        Document contractImpl = dispatcherHelper.getDocument(contractUri);
        this.contractImpl = contractImpl;
        contractImplHelper(contractImpl);
    }

    /**
     * setContractImpl(String contractUri)
     * 
     * This method set the actual contract implementation via a URI.
     * 
     * Here we set the description and the usage instruction of the contract
     * implementation. As well we prepare the transformer. The simplest form is
     * a contract that does not need a pm. It can provide all data through the
     * transformation. A more dynamic contract would provide properties to the
     * transformation (contractImpl) to apply limited business logic.
     * 
     * @param contractUri
     * @throws Exception
     */
    public void setContractImpl(Document contractImpl) throws Exception {
        this.contractImpl = contractImpl;
        contractImplHelper(contractImpl);
    }

    /**
     * @param contractImpl
     * @throws Exception
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerConfigurationException
     * @throws IllegalArgumentException
     */
    private void contractImplHelper(Document contractImpl) throws Exception,
            TransformerFactoryConfigurationError,
            TransformerConfigurationException, IllegalArgumentException {
        NodeList template = contractImpl
                .getElementsByTagName(CONTRACT_IMPL_ROOT_ELEMENT);
        if (template.getLength() == 1) {
            Element templateElement = (Element) template.item(0);
            String format = templateElement.getAttribute(CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE);
            if ("".equals(format) | format == null) {
                throw new DispatcherException(DispatcherException.ERROR_500 + "\n"
                        + "component: ContractBean" + "\n"
                        + "message: inputFormat cannot be null");
            } else if ("xsl".equals(format)) {
                NodeList list_transformer = contractImpl
                        .getElementsByTagName("xsl:stylesheet");
                if (list_transformer.getLength() == 1) {
                    Element node = (Element) list_transformer.item(0);
                    TransformerFactory tFactory = TransformerFactory
                            .newInstance();
                    DOMSource stylesource = new DOMSource(node);
                    Transformer transformer = tFactory
                            .newTransformer(stylesource);
                    if (transformer == null)
                        throw new DispatcherException(
                                DispatcherException.ERROR_500
                                        + "\n"
                                        + "component: ContractBean"
                                        + "\n"
                                        + "message: Could not setup transformer in the contractBean."
                                        + "\n"
                                        + "Please check that the contract implementation is wellformed and valid");
                    /*
                     * FIXME: Set default properties
                     */
                    // transformer.setParameter()
                    transformer.setOutputProperty(
                            OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    this.contractTransformer = transformer;
                }
            } else {
                throw new DispatcherException( DispatcherException.ERROR_404+ "\n"
                        + "component: ContractBean" + "\n"
                        + "message: inputFormat=\""+format+"\" not implemented");
            }
        }
        NodeList description = contractImpl.getElementsByTagName("description");
        if (description.getLength() == 1) {
            Element node = (Element) description.item(0);
            this.contractDescription = node;
        }
        NodeList usage = contractImpl.getElementsByTagName("usage");
        if (usage.getLength() == 1) {
            Element node = (Element) usage.item(0);
            this.contractUsage = node;
        }
    }

    public HashMap getContract() {
        return contract;
    }

    public void put(Object key, Object element) {
        this.contract.put(key, element);
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public boolean isHasProperties() {
        return hasProperties;
    }

    public void setHasProperties(boolean hasProperties) {
        this.hasProperties = hasProperties;
    }

    public boolean isNugget() {
        return isNugget;
    }

    public void setNugget(boolean isNugget) {
        this.isNugget = isNugget;
    }

    public String getNuggetUri() {
        return nuggetUri;
    }

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
     *                      &lt;forrest:contract name=&quot;nav-section&quot; dataURI=&quot;cocoon://index.navigation.xml&quot;/&gt;
     * </pre>
     * 
     * @param nuggetUri
     * @throws Exception
     */
    public void setNuggetUri(String nuggetUri) throws Exception {
        this.nuggetUri = nuggetUri;
        Document rawData = dispatcherHelper.getDocument(nuggetUri);
        this.contractRawData = rawData;
    }

    public Node getContractRawData() {
        return contractRawData;
    }

    /**
     * This method invokes the transformation of the this.contractRawData with
     * the this.contractTransformer (make sure you set them before). The result
     * is set to this.contractResultData.
     * 
     * @throws TransformerException
     */
    public void setContractResultData() throws TransformerException {
        if (this.getContractRawData() == null
                || this.contractTransformer == null) {
            throw new TransformerException(
                    "You need to invoke first the transfomer and the rawData.");
        } else {
            DOMSource source = new DOMSource(contractRawData);
            DOMResult result = new DOMResult();
            this.contractTransformer.transform(source, result);
            this.setContractResultData(result);
        }
    }

    public void setContractRawData(Document contractRawData) {
        this.contractRawData = contractRawData;
    }

    public Transformer getContractTransformer() {
        return contractTransformer;
    }

    public void setContractTransformer(Transformer contractTransformer) {
        this.contractTransformer = contractTransformer;
    }

    public Element getStructurerContract() {
        return structurerContract;
    }

    public void setStructurerContract(Element structurerContract) {
        this.structurerContract = structurerContract;
    }

    public Document getContractImpl() {
        return contractImpl;
    }

    public Element getContractDescription() {
        return contractDescription;
    }

    public void setContractDescription(Element contractDescription) {
        this.contractDescription = contractDescription;
    }

    public Element getContractUsage() {
        return contractUsage;
    }

    public void setContractUsage(Element contractUsage) {
        this.contractUsage = contractUsage;
    }

    public Element[] getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(Element[] propertyList) {
        this.propertyList = propertyList;
    }

    public DOMResult getContractResultData() {
        return contractResultData;
    }

    public void setContractResultData(DOMResult contractResultData) {
        this.contractResultData = contractResultData;
    }

    public void recycle() {
        this.contract = null;
        this.contractDescription = null;
        this.contractImpl = null;
        this.contractName = null;
        this.contractRawData = null;
        this.contractResultData = null;
        this.contractTransformer = null;
        this.contractUsage = null;
        this.dispatcherHelper = null;
        this.hasProperties = false;
        this.isNugget = false;
        this.namespaceHelper = null;
        this.nuggetUri = null;
        this.propertyList = null;
        this.structurerContract = null;
    }

    public void initialize() throws ParserConfigurationException {
        this.contract = new HashMap();
        dispatcherHelper = new DispatcherHelper(this.manager);
    }
}
