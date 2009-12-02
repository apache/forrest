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
package org.apache.forrest.dispatcher.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;
import org.apache.forrest.dispatcher.api.Resolver;
import org.apache.forrest.dispatcher.exception.DispatcherException;

public class CocoonResolver implements Resolver {

  private final SourceResolver resolver;

  public CocoonResolver(final SourceResolver m_resolver) {
    resolver = m_resolver;
  }

  public InputStream resolve(final String uri) throws DispatcherException {
    InputStream stream = null;
    Source source = null;
    try {
      source = resolver.resolveURI(uri);
      stream = new BufferedInputStream(source.getInputStream());
    } catch (final Exception e) {
      throw new DispatcherException(e);
    } finally {
      if (null != source) {
        resolver.release(source);
      }
    }
    return stream;
  }

}
