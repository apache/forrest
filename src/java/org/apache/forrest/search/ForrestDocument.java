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

import java.io.File;

import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Utility class to make Lucene Documents from Forrest Documents
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
