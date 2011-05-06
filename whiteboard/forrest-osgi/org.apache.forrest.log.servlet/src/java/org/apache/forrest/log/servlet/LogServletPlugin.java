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
package org.apache.forrest.log.servlet;

import javax.servlet.ServletException;

import org.apache.forrest.log.LogPlugin.LOG;
import org.apache.forrest.log.servlet.service.LogBuffer;
import org.apache.forrest.log.servlet.service.LogServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.log.LogReaderService;
import org.osgi.util.tracker.ServiceTracker;

public class LogServletPlugin implements BundleActivator {

  private static final String SERVLET_ALIAS = "/logs";

  private ServiceTracker mHttpTracker;
  private LogServlet mServlet;

  public void start(final BundleContext context) throws Exception {
    LOG.debug("Log Servlet plugin starting");

    // register LogListener implemented by LogBuffer
    ServiceReference readerRef = context.getServiceReference
      (LogReaderService.class.getName());

    if (null != readerRef) {
      System.out.println("Registering LogBuffer");
      LogReaderService readerService = (LogReaderService) context.getService(readerRef);
      readerService.addLogListener(new LogBuffer());
      // LOG.debug("Log bundle starting (and self-hosting)");
    } else {
      System.out.println("Could not add log listener (LogReaderService is unavailable)");
    }

    // track OSGi HTTP service
    mHttpTracker = new ServiceTracker(context, HttpService.class.getName(), null);
    mHttpTracker.open();

    // create servlet instance
    mServlet = new LogServlet(context);

    /*
     * ServiceTracker.waitForService(long timeout) is not supposed
     * to be called from BundleActivator methods because activator
     * methods are expected to return quickly. The point of this
     * activator is to register the servlet with the HTTP service,
     * so I'm willing to wait.
     */
    try {
      mHttpTracker.waitForService(1000);
      HttpService service = (HttpService) mHttpTracker.getService();

      if (null != service) {
        try {
          service.registerServlet(SERVLET_ALIAS, mServlet, null, null);
        } catch (ServletException e) {
          e.printStackTrace();
        } catch (NamespaceException e) {
          e.printStackTrace();
        }
      }
    } catch (InterruptedException ie) {
      ;
    }
  }

  public void stop(BundleContext context) throws Exception {
    LOG.debug("Log Servlet plugin stopping");

    /*
     * Clean up our servlet registration
     * Without this, the framework would still unregister for us
     * but this is the only way to have destroy() called on our
     * servlet
     */
    HttpService service = (HttpService) mHttpTracker.getService();

    if (null != service) {
      service.unregister(SERVLET_ALIAS);
    }

    mHttpTracker.close();
  }

}
