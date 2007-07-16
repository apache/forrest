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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that mounts a Document in HTML as document
 *
 * @version $Id: $
 */
public class GenerateHTMLDoc {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( GenerateHTMLDoc.class );

    private static FileOutputStream fos;
    private static BufferedReader br;
    private static String LINE_SEPARATOR = String.valueOf((char) 13) + String.valueOf((char) 10);
    private static String stringReader;
    private static int functionCount = 0;
    private static String functionName = "";
    private static boolean parameterList = false;
    private static boolean useList = false;
    private boolean summary = true;
    private boolean description = true;

    public GenerateHTMLDoc(File fis, String destDir) {
        String nomeArquivo = fis.getName();
        try {
            fos = new FileOutputStream(destDir + nomeArquivo.substring(0, nomeArquivo.indexOf(".")) + ".htm");
            br = new BufferedReader(new FileReader(fis));

        } catch (FileNotFoundException fnfe) {
            log.error( "FileNotFoundException: " + fnfe.getMessage(), fnfe );
        }

        try {
            fos.write(("<html>" + LINE_SEPARATOR).getBytes());
            fos.write(("<head>" + LINE_SEPARATOR).getBytes());
            fos.write(("<style type='text/css'>" + LINE_SEPARATOR).getBytes());
            fos.write((".TableHeadingColor     { background: #CCCCFF } /* Dark mauve */" + LINE_SEPARATOR).getBytes());
            fos.write((".NavBarCell1    { background-color:#EEEEFF;}/* Light mauve */" + LINE_SEPARATOR).getBytes());
            fos.write(("</style>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TABLE WIDTH='100%'><TR><TD WIDTH='100%' CLASS='NavBarCell1'><H1>Javascript code documentation</H1></TD></TR></TABLE>" + LINE_SEPARATOR).getBytes());
            fos.write(("<title>Javascript code documentation</title>" + LINE_SEPARATOR).getBytes());
            fos.write(("</head>" + LINE_SEPARATOR).getBytes());
            fos.write(("<body>" + LINE_SEPARATOR).getBytes());
            fos.write(("<H2>Filename: " + nomeArquivo + "</H2>" + LINE_SEPARATOR).getBytes());
            fos.write(("<br>" + LINE_SEPARATOR).getBytes());
            fos.write(("<br>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TABLE BORDER='1' CELLPADDING='3' CELLSPACING='0' WIDTH='100%'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TR CLASS='TableHeadingColor'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<TD ALIGN='left' colspan='2'><FONT SIZE='+2'>" + LINE_SEPARATOR).getBytes());
            fos.write(("<B>Function Summary</B></FONT></TD>" + LINE_SEPARATOR).getBytes());
            fos.write(("</TR>" + LINE_SEPARATOR).getBytes());
            while (br.ready()) {
                stringReader = br.readLine();

                while (summary && null != stringReader && stringReader.indexOf("summary") == -1) {
                    stringReader = br.readLine();

                }
                summary = false;
                if (null != stringReader && stringReader.indexOf("/**") != -1) {
                    fos.write(("<TR>" + LINE_SEPARATOR).getBytes());
                    fos.write(("<TD WIDTH='30%' BGCOLOR='#f3f3f3'><font face='Verdana'><b><span id='Function" + functionCount + "'></b></font></span></TD>" + LINE_SEPARATOR).getBytes());
                    fos.write(("<TD WIDTH='70%'>" + LINE_SEPARATOR).getBytes());
                    stringReader = br.readLine();

                    while (null != stringReader && stringReader.indexOf("*/") == -1) {

                        if (stringReader.indexOf("* @") != -1) {
                            if ((stringReader.indexOf("author") == -1)) {
                                if (stringReader.indexOf("param") != -1) {
                                    if (parameterList == false) {
                                        parameterList = true;
                                        fos.write("<font size='-1' face='Verdana'><b>Parameters: </b></font>".getBytes());
                                        fos.write("<BR>".getBytes());
                                    }
                                    fos.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".getBytes());
                                    fos.write((stringReader.substring(stringReader.indexOf("* @param") + 9) + LINE_SEPARATOR).getBytes());
                                } else if (stringReader.indexOf("use") != -1) {
                                    if (useList == false) {
                                        useList = true;
                                        fos.write("<font size='-1' face='Verdana'><b>Uso: </b></font>".getBytes());
                                        fos.write("<BR>".getBytes());
                                    }
                                    fos.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".getBytes());
                                    fos.write((stringReader.substring(stringReader.indexOf("* @use") + 7) + LINE_SEPARATOR).getBytes());
                                } else if (stringReader.indexOf("return") != -1) {
                                    fos.write("<font size='-1' face='Verdana'><b>Return type: </b></font>".getBytes());
                                    fos.write((stringReader.substring(stringReader.indexOf("* @return") + 10) + LINE_SEPARATOR).getBytes());
                                }
                                fos.write("<BR>".getBytes());
                            }
                        } else {
                            if (description) {
                                description = false;
                                fos.write("<font size='-1' face='Verdana'><b>Description: </b></font>".getBytes());
                            } else
                                fos.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".getBytes());
                            fos.write((stringReader.substring(stringReader.indexOf("*") + 1) + LINE_SEPARATOR).getBytes());
                            fos.write("<BR>".getBytes());
                        }
                        stringReader = br.readLine();
                    }
                    description = true;
                    parameterList = false;
                    useList = false;
                    while (null != stringReader && stringReader.indexOf("function") == -1) {
                        stringReader = br.readLine();
                    }
                    if (stringReader.indexOf("function") != -1) {
                        if (stringReader.indexOf("{") != -1) {
                            functionName = stringReader.substring(stringReader.indexOf("function") + 9, stringReader.indexOf("{"));
                        } else {
                            functionName = stringReader.substring(stringReader.indexOf("function") + 9);
                        }
                    }
                    fos.write(("</TD>" + LINE_SEPARATOR).getBytes());
                    fos.write(("<script>document.all.Function" + functionCount + ".innerText = '" + functionName + "'; </script>" + LINE_SEPARATOR).getBytes());
                    functionCount++;
                    fos.write(("</TR>" + LINE_SEPARATOR).getBytes());
                }
            }
            fos.write(("</TABLE>" + LINE_SEPARATOR).getBytes());
            fos.write("</br>".getBytes());
            fos.write(("<a href='javascript:history.back()'><font size='+1'>Back</font></a>" + LINE_SEPARATOR).getBytes());
            fos.write("</body>".getBytes());
            fos.write("</html>".getBytes());

        } catch (IOException ioe) {
            log.error( "IOException: " + ioe.getMessage(), ioe );
        }

        if ( log.isInfoEnabled() )
        {
            log.info( "Html generated with success!");
        }
    }

    public static void main(String args[]) throws Exception{

        GenerateHTMLDoc main1 = new GenerateHTMLDoc(new File(args[0]), args[1]);
    }
}
