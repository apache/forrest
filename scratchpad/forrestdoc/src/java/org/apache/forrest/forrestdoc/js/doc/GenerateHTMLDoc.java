/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/js/doc/GenerateHTMLDoc.java,v 1.3 2004/02/19 23:46:23 nicolaken Exp $
 * $Revision: 1.3 $
 * $Date: 2004/02/19 23:46:23 $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Mailton
 *         <p/>
 *         Classe que monta um documento em HTML com a documentação
 *         do respectivo arquivo.
 */
public class GenerateHTMLDoc {

    private static FileInputStream fis;
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
            fnfe.printStackTrace();
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
            ioe.printStackTrace();
        }
        System.out.println("Html generated with success!");

    }

    public static void main(String args[]) throws Exception{

        GenerateHTMLDoc main1 = new GenerateHTMLDoc(new File(args[0]), args[1]);

    }
}




































