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
import org.apache.forrest.dispatcher.DispatcherException;

/**
 * Calculates which location to return for a given directory.
 * Here we are traversing the tree till we reach its root. 
 * <p>
 * We are looking first in the request string and then using a 
 * fallback algorithm to find alternative fallbacks.
 * <p> 
 * e.g. the request is "sample/index". First choice is to find: 
 * {$projectDir}sample/index{$projectExtension}<br>
 * If the file does not exist we will try with the fallback file
 * {$projectDir}sample/{$projectFallback}{$projectExtension}<br>
 * The last file we will try in our example is 
 * {$projectDir}{$projectFallback}{$projectExtension}.<br>
 * With this we have reached the root directory and we cannot find the
 * requested file the action will return null.
 * <p>
 * &lt;map:act type="RecursiveDirectoryTraversalAction"&gt;<br>
 *   &lt;map:parameter value="{../1}{1}" name="request"/&gt;<br>
 *   &lt;map:parameter value="{properties:dispatcher.theme}" name="projectFallback"/&gt;<br>
 *   &lt;map:parameter value="{properties:dispatcher.theme-ext}" 
 *                     name="projectExtension"/&gt;<br>
 *   &lt;map:parameter value="{properties:resources}structurer/url/" 
 *                     name="projectDir"/&gt;<br>
 * &lt;!--  url project-based theme-based = directory-based / parent-directory based (recursively) --&gt;<br>
 *         &lt;map:location src="{uri}" /&gt;<br>
 *  &lt;/map:act&gt;
 * 
 */
public class RecursiveDirectoryTraversalAction extends ServiceableAction
        implements ThreadSafe, Serviceable {

    SourceResolver resolver = null;
    HashMap map = new HashMap();

    private String request, projectFallback, projectExtension, projectDir,
            rest;

    /**
     * Set the current <code>ComponentManager</code> instance used by this
     * <code>Composable</code>.
     * 
     * @throws ServiceException
     */

    public void service(ServiceManager manager) throws ServiceException {
        this.resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
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
                + this.getProjectExtension();
        map = new HashMap();
        Map returnMap = act(uri);
        return returnMap;
    }
    
    /** Return a Map if Source 'uri' resolves and exists. */
    public Map act(String uri) {
        Source src = null;

        // The locationmap module will return null if there is no match for
        // the supplied hint, without the following the URI will be resolved to
        // the context root, which always exists, but does not contain a valid
        // resource.
        if (uri == null || uri == "") {
            return null;
        }

        try {
            this.computeResponseURI(uri, src);
            //src = resolver.resolveURI(uri);
            if (this.map.containsKey("uri")) {
                return this.map;
            } else {
                return null;
            }
        } catch (DispatcherException e) {
            getLogger().warn(
                    "Error reading from source '" + uri + "': "
                            + e.getMessage());
            return null;
        } finally {
            release(src);
        }
    }

    /**
     * @param resolver
     * @param uri
     *            *viewSelector* project-xdocs will return which view is
     *            responsible for the requested path. If no view
     *            (choice|fallback) could be found the template will return the
     *            viewFallback (resources/views/default.fv).
     * 
     * ex.: 1.request: index First choice: index.fv First/last fallback:
     * default.fv
     * 
     * 2.request: sample/index First choice: sample/index.fv First fallback:
     * sample/default.fv Last fallback: default.fv
     * 
     * 3.request: sample/subdir/index First choice: sample/subdir/index.fv First
     * fallback: sample/subdir/default.fv Second fallback: sample/default.fv
     * Last fallback: default.fv
     * 
     * ...
     * 
     * des.: The parent view (root-view) inherits to its children until a child
     * is overriding this view. This override can be used for directories
     * (default.fv) and/or files (*.fv). That means that the root view is the
     * default view as long no other view can be found in the requested child.
     * @throws DispatcherException 
     *  
     */
    private void computeResponseURI(String uri, Source src) throws DispatcherException{
        try {
          src = resolver.resolveURI(uri);
          if (src.exists()) {
            this.map.put("uri", uri);
        } else {
            if (this.getRest().lastIndexOf("/") > -1) {
                this.setRest(this.getRest().substring(0,
                        this.getRest().lastIndexOf("/")));
                uri = this.getProjectDir() + this.getRest() + "/"
                        + this.getProjectFallback()
                        + this.getProjectExtension();
                this.computeResponseURI(uri, src);
            } else {
                uri = this.getProjectDir() + this.getProjectFallback()
                        + this.getProjectExtension();
                src = resolver.resolveURI(uri);
                if (src.exists()) {
                    this.map.put("uri", uri);
                }
            }
        }
        } catch (MalformedURLException e) {
         throw new DispatcherException(e);
        } catch (IOException e) {
          throw new DispatcherException(e);
        }finally{
          release(src);
        }
        
    }
    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source) 
     */
   public void release(Source source) {
     if(source!=null){
       resolver.release(source);
     }
    }
    public void prepare(Parameters parameters, String src) {
        this.setRequest(parameters.getParameter("request", src));
        this.setProjectFallback(parameters.getParameter("projectFallback",
                src));
        this.setProjectExtension(parameters.getParameter("projectExtension",
                src));
        this.setProjectDir(parameters.getParameter("projectDir", src));
        this.setRest(this.getRequest());
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
     * @return Returns the projectExtension.
     */
    public String getProjectExtension() {
        return projectExtension;
    }

    /**
     * @param projectExtension
     *            The projectExtension to set.
     */
    public void setProjectExtension(String projectExtension) {
        this.projectExtension = projectExtension;
    }

    /**
     * @return Returns the projectFallback.
     */
    public String getProjectFallback() {
        return projectFallback;
    }

    /**
     * @param projectFallback
     *            The projectFallback to set.
     */
    public void setProjectFallback(String projectFallback) {
        this.projectFallback = projectFallback;
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

    /**
     * @return Returns the rest.
     */
    public String getRest() {
        return rest;
    }

    /**
     * @param rest
     *            The rest to set.
     */
    public void setRest(String rest) {
        this.rest = rest;
    }
}
