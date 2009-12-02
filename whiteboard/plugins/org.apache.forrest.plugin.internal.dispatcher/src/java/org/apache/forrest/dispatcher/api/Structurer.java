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
package org.apache.forrest.dispatcher.api;

import java.io.InputStream;

import org.apache.forrest.dispatcher.exception.DispatcherException;

/**
 * Wrapper interface to enable multible structurer implementation.
 * The StAX only implementation and the AXIOM implementation are 
 * the first ones.
 * 
 * @version 1.0
 * 
 */
public interface Structurer {

  /**
   * Will process the incoming structurer for the format
   * requested. The incoming structurerStream will be closed 
   * by this method. Implementations has to close the incoming
   * stream before returning the returning stream to be conform 
   * with this api.
   * @param structurerStream the stream that contains the structurer
   * we need to process.
   * @param format the format we want to process. 
   * @return the result of the processing in the requested format.
   * @throws DispatcherException
   */
  InputStream execute(InputStream structurerStream,
      String format) throws DispatcherException;

}