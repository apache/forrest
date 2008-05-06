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
package org.apache.forrest.dispatcher.acting;
/**
 * Looks up a meta data file to determine which resource type should be returned. 
 * Will parse the document and looks for the {$resourceTypeElement}.
 * If found the action returns {$resourceTypeBase}{$resourceTypeElement}.
 * <p>
 * component declaration (lm and sitemap):<br>
 * &lt;action name="resourceTypeAction" 
 *      src="org.apache.forrest.dispatcher.acting.ResourceTypeAction"/&gt;
 *<p>
 *pipline usage lm<br>
*  &lt;act type="resourceTypeAction"&gt;<br>
*     &lt;parameter value="{1}" name="request"/&gt;<br>
*     &lt;parameter value="{project:content.xdocs}" name="projectDir"/&gt;<br>
*     &lt;parameter value="lm://dispatcher.structurer.resourceType." name="resourceTypeBase"/&gt;<br>
*     &lt;parameter value=".xml.meta" name="metaExtension"/&gt;<br>
*     &lt;parameter value="resourceType" name="resourceTypeElement"/&gt;<br>
*     &lt;parameter value="http://apache.org/cocoon/lenya/page-envelope/1.0" name="resourceTypeElementNS"/&gt;<br>
*     &lt;!--  Meta data based --&gt;<br>
*     &lt;location src="{uri}" /&gt;<br>
 * &lt;/act&gt;
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class ResourceTypeAction extends ServiceableAction
        implements ThreadSafe, Serviceable {

    SourceResolver resolver = null;
    HashMap map = new HashMap();

    private String request, metaExtension, resourceTypeElement,resourceTypeElementNS, projectDir,resourceTypeBase;
            

    /**
     * Set the current &gt;code>ComponentManager</code> instance used by this
     * <code>Composable</code>.
     * 
     * @throws ServiceException
     */

    public void service(ServiceManager manager) throws ServiceException {
        this.resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
        this.manager=manager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector,
     *      org.apache.cocoon.environment.SourceResolver, java.util.Map,
     *      java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector,
            org.apache.cocoon.environment.SourceResolver resolver,
            Map objectModel, String source, Parameters parameters)
            throws Exception {
        this.prepare(parameters, source);
        String uri = this.getProjectDir() + this.getRequest()
                + this.getMetaExtension();
        Map returnMap = act(uri);
        return returnMap;
    }
    
    /** Return a Map if Source 'uri' resolves and exists. */
    public Map act(String uri) {
        Source src = null;
        if (uri == null || uri == "") {
            return null;
        }

        try {
            this.computeResponseURI(uri, src);
            if (this.map.containsKey("uri")) {
                return this.map;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            getLogger().warn(
                    "Selector URL '" + uri + "' is not a valid Source URL");
            return null;
        } catch (IOException e) {
            getLogger().warn(
                    "Error reading from source '" + uri + "': "
                            + e.getMessage());
            return null;
        } catch (Exception e) {
            getLogger().warn(
                    "Error reading from source '" + uri + "': "
                            + e.getMessage());
            return null;
        }finally {
            resolver.release(src);
        }
    }
       
       private void computeResponseURI(String uri, Source src)
            throws Exception {
        src = resolver.resolveURI(uri);
        if (src.exists()) {
            Document rawData =  org.apache.forrest.dispatcher.util.SourceUtil.readDOM(
                    uri, this.manager);
            NodeList type = rawData.getElementsByTagNameNS(getResourceTypeElementNS(),getResourceTypeElement());
            String typeString = type.item(0).getFirstChild().getNodeValue();
            Source typeSource = resolver.resolveURI(resourceTypeBase+typeString);
            if (typeSource.exists()) {
                this.map.put("uri", typeSource.getURI());
            }
        }
    }

    public void prepare(Parameters parameters, String src) {
        this.setRequest(parameters.getParameter("request", src));
        this.setMetaExtension(parameters.getParameter("metaExtension",
                src));
        this.setResourceTypeElement(parameters.getParameter("resourceTypeElement",
                src));
        this.setProjectDir(parameters.getParameter("projectDir", src));
        this.setResourceTypeElementNS(parameters.getParameter("resourceTypeElementNS",
                src));
        this.resourceTypeBase = parameters.getParameter("resourceTypeBase",
                src);
    }

    /**
     * @return Returns the projectDir.
     */
    public String getProjectDir() {
        return projectDir;
    }

    /**
     * @param projectDir
     *            The projectDir to set.
     */
    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    /**
     * @return Returns the resourceTypeElement.
     */
    public String getResourceTypeElement() {
        return resourceTypeElement;
    }

    /**
     * @param resourceTypeElement
     *            The resourceTypeElement to set.
     */
    public void setResourceTypeElement(String resourceTypeElement) {
        this.resourceTypeElement = resourceTypeElement;
    }

    /**
     * @return Returns the metaExtension.
     */
    public String getMetaExtension() {
        return metaExtension;
    }

    /**
     * @param metaExtension
     *            The metaExtension to set.
     */
    public void setMetaExtension(String metaExtension) {
        this.metaExtension = metaExtension;
    }

    /**
     * @return Returns the request.
     */
    public String getRequest() {
        return request;
    }

    /**
     * @param request
     *            The request to set.
     */
    public void setRequest(String request) {
        this.request = request;
    }

    /**
     * @return Returns the resolver.
     */
    public SourceResolver getResolver() {
        return resolver;
    }

    /**
     * @param resolver
     *            The resolver to set.
     */
    public void setResolver(SourceResolver resolver) {
        this.resolver = resolver;
    }

    public String getResourceTypeElementNS() {
        return resourceTypeElementNS;
    }

    public void setResourceTypeElementNS(String resourceTypeElementNS) {
        this.resourceTypeElementNS = resourceTypeElementNS;
    }

}
