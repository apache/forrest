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
package org.apache.forrest.forrestdoc.java.src;

import org.apache.forrest.forrestdoc.java.src.symtab.ReferenceTypes;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Cross-reference generation pass.
 * <p/>
 * For each package (= for each output directory):
 * Load reference.txt
 * Sort references
 * Generate HTML one public class at a time.
 *
 * @version $Id: $
 */
public class Pass2 {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( Pass2.class );

    /** Field DEFAULT_DIR */
    public static final String DEFAULT_DIR = ".";

    /** Field USAGE */
    public static final String USAGE = "Usage: java " + "-Doutdir=<doc dir>"
            + "[-Dtitle=<title>]"
            + "[-Dverbose=true]" + "javasrc.Pass2 ";

    /**
     * Method getOutDir
     *
     * @return
     */
    public String getOutDir() {
        return _outDir;
    }

    /**
     * Method setOutDir
     *
     * @param d
     */
    public void setOutDir(String d) {
        _outDir = d;
    }

    /**
     * Method getTitle
     *
     * @return
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Method setTitle
     *
     * @param t
     */
    public void setTitle(String t) {
        _title = t;
    }

    /**
     * Method getVerbose
     *
     * @return
     */
    public boolean getVerbose() {
        return _verbose;
    }

    /**
     * Method setVerbose
     *
     * @param val
     */
    public void setVerbose(boolean val) {
        _verbose = val;
    }

    /**
     * Constructor Pass2
     */
    public Pass2() {
        packageNames = new ArrayList();
        packageClasses = new Hashtable();
    }

    /**
     * Method run
     *
     * @param args
     * @throws IOException
     */
    public void run(String[] args) throws IOException {

        File outDir = new File(getOutDir());

        if (log.isDebugEnabled())
        {
            log.debug("run(String[]) - File outDir=" + outDir);
        }

        walkDirectories(null, outDir);
        Collections.sort(packageNames, stringComparator());

        // Create package files
        // I.e. generate HTML list of classes in each package
        if (log.isDebugEnabled())
        {
            log.debug("run(String[]) - Writing package files");
        }
        createPackageFiles();
        createPackageSummaryFiles();

        // Create the HTML for the package list
        createOverviewFrame();

        // Create main frames
        createIndex();
        createAllClassesFrame();
        createOverviewSummaryFrame();
    }

    /**
     * Method walkDirectories
     *
     * @param packageName
     * @param outDir
     * @throws IOException
     */
    private void walkDirectories(String packageName, File outDir)
            throws IOException {

        File refFile = new File(outDir, "references.txt");

        if (refFile.exists()) {

            // packageNames.add(packageName);
            // processRefFile(packageName, refFile);
            processRefFile(packageName, refFile);

            HashMap classes = (HashMap) packageClasses.get(packageName);

            if (classes.size() > 0) {
                packageNames.add(packageName);
            }
        }

        File[] entries = outDir.listFiles();

        for (int i = 0; i < entries.length; i++) {
            if (entries[i].isDirectory()) {
                String newPackageName = (packageName == null)
                        ? entries[i].getName()
                        : packageName + "."
                        + entries[i].getName();

                walkDirectories(newPackageName, entries[i]);
            }
        }
    }

