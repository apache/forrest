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
/*
import javanet.staxutils.BaseXMLInputFactory;
import javanet.staxutils.BaseXMLOutputFactory;
import javanet.staxutils.events.ProcessingInstructionEvent;
import javanet.staxutils.io.XMLWriterUtils;
*/
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
/*
import com.bea.xml.stream.XMLEventAllocatorBase;
*/
public class ContractHelperStAX {

    public static final String NS = "http://apache.org/forrest/templates/1.0";

    public static final String CONTRACT_ELEMENT = "contract";

    public static final String CONTRACT_NAME_ATT = "name";

    public static final String DESCRIPTION_ELEMENT = "description";

    public static final String TEMPLATE_ELEMENT = "template";

    public static final String TEMPLATE_FORMAT_ATT = "inputFormat";

    public static final String USAGE_ELEMENT = "usage";

    public static final String RESULT_XPATH = "xpath";

    private String name, usage, description;

    private XMLOutputFactory outputFactory = null;//BaseXMLOutputFactory.newInstance();

    private XMLInputFactory inputFactory = null;//BaseXMLInputFactory.newInstance();

    private DispatcherPropertiesHelper propertiesHelper;

    public ContractHelperStAX(String home) {
        propertiesHelper= new DispatcherPropertiesHelper(home);
    }

    public File execute(String dataUrl, HashMap param, String destination,
            File template) throws FileNotFoundException, SAXException {
        boolean useCache = false;
        // The incoming xml data for the transformation and the file we will
        // return
        File dataFile = new File(dataUrl);
        File cachedContract = new File(destination);
        if (!cachedContract.isFile()) {
            try {
                cachedContract.createNewFile();
            } catch (IOException e) {
                // if we cannot create a file that means that the parent path
                // does not exists
                File path = new File(cachedContract.getParent());
                path.mkdirs();
                try {
                    cachedContract.createNewFile();
                } catch (IOException e1) {
                    throw new SAXException("IOException: "+e.getMessage());
                }
            }
        } else {
            // compare the url and the file and the datauri and the structurer
            // url (not known here)
            long modifiedCache = cachedContract.lastModified();
            long modifiedContract = dataFile.lastModified();
            if (modifiedCache > modifiedContract)
                useCache = true;
        }

        if (!useCache) {
            // FIXME activate when stable
        }

        // Create transformer
        TransformerFactory transFact = TransformerFactory.newInstance();
        FileReader templateReader = new FileReader(template), dataReader;
        try {
            // here we need to inject the dataUri or foo
            if (!dataFile.isFile()) {
                // no dataFile defined trying cache for foo
                dataFile = new File(propertiesHelper.getMasterFooUrl().replace("file:///", ""));
                if (!dataFile.isFile()) {
                    // not in cache either, so we create it and
                    // store it in cache
                    Writer foo = new FileWriter(dataFile);
                    XMLStreamWriter fooWriter = outputFactory
                            .createXMLStreamWriter(foo);
                    fooWriter.writeStartDocument("UTF-8", "1.0");
                    fooWriter.writeStartElement("foo");
                    fooWriter.writeEndDocument();
                    fooWriter.flush();
                    fooWriter.close();
                    foo.close();
                }
            }
            dataReader = new FileReader(dataFile);
            Source xslSource = new StreamSource(templateReader);
            Source dataSource = new StreamSource(dataReader);
            // prepare transformation
            Transformer transformer = transFact.newTransformer(xslSource);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            // It seems that the only way to pass a xml file to a xsl as param
            // is as DOM
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            for (Iterator iter = param.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                File value = (File) param.get(key);
                transformer.setParameter(key, builder.parse(value));
            }
            // create a StreamResult and write the result to disk
            Result streamResult = new StreamResult(cachedContract);
            transformer.transform(dataSource, streamResult);
        } catch (Exception e1) {
            throw new SAXException("Exception: "+e1.getMessage());
        }

        return cachedContract;
    }

