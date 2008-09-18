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
package org.apache.forrest.generation;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.generation.AbstractGenerator;
import org.apache.cocoon.xml.AttributesImpl;
import org.apache.excalibur.source.SourceValidity;
import org.apache.excalibur.source.impl.validity.NOPValidity;
import org.xml.sax.SAXException;

/**
 * The module generator will connect to the input module
 * specified by the {@link #MODULE_PARAM}.
 * The generator will then create a property file out of the 
 * iteration of the {@link InputModule#getAttributeNames()} method.
 *  <p>
 *  The result will look like:<br>
 *  <code>
 *  &lt;properties&gt;<br>
 *  &nbsp;&lt;property value="InputModuleValue" name="InputModuleKey"/><br>
 *  &lt;/properties&gt;
 *  <p>
 *  Use it from the sitemap like:<br>
 *  <code>
 *  &lt;map:match pattern="module.*.properties"&gt;<br>
 *    &nbsp;&lt;map:generate type="module"&gt;<br>
 *     &nbsp;&nbsp; &lt;map:parameter name="input-module" value="{1}"/&gt;<br>
 *   &nbsp; &lt;/map:generate&gt;<br>
 *   &nbsp; &lt;map:serialize type="xml"/&gt;<br>
 *  &lt;/map:match&gt;
 *
 */
public class ModuleGenerator extends AbstractGenerator implements Serviceable,
        CacheableProcessingComponent {
    private ServiceSelector selector;

    private Iterator iterator;

    private InputModule inputModule;

    private String module;

    public static final String MODULE_PARAM = "input-module";

    public static final String PROP_NS = "http://apache.org/forrest/properties/1.0";

    public static final String NS_PREFIX = "forrest";

    public static final String ROOT_ELEMENT = "properties";

    public static final String CHILD_ELEMENT = "property";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String VALUE_ATTRIBUTE = "value";

    public void generate() throws IOException, SAXException,
            ProcessingException {
        this.contentHandler.startDocument();
        AttributesImpl attr = new AttributesImpl();
        attr.addAttribute("", MODULE_PARAM, MODULE_PARAM, "CDATA", module);
        this.contentHandler.startPrefixMapping(NS_PREFIX, PROP_NS);
        this.contentHandler.startElement(PROP_NS, ROOT_ELEMENT, ROOT_ELEMENT,
                attr);
        if (!(iterator == null)) {
            createProperty(attr);
        }

        this.contentHandler.endElement(PROP_NS, ROOT_ELEMENT, ROOT_ELEMENT);
        this.contentHandler.endPrefixMapping(NS_PREFIX);
        this.contentHandler.endDocument();

    }

    private void createProperty(AttributesImpl attr) throws SAXException {
        while (iterator.hasNext()) {
            attr.clear();
            String key = (String) iterator.next();
            attr.addAttribute("", NAME_ATTRIBUTE, NAME_ATTRIBUTE, "CDATA", key);
            try {
                Object value = inputModule.getAttribute(key, null, objectModel);
                if (!(value == null)) {
                    attr.addAttribute("", VALUE_ATTRIBUTE, VALUE_ATTRIBUTE,
                            "CDATA", (String) value);
                } else {
                    attr.addAttribute("", VALUE_ATTRIBUTE, VALUE_ATTRIBUTE,
                                    "CDATA",
                                    "NULL - Seems the input module is not supported by this generator");
                }
            } catch (Exception e) {
                attr.addAttribute("", VALUE_ATTRIBUTE, VALUE_ATTRIBUTE,
                        "CDATA",
                        "An error occured receiving/casting the object: " + e);
            }
            this.contentHandler.startElement(PROP_NS, CHILD_ELEMENT,
                    CHILD_ELEMENT, attr);
            this.contentHandler.endElement(PROP_NS, CHILD_ELEMENT,
                    CHILD_ELEMENT);
        }
    }

    public void setup(SourceResolver resolver, Map objectModel, String src,
            Parameters par) throws ProcessingException, SAXException,
            IOException {
        iterator = null;
        inputModule = null;
        this.objectModel = objectModel;
        try {
            module = par.getParameter(MODULE_PARAM);
            inputModule = (InputModule) selector.select(module);
            iterator = inputModule.getAttributeNames(null, objectModel);
        } catch (ServiceException e) {
            throw new ProcessingException(
                    "Could not lookup the module with name " + module + ". ", e);
        } catch (ConfigurationException e) {
            throw new ProcessingException(
                    "InputModule did not return an Iterator for inputModule.getAttributeNames(null, objectModel). ",
                    e);
        } catch (ParameterException e) {
            throw new ProcessingException(
                    "input-module is not set in the sitemap. Please set "
                            + MODULE_PARAM, e);
        }
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.selector = (ServiceSelector) manager.lookup(InputModule.ROLE
                + "Selector");
    }

    public Serializable getKey() {
        return this.module;
    }

    public SourceValidity getValidity() {
        return new NOPValidity();
    }
}
