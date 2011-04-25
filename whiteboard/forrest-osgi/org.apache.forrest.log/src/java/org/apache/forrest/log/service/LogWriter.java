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

    if (level > -1 && level < LEVEL_STRING.length) {
      switch (level) {
      case LogService.LOG_ERROR:
        sLogger.error(entry.getMessage());
        break;
      case LogService.LOG_WARNING:
        sLogger.warn(entry.getMessage());
        break;
      case LogService.LOG_INFO:
        sLogger.info(entry.getMessage());
        break;
      case LogService.LOG_DEBUG:
        sLogger.debug(entry.getMessage());
        break;
      default:
        sLogger.warn("LogEntry with unknown log level: " + level);
      }
    }
  }

}

