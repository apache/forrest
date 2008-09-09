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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javanet.staxutils.BaseXMLInputFactory;
import javanet.staxutils.BaseXMLOutputFactory;
import javanet.staxutils.events.EventFactory;
import javanet.staxutils.events.ProcessingInstructionEvent;
import javanet.staxutils.io.XMLWriterUtils;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.forrest.dispatcher.api.ContractOld;
import org.apache.forrest.dispatcher.impl.DefaultContract;
import org.apache.forrest.dispatcher.utils.CommonString;

import com.bea.xml.stream.XMLEventAllocatorBase;

public class StructurerHelperStAX {

    public static final String NS = "http://apache.org/forrest/templates/1.0";

    public static final String STRUCTUR_ELEMENT = "structur";

    public static final String STRUCTURER_ELEMENT = "structurer";

    public static final String STRUCTURER_TYPE_ATT = "type";

    public static final String STRUCTURER_HOOKS_ATT = "hooksXpath";

    public static final String CONTRACT_ELEMENT = "contract";

    public static final String CONTRACT_NAME_ATT = "name";

    public static final String CONTRACT_DATA_ATT = "dataURI";

    public static final String PROPERTY_ELEMENT = "property";

    public static final String HOOK_ELEMENT = "template";

    public static final String PROPERTY_NAME_ATT = CONTRACT_NAME_ATT;

    private String structurerResult;

    private String structurerId;

    private XMLInputFactory inputFactory = BaseXMLInputFactory.newInstance();

    private XMLOutputFactory outputFactory = BaseXMLOutputFactory.newInstance();

    private static DispatcherPropertiesHelper propertiesHelper;

    private String currentPath = "";

    private LinkedHashMap<String, LinkedHashSet> resultTree = new LinkedHashMap<String, LinkedHashSet>();

    private XMLEventFactory eventFactory = EventFactory.newInstance();

    private XMLEventAllocator allocator;

    private String home;

    public StructurerHelperStAX(String home) {
        propertiesHelper = new DispatcherPropertiesHelper(home);
        this.home=home;
    }

    public File execute(String sourceUrl, String type) throws IOException,
            FactoryConfigurationError, XMLStreamException, InstantiationException,
            IllegalAccessException, TransformerConfigurationException {
        // setting up the files
        structurerId = sourceUrl.replace(propertiesHelper.getStructurerRep(), "");
        structurerId = structurerId.replace(propertiesHelper.getStructurerSuffix(),
                "");
        structurerResult = propertiesHelper.getCacheStructure() + structurerId
                + "." + type;
        File cache = new File(structurerResult), structurerFile = new File(
                sourceUrl);
        // caching is ATM implemetent by writing the template to disk
        boolean useCache = false;
        // if no file exist, create it
        if (!cache.isFile()) {
            try {
                cache.createNewFile();
            } catch (Exception e) {
                // if we cannot create a file that means that the parent path
                // does not exists
                File path = new File(cache.getParent());
                path.mkdirs();
                cache.createNewFile();
            }
        } else {
            // compare the url and the file
            long modifiedCache = cache.lastModified();
            long modifiedContract = structurerFile.lastModified();
            if (modifiedCache > modifiedContract)
                useCache = true;
        }
        // FIXME activate if stable
        // if (!useCache) {
        URL structurerUrl;
        if (sourceUrl.indexOf(":/") > -1)
            structurerUrl = new URL(sourceUrl);
        else
            structurerUrl = new URL("file:///" + sourceUrl);
        InputStream in = structurerUrl.openStream();
        inputFactory.setEventAllocator(new XMLEventAllocatorBase());
        allocator = inputFactory.getEventAllocator();
        XMLStreamReader parser = inputFactory.createXMLStreamReader(in);
        // Create the output factory

        // Create XML event writer
        // Write the result to disk
        Writer result = new FileWriter(structurerResult);
        XMLEventWriter writer = outputFactory.createXMLEventWriter(result);
        processStructur(type, parser, writer);
        // }
        return cache;
    }

