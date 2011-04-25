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
package org.apache.forrest.plugin.output.html;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.apache.forrest.plugin.api.ForrestPlugin;
import org.apache.forrest.plugin.output.html.service.HtmlOutput;

public class HtmlOutputPlugin implements BundleActivator {

  public void start(final BundleContext context) throws Exception {
    System.out.println("HTML bundle starting");

    Properties props = new Properties();
    props.put("pluginType", "output");
    props.put("contentType", "text/html");
    context.registerService(ForrestPlugin.class.getName(), new HtmlOutput(context), props);
  }

  public void stop(BundleContext context) throws Exception {
    System.out.println("HTML bundle stopping");
  }

}