    /**
     * Method processRefFile
     *
     * @param packageName
     * @param refFile
     * @throws IOException
     */
    private void processRefFile(String packageName, File refFile)
            throws IOException {
        if (log.isDebugEnabled())
        {
            log.debug("processRefFile(String, File) - File refFile=" + refFile);
        }

        HashMap classes = (HashMap) packageClasses.get(packageName);

        if (classes == null) {
            classes = new HashMap();
        }

        // load the entire file
        String line;
        FileInputStream fis = new FileInputStream(refFile);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        Vector v = new Vector();

        while ((line = br.readLine()) != null) {
            v.addElement(line);
        }

        br.close();
        isr.close();
        fis.close();

        String[] lines = new String[v.size()];

        v.copyInto(lines);

        // sort the lines
        Arrays.sort(lines, stringComparator());

        // process one referentFileClass (=one source file) at a time
        BufferedWriter bw = null;
        String prevReferentFileClass = null;
        String prevReferentTag = null;

        for (int i = 0; i < lines.length; i++) {
            line = lines[i];

            if (line.charAt(0) == '#') {
                continue;
            }

            Reference ref = new Reference(line);

            if (!ref.referentFileClass.equals(prevReferentFileClass)) {

                // close current section, if any
                if (prevReferentTag != null) {
                    closeSection(bw, prevReferentTag);
                }

                // close current output file, if any
                if (bw != null) {
                    closeOutputFile(bw, prevReferentFileClass);
                }

                // open new output file
                bw = openOutputFile(packageName, ref);
                prevReferentFileClass = ref.referentFileClass;
                prevReferentTag = null;
            }

            if (!classes.containsKey(ref.referentClass)) {
                classes.put(ref.referentClass, ref.referentFileClass);
            }

            if (!ref.referentTag.equals(prevReferentTag)) {

                // write close-section stuff, if any
                if (prevReferentTag != null) {
                    closeSection(bw, prevReferentTag);
                }

                // write new heading based on new referent type
                prevReferentTag = ref.referentTag;

                openSection(bw, packageName, ref);
            }

            // write link for this reference
            if (!ref.referringMethod.equals("?")) {
                writeLink(bw, packageName, ref);
            }
        }

        // close the last output file
        if (bw != null) {
            closeOutputFile(bw, prevReferentFileClass);
        }

        if (log.isDebugEnabled())
        {
            log.debug("processRefFile(String, File) - class list for "+packageName+" is "+classes);
        }

        packageClasses.put(packageName, classes);
    }

    /**
     * Method closeOutputFile
     *
     * @param bw
     * @param referentFileClass
     * @throws IOException
     */
    private void closeOutputFile(BufferedWriter bw, String referentFileClass)
            throws IOException {

        bw.write("</body></html>");
        bw.flush();
        bw.close();

        if (log.isDebugEnabled())
        {
            log.debug("closeOutputFile(BufferedWriter, String) - close output file");
        }
    }

    /**
     * Method openOutputFile
     *
     * @param packageName
     * @param ref
     * @return
     * @throws IOException
     */
    private BufferedWriter openOutputFile(String packageName, Reference ref)
            throws IOException {
        if (log.isDebugEnabled())
        {
            log.debug("openOutputFile(String, Reference) - Reference ref=" + ref.referentFileClass);
        }

        File rootDir = new File(getOutDir());
        String relPath = (packageName == null)
                ? ref.referentFileClass
                : packageName.replace('.', File.separatorChar)
                + File.separatorChar + ref.referentFileClass;

        relPath += "_java_ref.html";

        File outFile = new File(rootDir, relPath);
        FileOutputStream fos = new FileOutputStream(outFile);
        OutputStreamWriter fw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter result = new BufferedWriter(fw);

        result.write("<html><head><link rel=\"stylesheet\" "
                + "type=\"text/css\" " + "href=\""
                + getBackupPath(packageName)
                + "styles.css\"></head><body>");

        return result;
    }

    /**
     * Method closeSection
     *
     * @param bw
     * @param referentTag
     * @throws IOException
     */
    private void closeSection(BufferedWriter bw, String referentTag)
            throws IOException {

        bw.write("</p>");

        if (log.isDebugEnabled())
        {
            log.debug("closeSection(BufferedWriter, String) - close section for referent " + referentTag);
        }
    }

