/*
 * Copyright 1999-2005 The Apache Software Foundation or its licensors,
 * as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.actions;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.acting.ServiceableAction;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Redirector;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;
import org.apache.cocoon.environment.SourceResolver;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * An action that writes a given document to the local build space.
 * 
 * Returns:
 * message - a human readable message indicating if everything was succesful.
 */
public class WriteToLocalBuildAction extends ServiceableAction implements ThreadSafe {
    Request request;
            
    public String writeFile(String stagingDir) {
      String url = request.getParameter("URI");
      String stagedFile = stagingDir + "/" + url;
          
      HttpClient client = new HttpClient();
      GetMethod get = new GetMethod("http://" + request.getServerName() + ":" + request.getServerPort() +"/" + url);
      
      try {
        int statusCode = client.executeMethod(get);
        
        if (statusCode != HttpStatus.SC_OK) {
          return "Failed to get document: " + get.getStatusLine();
        }
        
        byte[] responseBody = get.getResponseBody();
        
        File file = new File(stagedFile);
        FileWriter out = new FileWriter(file);
        out.write(new String(responseBody));
        out.close();
      } catch (Exception e) {
        return e.getMessage();
      } finally {
        get.releaseConnection();
      }
      
      return "File written";
    }
    
    public Map act (Redirector redirector, SourceResolver resolver, Map objectModel, String src, Parameters par) throws Exception {      
        Map response = new HashMap();
        String stagingDir = par.getParameter("staging-dir", "");
        
        request = ObjectModelHelper.getRequest(objectModel);
        response.put("message", writeFile(stagingDir));
        response.put("staging.dir", stagingDir);

        return response;
    }
}
