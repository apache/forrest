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
import org.osgi.service.log.LogService;

import org.apache.forrest.log.LogPlugin.LOG;
import org.apache.forrest.plugin.api.ForrestResult;
import org.apache.forrest.plugin.api.ForrestSource;

/**
 * Base implementation for output plugins. Output plugins override
 * {@link #transform(ForrestSource)}.
 */
public class BaseOutputPlugin extends AbstractPlugin {

  /**
   * Constructs a <code>BaseInputPlugin</code>
   * with the given {@link BundleContext}.
   *
   * @param context this bundle's context within the framework
   */
  public BaseOutputPlugin(final BundleContext context) {
    super(context);
  }

  /**
   * Output plugins do not implement this method. The base
   * implementation returns null.
   *
   * @param uri the source <code>URI</code>
   * @return null
   */
  public final ForrestSource getSource(URI uri) {
    LOG.debug("getSource() called on an output plugin, ignoring");

    return null;
  }

  /**
   * Returns a <code>ForrestResult</code> object to access
   * the result of the transformation.
   * <p>
   * Output plugins must override this method.
   * <p>
   * The base implementation returns null.
   *
   * @param source the <code>ForrestSource</code> internal format
   * @return the <code>ForrestResult</code> transformation result
   */
  public ForrestResult transform(ForrestSource source) {
    LOG.debug("BaseOutputPlugin.transform() must be implemented by a plugin, ignoring");

    return null;
  }

}
