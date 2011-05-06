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
package org.apache.forrest.http;

import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.forrest.log.LogPlugin.LOG;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Registers a servlet with the OSGi HTTP Service with alias
 * <code>ForrestServletPlugin.SERVLET_ALIAS</code>.
 */
public class ForrestServletPlugin implements BundleActivator {

  /**
   * Alias with which to register servlet.
   * <p>
   * A servlet alias of <code>/foo</code> will be accessible as
   * <code>http://localhost:8080/foo</code>.
   * @see HttpService#registerServlet(String, Servlet, Dictionary, HttpContext)
   */
  private static final String SERVLET_ALIAS = "/";

  /**
   * {@link ServiceTracker} to track the {@link HttpService}.
   */
  private ServiceTracker mHttpTracker;

  /**
   * Main servlet instance.
   */
  private ForrestServlet mServlet;

  /**
   * Starts tracking the {@link HttpService} and registers the servlet
   * instance.
   * @param context this bundle's context within the framework
   */
  // @Override
  public void start(final BundleContext context) throws Exception {
    LOG.debug("Servlet plugin starting");

    // track OSGi HTTP service
    mHttpTracker = new ServiceTracker(context, HttpService.class.getName(), null);
    mHttpTracker.open();

    // create servlet instance
    mServlet = new ForrestServlet(context);

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

  /**
   * Unregisters the servlet so that destroy is called on it. The
   * framework would still unregister the servlet without this call,
   * but then destroy would not be called on it.
   *
   * @param context this bundle's context within the framework
   *
   * @see HttpService#unregister(String)
   * @see javax.servlet.Servlet#destroy()
   */
  // @Override
  public void stop(BundleContext context) throws Exception {
    LOG.debug("Servlet plugin stopping");

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
