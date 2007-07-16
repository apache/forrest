/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.forrest.forrestdoc.java.src;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.util.SourceFileScanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Runs the javasrc converter as a task inside
 * the well known build tool "ant" (see ant.apache.org).
 *
 * @see <a href="http://ant.apache.org">http://ant.apache.org</a>
 * @version $Id: $
 */
public class JavaSrcTask extends MatchingTask {

    /** Field srcDir */
    private File srcDir;

    /** Field destDir */
    private File destDir;

    /** Field recurse */
    private boolean recurse = true;

    /** Field title */
    private String title = "JavaSrc";

    /** Field verbose */
    private boolean verbose = false;

    /**
     * Constructor for JavaSrcTask.
     */
    public JavaSrcTask() {
        super();
    }

    /**
     * Returns the directory where the Java sources are stored.
     *
     * @return String directory name
     */
    public File getSrcDir() {
        return srcDir;
    }

    /**
     * Sets the directory where the Java sources are stored.
     *
     * @param javaDir directory name
     */
    public void setSrcDir(File javaDir) {
        this.srcDir = javaDir;
    }

    /**
     * Returns the directory where the HTML output is written.
     *
     * @return String directory name
     */
    public File getDestDir() {
        return destDir;
    }

    /**
     * Sets the directory where the HTML output is written.
     *
     * @param htmlDir directory name
     */
    public void setDestDir(File htmlDir) {
        this.destDir = htmlDir;
    }

    /**
     * @throws BuildException
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException {

        if (srcDir == null) {

            // We directly change the user variable, because it
            // shouldn't lead to problems
            srcDir = this.getProject().resolveFile(".");
        }

        // find the files/directories
        DirectoryScanner dirScanner = getDirectoryScanner(srcDir);

        // get a list of files to work on
        String[] allSourceFiles = dirScanner.getIncludedFiles();
        SourceFileScanner sourceScanner = new SourceFileScanner(this);
        FileNameMapper sourceToOutMapper = new GlobPatternMapper();

        sourceToOutMapper.setFrom("*");
        sourceToOutMapper.setTo("*.java");

        String[] sourceFilesToProcess = sourceScanner.restrict(allSourceFiles,
                srcDir, destDir, sourceToOutMapper);

        if (sourceFilesToProcess.length > 0) {
            String files = ((sourceFilesToProcess.length == 1)
                    ? " file"
                    : " files");

            log("Converting " + sourceFilesToProcess.length + files,
                    Project.MSG_INFO);
        }

        for (int i = 0; i < sourceFilesToProcess.length; ++i) {
            sourceFilesToProcess[i] =
                    new File(srcDir, sourceFilesToProcess[i]).getAbsolutePath();
        }

        Pass1 p1 = new Pass1();

        p1.initializeDefaults();
        p1.setOutDir(destDir.getAbsolutePath());
        p1.setRecurse(recurse);
        p1.setTitle(title);
        p1.setVerbose(verbose);
        p1.run(sourceFilesToProcess);

        Pass2 p2 = new Pass2();

        p2.initializeDefaults();
        p2.setOutDir(destDir.getAbsolutePath());
        p2.setTitle(title);
        p2.setVerbose(verbose);

        try {
            p2.run(new String[]{
            });
            printCSSFile();
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        }
    }

    /**
     * Method getRecurse
     *
     * @return
     */
    public boolean getRecurse() {
        return recurse;
    }

    /**
     * Method setRecurse
     *
     * @param recurse
     */
    public void setRecurse(boolean recurse) {
        this.recurse = recurse;
    }

    /**
     * Method getTitle
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method setTitle
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Method getVerbose
     *
     * @return
     */
    public boolean getVerbose() {
        return verbose;
    }