    /**
     * Method openSection
     *
     * @param bw
     * @param referentPackage
     * @param ref
     * @throws IOException
     */
    private void openSection(
            BufferedWriter bw, String referentPackage, Reference ref)
            throws IOException {

        if (ref.referentType.equals(ReferenceTypes.CLASS_REF)) {
            bw.write("<p class=\"classReflist\">");

            String nameString =
                    "<p class=\"classReflistHeader\">Class: <a name="
                    + ref.referentTag + " href=" + ref.referentFileClass
                    + "_java.html#" + ref.referentTag + ">" + ref.referentClass
                    + "</a></p>";

            bw.write(nameString);
        } else if (ref.referentType.equals(ReferenceTypes.METHOD_REF)) {
            bw.write("<p class=\"methodReflist\">");
            bw.write("<!-- hello -->");

            String nameString =
                    "<p class=\"methodReflistHeader\">Method: <a name="
                    + ref.referentTag + " href=" + ref.referentFileClass
                    + "_java.html#" + ref.referentTag + ">" + ref.referentTag
                    + "</a></p>";

            bw.write(nameString);
        } else if (ref.referentType.equals(ReferenceTypes.VARIABLE_REF)) {
            bw.write("<p class=\"variableReflist\">");

            String nameString =
                    "<p class=\"variableReflistHeader\">Variable: <a name="
                    + ref.referentTag + " href=" + ref.referentFileClass
                    + "_java.html#" + ref.referentTag + ">" + ref.referentTag
                    + "</a></p>";

            bw.write(nameString);
        } else {
            bw.write("<p>open section " + ref.referentType + "</p>");
        }

        if (log.isDebugEnabled())
        {
            log.debug("openSection(BufferedWriter, String, Reference) - open section for referent=" + ref.referentTag);
        }
    }

    /**
     * Method writeLink
     *
     * @param bw
     * @param referentPackage
     * @param ref
     * @throws IOException
     */
    private void writeLink(
            BufferedWriter bw, String referentPackage, Reference ref)
            throws IOException {

        String linkFilename = sourceName(referentPackage, ref);

        if (ref.referentType.equals(ReferenceTypes.CLASS_REF)) {
            String linkString = "<p class=\"classRefItem\"><a href="
                    + linkFilename + "#" + ref.referringLineNumber
                    + ">" + ref.referringPackage + "."
                    + ref.referringClass + "."
                    + ref.referringMethod + " ("
                    + ref.referringFile + ":"
                    + ref.referringLineNumber + ")</a></p>\n";

            bw.write(linkString);
        } else if (ref.referentType.equals(ReferenceTypes.METHOD_REF)) {
            String linkString = "<p class=\"methodRefItem\"><a href="
                    + linkFilename + "#" + ref.referringLineNumber
                    + ">" + ref.referringPackage + "."
                    + ref.referringClass + "."
                    + ref.referringMethod + " ("
                    + ref.referringFile + ":"
                    + ref.referringLineNumber + ")</a></p>\n";

            bw.write(linkString);
        } else if (ref.referentType.equals(ReferenceTypes.VARIABLE_REF)) {
            String linkString = "<p class=\"variableRefItem\"><a href="
                    + linkFilename + "#" + ref.referringLineNumber
                    + ">" + ref.referringPackage + "."
                    + ref.referringClass + "."
                    + ref.referringMethod + " ("
                    + ref.referringFile + ":"
                    + ref.referringLineNumber + ")</a></p>\n";

            bw.write(linkString);
        } else {
            bw.write("<p>link for a " + ref.referentType + "</p>");
        }
    }

    /**
     * Return path to referring X_java.html file, relative to referent directory.
     *
     * @param referentPackage
     * @param ref
     * @return
     */
    private String sourceName(String referentPackage, Reference ref) {

        String result = getBackupPath(referentPackage)
                + ref.referringPackage.replace('.', '/') + '/'
                + ref.referringClass + "_java.html";

        return result;
    }

    // lifted from Pass1.java -- still need it in Pass1?  If so, share.

    /**
     * Method getBackupPath
     *
     * @param packageName
     * @return
     */
    private String getBackupPath(String packageName) {

        StringTokenizer st = new StringTokenizer(packageName, ".");
        String backup = "";
        int dirs = 0;
        String newPath;

        if (log.isDebugEnabled())
        {
            log.debug("getBackupPath(String) - String packageName=" + packageName);
        }
        dirs = st.countTokens();

        for (int j = 0; j < dirs; j++) {
            backup = backup + "../";    // _separatorChar;
        }

        newPath = backup;

        if (log.isDebugEnabled())
        {
            log.debug("getBackupPath(String) - String newPath=" + newPath);
        }

        return (newPath);
    }

