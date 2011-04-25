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
package org.apache.forrest.plugin.output.pdf.service;

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

import org.apache.forrest.plugin.api.BaseOutputPlugin;
import org.apache.forrest.plugin.api.ForrestResult;
import org.apache.forrest.plugin.api.ForrestSource;
import org.apache.forrest.plugin.api.ForrestStreamSource;

public class PdfOutput extends BaseOutputPlugin {

  public PdfOutput(final BundleContext context) {
    super(context);
  }

  @Override
  public ForrestResult transform(ForrestSource source) {
    if (null == source) {
      throw new IllegalArgumentException("I told you, null won't work");
    }

    System.out.println("Hello, this is the pdf output plugin handling transform(" + source + ")");

    try {
      ServiceReference[] refs = getBundleContext().getServiceReferences(TransformerFactory.class.getName(), null);
      TransformerFactory factory = null;

      if (null != refs) {
        // get first available service
        for (int i = 0; i < refs.length; i++) {
          Object obj = getBundleContext().getService(refs[i]);

          if (null != obj && obj instanceof TransformerFactory) {
            System.out.println("Found the right class: " + obj.getClass().getName());
            factory = (TransformerFactory) obj;
            break;
          } else {
            if (null != obj) {
              System.out.println("Found the wrong class: " + obj.getClass().getName());
            } else {
              System.out.println("Null service");
            }
          }
        }
      }

      if (null != factory) {
        System.out.println("factory " + factory.getClass().getName());

        InputStream in = PdfOutput.class.getClassLoader().getResourceAsStream
          ("resources/stylesheets/internal-to-fo.xsl");

        if (null != in) {
          System.out.println("Found the output stylesheet");

          Transformer transformer = factory.newTransformer
            (new StreamSource(in));

          if (null != transformer) {
            System.out.println("transformer " + transformer.getClass().getName());
          }

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
          System.out.println("Didn't find the stylesheet");
        }
      }
    } catch (InvalidSyntaxException ise) {
      System.out.println("Check your filter: " + ise);
    } catch (TransformerFactoryConfigurationError tfce) {
      System.out.println("There is a problem at the factory: " + tfce);
    } catch (TransformerConfigurationException tce) {
      System.out.println(tce);
    } catch (TransformerException te) {
      System.out.println("That didn't work: " + te);
    }

    return null;
  }

}
