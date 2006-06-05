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
package org.apache.forrest.acting;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * This action simply checks to see if a resource identified by the <code>src</code>
 * sitemap attribute exists or not. The action returns empty <code>Map</code> if
 * resource exists, <code>null</code> otherwise.
 * 
 * <p>Instead of src attribute, source can be specified using
 * parameter named <code>url</code> (this is old syntax, should be removed soon).
 * 
 * <p><b>NOTE:</b> {@link org.apache.cocoon.selection.ResourceExistsSelector}
 * should be preferred to this component, as the semantics of a Selector better
 * matches the supplied functionality.
 *
 * @author <a href="mailto:balld@apache.org">Donald Ball</a>
 * @version CVS $Id$
 */

public class FallbackResolverAction extends ServiceableAction implements
        ThreadSafe {
    private FallbackResolverHelper fallbackHelper = new FallbackResolverHelper();

    public Map act(Redirector redirector, SourceResolver resolver,
            Map objectModel, String src, Parameters parameters)
            throws Exception {
        this.prepare(parameters, src);
        String uri = fallbackHelper.getProjectDir() + fallbackHelper.getRequest()
                + fallbackHelper.getProjectExtension();
        computeResponseURI(resolver, uri);
        if (fallbackHelper.getResponseURI()==null){
            uri = fallbackHelper.getDefaultDir()+"/"
                +fallbackHelper.getProjectFallback()
                +fallbackHelper.getDefaultExtension();
            if (this.resourceExist(resolver, uri)) {
                fallbackHelper.setResponseURI(uri);
            }else{
                fallbackHelper.setResponseURI(fallbackHelper.getDefaultDir()+"/"
                        +fallbackHelper.getDefaultFallback()
                        +fallbackHelper.getDefaultExtension());
            }
        }
        HashMap map = new HashMap();
        map.put("uri", fallbackHelper.getResponseURI());
        return map;
    }

/**
 * prepare(Parameters parameters, String src)
 *
 *  <map:parameter value="{1}" name="request"/><br/>
 *  <map:parameter value="{project:theme}" name="projectFallback"/><br/>
 *  <map:parameter value="{project:theme-ext}" name="projectExtension"/><br/>
 *  <map:parameter value="{project:content.xdocs}" name="projectDir"/><br/>
 *  <map:parameter value="{defaults:view-themes}" name="defaultDir"/><br/>
 *  <map:parameter value="{defaults:theme}" name="defaultFallback"/><br/>
 *  <map:parameter value="{defaults:theme-ext}" name="defaultExtension"/>
 *
 */
    public void prepare(Parameters parameters, String src) {
        fallbackHelper.setRequest(parameters.getParameter("request", src));
        fallbackHelper.setProjectFallback(parameters.getParameter(
                "projectFallback", src));
        fallbackHelper.setProjectExtension(parameters.getParameter(
                "projectExtension", src));
        fallbackHelper
                .setProjectDir(parameters.getParameter("projectDir", src));
        fallbackHelper.setDefaultFallback(parameters.getParameter(
                "defaultFallback", src));
        fallbackHelper.setDefaultExtension(parameters.getParameter(
                "defaultExtension", src));
        fallbackHelper.setDefaultDir(parameters.getParameter(
                "defaultDir", src));
        fallbackHelper.setRest(fallbackHelper.getRequest());
        fallbackHelper.setResponseURI(null);
    }

    public void computeResponseURI(SourceResolver resolver, String uri) {
        if (this.resourceExist(resolver, uri)) {
            fallbackHelper.setResponseURI(uri);
        } else {
            if (fallbackHelper.getRest().lastIndexOf("/") > -1) {
                fallbackHelper.setRest(fallbackHelper.getRest().substring(0,
                        fallbackHelper.getRest().lastIndexOf("/")));
                uri = fallbackHelper.getProjectDir() + fallbackHelper.getRest()
                        + "/" + fallbackHelper.getProjectFallback()
                        + fallbackHelper.getProjectExtension();
                this.computeResponseURI(resolver,uri);
            }else{
                uri = fallbackHelper.getProjectDir()
                    +fallbackHelper.getProjectFallback()
                    +fallbackHelper.getProjectExtension();
                if (this.resourceExist(resolver, uri)) {
                    fallbackHelper.setResponseURI(uri);
                }
            }
        }
    }

    public boolean resourceExist(SourceResolver resolver, String uri) {
        Source source = null;
        try {
            source = resolver.resolveURI(uri);
            if (source.exists()) {
                return true;
            }
        } catch (SourceNotFoundException e) {
            // Do not log
        } catch (Exception e) {
            getLogger().warn("Exception resolving resource " + uri, e);
        } finally {
            if (source != null) {
                resolver.release(source);
            }
        }
        return false;
    }
}
