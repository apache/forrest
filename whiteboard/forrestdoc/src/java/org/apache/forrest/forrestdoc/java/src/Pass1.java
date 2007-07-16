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

import org.apache.log4j.Logger;

import org.apache.forrest.forrestdoc.java.src.symtab.HTMLTag;
import org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer;
import org.apache.forrest.forrestdoc.java.src.symtab.PackageDef;
import org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable;
import org.apache.forrest.forrestdoc.java.src.util.JSCollections;
import org.apache.forrest.forrestdoc.java.src.util.SkipCRInputStream;
import org.apache.forrest.forrestdoc.java.src.xref.FileListener;
import org.apache.forrest.forrestdoc.java.src.xref.JavaXref;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Class Pass1
 *
 * @version $Id: $
 */
public class Pass1 implements FileListener {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( Pass1.class );

    /** Field DEFAULT_DIR */
    public static final String DEFAULT_DIR = ".";

    /** Field USAGE */
    public static final String USAGE =
            "Usage: java " + "[-Doutdir=<doc dir>]" + "[-Dtitle=<title>]"
            + "[-Dverbose=true]" + "javasrc.Pass1 "
            + "<source dir> [<source dir> <source dir> ...]";

    /** Field inputFiles */
    private HashSet inputFiles = new HashSet();

    /**
     * Constructor Pass1
     */
    public Pass1() {
    }

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
     * Method getRecurse
     *
     * @return
     */
    public boolean getRecurse() {
        return _doRecurse;
    }

    /**
     * Method setRecurse
     *
     * @param recurse
     */
    public void setRecurse(boolean recurse) {
        _doRecurse = recurse;
    }

    /**
     * Method setVerbose
     *
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        _verbose = verbose;
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
     * @see org.apache.forrest.forrestdoc.java.src.xref.FileListener#notify(java.lang.String)
     */
    public void notify(String path) {
        printAdvancement(path);
        inputFiles.add(path);
    }

    /**
     * Method run
     *
     * @param args
     */
    public void run(String[] args) {

        // Use a try/catch block for parser exceptions
        try {

            // create a new symbol table
            SymbolTable symbolTable = SymbolTable.getSymbolTable();

            // if we have at least one command-line argument
            if (args.length > 0) {
                print("outDirPath is " + getOutDir());

                symbolTable.setOutDirPath(getOutDir());

                println("Parsing");

                // for each directory/file specified on the command line
                for (int i = 0; i < args.length; i++) {
                    JavaXref.doFile(new File(args[i]), symbolTable,
                            getRecurse(), this);    // parse it
                }

                println("Resolving types");

                // resolve the types of all symbols in the symbol table
                symbolTable.resolveTypes();
                symbolTable.resolveRefs();
            } else {
                println(USAGE);
                return;
            }

            // Iterate through each package
            Hashtable packageTable = symbolTable.getPackages();
            Enumeration pEnum = packageTable.elements();

            println("Persisting definitions");

            while (pEnum.hasMoreElements()) {
                PackageDef pDef = (PackageDef) pEnum.nextElement();

                printAdvancement("Processing package " + pDef.getName());

                // Generate tags for each package.  We cannot do one class
                // at a time because more than one class might be in a
                // single file, and we write out each file only one time.
                HTMLTagContainer tagList = new HTMLTagContainer();

                pDef.generateTags(tagList);

                Hashtable fileTable = tagList.getFileTable();
                Enumeration enumList = fileTable.keys();
                Vector tempFileTags = new Vector();

                while (enumList.hasMoreElements()) {
                    tempFileTags.clear();

                    File f = (File) enumList.nextElement();

                    if (inputFiles.contains(f.getAbsolutePath())) {
                        Vector fileTags = (Vector) fileTable.get(f);

                        tempFileTags.addAll(fileTags);

                        // Generate the HTML tags for all references in this file
                        // I.e. generate HTML mark-up of this .java file
                        SymbolTable.createReferenceTags(f, tempFileTags);
                        SymbolTable.getCommentTags(f, tempFileTags);
                        SymbolTable.getLiteralTags(f, tempFileTags);
                        SymbolTable.getKeywordTags(f, tempFileTags);
                        createClassFiles(tempFileTags);
                    }
                }

                // Create reference files
                // I.e. generate HTML mark-up of all definitions in this package's .java files
                // (no longer -- this happens in Pass2 now)
                // System.out.println("\nWriting definition HTML...");
                // pDef.generateReferenceFiles(getOutDir());
                pDef.persistDefinitions(getOutDir());
            }

            println("Persisting references");

            symbolTable.persistRefs(getOutDir());
        } catch (Exception e) {
            log.error( "Exception: " + e.getMessage(), e );
            //System.exit(1);                                 // make this behavior an option?
        }
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
            title = "Pass1: " + outdir;
        }

