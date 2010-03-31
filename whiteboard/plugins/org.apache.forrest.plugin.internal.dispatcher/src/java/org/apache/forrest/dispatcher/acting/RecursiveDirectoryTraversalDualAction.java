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
import org.apache.cocoon.environment.Redirector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.forrest.dispatcher.exception.DispatcherException;

/**
 * Calculates which location to return for a given directory. Here we are
 * traversing the tree till we reach its root.
 * <p>
 * We are looking first in the request string and then using a fallback
 * algorithm to find alternative fallbacks.
 * <p>
 * e.g. the request is "sample/index". First choice is to find:
 * {$projectDir}sample/index{$projectExtension}<br>
 * If the file does not exist we will try with the fallback file
 * {$projectDir}sample/{$projectFallback}{$projectExtension}<br>
 * The last file we will try in our example is
 * {$projectDir}{$projectFallback}{$projectExtension}.<br>
 * With this we have reached the root directory and we cannot find the requested
 * file the action will return null.
 * <p>
 * &lt;map:act type="RecursiveDirectoryTraversalAction"&gt;<br>
 * &lt;map:parameter value="{../1}{1}" name="request"/&gt;<br>
 * &lt;map:parameter value="{properties:dispatcher.theme}"
 * name="projectFallback"/&gt;<br>
 * &lt;map:parameter value="{properties:dispatcher.theme-ext}"
 * name="projectExtension"/&gt;<br>
 * &lt;map:parameter value="{properties:resources}structurer/url/"
 * name="projectDir"/&gt;<br>
 * &lt;!-- url project-based theme-based = directory-based / parent-directory
 * based (recursively) --&gt;<br>
 * &lt;map:location src="{uri}" /&gt;<br>
 * &lt;/map:act&gt;
 * 
 */
public class RecursiveDirectoryTraversalDualAction extends AbstractTraversal
        implements ThreadSafe, Serviceable {

    HashMap map = new HashMap();
    private String firstMatchPattern;

    /**
     * Set the current <code>ComponentManager</code> instance used by this
     * <code>Composable</code>.
     * 
     * @throws ServiceException
     */

    public void service(ServiceManager manager) throws ServiceException {
        setResolver((SourceResolver) manager.lookup(SourceResolver.ROLE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.cocoon.acting.Action#act(org.apache.cocoon.environment.Redirector
     * , org.apache.cocoon.environment.SourceResolver, java.util.Map,
     * java.lang.String, org.apache.avalon.framework.parameters.Parameters)
     */
    public Map act(Redirector redirector,
            org.apache.cocoon.environment.SourceResolver resolver,
            Map objectModel, String source, Parameters parameters)
            throws Exception {
        this.prepare(parameters);
        map = new HashMap();
        Map returnMap = act();
        return returnMap;
    }

    /** Return a Map if Source 'uri' resolves and exists. */
    public Map act() {
        String uri = this.getProjectDir() + this.getRequest()
                + this.getProjectExtension();
        Source src = null;

        // The locationmap module will return null if there is no match for
        // the supplied hint, without the following the URI will be resolved to
        // the context root, which always exists, but does not contain a valid
        // resource.
        if (uri == null || uri == "") {
            return null;
        }

        try {
            int index = uri.lastIndexOf("/");
            int rest = getRequest().lastIndexOf("/");
            if (rest > -1){
                this.setRest(getFather());
            }
            this.firstMatchPattern = uri.substring(index + 1);
            this.computeResponseURIFirstMatch(uri, src);

            // src = resolver.resolveURI(uri);
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

    private void computeResponseURIFirstMatch(String uri, Source src)
            throws DispatcherException {
        try {
            src = resolver.resolveURI(uri);
            if (src.exists()) {
                this.map.put("uri", uri);
            } else {
                uri = replaceLast(uri);
                src = resolver.resolveURI(uri);
                if (src.exists()) {
                    this.map.put("uri", uri);
                    return;
                }
                if (this.getRest().lastIndexOf("/") > -1) {
                    this.setRest(getFather());
                    uri = this.getProjectDir() + this.getRest() + "/"
                            + this.firstMatchPattern;
                    this.computeResponseURIFirstMatch(uri, src);
                } 
            }
        } catch (MalformedURLException e) {
            throw new DispatcherException(e);
        } catch (IOException e) {
            throw new DispatcherException(e);
        } finally {
            release(src);
        }
    }

    private String replaceLast(String uri) {
        String newUri = this.getProjectDir();
        int index = uri.lastIndexOf("/");
        if (index > -1) {
            newUri =uri.substring(0, index)+ "/";
        }
        return  newUri + this.getProjectFallback() + this.getProjectExtension();
    }
}
