/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/Pass2.java,v 1.1 2004/02/09 11:09:18 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:18 $
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
package org.apache.forrest.forrestdoc.java.src;

import org.apache.forrest.forrestdoc.java.src.symtab.ReferenceTypes;

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
 */
public class Pass2 {

    /** Field debug */
    public static final boolean debug = false;

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
    public static String getOutDir() {
        return _outDir;
    }

    /**
     * Method setOutDir
     * 
     * @param d 
     */
    public static void setOutDir(String d) {
        _outDir = d;
    }

    /**
     * Method getTitle
     * 
     * @return 
     */
    public static String getTitle() {
        return _title;
    }

    /**
     * Method setTitle
     * 
     * @param t 
     */
    public static void setTitle(String t) {
        _title = t;
    }

    /**
     * Method getVerbose
     * 
     * @return 
     */
    public static boolean getVerbose() {
        return _verbose;
    }

    /**
     * Method setVerbose
     * 
     * @param val 
     */
    public static void setVerbose(boolean val) {
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

        // System.out.println("outdir="+getOutDir());
        File outDir = new File(getOutDir());

        walkDirectories(null, outDir);
        Collections.sort(packageNames, stringComparator());

        // Create package files
        // I.e. generate HTML list of classes in each package
        // System.out.println("\nWriting package files...");
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

        // System.out.println("process "+refFile.getAbsolutePath());
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

        // System.out.println("class list for "+packageName+" is "+classes);
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

        // System.out.println("close output file");
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

        // System.out.println("open output file for referent class "+ref.referentFileClass);
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

        // System.out.println("close section for referent "+referentTag);
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

        // System.out.println("open section for referent "+ref.referentTag);
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

        // System.out.println("gBP Package Name for BackupPath: "+ packageName);
        dirs = st.countTokens();

        for (int j = 0; j < dirs; j++) {
            backup = backup + "../";    // _separatorChar;
        }

        newPath = backup;

        // System.out.println("gBP Package Name for newpath: "+newPath);
        return (newPath);
    }

    /**
     * Method createPackageFiles
     */
    private void createPackageFiles() {

        String packageName;
        String fileName;
        Vector tags;
        File file;
        PrintWriter pw;

        // String className;
        int totalClassCount = 0;
        Iterator packageIter = packageNames.iterator();

        while (packageIter.hasNext()) {
            packageName = (String) packageIter.next();

            List classes = orderedPackageClasses(packageName);

            // System.out.println(packageName+" has "+classes.size()+" classes");
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
                    String fileClassName = (String) cf.fileName;
                    int j = className.indexOf('.');
                    String anchor;
                    String filePrefix;

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
                System.err.println("Error writing file:" + fileName);
                ex.printStackTrace();
            }
        }

        System.out.println(totalClassCount + " classes total");
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
                return ((String) o1).compareTo(o2);
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                String fileClassName = (String) cf.fileName;
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
            e.printStackTrace();
        }
    }

    /**
     * Method createPackageFiles
     */
    private void createPackageSummaryFiles() {

        String packageName;
        String fileName;
        Vector tags;
        File file;
        PrintWriter pw;

        // String className;
        int totalClassCount = 0;
        Iterator packageIter = packageNames.iterator();

        while (packageIter.hasNext()) {
            packageName = (String) packageIter.next();

            List classes = orderedPackageClasses(packageName);

            // System.out.println(packageName+" has "+classes.size()+" classes");
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
                    String fileClassName = (String) cf.fileName;
                    int j = className.indexOf('.');
                    String anchor;
                    String filePrefix;

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
                System.err.println("Error writing file:" + fileName);
                ex.printStackTrace();
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

            pw.println("<h2>" + Pass2.getTitle() + "</h2>");

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
            e.printStackTrace();
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

        String outdir = (String) System.getProperty("outdir");

        if (outdir == null) {
            outdir = DEFAULT_DIR;
        }

        Pass2.setOutDir(outdir);

        String title = (String) System.getProperty("title");

        if (title == null) {
            title = "Pass2: " + outdir;
        }

        Pass2.setTitle(title);

        boolean verbose = false;
        String verboseStr = (String) System.getProperty("verbose");

        if (verboseStr != null) {
            verboseStr = verboseStr.trim();

            if (verboseStr.equalsIgnoreCase("on")
                    || verboseStr.equalsIgnoreCase("true")
                    || verboseStr.equalsIgnoreCase("yes")
                    || verboseStr.equalsIgnoreCase("1")) {
                verbose = true;
            }
        }

        Pass2.setVerbose(verbose);
    }

    /**
     * Method createDirs
     * 
     * @param f 
     */
    private static void createDirs(File f) {

        String parentDir = f.getParent();
        File directory = new File(parentDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Method main
     * 
     * @param args 
     * @throws Exception 
     */
    public static void main(String args[]) throws Exception {

        Pass2 p2 = new Pass2();

        p2.initializeDefaults();
        p2.run(args);
    }

    /** Field packageNames */
    ArrayList packageNames;

    /** Field packageClasses */
    Hashtable packageClasses;

    /** Field _outDir */
    private static String _outDir;

    /** Field _title */
    private static String _title;

    /** Field _verbose */
    private static boolean _verbose;
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
 * 
 * @author 
 * @version %I%, %G%
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
