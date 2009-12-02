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
package org.apache.forrest.dispatcher.exception;

/**
 * Dispatcher Exception
 */
public class DispatcherException extends Exception {
    public static final String ERROR_404 =  "404 - Source not found";
    public static final String ERROR_500 =  "500 - Internal server error";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static final String DISPATCHER_ERROR =  "dispatcherError: ";
    public static final String DISPATCHER_STACK = "\n\ndispatcherStack:\n";

    /**
     * Creates a new DispatcherException.
     */
    public DispatcherException() {
        super();
    }

    /**
     * Creates a new DispatcherException.
     * @param message the exception message
     */
    public DispatcherException(String message) {
        super(message);
    }

    /**
     * Creates a new DispatcherException.
     * @param message the exception message
     * @param cause the cause of the exception
     */
    public DispatcherException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new DispatcherException.
     * @param cause the cause of the exception
     */
    public DispatcherException(Throwable cause) {
        super(cause);
    }

}
