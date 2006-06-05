/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        Enumeration enumList = loader.getProjects().elements();
        while (enumList.hasMoreElements()) {
            printer.addProject((VizProject) enumList.nextElement());
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


