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
package org.apache.forrest.plugin.output.html.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import org.apache.forrest.log.LogPlugin.LOG;
import org.apache.forrest.plugin.api.BaseOutputPlugin;
import org.apache.forrest.plugin.api.ForrestResult;
import org.apache.forrest.plugin.api.ForrestSource;
import org.apache.forrest.plugin.api.ForrestStreamSource;

public class HtmlOutput extends BaseOutputPlugin {

  // private BundleContext mContext;

  public HtmlOutput(final BundleContext context) {
    super(context);
  }

  @Override
  public ForrestResult transform(ForrestSource source) {
    if (null == source) {
      throw new IllegalArgumentException("I told you, null won't work");
    }

    LOG.debug("Hello, this is the html output plugin handling transform(" + source + ")");

    try {
      ServiceReference[] refs = getBundleContext().getServiceReferences
        (TransformerFactory.class.getName(), null);
      TransformerFactory factory = null;

      if (null != refs) {
        // get first available service
        for (int i = 0; i < refs.length; i++) {
          Object obj = getBundleContext().getService(refs[i]);

          if (null != obj && obj instanceof TransformerFactory) {
            factory = (TransformerFactory) obj;
            break;
          } else {
            if (null != obj) {
              LOG.debug("Found the wrong class: " + obj.getClass().getName());
            } else {
              LOG.debug("Could not find TransformerFactory through service registry");
            }
          }
        }
      }

      if (null != factory) {
        InputStream in = HtmlOutput.class.getClassLoader().getResourceAsStream
          ("resources/stylesheets/internal-to-html.xsl");

        if (null != in) {
          LOG.debug("Found the output stylesheet");

          Transformer transformer = factory.newTransformer
            (new StreamSource(in));

          ByteArrayOutputStream bytes = new ByteArrayOutputStream();
          StreamResult internalStream = new StreamResult(bytes);

          /*
           * the source input stream is not needed here
           * because this transformation starts with
           * the internal format; the input stream would
           * lead to the raw source before conversion
           * to internal format
           */
          /*
          InputStream sourceIn = source.getInputStream();

          if (null == sourceIn) {
            System.out.println("ForrestSource.getInputStream() is null");
          }
          */

          transformer.transform
            (new StreamSource
             (new StringReader(source.getInternalRepresentationAsString())),
             internalStream);


          // build ForrestResult container and return it
          final ByteArrayOutputStream buf = bytes;

          ForrestResult forrestResult = new ForrestResult() {

              public String getResultAsString() {
                return buf.toString();
              }

            };

          try {
            in.close();
            // sourceIn.close();
          } catch (IOException ioe) {
            ; // failed to close stream
          }

          return forrestResult;
        } else {
          LOG.debug("Didn't find the stylesheet");
        }
      }
    } catch (InvalidSyntaxException ise) {
      LOG.debug("Check your filter string", ise);
    } catch (TransformerFactoryConfigurationError tfce) {
      LOG.debug("There is a problem at the factory", tfce);
    } catch (TransformerConfigurationException tce) {
      LOG.debug("The transformer could not be configured", tce);
    } catch (TransformerException te) {
      LOG.debug("The transformation broke", te);
    }

    return null;
  }

}
