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
import org.apache.forrest.dispatcher.lenya.xml.NamespaceHelper;
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
     *                           &lt;forrest:contract name=&quot;nav-section&quot; dataURI=&quot;cocoon://index.navigation.xml&quot;/&gt;
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
     *                           &lt;forrest:contract name=&quot;nav-main-testing&quot; nugget=&quot;cocoon://index.navigation.xml&quot;&gt;
     *                            &lt;forrest:property name=&quot;nav-main-testing-test1&quot; &gt;Just a test&lt;/forrest:property&gt;
     *                           &lt;/forrest:contract&gt;
     * </pre>
     */
    public static final String PROPERTY_ELEMENT = "property";
    
    /**
     * Each property must have an unique identifier. The attribute
     * "PROPERTY_ID_ATTRIBUTE" stands for this id
     */
    static public final String PROPERTY_ID_ATTRIBUTE = "name";

    private Element[] propertyList;

    /**
     * The implementation of the contract
     */
    private Document contractImpl;

    /**
     * The description of the contract
     */
    private Element contractDescription;

    /**
     * The usage instruction of this contract for the structurer
     */
    private Element contractUsage;

    /**
     * The raw data that should be used in the transformation. Either coming
     * from the business service or from the calling class (as e.g. foo element
     * document)
     */
    private Document contractRawData;

    /**
     * The transformer that will transform the raw data with the contract
     * implementation
     */
    private Transformer contractTransformer;

    /**
     * The resulting data from the transformation
     */
    private DOMResult contractResultData;

    /**
     * Does this contract request a business service
     */
    private boolean isNugget = false;

    /**
     * Does the contract has properties
     */
    private boolean hasProperties = false;

    /**
     * The name of the contract
     */
    private String contractName;

    /**
     * The uri of the business service
     */
    private String nuggetUri;

    /*
     * Helper
     */
    protected NamespaceHelper namespaceHelper;

    protected DispatcherHelper dispatcherHelper;

    protected ServiceManager manager;

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
        dispatcherHelper = new DispatcherHelper(manager);
    }

    /**
     * Recycle the component
     */
    public void recycle() {
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
    }

    /**
     * initialize the contract (normally done after recycle) Do not use to
     * create a new instance!!!
     * 
     * @throws ParserConfigurationException
     */
    public void initialize() throws ParserConfigurationException {
        dispatcherHelper = new DispatcherHelper(this.manager);
    }

    /**
     * setContractImpl(String contractUri)
     * 
     * This method invokes the setting of the actual contract implementation via
     * an URI.
     * 
     * @param contractUri
     * @throws Exception
     */
    public void setContractImpl(String contractUri) throws Exception {
        Document _contractImpl = dispatcherHelper.getDocument(contractUri);
        this.contractImpl = _contractImpl;
        contractImplHelper(this.contractImpl);
    }

    /**
     * setContractImpl(Document _contractImpl)
     * 
     * This method invokes the setting of the actual contract implementation via
     * a document.
     * 
     * @param contractUri
     * @throws Exception
     */
    public void setContractImpl(Document _contractImpl) throws Exception {
        this.contractImpl = _contractImpl;
        contractImplHelper(this.contractImpl);
    }

    /**
     * contractImplHelper(Document _contractImpl)
     * 
     * This method set the actual contract implementation via a document.
     * 
     * Here we set the description and the usage instruction of the contract
     * implementation. As well we prepare the transformer. The simplest form is
     * a contract that does not need a pm. It can provide all data through the
     * transformation. A more dynamic contract would provide properties to the
     * transformation (contractImpl) to apply limited business logic.
     * 
     * @param _contractImpl
     * @throws Exception
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerConfigurationException
     * @throws IllegalArgumentException
     */
    private void contractImplHelper(Document _contractImpl) throws Exception,
            TransformerFactoryConfigurationError,
            TransformerConfigurationException, IllegalArgumentException {
        NodeList template = _contractImpl
                .getElementsByTagName(CONTRACT_IMPL_ROOT_ELEMENT);
        if (template.getLength() == 1) {
            Element templateElement = (Element) template.item(0);
            String format = templateElement
                    .getAttribute(CONTRACT_IMPL_INPUT_FORMAT_ATTRIBUTE);
            if ("".equals(format) | format == null) {
                throw new DispatcherException(DispatcherException.ERROR_500
                        + "\n" + "component: ContractBean" + "\n"
                        + "message: inputFormat cannot be null");
            } else if ("xsl".equals(format)) {
                NodeList list_transformer = _contractImpl
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
                throw new DispatcherException(DispatcherException.ERROR_404
                        + "\n" + "component: ContractBean" + "\n"
                        + "message: inputFormat=\"" + format
                        + "\" not implemented");
            }
        }
        NodeList description = _contractImpl.getElementsByTagName("description");
        if (description.getLength() == 1) {
            Element node = (Element) description.item(0);
            this.contractDescription = node;
        }
        NodeList usage = _contractImpl.getElementsByTagName("usage");
        if (usage.getLength() == 1) {
            Element node = (Element) usage.item(0);
            this.contractUsage = node;
        }
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
     *                          &lt;forrest:contract name=&quot;nav-section&quot; dataURI=&quot;cocoon://index.navigation.xml&quot;/&gt;
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

    /**
     * This method invokes the transformation of the this.contractRawData with
     * the this.contractTransformer (make sure you set them before). The result
     * is set to this.contractResultData.
     * 
     * @throws TransformerException
     */
    public void setContractResultData() throws DispatcherException {
        if (this.getContractRawData() == null
                || this.contractTransformer == null) {
            throw new DispatcherException(
                    DispatcherException.ERROR_500
                            + "\n"
                            + "component: ContractBean"
                            + "\n"
                            + "message: Could not transform the result data in contractBean."
                            + "\n"
                            + "You need to invoke first the transfomer and the rawData.");
        } else {
            DOMSource source = new DOMSource(contractRawData);
            DOMResult result = new DOMResult();
            try {
                this.contractTransformer.transform(source, result);
            } catch (TransformerException e) {
                throw new DispatcherException(
                        DispatcherException.ERROR_500
                                + "\n"
                                + "component: ContractBean"
                                + "\n"
                                + "message: Could not transform the result data in contractBean."
                                + "\n"
                                + "While trying to transform the raw data with the transformer, following error was thrown:\n"
                                + e);
            }
            this.setContractResultData(result);
        }
    }

    /*
     * Simple getter and setter methods
     */
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

    public Node getContractRawData() {
        return contractRawData;
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

}