    private void processStructur(String type, XMLStreamReader parser,
            XMLEventWriter writer) throws XMLStreamException, IOException,
            FactoryConfigurationError, InstantiationException,
            IllegalAccessException, TransformerConfigurationException {
        while (true) {
            int event = parser.next();
            switch (event) {
            case XMLStreamConstants.END_DOCUMENT:
                writer.flush();
                parser.close();
                return;

            case XMLStreamConstants.START_ELEMENT:
                if (parser.getLocalName().equals(STRUCTURER_ELEMENT)) {
                    System.out.println(STRUCTURER_ELEMENT + " " + structurerId);
                    System.out.println("*********** START ****************");
                    String m_type = "", path = "";
                    // Get attribute names
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        String localName = parser.getAttributeLocalName(i);
                        if (localName.equals(STRUCTURER_TYPE_ATT)) {
                            // Return value
                            m_type = parser.getAttributeValue(i);
                        } else if (localName.equals(STRUCTURER_HOOKS_ATT)) {
                            path = parser.getAttributeValue(i);
                        }
                    }
                    if (m_type.equals(type)) {
                        System.out.println("matched - need to process");
                        // adding the default path
                        if (!"/".equals(String.valueOf(path.charAt(0)))) {
                            path = "/" + path;
                        }
                        currentPath = path;
                        processStructurer(parser, writer, type);
                    } else {
                        System.out.println("no-matched");
                    }
                }
                break;

            default:
                break;
            }

        }

    }

    private void processStructurer(XMLStreamReader parser, XMLEventWriter writer,
            String type) throws XMLStreamException, IOException,
            FactoryConfigurationError, InstantiationException,
            IllegalAccessException, TransformerConfigurationException {

        // start processing
        while (true) {
            int event = parser.next();
            switch (event) {

            case XMLStreamConstants.END_ELEMENT:
                if (parser.getLocalName().equals(STRUCTURER_ELEMENT)) {
                    System.out
                            .println("************ STRUCTURER_ELEMENT END ***************");
                    // setting up the document
                    System.out.println("Preparing the result object.");
                    writer.add(eventFactory.createStartDocument("UTF-8", "1.0"));
                    // we have now a resultTree setup
                    System.out.println("we have now a resultTree setup: "
                            + resultTree.size());
                    Iterator<String> iterator = resultTree.keySet().iterator();
                    String[] paths = resultTree.keySet().toArray(new String[1]);
                    String rootPath = CommonString.common(paths);
                    System.out.println("setting up root path");
                    String[] tokenizer = rootPath.split("/");
                    openPaths(writer, tokenizer);
                    // here goes the contract/hook stuff
                    // the idea is to decode the contracts and hooks from the hashmap
                    // and process it here.
                    System.out
                            .println(" here goes the contract/hook stuff the idea is to store the contracts and hooks in a hashmap and process it here.");
                    while (iterator.hasNext()) {
                        String element = iterator.next();
                        final String replaceFirst = element.replaceFirst(rootPath,
                                "");
                        System.out.println(replaceFirst);
                        System.out
                        .println("Now we need to look up the object and process it.");
                        final String[] split = replaceFirst.split("/");
                        if (split.length > 1) {
                            System.out.println("replaceFirst.split(\"/\").length:  "+split.length);
                            openPaths(writer, split);
                            injectResult(writer, element);
                            closingPaths(writer, split);
                        } else {
                            StartElement start = eventFactory.createStartElement(
                                    "", "", replaceFirst);
                            writer.add((XMLEvent) start);
                            
                            injectResult(writer, element);
                            EndElement end = eventFactory.createEndElement("", "",
                                    replaceFirst);
                            writer.add((XMLEvent) end);
                        }

                    }
                    closingPaths(writer, tokenizer);
                    writer.add(eventFactory.createEndDocument());
                    resultTree.clear();
                    return;
                } else
                    System.out.println("end-name " + parser.getLocalName());
                break;

            case XMLStreamConstants.START_ELEMENT:
                String localName=parser.getLocalName();
                System.out
                        .println("Here we need to check for hooks or contract and delegate the processing");
                if (localName.equals(CONTRACT_ELEMENT)) {
                   // currentPath += "/" + localName;
                    System.out.println("CONTRACT " + localName);
                    System.out.println("Path " + currentPath);
                    processContract(parser, writer, allocator);
                 //   currentPath = currentPath.substring(0, currentPath.lastIndexOf("/" + localName));
                } else if (localName.equals(HOOK_ELEMENT)) {
                    System.out.println("HOOKS " + localName);
                    System.out.println("NOT IMPLEMENTED");
                }
                break;

            default:
                // writer.add(allocator.allocate(parser));
                break;
            }

        }
    }

    private void openPaths(XMLEventWriter writer, String[] tokenizer) throws XMLStreamException {
        for (int i = 0; i < tokenizer.length; i++) {
            if (!tokenizer[i].equals("")) {
                StartElement value = eventFactory.createStartElement(
                        "", "", tokenizer[i]);
                writer.add((XMLEvent) value);
            }
        }
    }

    private void closingPaths(XMLEventWriter writer, String[] tokenizer) throws XMLStreamException {
        // closing the initial paths again
        for (int j = tokenizer.length - 1; j >= 0; j--) {
            if (!tokenizer[j].equals("")) {
                EndElement value = eventFactory.createEndElement("",
                        "", tokenizer[j]);
                writer.add((XMLEvent) value);
            }
        }
    }

    private void injectResult(XMLEventWriter writer, String element) throws XMLStreamException {
        LinkedHashSet<XMLEvent> part=resultTree.get(element);
        Object[] partResult = part.toArray();
        for (int i = 0; i < partResult.length; i++) {
            writer.add((XMLEvent) partResult[i]);
        }
    }

    private void processContract(XMLStreamReader parser, XMLEventWriter writer,
            XMLEventAllocator allocator) throws XMLStreamException, IOException,
            FactoryConfigurationError, InstantiationException,
            IllegalAccessException, TransformerConfigurationException {
        String name = "", data = "";
        // Get attribute names
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String localName = parser.getAttributeLocalName(i);
            if (localName.equals(CONTRACT_NAME_ATT)) {
                // Return value
                name = parser.getAttributeValue(i);
            } else if (localName.equals(CONTRACT_DATA_ATT)) {
                data = parser.getAttributeValue(i);
            }
        }

        // Where we want to save the resulting contract after transformation
        String destination = propertiesHelper.getCacheContracts() + name + "/"
                + structurerId + ".xpath.xml";
        String source = propertiesHelper.getContractRep() + name
                + propertiesHelper.getContractSuffix();
        String cachedTemplate = propertiesHelper.getCacheContracts() + name
                + propertiesHelper.getContractSuffix() + ".xsl";
        ContractOld contract = new DefaultContract(home);
        contract.initialize(source, cachedTemplate);
        HashMap<String, File> param = new HashMap<String, File>();
        while (true) {
            int event = parser.next();
            switch (event) {
            case XMLStreamConstants.END_ELEMENT:
                if (parser.getLocalName().equals(CONTRACT_ELEMENT)) {
                    System.out
                            .println("here we need to execude the transformation");
                    System.out.println("data "+data+" destination "+ destination);
                    processContractResult(data, destination, contract, param);
                    System.out.println("************ END CONTRACT ***************");
                    // FROM HERE
                    return;
                }

                break;

            case XMLStreamConstants.START_ELEMENT:
                if (parser.getLocalName().equals(PROPERTY_ELEMENT)) {
                    processProperty(parser, param, name);
                }
                break;

            default:
                break;
            }
        }

    }

    private void processContractResult(String data, String destination,
            ContractOld contract, HashMap<String, File> param)
            throws XMLStreamException, FileNotFoundException {
        File contractResult = contract.execute(data, param, destination);
        LinkedHashSet<XMLEvent> pathElement;
        XMLStreamReader contractResultReader = inputFactory
                .createXMLStreamReader(new FileReader(contractResult));
        // Main event loop
        while (true) {
            int resultEvent = contractResultReader.next();
            switch (resultEvent) {
            case XMLStreamConstants.START_ELEMENT:
                if (contractResultReader.getLocalName().equals("part")) {
                    String xpath = "", injectionPoint = "";
                    // Get attribute names
                    for (int i = 0; i < contractResultReader.getAttributeCount(); i++) {
                        String localName = contractResultReader
                                .getAttributeLocalName(i);
                        if (localName.equals(ContractHelperStAX.RESULT_XPATH)) {
                            // Return value
                            xpath = contractResultReader.getAttributeValue(i);
                        }
                    }
                    if (xpath.equals("")) {
                        // iterate through the children and add them
                        // to the pathElement
                        if (resultTree.containsKey(currentPath))
                            pathElement = resultTree.get(currentPath);
                        else
                            pathElement = new LinkedHashSet<XMLEvent>();
                        injectionPoint = currentPath;
                        inject(pathElement, contractResultReader, injectionPoint);
                        // as soon as you find the end element add
                        // it back to the resultTree
                    } else {
                        // iterate through the children and add them
                        // to the xpath defined
                        if (resultTree.containsKey(xpath))
                            pathElement = resultTree.get(xpath);
                        else
                            pathElement = new LinkedHashSet<XMLEvent>();
                        injectionPoint = xpath;
                        inject(pathElement, contractResultReader, injectionPoint);
                    }
                }
                break;
            case XMLStreamConstants.END_DOCUMENT:
                return;
            default:
                break;
            }
        }
    }

    private void inject(LinkedHashSet<XMLEvent> pathElement,
            XMLStreamReader parser, String injectionPoint)
            throws XMLStreamException {
        System.out.println("injectionPoint " + injectionPoint);
        while (true) {
            int event = parser.next();
            XMLEvent currentEvent = allocator.allocate(parser);
            switch (event) {
            case XMLStreamConstants.END_ELEMENT:
                if (parser.getLocalName().equals("part")) {
                    System.out.println("Trying to add to hash " + injectionPoint);
                    resultTree.put(injectionPoint, pathElement);
                   /* System.out.println("resultTree.size() " + resultTree.size());
                    Iterator<String> iterator = resultTree.keySet().iterator();
                    while (iterator.hasNext()) {
                        System.out.println("key " + iterator.next());

                    }*/
                    return;
                } else {
                    pathElement.add(currentEvent);
                    break;
                }
            default:
                pathElement.add(currentEvent);
                break;
            }
        }
    }

    private void processProperty(XMLStreamReader parser,
            HashMap<String, File> transformer, String contractName)
            throws XMLStreamException {
        XMLEventWriter writerProperty = null;
        XMLEvent currentEvent = allocator.allocate(parser);
        String propertyName = null;
        // Get attribute names
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String localName = parser.getAttributeLocalName(i);
            if (localName.equals(PROPERTY_NAME_ATT)) {
                // Return value
                propertyName = parser.getAttributeValue(i);
            }
        }
        // Write the result to disk
        File cache = new File(propertiesHelper.getCacheStructure() + structurerId
                + "/" + contractName + ".xpath.property." + propertyName + ".xml");
        if (!cache.isFile()) {
            try {
                cache.createNewFile();
            } catch (IOException e) {
                // if we cannot create a file that means that the parent path
                // does not exists
                File path = new File(cache.getParent());
                path.mkdirs();
                try {
                    cache.createNewFile();
                } catch (IOException e1) {
                    throw new XMLStreamException("IOException: "+e.getMessage());
                }
            }
        }
        Writer result = null;
        try {
            result = new FileWriter(cache.getAbsoluteFile());
            ProcessingInstruction procInst = new ProcessingInstructionEvent("xml",
                    "version=\"1.0\" encoding=\"UTF-8\"");
            XMLWriterUtils.writeProcessingInstruction(procInst, result);
        } catch (IOException e) {
            throw new XMLStreamException("IOException: "+e.getMessage());
        }
        writerProperty = outputFactory.createXMLEventWriter(result);
        writerProperty.add(currentEvent);
        while (true) {
            int event = parser.next();
            currentEvent = allocator.allocate(parser);
            switch (event) {

            case XMLStreamConstants.END_ELEMENT:
                if (parser.getLocalName().equals(PROPERTY_ELEMENT)) {
                    writerProperty.add(currentEvent);
                    writerProperty.flush();
                    transformer.put(propertyName, cache);
                    return;
                } else {
                    writerProperty.add(currentEvent);
                    break;
                }

            default:
                writerProperty.add(currentEvent);
                break;
            }
        }
    }
}