        setTitle(title);

        boolean doRecurse = true;
        String recurseStr = System.getProperty("recurse");

        if (recurseStr != null) {
            recurseStr = recurseStr.trim();

            if (recurseStr.equalsIgnoreCase("off")
                    || recurseStr.equalsIgnoreCase("false")
                    || recurseStr.equalsIgnoreCase("no")
                    || recurseStr.equalsIgnoreCase("0")) {
                doRecurse = false;
            }
        }

        setRecurse(doRecurse);

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
    public void createDirs(File f) {

        String parentDir = f.getParent();
        File directory = new File(parentDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Method getBackupPath
     *
     * @param tagList
     * @param element
     * @return
     */
    public String getBackupPath(Object[] tagList, int element) {

        HTMLTag t = (HTMLTag) tagList[element];
        String packageName = t.getPackageName();

        if (packageName.equals("")) {
            File tempFile = t.getFile();
            int i = Math.min(element + 1, tagList.length);
            HTMLTag tempTag = (HTMLTag) tagList[i];

            while (tempTag.getFile().equals(tempFile) && (i < tagList.length)) {
                if ((tempTag.getPackageName() != null)
                        && (tempTag.getPackageName().length() > 0)) {
                    packageName = tempTag.getPackageName();

                    break;
                }

                i++;

                tempTag = (HTMLTag) tagList[i];
            }
        }

        return getBackupPath(packageName);
    }

    /**
     * Returns the path to the top level of the source hierarchy from the files
     * og\f a given class.
     *
     * @param packageName the package to get the backup path for
     * @return
     * @returns the path from the package to the top level, as a string
     */
    public static String getBackupPath(String packageName) {

        StringTokenizer st = new StringTokenizer(packageName, ".");
        String backup = "";
        int dirs = 0;
        String newPath = "";

        if (log.isDebugEnabled())
        {
            log.debug("getBackupPath(String) - Package Name for BackupPath=" + packageName);
        }

        dirs = st.countTokens();

        for (int j = 0; j < dirs; j++) {
            backup = backup + "../";
        }

        newPath = backup;

        if (log.isDebugEnabled())
        {
            log.debug("getBackupPath(String) - Package Name for newPath=" + newPath);
        }

        return (newPath);
    }

    /**
     * Method createClassFile
     *
     * @param tagList
     * @param element
     * @return
     * @throws IOException
     */
    public HTMLOutputWriter createClassFile(Object[] tagList, int element)
            throws IOException {

        HTMLTag t = (HTMLTag) tagList[element];
        String packageName = t.getPackageName();

        if (packageName.equals("")) {
            File tempFile = t.getFile();
            int i = Math.min(element + 1, tagList.length);
            HTMLTag tempTag = (HTMLTag) tagList[i];

            while (tempTag.getFile().equals(tempFile) && (i < tagList.length)) {
                if ((tempTag.getPackageName() != null)
                        && (tempTag.getPackageName().length() > 0)) {
                    packageName = tempTag.getPackageName();

                    break;
                }

                i++;

                tempTag = (HTMLTag) tagList[i];
            }
        }

        String fileName = t.getFile().toString();

        if (log.isDebugEnabled())
        {
            log.debug("createClassFile(Object[], int) - Package name=" + t.getPackageName());
        }

        String packagePath = packageName.replace('.', File.separatorChar);
        //String htmlPackagePath = packageName.replace('.', '/');
        String pathName = getOutDir() + File.separatorChar + packagePath;

        int position = fileName.lastIndexOf(File.separatorChar);

        if (position == -1) {
            position = 0;
        }

        String baseName = fileName.substring(position, fileName.length());
        //String className = baseName.substring(
        //        0, baseName.lastIndexOf('.')).replace(File.separatorChar, '.');

        baseName = baseName.replace('.', '_');
        baseName = baseName + ".html";

        String newFileName = pathName + File.separatorChar + baseName;
        File f = new File(newFileName);

        createDirs(f);

        HTMLOutputWriter output = new LineOutputWriter(
                new BufferedOutputStream(new FileOutputStream(f)));
        String backup = getBackupPath(tagList, element);
        String header =
                "<head>\n"
                + "<LINK rel=\"stylesheet\" type=\"text/css\" name=\"style1\" "
                + "href=\"" + backup + "styles.css\">\n" + "</head>\n";

        output.write(header, 0, header.length());

        // "'<A HREF=\"./"+htmlPackagePath+"/classList.html\" TARGET=\"packageFrame\">" + packageName + "</A>: " + SymbolTable.getClassList(t.getFile()) + "');\n"+
        packagePath = packageName.replace('.', '/');

        output.write("<pre>\n", 0, 6);

        return (output);
    }

    /**
     * Method finishFile
     *
     * @param input
     * @param output
     * @throws IOException
     */
    public void finishFile(LineNumberReader input, HTMLOutputWriter output)
            throws IOException {

        while (_currentChar != -1) {
            output.writeHTML(_currentChar);

            _currentChar = input.read();
        }

        input.close();
        output.write("</pre>\n", 0, 7);
        output.flush();
        output.close();
    }

    /**
     * Method writeUntilNextTag
     *
     * @param t
     * @param input
     * @param output
     * @throws IOException
     */
    public void writeUntilNextTag(
            HTMLTag t, LineNumberReader input, HTMLOutputWriter output)
            throws IOException {

        if (log.isDebugEnabled())
        {
            log.debug("writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Looking for next tag line:|");
        }

        while ((_currentChar != -1)
                && (input.getLineNumber() + 1) != t.getLine()) {
            output.writeHTML(_currentChar);

            if (log.isDebugEnabled())
            {
                log.debug("writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar" + _currentChar);
            }

            _currentChar = input.read();
        }

        // Write out last carriage return
        output.writeHTML(_currentChar);

        if (log.isDebugEnabled())
        {
            log.debug("writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar" + _currentChar);
        }

        _currentChar = input.read();

        if (log.isDebugEnabled())
        {
            log.debug("writeUntilNextTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - |");
        }
    }

    /**
     * Method writeComment
     *
     * @param t
     * @param input
     * @param output
     * @throws IOException
     */
    public void writeComment(
            HTMLTag t, LineNumberReader input, HTMLOutputWriter output)
            throws IOException {

        int length = t.getLength();
        int i = 0;

        output.write("<span class=\"comment\">");

        while (i < length) {
            if(_currentChar == '\n') {
                output.write("</span>");
            }
            output.writeHTML((char) _currentChar);

            if (_currentChar == '\n') {
                output.write("<span class=\"comment\">");
                _currentColumn = 0;
            }

            _currentChar = input.read();

            _currentColumn++;
            i++;
        }

        output.write("</span>");

        if (_currentChar == '\n') {
            _currentColumn = 0;
        }
    }

    /**
     * Method writeHTMLTag
     *
     * @param t
     * @param input
     * @param output
     * @throws IOException
     */
    public void writeHTMLTag(
            HTMLTag t, LineNumberReader input, HTMLOutputWriter output)
            throws IOException {

        // Write out line from current column to tag start column
        if (log.isDebugEnabled())
        {
            log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Current column=" + _currentColumn);
            log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Writing up to tag start:|");
        }

        while (_currentColumn < t.getStartColumn()) {
            output.writeHTML(_currentChar);

            if (_currentChar == '\n') {
                _currentColumn = 0;
            }

            if (log.isDebugEnabled())
            {
                log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar=" + _currentChar);
            }

            _currentChar = input.read();

            _currentColumn++;
        }

        if (log.isDebugEnabled())
        {
            log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - |");
        }

        // Check for comment
        if (t.isComment()) {
            writeComment(t, input, output);
        } else if (t.isLiteral()) {
                writeLiteral(t, input, output);
        } else if (t.isKeyword()) {
                writeKeyword(t, input, output);
        } else {

            // Write HTML tag
            output.write(t.getText());

            if (log.isDebugEnabled())
            {
                log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Wrote tag:" + t.getText());
            }

            // Read past original token
            int length = t.getOrigLength();

            length = length - t.getNumBreaks();

            if (log.isDebugEnabled())
            {
                log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - Skipping:\"");
            }

            for (int j = 0; j < length; j++) {
                if (log.isDebugEnabled())
                {
                    log.debug("writeHTMLTag(HTMLTag, LineNumberReader, HTMLOutputWriter) - _currentChar=" + _currentChar);
                }

                if (_currentChar == '\n') {
                    _currentColumn = 0;
                }

                _currentChar = input.read();

                _currentColumn++;
            }

            if (_currentChar == '\n') {
                _currentColumn = 0;
            }
        }
    }

    private void writeLiteral(
            HTMLTag t, LineNumberReader input, HTMLOutputWriter output)
            throws IOException {

        int length = t.getLength();
        int i = 0;

        output.write("<span class=\"string\">");

        while (i < length) {
            output.writeHTML((char) _currentChar);

            if (_currentChar == '\n') {
                _currentColumn = 0;
            }

            _currentChar = input.read();

            _currentColumn++;
            i++;
        }

        output.write("</span>");

        if (_currentChar == '\n') {
            _currentColumn = 0;
        }
    }

    private void writeKeyword(
            HTMLTag t, LineNumberReader input, HTMLOutputWriter output)
            throws IOException {

        int length = t.getLength();
        int i = 0;

        output.write("<strong>");

        while (i < length) {
            output.writeHTML((char) _currentChar);

            if (_currentChar == '\n') {
                _currentColumn = 0;
            }

            _currentChar = input.read();

            _currentColumn++;
            i++;
        }

        output.write("</strong>");

        if (_currentChar == '\n') {
            _currentColumn = 0;
        }
    }
    /**
     * Method createClassFiles
     *
     * @param tagList
     */
    public void createClassFiles(Vector tagList) {

        HTMLTag t;
        File javaFile;
        LineNumberReader input;
        HTMLOutputWriter output;
        Object[] sortedList;

        sortedList = JSCollections.sortVector(tagList);

        // Collections.sort(tagList);
        t = (HTMLTag) sortedList[0];
        javaFile = t.getFile();

        printAdvancement("Writing tags for file " + javaFile.toString());

        // Create first file
        try {
            output = createClassFile(sortedList, 0);
            input = new LineNumberReader(
                    new InputStreamReader(
                            new SkipCRInputStream(new FileInputStream(javaFile))));
            _currentChar = input.read();
            _currentColumn = 1;
        } catch (Exception e) {
            log.error( "1: Could not open file:" + javaFile.getAbsolutePath() + " or html file.", e );
            return;
        }

        for (int i = 0; i < sortedList.length; i++) {
            t = (HTMLTag) sortedList[i];

            if (log.isDebugEnabled())
            {
                log.debug("createClassFiles(Vector) - nTag Text=\"" + t.getText() + "\"");
                log.debug("createClassFiles(Vector) - Length=" + t.getOrigLength());
                log.debug("createClassFiles(Vector) - Line,col=" + t.getLine() + "," + t.getStartColumn());
            }

            // Check for new java file encountered.
            // Close previous files and open new ones.
            String currentFile = javaFile.toString();
            String newFile = t.getFile().toString();

            if (log.isDebugEnabled())
            {
                log.debug("createClassFiles(Vector) - cur file=|" + currentFile + "|");
                log.debug("createClassFiles(Vector) - new file=|" + newFile + "|");
            }

            if (!newFile.equals(currentFile)) {
                try {

                    // Write out rest of previous file
                    finishFile(input, output);

                    // Open new file
                    javaFile = t.getFile();
                    input = new LineNumberReader(
                            new InputStreamReader(
                                    new SkipCRInputStream(
                                            new FileInputStream(javaFile))));
                    output = createClassFile(sortedList, i);
                    _currentColumn = 1;
                    _currentChar = input.read();
                } catch (Exception e) {
                    log.error( "2: Error handling tag:" + t, e );

                    continue;
                }
            }

            // Check for new line encountered
            if (t.getLine() != (input.getLineNumber() + 1)) {
                _currentColumn = 1;

                // Write out characters until we reach the line
                try {
                    writeUntilNextTag(t, input, output);
                } catch (Exception e) {
                    log.error( "3: Error handling tag:" + t, e );

                    continue;
                }
            }

            try {
                writeHTMLTag(t, input, output);
            } catch (Exception e) {
                log.error( "4: Error handling tag:" + t, e );

                continue;
            }
        }    // end for

        // Finish writing out the file
        try {
            finishFile(input, output);
        } catch (IOException e) {
            log.error( "IOException: " + e.getMessage(), e );
        }
    }

    private void print(String description) {
        System.out.print(description);
    }

    private void println(String description) {
        System.out.print("\n");
        System.out.print(description);
    }

    private void printAdvancement(String description) {
        if ( getVerbose() ) {
            System.out.println(description);
        } else {
            System.out.print(".");
        }
    }

    /**
     * Method main
     *
     * @param args
     */
    public static void main(String args[]) {

        Pass1 p1 = new Pass1();

        p1.initializeDefaults();
        p1.run(args);
    }

    /** Field _currentColumn */
    private int _currentColumn;

    /** Field _currentChar */
    private int _currentChar;

    /** Field _outDir */
    private String _outDir;

    /** Field _title */
    private String _title;

    /** Field _doRecurse */
    private boolean _doRecurse;

    /** Field _verbose */
    private boolean _verbose;
}
