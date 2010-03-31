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

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

public abstract class AbstractTraversal extends ServiceableAction {

    
    SourceResolver resolver = null;
    String rest;
    String projectFallback;
    String projectDir;
    String projectExtension;
    String request;
    
    public AbstractTraversal() {
        super();
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

    protected String getFather() {
        return this.getRest().substring(0, this.getRest().lastIndexOf("/"));
    }

    /**
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release(Source source) {
        if (source != null) {
            resolver.release(source);
        }
    }

    protected void prepare(Parameters parameters) throws ParameterException {
        this.setRequest(parameters.getParameter("request"));
        this.setProjectFallback(parameters.getParameter("projectFallback"));
        this.setProjectExtension(parameters.getParameter("projectExtension"));
        this.setProjectDir(parameters.getParameter("projectDir"));
        this.setRest(this.getRequest());
    }

}
