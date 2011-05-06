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
package org.apache.forrest.log.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.osgi.framework.Bundle;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogWriter implements LogListener {

  private static final String[] LEVEL_STRING = { "", "ERROR", "WARNING", "INFO", "DEBUG" };

  private static final Logger sLogger = LoggerFactory.getLogger(LogWriter.class);

  // @Override
  public void logged(LogEntry entry) {
    int level = entry.getLevel();
    Bundle bundle = entry.getBundle();
    String symName = "";
    String version = "";

    if (null != bundle) {
      symName = bundle.getSymbolicName();
      version = bundle.getVersion().toString();
    }

    // default timezone and locale
    Date entryTimeStamp = new Date(entry.getTime());

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss a z");

    StringBuilder msg = new StringBuilder();
    msg.append(dateFormat.format(entryTimeStamp))
      .append(" ")
      // .append(logLevelToString(entry.getLevel()))
      // .append(" ")
      .append(symName)
      .append(" (")
      .append(version)
      .append(") ")
      .append(entry.getMessage());

    if (level > -1 && level < LEVEL_STRING.length) {
      switch (level) {
      case LogService.LOG_ERROR:
        sLogger.error(msg.toString());
        break;
      case LogService.LOG_WARNING:
        sLogger.warn(msg.toString());
        break;
      case LogService.LOG_INFO:
        sLogger.info(msg.toString());
        break;
      case LogService.LOG_DEBUG:
        sLogger.debug(msg.toString());
        break;
      default:
        sLogger.warn("LogEntry with unknown log level: " + level);
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

}
