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

import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Utility class to make Lucene Documents from Forrest Documents
 * @author Ramon Prades [RPR]
 * @version $Id: ForrestDocument.java,v 1.2 2003/09/13 01:21:39 cheche Exp $
 */
public class ForrestDocument {

  /**
   * Makes the Lucene document asking the parser to extract
   * the relevant information.
   */
  public static Document document(File file) {
    // Instantiate a parser for this file
    Document doc = null;
    ForrestDocumentSAXParser parser = new ForrestDocumentSAXParser();
    try {
      HashMap results = parser.parseDocument(file);
      doc = processInfo(file, results);
    }
    catch (Exception ex) {
      // Not a forrest doc
    }
    return doc;
  }

  /**
   * Process the results returned from the parser and creates the
   * Lucene document
   */
  private static Document processInfo(File file, HashMap results) {
    Document doc = new Document();
    // Get info
    String docTitle = (String) getFromResults("title", results);
    String docSummary = (String) getFromResults("abstract", results);
    String docAuthor = (String) getFromResults("author", results);
    String docContents = (String) getFromResults("body", results);
    // Index and store title and summary
    doc.add(Field.Text("title", docTitle));
    doc.add(Field.Text("summary", docSummary));
    doc.add(Field.Text("author", docAuthor));
    // Index but don't store contents
    doc.add(Field.UnStored("contents", docTitle + " " + docSummary + " " + docContents));
    return doc;
  } // document

  /*
   * Utility method to extract a key from a hashmap
   */
  private static Object getFromResults(String key, HashMap results) {
    if (results.containsKey(key)) {
      return results.get(key);
    } else {
      return "";
    }
  } // getFromResults  }

} // Class ForrestDocument