    /**
     * Method createPackageFiles
     */
    private void createPackageFiles() {

        String packageName;
        String fileName;
        File file;
        PrintWriter pw;

        // String className;
        int totalClassCount = 0;
        Iterator packageIter = packageNames.iterator();

        while (packageIter.hasNext()) {
            packageName = (String) packageIter.next();

            List classes = orderedPackageClasses(packageName);

            if (log.isDebugEnabled())
            {
                log.debug("createPackageFiles() - " + packageName+" has "+classes.size()+" classes");
            }

            totalClassCount += classes.size();
            fileName = getOutDir() + File.separatorChar
                    + packageName.replace('.', File.separatorChar)
                    + File.separatorChar + "classList.html";
            file = new File(fileName);

            createDirs(file);

            try {
                pw = new PrintWriter(
                        new BufferedOutputStream(new FileOutputStream(file)));

                String header =
                        "<head>\n"
                        + "<LINK rel=\"stylesheet\" type=\"text/css\" name=\"style1\" "
                        + "href=\"" + getBackupPath(packageName)
                        + "styles.css\">\n" + "</head><body>\n";

                pw.println(header);
                pw.println("<h3>");
                pw.println("<a href=\"package-summary.html\" target=\"classFrame\">"+packageName+"</a>");
                pw.println("</h3>");
                pw.println("<p class=packagename>" + packageName + "</p>");

                pw.println("<h3>Classes</h3>");
                Iterator iter = classes.iterator();

                while (iter.hasNext()) {
                    ClassFile cf = (ClassFile) iter.next();
                    String className = cf.className;
                    String fileClassName = cf.fileName;
                    //int j = className.indexOf('.');
                    String anchor;

                    // if (j == -1)
                    // {
                    anchor = className;

                    // }
                    // else
                    // {
                    // anchor = className.substring(j+1);
                    // }
                    String tag = "<p class=\"classListItem\"><a href=\""
                            + fileClassName + "_java.html#" + anchor
                            + "\" TARGET=\"classFrame\">" + className
                            + "</a></p>";

                    pw.println(tag);
                }

                pw.println("</body></html>");
                pw.close();
            } catch (Exception ex) {
                log.error( "Error writing file:" + fileName, ex );
            }
        }

        println(totalClassCount + " classes total");
    }

    /**
     * Return alphabetized list of all classes in a package, including inner classes.
     *
     * @param packageName
     * @return
     */
    private List orderedPackageClasses(String packageName) {

        HashMap hm = (HashMap) packageClasses.get(packageName);

        // Hmmm, is this supposed to be easier than using Hashtable.keys()?
        Set es = hm.entrySet();
        Iterator iter = es.iterator();
        List result = new ArrayList();

        while (iter.hasNext()) {
            Map.Entry me = (Map.Entry) iter.next();
            ClassFile cf = new ClassFile((String) me.getKey(),
                    (String) me.getValue());

            result.add(cf);
        }

        Collections.sort(result, classFileComparator());

        return result;
    }

    /**
     * Method orderedAllClasses
     *
     * @return
     */
    private List orderedAllClasses() {

        List result = new ArrayList();
        Iterator packageIter = packageNames.iterator();

        while (packageIter.hasNext()) {
            String packageName = (String) packageIter.next();
            HashMap hm = (HashMap) packageClasses.get(packageName);
            String packageFileName = "";

            if (packageName.indexOf('.') != -1) {
                packageFileName = packageName.replace('.', '/');
                packageFileName += '/';
            }

            // Hmmm, is this supposed to be easier than using Hashtable.keys()?
            Set es = hm.entrySet();
            Iterator iter = es.iterator();

            while (iter.hasNext()) {
                Map.Entry me = (Map.Entry) iter.next();
                ClassFile cf = new ClassFile((String) me.getKey(),
                        packageFileName
                        + (String) me.getValue());

                result.add(cf);
            }
        }

        Collections.sort(result, classFileComparator());

        return result;
    }

    /**
     * Method stringComparator
     *
     * @return
     */
    private Comparator stringComparator() {

        return new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((String) o1).compareTo((String)o2);
            }

