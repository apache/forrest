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
import java.io.IOException;
import java.util.Date;

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
 * @version CVS $Id: ForrestIndexer.java,v 1.2 2004/02/19 23:39:51 nicolaken Exp $
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
