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

package org.apache.forrest.forrestdoc.js.doc;

import org.apache.log4j.Logger;

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
 * Searches all javascript files and creates a index HTML
 * with links to documentation
 *
 * @version $Id: $
 */
public class GenerateHTMLIndex {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( GenerateHTMLIndex.class );

    private static File file = null;
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
                log.error( "FileNotFoundException: " + e.getMessage(), e );
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

            if ( log.isInfoEnabled() )
            {
                log.info( "Number of .js files: " + v.size());
            }

            for (int i = 0; i < v.size(); i++) {
                if ( log.isInfoEnabled() )
                {
                    log.info( "file: " + file.getName());
                }
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
                    log.error( "FileNotFoundException: " + fnfe.getMessage(), fnfe );
                }

                fos.write(("</TD>" + LINE_SEPARATOR).getBytes());
                fos.write(("</TR>" + LINE_SEPARATOR).getBytes());
            }

            fos.write(("</TABLE>" + LINE_SEPARATOR).getBytes());
            fos.write("</body>".getBytes());
            fos.write("</html>".getBytes());

        } catch (IOException ioe) {
            log.error( "IOException: " + ioe.getMessage(), ioe );
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