            public boolean equals(Object o) {
                return false;
            }
        };
    }

    /**
     * Method classFileComparator
     *
     * @return
     */
    private Comparator classFileComparator() {

        return new Comparator() {

            public int compare(Object o1, Object o2) {

                ClassFile cf1 = (ClassFile) o1;
                ClassFile cf2 = (ClassFile) o2;

                return cf1.className.compareTo(cf2.className);
            }

            public boolean equals(Object o) {
                return false;
            }
        };
    }

    /**
     * Method createIndex
     */
    private void createIndex() {

        String fileName = getOutDir() + File.separatorChar + "index.html";
        File file = new File(fileName);

        createDirs(file);

        try {
            PrintWriter pw = new PrintWriter(
                    new BufferedOutputStream(new FileOutputStream(file)));

            pw.println("<TITLE>" + getTitle() + "</TITLE>");
            pw.println("<FRAMESET cols=\"20%,80%\">");
            pw.println("  <FRAMESET rows=\"30%,70%\">");
            pw.println(
                    "    <FRAME src=\"overview-frame.html\" name=\"packageListFrame\">");
            pw.println(
                    "    <FRAME src=\"allclasses-frame.html\" name=\"packageFrame\">");
            pw.println("  </FRAMESET>");
            pw.println("  ");
            pw.println("  <FRAMESET rows=\"*\">");
            pw.println(
                    "    <FRAME src=\"overview-summary.html\" name=\"classFrame\">");
            pw.println("  </FRAMESET>");
            pw.println("</FRAMESET>");
            pw.close();
        } catch (Exception e) {
            log.error( "Exception: " + e.getMessage(), e );
        }
    }

    /**
     * Create the HTML for the list of all packages.
     */
    private void createOverviewFrame() {

        String packageName;
        String packageFileName;
        String fileName = getOutDir() + File.separatorChar
                + "overview-frame.html";
        File file = new File(fileName);

        createDirs(file);

        try {
            PrintWriter pw = new PrintWriter(
                    new BufferedOutputStream(new FileOutputStream(file)));
            Iterator iter = packageNames.iterator();

            pw.println("<html><head><link rel=\"stylesheet\" type=\"text/css\""
                    + "href=\"styles.css\"></head><body>");

            pw.println("<h3><a href=\"allclasses-frame.html\" target=\"packageFrame\">All Classes</a></h3>");
            pw.println("<h3>Packages</h3>");
            while (iter.hasNext()) {
                packageName = (String) iter.next();
                packageFileName = packageName.replace('.', '/') + '/'
                        + "classList.html";

                pw.println("<p class=\"packageListItem\"><A HREF=\""
                        + packageFileName + "\" TARGET=\"packageFrame\">"
                        + packageName + "</A></p>");
            }

            pw.println("</body></html>");
            pw.close();
        } catch (Exception e) {
            log.error( "Exception: " + e.getMessage(), e );
        }
    }

    /**
     * Method createAllClassesFrame
     */
    private void createAllClassesFrame() {

        String fileName = getOutDir() + File.separatorChar
                + "allclasses-frame.html";
        File file = new File(fileName);

        createDirs(file);

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));

            pw.println("<html><head><link rel=\"stylesheet\" type=\"text/css\""
                    + "href=\"styles.css\"></head><body>");

            pw.println("<h3>All Classes</h3>");
            Iterator iter = orderedAllClasses().iterator();

            while (iter.hasNext()) {
                ClassFile cf = (ClassFile) iter.next();
                String className = cf.className;
                String fileClassName = cf.fileName;
                String anchor = className;
                String tag =
                        "<p class=\"classListItem\"><a href=\"" + fileClassName
                        + "_java.html#" + anchor + "\" TARGET=\"classFrame\">"
                        + className + "</a></p>";

                pw.println(tag);
            }

            pw.println("</body></html>");
            pw.close();
        } catch (Exception e) {
            log.error( "Exception: " + e.getMessage(), e );
        }
    }

    /**
     * Method createPackageFiles
     */
    private void createPackageSummaryFiles() {

        String packageName;
        String fileName;
        File file;
        PrintWriter pw;

        // String className;
        int totalClassCount = 0;
        Iterator packageIter = packageNames.iterator();

        while (packageIter.hasNext()) {
            packageName = (String) packageIter.next();

            List classes = orderedPackageClasses(packageName);

            if (log.isDebugEnabled())
            {
                log.debug("createPackageSummaryFiles() - " + packageName+" has "+classes.size()+" classes" );
            }

            totalClassCount += classes.size();
            fileName = getOutDir() + File.separatorChar
                    + packageName.replace('.', File.separatorChar)
                    + File.separatorChar + "package-summary.html";
            file = new File(fileName);

            createDirs(file);

            try {
                pw = new PrintWriter(
                        new BufferedOutputStream(new FileOutputStream(file)));

                String header =
                        "<head>\n"
                        + "<LINK rel=\"stylesheet\" type=\"text/css\" name=\"style1\" "
                        + "href=\"" + getBackupPath(packageName)
                        + "styles.css\">\n" + "</head><body>\n";

                pw.println(header);
                createPackageSummaryFilesExtras(pw, getBackupPath(packageName), "package-summary.html");

                pw.println("<h2>" + packageName + "</h2>");

                pw.println("<table class=\"summary\">");
                pw.println("<thead>");
                pw.println("<tr>");
                pw.println("<th>Class Summary</th>");
                pw.println("</tr>");
                pw.println("</thead>");
                pw.println("<tbody>");

                Iterator iter = classes.iterator();

                while (iter.hasNext()) {
                    ClassFile cf = (ClassFile) iter.next();
                    String className = cf.className;
                    String fileClassName = cf.fileName;
                    String anchor;

                    anchor = className;
                    pw.println("<tr>");
                    pw.println("<td>");
                    pw.println("<a href=\""
                            + fileClassName + "_java.html#" + anchor
                            + "\" TARGET=\"classFrame\">" + className
                            + "</a>");
                    pw.println("</td>");
                    pw.println("</tr>");
                }
                pw.println("</tbody>");
                pw.println("</table>");

                createPackageSummaryFilesExtras(pw, getBackupPath(packageName), "package-summary.html");
                pw.println("          <hr></hr>\n" +
                        "          Copyright &copy; 2001-2003 Apache Software Foundation. All Rights Reserved.");
                pw.println("</body></html>");
                pw.close();
            } catch (Exception ex) {
                log.error( "Error writing file:" + fileName, ex);
            }
        }
    }

    private void createPackageSummaryFilesExtras(PrintWriter pw, String root, String current) {
        pw.println("<div class=\"overview\">");
        pw.println("<ul>");
        pw.println("<li><a href=\"" + root + "overview-summary.html\">Overview</a></li>");
        pw.println("<li class=\"selected\">Package</li>");
        pw.println("</ul>");
        pw.println("</div>");
        pw.println("<div class=\"framenoframe\">");
        pw.println("<ul>");
        pw.println("<li>");
        pw.println("<a href=\"" + root + "index.html\" target=\"_top\">FRAMES</a>");
        pw.println("</li>");
        pw.println("<li>");
        pw.println("<a href=\""+ current +"\" target=\"_top\">NO FRAMES</a>");
        pw.println("</li>");
        pw.println("</ul>");
        pw.println("</div>");
    }


    /**
     * Method createOverviewSummaryFrame
     */
    private void createOverviewSummaryFrame() {

        String fileName = getOutDir() + File.separatorChar
                + "overview-summary.html";
        File file = new File(fileName);

        createDirs(file);

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            pw.println("<html><head><link rel=\"stylesheet\" type=\"text/css\""
                    + "href=\"styles.css\"></head><body>");

            createOverviewSummaryFrameExtras(pw);

            pw.println("<h2>" + getTitle() + "</h2>");

            pw.println("<table class=\"summary\">");
            pw.println("<thead>");
            pw.println("<tr>");
            pw.println("<th>Packages</th>");
            pw.println("</tr>");
            pw.println("</thead>");
            pw.println("<tbody>");

            Iterator iter = packageNames.iterator();
            while (iter.hasNext()) {
                String packageName = (String) iter.next();
                String packageFileName = packageName.replace('.', '/') + '/'
                        + "package-summary.html";

                pw.println("<tr>");
                pw.println("<td>");
                pw.println("<a href=\"" + packageFileName + "\">" + packageName + "</a>");
                pw.println("</td>");
                pw.println("</tr>");
            }

            pw.println("</tbody>");
            pw.println("</table>");

            createOverviewSummaryFrameExtras(pw);
            pw.println("          <hr></hr>\n" +
                    "          Copyright &copy; 2001-2003 Apache Software Foundation. All Rights Reserved.");
            pw.println("</body></html>");
            pw.close();
        } catch (Exception e) {
            log.error( "Error writing file:" + fileName, e);
        }
    }

    private void createOverviewSummaryFrameExtras(PrintWriter pw) {
        pw.println("<div class=\"overview\">");
        pw.println("<ul>");
        pw.println("<li class=\"selected\">Overview</li>");
        pw.println("<li>Package</li>");
        pw.println("</ul>");
        pw.println("</div>");
        pw.println("<div class=\"framenoframe\">");
        pw.println("<ul>");
        pw.println("<li>");
        pw.println("<a href=\"index.html\" target=\"_top\">FRAMES</a>");
        pw.println("</li>");
        pw.println("<li>");
        pw.println("<a href=\"overview-summary.html\" target=\"_top\">NO FRAMES</a>");
        pw.println("</li>");
        pw.println("</ul>");
        pw.println("</div>");
    }

    /**
     * Method initializeDefaults
     */
    public void initializeDefaults() {

        String outdir = System.getProperty("outdir");

        if (outdir == null) {
            outdir = DEFAULT_DIR;
        }

        setOutDir(outdir);

        String title = System.getProperty("title");

        if (title == null) {
            title = "Pass2: " + outdir;
        }

        setTitle(title);

        boolean verbose = false;
        String verboseStr = System.getProperty("verbose");

        if (verboseStr != null) {
            verboseStr = verboseStr.trim();

            if (verboseStr.equalsIgnoreCase("on")
                    || verboseStr.equalsIgnoreCase("true")
                    || verboseStr.equalsIgnoreCase("yes")
                    || verboseStr.equalsIgnoreCase("1")) {
                verbose = true;
            }
        }

        setVerbose(verbose);
    }

    /**
     * Method createDirs
     *
     * @param f
     */
    private void createDirs(File f) {

        String parentDir = f.getParent();
        File directory = new File(parentDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void println(String description) {
        System.out.print("\n");
        System.out.println(description);
    }

    /**
     * Method main
     *
     * @param args
     * @throws Exception
     */
    public void main(String args[]) throws Exception {

        Pass2 p2 = new Pass2();

        p2.initializeDefaults();
        p2.run(args);
    }

    /** Field packageNames */
    ArrayList packageNames;

    /** Field packageClasses */
    Hashtable packageClasses;

    /** Field _outDir */
    private String _outDir;

    /** Field _title */
    private String _title;

    /** Field _verbose */
    private boolean _verbose;
}

/**
 * An entry in the references.txt file
 */
class Reference {

    /**
     * Constructor Reference
     *
     * @param line
     */
    Reference(String line) {

        StringTokenizer st = new StringTokenizer(line, "|");

        referentFileClass = st.nextToken();
        referentClass = st.nextToken();
        referentTag = st.nextToken();
        referentType = st.nextToken();
        referringPackage = st.nextToken();
        referringClass = st.nextToken();
        referringMethod = st.nextToken();
        referringFile = st.nextToken();
        referringLineNumber = st.nextToken();
    }

    /** Field referentFileClass */
    String referentFileClass;

    /** Field referentClass */
    String referentClass;

    /** Field referentType */
    String referentType;

    /** Field referentTag */
    String referentTag;

    /** Field referringPackage */
    String referringPackage;

    /** Field referringClass */
    String referringClass;

    /** Field referringMethod */
    String referringMethod;

    /** Field referringFile */
    String referringFile;

    /** Field referringLineNumber */
    String referringLineNumber;
}

/**
 * Class ClassFile
 */
class ClassFile {

    /** Field className */
    public String className;

    /** Field fileName */
    public String fileName;

    /**
     * Constructor ClassFile
     *
     * @param className
     * @param fileName
     */
    public ClassFile(String className, String fileName) {
        this.className = className;
        this.fileName = fileName;
    }
}
