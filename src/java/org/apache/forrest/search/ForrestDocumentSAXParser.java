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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * <p>Parses a Forrest Document and extracts the information to use when
 * generating Lucene indexes.</p>
 * <p>The parser scans the document searching for a number of tags. When a match
 * is found, it buffers all the text contained in the full subtree. When the parser
 * is buffering text, it ignores all tags and just keeps the text.</p>
 * <p>As an example consider the following document:</p>
 * <code>
 * <pre>
 * &lt;document&gt;
 * &lt;header&gt;
 * &lt;title&gt;The title&lt;/title&gt;
 * &lt;abstract&gt;An example&lt;/abstract&gt;
 * &lt;/header&gt;
 * &lt;body&gt;
 * &lt;section&gt;
 * &lt;title&gt;The Section&lt;/title&gt;
 * &lt;p&gt;Some text with &lt;strong&gt;embedded&lt;/strong&gt; tags&lt;/p&gt;
 * &lt;section&gt;
 * &lt;/body&gt;
 * &lt;/document&gt;
 * </pre>
 * </code>
 * <p>If the parser is applied to <code>body</code> the result will be
 * "The Section Some text with embedded tags". This permits the parser to generate
 * fields with the full content of the body, so it can be indexed and searched later.</p>
 * <p>If the parser now checks for <code>title</code> and <code>body</code> the
 * results will be "The title" for <code>title</code> and the same as above for <code>body</code>.
 * This demosntrates the parser is ignoring the <code>title</code> inside the
 * <code>body</code>, since while the parser is buffering <code>body</code> is
 * ignoring all the tags. This feature is useful to capture information inside
 * the header.</p>
 * <p>This is all what is needed to pass the information to Lucene, and by using this
 * algorithm the class gets quite simple.</p>
 *
 * <p><em>(Hope my English it's not too bad ;-)</em></p>
 *
 * @author Ramon Prades [RPR]
 * @version $Id: ForrestDocumentSAXParser.java,v 1.1 2003/10/20 09:50:56 nicolaken Exp $
 */

public class ForrestDocumentSAXParser extends DefaultHandler {

  // Parser configuration constants
  static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
  static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
  static final String EXTERNAL_DTD_FEATURE_ID = "http://apache.org/xml/features/nonvalidating/load-external-dtd";


  // List with the tags to capture
  static final String[] FORREST_HEADER_INDEXERS = {"title","abstract","body"};
  static String docAuthors = "";
  // Control variables
  XMLReader parser = null;
  HashMap results = null;
  String currentElement = "";
  StringBuffer textBuffer = new StringBuffer();
  Vector tags = null;
  boolean buffering = false;
  boolean isForrest = false;

  /**
   * Constructor. Initiliazes the parser.
   */
  public ForrestDocumentSAXParser() {
    super();
    // Load the list of interesting tags in a vector for later processing
    tags = new Vector();
    for (int i=0; i<FORREST_HEADER_INDEXERS.length; i++) {
      tags.add(FORREST_HEADER_INDEXERS[i]);
    }
    // Instantiate the SAX parser
    try {
      parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
      parser.setFeature(VALIDATION_FEATURE_ID, false);
      parser.setFeature(EXTERNAL_DTD_FEATURE_ID, false);
      parser.setContentHandler(this);
      parser.setErrorHandler(this);
    } catch (SAXException ex) {
      System.err.println("Error getting the parser (" + ex.getMessage() + ")");
    }
  } // Constructor

  /**
   * Gets a parser and parses the selected document
   * @param fileName Forrest document file name
   */
  public HashMap parseDocument(String fileName) throws SAXException {
    try {
      parser.parse(new InputSource(fileName));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return results;
  } // parseDocument

  /**
   * Gets a parser and parses the selected document
   * @param file Forrest document file
   */
  public HashMap parseDocument(File file) throws SAXException {
    try {
      parser.parse(new InputSource(new java.io.FileInputStream(file)));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return results;
  } // parseDocument

  /**
   * Gets the results
   * @return
   */
  public HashMap getResults() {
      return results;
  } // getResults

  /**
   * Triggered when a new document is about to be parsed
   */
  public void startDocument() {
    // Reset control variables
    textBuffer.setLength(0);
    results = new HashMap();
    isForrest = false;
    docAuthors = "";
  } // startDocument

  /**
   * Saves authors when document fully parsed
   */
  public void endDocument() {
    results.put("author", docAuthors);
  }

  /**
   * Triggered when a new element is about to be parsed
   */
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException  {
    // Check the new tag only when not buffering
    if (!buffering) {
      // Check the root element to see if the document is a Forrest one
      if (!isForrest && (!localName.equals("document"))) {
        results = null;
        // If not forrest, throw an exception to stop parsing (speed matters!)
        throw new SAXException("The document is not a Forrest document!");
      }
      // Is Forrest, so carry on processing
      isForrest = true;
      // Check "person". Here we want the attribute "@name"
      if (localName.equals("person")) {
        String separator = "";
        if (docAuthors!=null && docAuthors.length()>0) {
          separator = ";";
        }
        docAuthors += separator + attributes.getValue("name");
      } else if (tags.contains(localName)) {
        currentElement = localName;
        buffering = true;
      }
    }
  } // startElement

  /**
   * End of element detected. If the closing element is the one the parser is
   * bufferig, store the text, otherwise don't do anything
   */
  public void endElement(String uri, String localName, String qName) {
    if (buffering) {
     if (localName.equals(currentElement)) {
       buffering = false;
       results.put(currentElement, textBuffer.toString());
       textBuffer.setLength(0); // reset buffer
     } else {
       // add an extra space to avoid the following case:
       // <body>
       //   <section>
       //     <title>A title</title>
       //     <p>A paragraph</p>
       //   </section>
       // </body>
       // Unless an extra space is added the result would be: "A titleA paragraph"
       textBuffer.append(' ');
     }
    }
  } // endElement

  /**
   * Buffer the parsed character when "doCapture" tells so.
   */
  public void characters(char[] cbuf, int start, int len) {
    if (buffering) {
      textBuffer.append(cbuf, start, len);
    }
  } // characters

} // ForrestDocumentSAXParser
