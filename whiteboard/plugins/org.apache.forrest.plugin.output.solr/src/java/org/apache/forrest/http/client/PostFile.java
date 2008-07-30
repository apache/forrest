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
package org.apache.forrest.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class PostFile {
    /**
     * The AGENT name
     */
    public static final String AGENT = "forrest[" + PostFile.class.getName() + "]";

    private String destinationUrl, srcUrl;

    private HttpClient client;

    private PostMethod filePost;

    private int statusCode;

    /**
     * @param destinationUrl -
     *            the url of the server listener (e.g. servlet)
     * @param srcUrl -
     *            the src url of the file to post
     * @throws MalformedURLException
     * @throws IOException
     */
    public PostFile(String destinationUrl, InputStream srcStream)
            throws MalformedURLException, IOException {
        this.destinationUrl = destinationUrl;
        client = new HttpClient();
        filePost = prepareFilePost(destinationUrl, srcStream);
        statusCode = client.executeMethod(filePost);
    }

    public void post(String destinationUrl, String srcUrl)
            throws MalformedURLException, IOException {
        this.destinationUrl = destinationUrl;
        this.srcUrl = srcUrl;
        client = new HttpClient();
        filePost = prepareFilePost(destinationUrl, srcUrl);
        statusCode = client.executeMethod(filePost);
    }
    
    public void releaseConnection(){
      if (null!=filePost){
        filePost.releaseConnection();
      }
    }

    public int statusCode() {
        return statusCode;
    }

    public String getResponseCharSet() {
        return filePost.getResponseCharSet();
    }

    public String getResponseBodyAsString() {
        return filePost.getResponseBodyAsString();
    }

    public InputStream getResponseBodyAsStream() throws MalformedURLException,
            IOException {
        return filePost.getResponseBodyAsStream();
    }

    private PostMethod prepareFilePost(String solrBase, String srcUrl)
            throws IOException, MalformedURLException {
        PostMethod filePost = new PostMethod(solrBase);
        filePost.addRequestHeader("Content-type", "text/xml; charset=utf-8");
        filePost.addRequestHeader("User-Agent", AGENT);
        filePost.setRequestBody(new URL(srcUrl).openStream());
        return filePost;
    }

    private PostMethod prepareFilePost(String solrBase, InputStream src)
            throws IOException, MalformedURLException {
        PostMethod filePost = new PostMethod(solrBase);
        filePost.addRequestHeader("Content-type", "text/xml; charset=utf-8");
        filePost.addRequestHeader("User-Agent", AGENT);
        filePost.setRequestBody(src);
        return filePost;
    }

    public String getsolrBase() {
        return destinationUrl;
    }

    public String getSrc() {
        return srcUrl;
    }

}
