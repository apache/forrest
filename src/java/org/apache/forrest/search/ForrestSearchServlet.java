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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

/**
 * <p>This sevlet processes all search request inside a Forrest site.</p>
 * @author Ramon Prades [RPR]
 * @version $Id: ForrestSearchServlet.java,v 1.2 2004/02/19 23:39:51 nicolaken Exp $
 */
public class ForrestSearchServlet extends HttpServlet {

  private static final String CONTENT_TYPE = "text/html";
  private ForrestSearcher searcher = null;
  private static ForrestSearchRenderer renderer = null;
  private String servletPath = "";
  private String indexDir = "";     // Full path to lucene index directory
  private String skin = "";         // Skin configured
  private String searchPage = "/search.html";
  private static StringBuffer cache = null;


  /**
   * Prepares the servlet
   * @throws ServletException
   */
  public void init() throws ServletException {
    servletPath = this.getServletContext().getRealPath("");
    // FIXME: indexDir is hardcoded
    indexDir = servletPath + "/lucene-index";
    searcher = new ForrestSearcher();
    String skin = this.getInitParameter("project-skin");
    renderer = new ForrestSearchRenderer(servletPath, skin);
 } // init

  /**
   * Process the HTTP Get request
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html");
    PrintWriter out = response.getWriter();
    // Query string should be in parametre "query".
    // A valid forrest document is returned.
    String query = request.getParameter("query");
    // Render the resulting document. Ideally the document
    // should be passed to Cocoon, but for the time being
    // use the renderer
    Document doc = searcher.search(indexDir, query);
    String page = renderer.render(doc);
    out.print(page);
  }

  //Clean up resources
  public void destroy() {
  }

} // ForrestSearchServlet
