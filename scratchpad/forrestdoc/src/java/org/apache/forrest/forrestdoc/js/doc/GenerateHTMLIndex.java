/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/js/doc/GenerateHTMLIndex.java,v 1.3 2004/02/19 23:53:02 nicolaken Exp $
 * $Revision: 1.3 $
 * $Date: 2004/02/19 23:53:02 $
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

package org.apache.forrest.forrestdoc.js.doc;

import org.apache.tools.ant.BuildException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * @author Mailton
 *         <p/>
 *         Searches all javascript files and creates a index HTML
 *         with links to documentation
 */
public class GenerateHTMLIndex {

    private static File file, file2 = null;
    private static FileInputStream fis;
    private static FileOutputStream fos;
    private static BufferedReader br;
    private static String stringReader;
    private static String LINE_SEPARATOR = String.valueOf((char) 13) + String.valueOf((char) 10);
    private static Vector v = new Vector();
    private static GenerateHTMLDoc docGenerator;

    public GenerateHTMLIndex(String jSDir, String destDir) {

        if (jSDir == null) {
            throw new BuildException("jSDir attribute can't be empty");
        }
        if (destDir == null) {
            throw new BuildException("destDir can't be empty");
        }
        if (!"/".equals(jSDir.substring(jSDir.length() - 1))) {
            jSDir = jSDir + "/";
        }
        if (!"/".equals(destDir.substring(destDir.length() - 1))) {
            destDir = destDir + "/";
        }

        file = new File(jSDir);
        
        if (!file.isDirectory()) {
            throw new BuildException("destDir has to be a directory");
        }
        
        collectFiles(file, v);
        
        try {
            fos = new FileOutputStream(destDir + "index.htm");

        } catch (FileNotFoundException fnfe) {
            try {
                file = new File(destDir);
                file.mkdir();
                fos = new FileOutputStream(destDir + "index.htm");
            } catch (FileNotFoundException e) {
            }
        }
        try {
            fos.write(("<html>" + LINE_SEPARATOR).getBytes());
            fos.write(("<head>" + LINE_SEPARATOR).getBytes());
            fos.write(("<style type='text/css'>" + LINE_SEPARATOR).getBytes());
            fos.write((".TableHeadingColor     { background: #CCCCFF } /* Dark mauve */" + LINE_SEPARATOR).getBytes());
            fos.write((".NavBarCell1    { background-color:#EEEEFF;}/* Light mauve */" + LINE_SEPARATOR).getBytes());
            fos.write(("</style>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TABLE WIDTH='100%'><TR><TD WIDTH='100%' CLASS='NavBarCell1'><H1>JavaScript Code Documentation</H1></TD></TR></TABLE>" + LINE_SEPARATOR).getBytes());
            fos.write(("<title>JavaScript Code Documentation</title>" + LINE_SEPARATOR).getBytes());
            fos.write(("</head>" + LINE_SEPARATOR).getBytes());
            fos.write(("<body>" + LINE_SEPARATOR).getBytes());
            fos.write(("<H2>Index</H2>" + LINE_SEPARATOR).getBytes());
            fos.write(("<br>" + LINE_SEPARATOR).getBytes());
            fos.write(("<br>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TABLE BORDER='1' CELLPADDING='3' CELLSPACING='0' WIDTH='100%'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TR CLASS='TableHeadingColor'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TD ALIGN='left'><FONT SIZE='+2' WIDTH='30%'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<B>Filename</B></FONT></TD>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TD ALIGN='left'><FONT SIZE='+2' WIDTH='70%'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<B>Summary</B></FONT></TD>" + LINE_SEPARATOR).getBytes());
            fos.write(("</TR>" + LINE_SEPARATOR).getBytes());

            for (int i = 0; i < v.size(); i++) {
                file = (File) v.get(i);
                docGenerator = new GenerateHTMLDoc(file, destDir);
            }
            System.out.println("Number of .js files: " + v.size());
            for (int i = 0; i < v.size(); i++) {
                System.out.println(file.getName());
                file = (File) v.get(i);

                fos.write(("<TR>" + LINE_SEPARATOR).getBytes());
                fos.write(("<TD WIDTH='30%' BGCOLOR='#f3f3f3'><font face='Verdana'><b><a href='" + file.getName().substring(0, file.getName().indexOf(".")) + ".htm" + "'>" + file.getName() + "</a></b></font></TD>" + LINE_SEPARATOR).getBytes());
                fos.write(("<TD WIDTH='70%'>" + LINE_SEPARATOR).getBytes());

                try {
                    fis = new FileInputStream(file);
                    br = new BufferedReader(new InputStreamReader(fis));

                    while (br.ready()) {
                        stringReader = br.readLine();
                        if (null != stringReader && stringReader.indexOf("/**") != -1) {
                            stringReader = br.readLine();
                            while (null != stringReader && stringReader.indexOf("*/") == -1) {
                                if (stringReader.indexOf("* @") != -1) {
                                    if (stringReader.indexOf("summary") != -1) {

                                        fos.write((stringReader.substring(stringReader.indexOf("* @summary") + 11) + LINE_SEPARATOR).getBytes());
                                        fos.write("<BR>".getBytes());
                                    }
                                }
                                stringReader = br.readLine();
                            }
                        }
                    }

                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                }

                fos.write(("</TD>" + LINE_SEPARATOR).getBytes());
                fos.write(("</TR>" + LINE_SEPARATOR).getBytes());
            }

            fos.write(("</TABLE>" + LINE_SEPARATOR).getBytes());
            fos.write("</body>".getBytes());
            fos.write("</html>".getBytes());

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    
    private void collectFiles(File baseDir, Vector fileVector) {
        File[] fileList = baseDir.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()){
                collectFiles(fileList[i], fileVector);
            }    
            else if (fileList[i].getName().indexOf(".js") != -1) {
                v.addElement(fileList[i]);
            }
        }
    }    
            
    public static void main(String[] args) {
        GenerateHTMLIndex index = new GenerateHTMLIndex(args[0], args[1]);
    }
}





















