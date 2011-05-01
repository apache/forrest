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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
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
import org.apache.forrest.plugin.api.ForrestPlugin;
import org.apache.forrest.plugin.api.ForrestResult;
import org.apache.forrest.util.ContentType;

public class LogServlet extends HttpServlet {

  private static final long serialVersionUID = 3575916939233594893L;

  private BundleContext mBundleContext;
  private ServiceTracker mTracker;

  public LogServlet(final BundleContext context) {
    mBundleContext = context;
    mTracker = new ServiceTracker(mBundleContext,
                                  LogReaderService.class.getName(),
                                  null);
    mTracker.open();
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

    LogReaderService service = (LogReaderService) mTracker.getService();

    if (null != service) {
      Enumeration<?> logEntries = service.getLog();

      if (null != logEntries) {
        while (logEntries.hasMoreElements()) {
          LogEntry entry = (LogEntry) logEntries.nextElement();
          Bundle bundle = entry.getBundle();
          String symName = "";
          String version = "";

          if (null != bundle) {
            symName = bundle.getSymbolicName();
            version = bundle.getVersion().toString();
          }

          StringBuilder msg = new StringBuilder();
          msg.append(logLevelToString(entry.getLevel()))
            .append(": ")
            .append(symName)
            .append(" (")
            .append(version)
            .append(") ")
            .append(entry.getMessage());

          writer.println(msg.toString());
        }
      }
    }
  }

  private String logLevelToString(int level) {
    switch (level) {
    case LogService.LOG_ERROR:
      return "ERROR";
    case LogService.LOG_WARNING:
      return "WARN";
    case LogService.LOG_INFO:
      return "INFO";
    case LogService.LOG_DEBUG:
      return "DEBUG";
    default:
      return "UNKNOWN";
    }
  }

  @Override
  public void destroy() {
    mTracker.close();
    mBundleContext = null;
  }

}
