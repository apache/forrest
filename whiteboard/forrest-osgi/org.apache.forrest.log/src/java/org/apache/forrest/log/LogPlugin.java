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
package org.apache.forrest.log;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import org.apache.forrest.log.service.LogWriter;

public class LogPlugin implements BundleActivator {

  private static LogPlugin sInstance = null;

  private ServiceTracker mLogTracker;

  public void start(final BundleContext context) throws Exception {
    sInstance = this;
    mLogTracker = new ServiceTracker(context, LogService.class.getName(), null);
    mLogTracker.open();

    ServiceReference readerRef = context.getServiceReference(LogReaderService.class.getName());

    if (null != readerRef) {
      System.out.println("Registering LogWriter");
      LogReaderService readerService = (LogReaderService) context.getService(readerRef);
      readerService.addLogListener(new LogWriter());
      getDefault().getLogService().log(LogService.LOG_DEBUG, "Log bundle starting (and self-hosting)");
    } else {
      System.out.println("Could not add log listener (LogReaderService is unavailable)");
    }
  }

  public void stop(BundleContext context) throws Exception {
    sInstance = null;
    mLogTracker.close();
    System.out.println("Log bundle stopping");
  }

  public static LogPlugin getDefault() {
    return sInstance;
  }

  public LogService getLogService() {
    LogService theServ = (LogService) mLogTracker.getService();

    return theServ;
  }

}
