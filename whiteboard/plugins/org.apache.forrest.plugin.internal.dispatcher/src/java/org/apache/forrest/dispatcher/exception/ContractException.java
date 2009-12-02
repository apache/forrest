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
package org.apache.forrest.dispatcher.exception;

public class ContractException extends DispatcherException {

  private static final long serialVersionUID = -4554841210715375589L;

  public ContractException(String message) {
    super(message);
  }

  public ContractException(Throwable cause) {
    super(cause);
  }

  /**
   * Create a message header for a given contract
   */
  public ContractException(String name, String message, Throwable cause) {
    super(DISPATCHER_ERROR + ERROR_500 + "\nContract \"" + name
        + "\" has produced an exception.\n " + message
        + DispatcherException.DISPATCHER_STACK + cause);
  }
}
