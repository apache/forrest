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
import java.io.IOException;

import java.util.Date;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;

import org.apache.lucene.util.Arrays;

/**
 * <p>Indexes all xml forrest documents below a given directory.</p>
 * <p>Parametres:</p>
 * <ul>
 * <li>
 * <strong><code>-index index_directory</code></strong> Directory where
 * the index is to be created
 * </li>
 * <li>
 * <strong><code>root_directory</code></strong> forrest 'xdocs' directory
 * </li>
 * </ul>
 * <h3>Current Limitations/todo</h3>
 * <ul>
 * <li>This version indexes Forrest XML documents only. Would be nice if PDF and
 * HTML could be added.</li>
 * <li>FAQ and TODO aren't indexed. Add support to that.</li>
 * <li>Full index created every time. Create sort of incremental indexing.</li>
 * <li>Could be a good idea to create a list of "reserved" filenames (i.e. book.xml
 * or status.xml) and force the indexer to skip them.</li>
 *
 * @author Ramon Prades [RPR]
 * @version CVS $Id: ForrestIndexer.java,v 1.2 2003/09/13 01:21:39 cheche Exp $
 */
public class ForrestIndexer {

  // Info about the class itself
  private static final String VERSION = "Version 0.21 (2003-08-08)";
  private static final String DIVIDER =
      "==============================================================================";
  private static final String BANNER = "ForrestIndexer (Powered by Lucene) " + VERSION;
  private static final String COPYRIGHT =
      "Copyright (c) 2001, 2003 The Apache Software Foundation.  All rights reserved.";
  private static final String USAGE =
      "ForrestIndexer [-index <index_directory>] <root_directory>";

  // Some vars
  private static IndexReader reader; // Existing index
  private static IndexWriter writer; // New index being built
  private static String rootPath = "";

  /**
   * Main method. See parametres at class javadoc.
   */
  public static void main(String[] argv) {
    try {
      String index = "";
      boolean create = true;
      File root = null;
      if (argv.length == 0) {
        System.err.println("Usage: " + USAGE);
        return;
      }

      // Get parametres from args
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("-index")) { // parse -index option
          index = argv[++i];
        } else if (i != argv.length - 1) {
          System.err.println("Usage: " + USAGE);
          return;
        } else {
          root = new File(argv[i]);
        }
      }
      // Debugging
      // index = "C:/dev/uimlsite/build/webapp/index";
      // root = new File("C:/dev/uimlsite/src/documentation/content/xdocs");

      // Print banner
      System.out.println(DIVIDER);
      System.out.println(BANNER);
      System.out.println(COPYRIGHT);
      System.out.println(DIVIDER);
      System.out.println("");
      rootPath = root.getPath().trim();
      System.out.println("Source Directory: " + rootPath);
      System.out.println("Index Directory: " + index);
      System.out.println("");

      Date start = new Date();
      writer = new IndexWriter(index, new StandardAnalyzer(), create);
      writer.maxFieldLength = 1000000;
      indexDocs(root); // add new docs
      System.out.print("Index created! - Total milliseconds ");
      System.out.println(new Date().getTime() - start.getTime());
      System.out.println("");

      System.out.println("Optimizing index...");
      writer.optimize();
      writer.close();
      System.out.print("Index optimized! - Total milliseconds ");
      System.out.println(new Date().getTime() - start.getTime());
    } catch (Exception e) {
      System.err.println(" Exception in " + e.getClass() +
                         "\n with message: " + e.getMessage());
      e.printStackTrace();
    }
  } // main

  /*
   * Create the index
   */
  private static void indexDocs(File file) {
    if (file.isDirectory()) { // if a directory
      String[] files = file.list(); // list its files
      Arrays.sort(files); // sort the files
      for (int i = 0; i < files.length; i++) { // recursively index them
        indexDocs(new File(file, files[i]));
      }
    } else if (file.getPath().endsWith(".xml")) { // index .txt files
      String filePath = getRelativePath(file.getPath(), rootPath);
      System.out.print("Indexing ... " + filePath);
      Document doc = ForrestDocument.document(file);
      if (doc == null) {
        System.out.println(" [Ignored]");
      } else {
        try {
          // Add last modified and path
          doc.add(Field.Keyword("modified", new Long(file.lastModified()).toString()));
          doc.add(Field.Keyword("path", filePath));
          writer.addDocument(doc); // add docs unconditionally
        } catch (IOException ex) {
          System.out.println(" [Error: " + ex.getMessage() + "]");
        }
        System.out.println(" [Done]");
      }
    }
  } // indexDocs

  /*
   * Utility method to calculate the relative path of a file
   */
  private static String getRelativePath(String filePath, String rootPath) {
    return filePath.substring(rootPath.length()+1);
  } // getRelativePath
} // Class ForrestLuceneIndexer
