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
package org.apache.forrest.plugin.api;

import java.net.URI;

import org.osgi.framework.BundleContext;

/**
 * An abstract base class for plugins.
 * <p>
 * This class stores the {@link BundleContext} as a convenience to
 * subclasses.
 *
 * @see #getBundleContext()
 * @see ForrestPlugin
 * @see BaseInputPlugin
 * @see BaseOutputPlugin
 */
public abstract class AbstractPlugin implements ForrestPlugin {

  private BundleContext mContext;

  public AbstractPlugin(final BundleContext context) {
    mContext = context;
  }

  protected final BundleContext getBundleContext() {
    return mContext;
  }

  public abstract ForrestSource getSource(URI uri);

  public abstract ForrestResult transform(ForrestSource source);

}
