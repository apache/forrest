/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.dispatcher.impl.helper;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;

public class LoggingErrorListener implements ErrorListener {

private Log logger;
  
  public LoggingErrorListener(Log logger) {
    this.logger = logger;
  }
  
  public void warning(TransformerException exception) {
 // Don't throw an exception and stop the processor
    // just for a warning; but do log the problem
    logger.warn(exception.getMessage(), exception);
    
  }
  
  public void error(TransformerException exception)
   throws TransformerException {
    // XSLT is not as draconian as XML. There are numerous errors
    // which the processor may but does not have to recover from; 
    // e.g. multiple templates that match a node with the same
    // priority. I do not want to allow that so I throw this 
    // exception here.
    logger.error(exception.getMessage(), exception);
  
    throw exception;
    
  }
  
  public void fatalError(TransformerException exception)
   throws TransformerException {
 // This is an error which the processor cannot recover from; 
    // e.g. a malformed stylesheet or input document
    // so I must throw this exception here.
    logger.fatal(exception.getMessage(), exception);
    throw exception;
  }


}
