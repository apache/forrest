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
import java.net.MalformedURLException;
import java.net.URL;

public class TestPostFile {
    public TestPostFile(String destinationUrl, String srcUrl) throws MalformedURLException, IOException{
        PostFile post = new PostFile(destinationUrl, new URL(srcUrl).openStream());
        System.out.println("body: "+post.getResponseBodyAsString());
    }
    public static void main (String[] args) throws MalformedURLException, IOException{
        TestPostFile test = new TestPostFile(args[0],args[1]);
    }
}