    public File getTemplate(String sourceUrl, String destination)
            throws IOException, FactoryConfigurationError, XMLStreamException,
            InstantiationException, IllegalAccessException {
        // setting up the files
        String fileName = destination;
        File cache = new File(fileName), contractFile = new File(sourceUrl);
        // caching is ATM implemetent by writing the template to disk
     /*   boolean useCache = false;
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
            long modifiedContract = contractFile.lastModified();
            if (modifiedCache > modifiedContract)
                useCache = true;
        }
        // !useCache
        if (true) {
            URL contractUrl;
            // if we do not have a protocol defined
            // we define the file protocoll as default
            if (sourceUrl.indexOf(":/") > -1)
                contractUrl = new URL(sourceUrl);
            else
                contractUrl = new URL("file:///" + sourceUrl);
            InputStream in = contractUrl.openStream();
            inputFactory.setEventAllocator(new XMLEventAllocatorBase());
            XMLEventAllocator allocator = inputFactory.getEventAllocator();
            XMLStreamReader parser = inputFactory.createXMLStreamReader(in);
            // Create XML event writer
            // Write the result to disk
            Writer result = new FileWriter(fileName);
            ProcessingInstruction procInst = new ProcessingInstructionEvent("xml",
                    "version=\"1.0\" encoding=\"UTF-8\"");
            XMLWriterUtils.writeProcessingInstruction(procInst, result);
            XMLEventWriter writer = outputFactory.createXMLEventWriter(result);
            while (true) {
                int event = parser.next();
                if (event == XMLStreamConstants.END_DOCUMENT) {
                    break;
                }
                if (event == XMLStreamConstants.START_ELEMENT) {
                    if (parser.getLocalName().equals(CONTRACT_ELEMENT))
                        processContract(parser);
                    if (parser.getLocalName().equals(DESCRIPTION_ELEMENT))
                        processDescription(parser);
                    if (parser.getLocalName().equals(USAGE_ELEMENT))
                        processUsage(parser);
                    if (parser.getLocalName().equals(TEMPLATE_ELEMENT))
                        processTemplate(parser, writer, allocator);
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                }
            }
            writer.flush();
            parser.close();
        }*/
        return cache;
    }

    private static void processTemplate(XMLStreamReader parser,
            XMLEventWriter writer, XMLEventAllocator allocator)
            throws XMLStreamException {
        String role = "";
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            // Get attribute name
            String localName = parser.getAttributeLocalName(i);
            if (localName.equals(TEMPLATE_FORMAT_ATT)) {
                // Return value
                role = parser.getAttributeValue(i);
            }
        }
        if (role.equals("xsl")) {
            while (true) {
                int event = parser.next();
                switch (event) {

                case XMLStreamConstants.END_ELEMENT:
                    if (parser.getNamespaceURI() != null) {
                        if (parser.getNamespaceURI().equals(NS)
                                & parser.getLocalName().equals(TEMPLATE_ELEMENT))
                            return;
                        else
                            writer.add(allocator.allocate(parser));
                    } else
                        writer.add(allocator.allocate(parser));
                    break;

                default:
                    writer.add(allocator.allocate(parser));
                    break;
                }
            }
        }
    }

    private void processUsage(XMLStreamReader parser) throws XMLStreamException {
        while (true) {
            int event = parser.next();
            switch (event) {

            case XMLStreamConstants.CHARACTERS:
                if (parser.getText().replace(" ", "").length() > 1) 
                    usage = parser.getText().trim();
                break;

            case XMLStreamConstants.END_ELEMENT:
                if (parser.getLocalName().equals(USAGE_ELEMENT))
                    return;
                break;
            }
        }
    }

    private void processDescription(XMLStreamReader parser)
            throws XMLStreamException {
        while (true) {
            int event = parser.next();
            switch (event) {
            case XMLStreamConstants.START_ELEMENT:
                System.out.println("ignoring " + parser.getLocalName());
                break;

            case XMLStreamConstants.CHARACTERS:
                if (parser.getText().replace(" ", "").length() > 1) {
                    description = parser.getText().trim();
                }
                break;

            case XMLStreamConstants.END_ELEMENT:
                if (parser.getLocalName().equals(DESCRIPTION_ELEMENT))
                    return;
                break;
            }
        }
    }

    private void processContract(XMLStreamReader parser) {
        String contractName = "";
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            // Get attribute name
            String localName = parser.getAttributeLocalName(i);
            if (localName.equals(CONTRACT_NAME_ATT)) {
                // Return value
                contractName = parser.getAttributeValue(i);
            }
        }
        name = contractName;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

}
