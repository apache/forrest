/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.forrest.search;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Description: </p>
 */

public class ForrestSearchRenderer {
  Transformer transformer = null;
  Transformer transformer2 = null;
  private String skinconf = "";
  private static final String doc2html = "document2html.xsl";
  private static final String site2xhtml = "site2xhtml.xsl";

  public ForrestSearchRenderer(String rootPath, String skin) {
    String fullPath = rootPath + "/skins/" + skin + "/xslt/html/";
    // Instantiate  a TransformerFactory.
    TransformerFactory tFactory = TransformerFactory.newInstance();
    try {
      skinconf = rootPath + "/skinconf.xml";
      transformer = tFactory.newTransformer
          (new javax.xml.transform.stream.StreamSource(fullPath + doc2html));
      transformer.setParameter("config-file", skinconf);
      transformer.setParameter("notoc", "true");
      transformer.setParameter("dynamic-page", "true");
      transformer2 = tFactory.newTransformer
          (new javax.xml.transform.stream.StreamSource(fullPath + site2xhtml));
      transformer2.setParameter("config-file", skinconf);
    } catch (TransformerConfigurationException ex) {
      System.err.println("Transformer Config exception");
    }
  } // Constructor

  public String render(Document dom) {
    String page = null;

    try {
      Document doc = new DocumentImpl();
      Element root = doc.createElement("site");
      DOMResult domResult = new DOMResult(root);
      transformer.transform(new DOMSource(dom.getDocumentElement()), domResult);

      OutputStream result = new ByteArrayOutputStream();
      javax.xml.transform.stream.StreamResult theResult = new javax.xml.transform.stream.StreamResult(result);

      transformer2.transform(new DOMSource(domResult.getNode()), theResult);
      page = result.toString();
    } catch (TransformerException ex) {
      ex.printStackTrace();
    }

    return page;
  } // render

}