    /**
     * Method setVerbose
     *
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    String cssText = "/* Javadoc style sheet */\n" +
            "/* Define colors, fonts and other style attributes here to override the defaults  */\n" +
            "body { \n" +
            "\tbackground-color: #fff;\n" +
            "\tfont-family: Arial, Helvetica, sans-serif;\n" +
            "}\n" +
            "\n" +
            "a:link {\n" +
            " color: #00f;\n" +
            "}\n" +
            "a:visited {\n" +
            " color: #00a;\n" +
            "}\n" +
            "\n" +
            "a:active, a:hover {\n" +
            " color: #f30 !important;\n" +
            "}\n" +
            "\n" +
            "ul, li\t{\n" +
            "\tlist-style-type:none ;\n" +
            "\tmargin:0;\n" +
            "\tpadding:0;\n" +
            "}\n" +
            "\n" +
            "table td{\n" +
            "\tpadding: 3px;\n" +
            "\tborder: 1px solid #000;\n" +
            "}\n" +
            "table{\n" +
            "\twidth:100%;\n" +
            "\tborder: 1px solid #000;\n" +
            "\tborder-collapse: collapse;\n" +
            "}\n" +
            "\n" +
            "div.overview {\n" +
            "\tbackground-color:#ddd;\n" +
            "\tpadding: 4px 4px 4px 0;\n" +
            "}\n" +
            "div.overview li, div.framenoframe li {\n" +
            "\tdisplay: inline;\n" +
            "}\n" +
            "div.framenoframe {\n" +
            "\ttext-align: center;\n" +
            "\tfont-size: x-small;\n" +
            "}\n" +
            "div.framenoframe li {\n" +
            "\tmargin: 0 3px 0 3px;\n" +
            "}\n" +
            "div.overview li {\n" +
            "\tmargin:3px 3px 0 3px;\n" +
            "\tpadding: 4px;\n" +
            "}\n" +
            "li.selected {\n" +
            "\tbackground-color:#888;\n" +
            "\tcolor: #fff;\n" +
            "\tfont-weight: bold;\n" +
            "}\n" +
            "\n" +
            "table.summary {\n" +
            "\tmargin-bottom: 20px;\n" +
            "}\n" +
            "table.summary td, table.summary th {\n" +
            "\tfont-weight: bold;\n" +
            "\ttext-align: left;\n" +
            "\tpadding: 3px;\n" +
            "}\n" +
            "table.summary th{\n" +
            "\tbackground-color:#036;\n" +
            "\tcolor: #fff;\n" +
            "}\n" +
            "table.summary td{\n" +
            "\tbackground-color:#eee;\n" +
            "\tborder: 1px solid black;\n" +
            "}\n" +
            "\n" +
            "em {\n" +
            "\tcolor: #A00;\n" +
            "}\n" +
            "em.comment {\n" +
            "\tcolor: #390;\n" +
            "}\n" +
            ".string {\n" +
            "\tcolor: #009;\n" +
            "}\n" +
            "div#footer {\n" +
            "\ttext-align:center;\n" +
            "}\n" +
            "#overview {\n" +
            "\tpadding:2px;\n" +
            "}\n" +
            "            \n" +
            "            \n" +
            "hr {\n" +
            "\theight: 1px;\n" +
            "\tcolor: #000;\n" +
            "}";
    /**
     * Method printCSSFile
     *
     * @throws IOException
     */
    private void printCSSFile() throws IOException {

        FileOutputStream css = new FileOutputStream(new File(destDir,
                "styles.css"));
        PrintWriter pw = new PrintWriter(css);

        //pw.println(
        //        "body { font-family: lucida, verdana, arial, sans-serif; font-size:10pt } ");
        //pw.println("pre { font-size:10pt } ");
        pw.println(cssText);
        pw.println(".comment   { color:#007d00; font-style:italic } ");
        pw.println(".linenum   { color:#888 font-weight:normal; } ");
        pw.println(".textDiv   { font-weight:bold } ");
        pw.println("");
        pw.println(".packageListItem { margin:0; padding:0; border:0 } ");
        pw.println(".packageName {  font-weight:bold } ");
        pw.println("");
        pw.println(".classDef  { color:#875b37;font-weight:bold } ");
        pw.println(".classRef  { color:#875b37;font-weight:normal; } ");
        pw.println(".classListItem  {  margin:0; padding:0; border:0 } ");
        pw.println(".classReflist         { border-bottom:solid } ");
        pw.println(
                ".classReflistHeader   { font-weight:bold; border:0; margin:0; padding:0 } ");
        pw.println(".classRefItem         { margin:0; padding:0; border:0 } ");
        pw.println("p.classRefItem a        { color:#875b37 } ");
        pw.println("p.classReflistHeader a  { color:#875b37 } ");
        pw.println("");
        pw.println("");
        pw.println(".methodDef { color:#377587;font-weight:bold } ");
        pw.println(".methodRef { color:#377587;font-weight:normal } ");
        pw.println(".methodReflist         { } ");
        pw.println(
                ".methodReflistHeader   { font-weight:bold; border:0; margin:0; padding:0} ");
        pw.println(
                ".methodRefItem         { margin:0; padding:0; border:0; color:#009 } ");
        pw.println("p.methodRefItem a        { color:#377587 } ");
        pw.println("p.methodReflistHeader a  { color:#377587 } ");
        pw.println("");
        pw.println("");
        pw.println(".varDef    { color:#232187;font-weight:bold } ");
        pw.println(".varRef    { color:#232187;font-weight:normal } ");
        pw.println(".variableReflist         { } ");
        pw.println(
                ".variableReflistHeader   { color:#000000;font-weight:bold;  border:0; margin:0; padding:0} ");
        pw.println(
                ".variableRefItem         { margin:0; padding:0; border:0; color:#660 } ");
        pw.println("p.variableRefItem a        { color:#232187; } ");
        pw.println("p.variableRefListheader a  { color:#232187; } ");
        pw.println("");
        pw.close();
    }
}
