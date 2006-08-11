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
package org.apache.forrest.dispatcher;

import java.beans.Beans;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.avalon.framework.service.ServiceManager;
import org.apache.forrest.dispatcher.lenya.xml.NamespaceHelper;
import org.apache.forrest.dispatcher.transformation.DispatcherTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author thorsten
 * 
 */
public class ContractBeanDOMImpl extends Beans implements ContractBean {

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

    private HashMap parameterHelper;

    private String m_systemID;

    private URIResolver m_uriResolver;

    private Object defaultVariables;

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
     * @param parameterHelper
     * @param defaultProperties 
     * @throws ParserConfigurationException
     */
    public ContractBeanDOMImpl(ServiceManager manager, HashMap parameterHelper,Document defaultProperties, URIResolver uriResolver)
            throws ParserConfigurationException {
        m_uriResolver=uriResolver;
        this.manager = manager;
        dispatcherHelper = new DispatcherHelper(manager);
        this.parameterHelper = parameterHelper;
        this.defaultVariables=defaultProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#recycle()
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#initialize()
     */
    public void initialize() throws ParserConfigurationException {
        dispatcherHelper = new DispatcherHelper(this.manager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractImpl(java.lang.String)
     */
    public void setContractImpl(String contractUri) throws Exception {
        m_systemID = contractUri;
        Document _contractImpl = dispatcherHelper.getDocument(contractUri);
        this.contractImpl = _contractImpl;
        contractImplHelper(this.contractImpl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractImpl(org.w3c.dom.Document)
     */
    public void setContractImpl(Document _contractImpl, String systemID)
            throws Exception {
        m_systemID = systemID;
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
                        + "\n" + "component: ContractBean" + "\n" + "message:"
                        + "\n" + "inputFormat cannot be null");
            } else if ("xsl".equals(format)) {
                NodeList list_transformer = _contractImpl
                        .getElementsByTagName("xsl:stylesheet");
                if (list_transformer.getLength() == 1) {
                    Node node = list_transformer.item(0);
                    TransformerFactory transFact = TransformerFactory
                            .newInstance();
                    DOMSource stylesource = new DOMSource(node);
                    stylesource.setSystemId(m_systemID);
                    transFact.setURIResolver(m_uriResolver);
                    Templates cachedXSLT = transFact.newTemplates(stylesource);
                    if (cachedXSLT == null)
                        throw new DispatcherException(
                                DispatcherException.ERROR_500
                                        + "\n"
                                        + "component: ContractBean"
                                        + "\n"
                                        + "message:"
                                        + "\n"
                                        + "Could not setup transformer in the contractBean."
                                        + "\n"
                                        + "Please check that the contract implementation is wellformed and valid!"
                                        + "\n"
                                        + "\n"
                                        + "One reason that an implementation may not be valid is that you are using variables that cannot be resolved."
                                        + "\n"
                                        + "Please see the logs and the sysout for more information, you may are see right away the error.");
                    /**
                     * Set default properties
                     */
                    // default forrest properties
                    Transformer transformer = cachedXSLT.newTransformer();
                    transformer.setParameter("defaultVariables",
                            this.defaultVariables);
                    transformer.setOutputProperty(
                            OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    this.contractTransformer = transformer;
                }
            } else {
                throw new DispatcherException(DispatcherException.ERROR_404
                        + "\n" + "component: ContractBean" + "\n" + "message:"
                        + "\n" + "inputFormat=\"" + format
                        + "\" not implemented");
            }
        }
        NodeList description = _contractImpl
                .getElementsByTagName("description");
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

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setNuggetUri(java.lang.String)
     */
    public void setNuggetUri(String nuggetUri) throws Exception {
        this.nuggetUri = nuggetUri;
        Document rawData = dispatcherHelper.getDocument(nuggetUri);
        this.contractRawData = rawData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractResultData()
     */
    public void setContractResultData() throws DispatcherException {
        if (this.getContractRawData() == null
                || this.contractTransformer == null) {
            throw new DispatcherException(
                    DispatcherException.ERROR_500
                            + "\n"
                            + "component: ContractBean"
                            + "\n"
                            + "message:"
                            + "\n"
                            + "Could not transform the result data in contractBean."
                            + "\n"
                            + "You need to invoke first the transfomer and the rawData."
                            + "\n"
                            + "If you see \"The contract \"null\" has thrown thrown an exception...\" "
                            + "that can mean you nested contracts, which is forbidden!"

            );
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
                                + "message:"
                                + "\n"
                                + "Could not transform the result data in contractBean."
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
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractName()
     */
    public String getContractName() {
        return contractName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractName(java.lang.String)
     */
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#isHasProperties()
     */
    public boolean isHasProperties() {
        return hasProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setHasProperties(boolean)
     */
    public void setHasProperties(boolean hasProperties) {
        this.hasProperties = hasProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#isNugget()
     */
    public boolean isNugget() {
        return isNugget;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setNugget(boolean)
     */
    public void setNugget(boolean isNugget) {
        this.isNugget = isNugget;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getNuggetUri()
     */
    public String getNuggetUri() {
        return nuggetUri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractRawData()
     */
    public Node getContractRawData() {
        return contractRawData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractRawData(org.w3c.dom.Document)
     */
    public void setContractRawData(Document contractRawData) {
        this.contractRawData = contractRawData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractTransformer()
     */
    public Transformer getContractTransformer() {
        return contractTransformer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractTransformer(javax.xml.transform.Transformer)
     */
    public void setContractTransformer(Transformer contractTransformer) {
        this.contractTransformer = contractTransformer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractImpl()
     */
    public Document getContractImpl() {
        return contractImpl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractDescription()
     */
    public Element getContractDescription() {
        return contractDescription;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractDescription(org.w3c.dom.Element)
     */
    public void setContractDescription(Element contractDescription) {
        this.contractDescription = contractDescription;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractUsage()
     */
    public Element getContractUsage() {
        return contractUsage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractUsage(org.w3c.dom.Element)
     */
    public void setContractUsage(Element contractUsage) {
        this.contractUsage = contractUsage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getPropertyList()
     */
    public Element[] getPropertyList() {
        return propertyList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setPropertyList(org.w3c.dom.Element[])
     */
    public void setPropertyList(Element[] propertyList) {
        this.propertyList = propertyList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#getContractResultData()
     */
    public DOMResult getContractResultData() {
        return contractResultData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.forrest.dispatcher.ContractBeanInterface#setContractResultData(javax.xml.transform.dom.DOMResult)
     */
    public void setContractResultData(DOMResult contractResultData) {
        this.contractResultData = contractResultData;
    }

}
