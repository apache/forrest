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

import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

/**
 * <p>Searches the index for a given query string.</p>
 * @author Ramon Prades [RPR]
 * @version $Id: ForrestSearcher.java,v 1.2 2004/02/19 23:39:51 nicolaken Exp $
 */
public class ForrestSearcher {
  public ForrestSearcher() {
  }

  /**
   * Searches "queryString" in "indexDir" and returns a Forrest Document (v1.2)
   * with the list of matches.
   * @param indexDir Directory with the Lucene index
   * @param queryString String to search
   * @return Forrest document
   */
  public Document search(String indexDir, String queryString) {
    // Create a Forrest document with the results
    DOMImplementation domImpl = new org.apache.xerces.dom.DOMImplementationImpl();
    DocumentType docType =
        domImpl.createDocumentType("document", "-//APACHE//DTD Documentation V1.1//EN", "document-v12.dtd");
    Document doc = domImpl.createDocument("", "document", docType);
    Element rootNode = doc.getDocumentElement();
    Element headerNode = doc.createElement("header");
    headerNode.appendChild(this.makeElement(doc, "title", "Search Results"));
    rootNode.appendChild(headerNode);
    Element bodyNode = doc.createElement("body");
    rootNode.appendChild(bodyNode);

    // Element sectionNode = doc.createElement("section");
    // bodyNode.appendChild(sectionNode);
    // sectionNode.appendChild(makeElement(doc, "title", "List of Matches"));

    IndexSearcher searcher = null;
    try {
      searcher = new IndexSearcher(indexDir);
    } catch (IOException ex) {
      System.err.println("Error: Index dir not found!");
      ex.printStackTrace();
    }
    Hits hits = null;
    int count = 0;
    if (queryString==null || queryString.length()==0) {
      Element pNode = doc.createElement("p");
      String txt = "Please enter a valid query";
      pNode.appendChild(doc.createTextNode(txt));
      bodyNode.appendChild(pNode);
    } else {
      Query query = null;
      try {
        query = QueryParser.parse(queryString, "contents", new StandardAnalyzer());
      } catch (ParseException ex3) {
        System.out.println("QueryParser error!");
        ex3.printStackTrace();
      }
      try {
        hits = searcher.search(query);
      } catch (IOException ex1) {
        System.err.println("Error in search");
        ex1.printStackTrace();
      }

      // Build the section with the list of matches
      count = hits.length();
      Element pNode = doc.createElement("p");
      String txt = "";
      if (count == 0) {
        txt = "No documents found matching: ";
        pNode.appendChild(doc.createTextNode(txt));
        Element emNode = doc.createElement("em");
        pNode.appendChild(emNode);
        emNode.appendChild(doc.createTextNode(queryString));
        bodyNode.appendChild(pNode);
      } else {
        if (count == 1) {
          txt = count + " document found matching: ";
        } else {
          txt = count + " documents found matching: ";
        }
        pNode.appendChild(doc.createTextNode(txt));
        Element emNode = doc.createElement("em");
        pNode.appendChild(emNode);
        emNode.appendChild(doc.createTextNode(queryString));
        //pNode.appendChild(doc.createElement("em").appendChild(doc.createTextNode(queryString)));
        bodyNode.appendChild(pNode);
        Element listNode = doc.createElement("ul");
        // sectionNode.appendChild(listNode);
        bodyNode.appendChild(listNode);

        for (int i = 0; i < count; i++) {
          try {
            String title = hits.doc(i).get("title");
            String summary = hits.doc(i).get("summary");
            String authors = hits.doc(i).get("author");
            String path = StringUtils.replace(hits.doc(i).get("path"),".xml", ".html");
            float score = hits.score(i);
            Date modified = new Date(new Long(hits.doc(i).get("modified")).
                                     longValue());
            java.text.DateFormat formatter = new java.text.SimpleDateFormat();
            String strModified = formatter.format(modified);

            Element listItem = doc.createElement("li");
            listNode.appendChild(listItem);
            Element strongNode = doc.createElement("strong");
            listItem.appendChild(strongNode);
            Element linkNode = doc.createElement("link");
            linkNode.setAttribute("href", path);
            linkNode.appendChild(doc.createTextNode(title));
            strongNode.appendChild(linkNode);

            String scoreText = " [" + score + "]";
            listItem.appendChild(doc.createTextNode(scoreText));
            listItem.appendChild(doc.createElement("br"));

            if (summary != null && summary.length() > 0) {
              listItem.appendChild(doc.createTextNode(summary));
              listItem.appendChild(doc.createElement("br"));
            }
            Element lastLine = doc.createElement("em");
            listItem.appendChild(lastLine);
            lastLine.appendChild(doc.createTextNode("url: " + path));
            if (authors != null && authors.length() > 0) {
              lastLine.appendChild(doc.createTextNode(" - author: " + authors));
            }
            lastLine.appendChild(doc.createTextNode(" - last modified: " +
                strModified));
            listItem.appendChild(doc.createElement("br"));
            listItem.appendChild(doc.createElement("br"));

          } catch (DOMException ex2) {
            System.err.println("DOM Error building results document (" +
                               ex2.getMessage() + ")");
          } catch (IOException ex2) {
            System.err.println("IO Error building results document (" +
                               ex2.getMessage() + ")");
          } catch (NumberFormatException ex2) {
            System.err.println("NUMBERFORMAT Error building results document (" +
                               ex2.getMessage() + ")");
          }
        } // for
      } // if (count==0) ...
      } // if queryString not null
    return doc;
  } // search

  /*
   * Utility method to contruct a DOM element with no attributes and
   * ine text child
   */
  private Element makeElement(Document doc, String name, String text) {
    Element e = doc.createElement(name);
    e.appendChild(doc.createTextNode(text));
    return e;
  }
} // ForrestSearcher
