/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/ant/doc/Vizant.java,v 1.1 2004/02/09 11:09:21 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:21 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Alexandria", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.forrest.forrestdoc.ant.doc;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Vizant task.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class Vizant extends Task {
    private File antfile;
    private File outfile;
    private VizProjectLoader loader;
    private VizPrinter printer;
    private VizWriter writer;

    public void init() {
        loader = getLoader();
        printer = getPrinter();
    }

    public void setAntfile(File antfile) throws BuildException {
        this.antfile = antfile;
        try {
            loader.setInputStream(new FileInputStream(antfile));
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        }
    }

    public void setOutfile(File outfile) {
        this.outfile = outfile;
    }

    public void setGraphid(String graphid) {
        printer.setGraphid(graphid);
    }

    public void setFrom(String targetName) {
        printer.setFrom(targetName);
    }

    public void setTo(String targetName) {
        printer.setTo(targetName);
    }

    public void setNocluster(boolean noclustor) {
        printer.setNocluster(noclustor);
    }

    public void setUniqueref(boolean uniqueref) {
        loader.uniqueRef(uniqueref);
    }

    public void setIgnoreant(boolean opt) {
        loader.ignoreAnt(opt);
    }

    public void setIgnoreantcall(boolean opt) {
        loader.ignoreAntcall(opt);
    }

    public void setIgnoredepends(boolean opt) {
        loader.ignoreDepends(opt);
    }

    public void addConfiguredAttrstmt(VizAttrStmt attrstmt)
            throws BuildException {
        attrstmt.checkConfiguration();
        printer.addAttributeStatement(attrstmt);
    }

    public void addSubgraph(VizSubgraph subgraph) {
        subgraph.setPrinter(printer);
    }

    public void execute() throws BuildException {
        checkConfiguration();
        loadProjects();
        writeDotToOutfile();
    }

    protected VizPrinter getPrinter() {
        return new VizPrinter();
    }

    protected VizProjectLoader getLoader() {
        return new VizProjectLoaderImpl();
    }

    protected void checkConfiguration() throws BuildException {
        if (antfile == null) {
            throw new BuildException("antfile attribute is required");
        }
        if (outfile == null) {
            throw new BuildException("outfile attribute is required");
        }
    }

    protected void loadProjects() throws BuildException {
        Enumeration enum = loader.getProjects().elements();
        while (enum.hasMoreElements()) {
            printer.addProject((VizProject) enum.nextElement());
        }
    }

    protected void writeDotToOutfile() throws BuildException {
        VizFileWriter out = null;
        try {
            out = new VizFileWriter(outfile);
            print(out);
        } catch (IOException e) {
            throw new BuildException(e.toString());
        } finally {
            if (out != null)
                out.close();
        }
    }

    protected void print(VizWriter out) {
        printer.setWriter(out);
        printer.print();
    }

    public class VizFileWriter implements VizWriter {
        private PrintWriter out = null;

        public VizFileWriter(File outfile) throws IOException {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
        }

        public void print(String str) {
            out.print(str);
        }

        public void println(String str) {
            out.println(str);
        }

        public void close() {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}


