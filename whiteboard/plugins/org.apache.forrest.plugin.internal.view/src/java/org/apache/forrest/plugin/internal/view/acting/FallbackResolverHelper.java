/*
 * Copyright 1999-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.forrest.plugin.internal.view.acting;

/**
 *  <map:parameter value="{1}" name="request"/><br/>
 *  <map:parameter value="{project:theme}" name="projectFallback"/><br/>
 *  <map:parameter value="{project:theme-ext}" name="projectExtension"/><br/>
 *  <map:parameter value="{project:content.xdocs}" name="projectDir"/><br/>
 *  <map:parameter value="{defaults:view-themes}" name="defaultDir"/><br/>
 *  <map:parameter value="{defaults:theme}" name="defaultFallback"/><br/>
 *  <map:parameter value="{defaults:theme-ext}" name="defaultExtension"/>
 *
 */
public class FallbackResolverHelper {
    private String request, projectFallback, projectExtension, projectDir,
            defaultFallback, defaultExtension, defaultDir, rest,responseURI;
    /**
     * @return Returns the defaultExtension.
     */
    public String getDefaultExtension() {
        return defaultExtension;
    }
    /**
     * @param defaultExtension The defaultExtension to set.
     */
    public void setDefaultExtension(String defaultExtension) {
        this.defaultExtension = defaultExtension;
    }
    /**
     * @return Returns the defaultFallback.
     */
    public String getDefaultFallback() {
        return defaultFallback;
    }
    /**
     * @param defaultFallback The defaultFallback to set.
     */
    public void setDefaultFallback(String defaultFallback) {
        this.defaultFallback = defaultFallback;
    }
    /**
     * @return Returns the request.
     */
    public String getRequest() {
        return request;
    }
    /**
     * @param request The request to set.
     */
    public void setRequest(String path) {
        this.request = path;
    }
    /**
     * @return Returns the projectDir.
     */
    public String getProjectDir() {
        return projectDir;
    }
    /**
     * @param projectDir The projectDir to set.
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
     * @param projectExtension The projectExtension to set.
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
     * @param projectFallback The projectFallback to set.
     */
    public void setProjectFallback(String projectFallback) {
        this.projectFallback = projectFallback;
    }
    /**
     * @return Returns the rest.
     */
    public String getRest() {
        return rest;
    }
    /**
     * @param rest The rest to set.
     */
    public void setRest(String rest) {
        this.rest = rest;
    }
    /**
     * @return Returns the responseURI.
     */
    public String getResponseURI() {
        return responseURI;
    }
    /**
     * @param responseURI The responseURI to set.
     */
    public void setResponseURI(String responseURI) {
        this.responseURI = responseURI;
    }
    /**
     * @return Returns the defaultDir.
     */
    public String getDefaultDir() {
        return defaultDir;
    }
    /**
     * @param defaultDir The defaultDir to set.
     */
    public void setDefaultDir(String defaultDir) {
        this.defaultDir = defaultDir;
    }
}
