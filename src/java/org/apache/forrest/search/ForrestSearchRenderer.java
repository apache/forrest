/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache Forrest" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.forrest.search;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.dom.DocumentImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
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
