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
package org.apache.forrest.log.servlet.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import org.apache.forrest.log.LogPlugin.LOG;
import org.apache.forrest.util.ContentType;

public class LogServlet extends HttpServlet {

  private static final long serialVersionUID = 3575916939233594893L;

  private BundleContext mBundleContext;

  public LogServlet(final BundleContext context) {
    mBundleContext = context;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    resp.setContentType("text/plain");
    printLatestEntries(resp.getWriter());
  }

  private void printLatestEntries(PrintWriter writer) {
    if (null == writer) {
      throw new IllegalArgumentException("null writer in printLatestEntries()");
    }

    if (!LogBuffer.sLogBuffer.isEmpty()) {
      Iterator<String> iterator = LogBuffer.sLogBuffer.iterator();

      while (iterator.hasNext()) {
        writer.println(iterator.next());
      }
    } else {
      writer.println("no log entries");
    }
  }

  @Override
  public void destroy() {
    mBundleContext = null;
  }

}
