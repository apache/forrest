/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/LineOutputWriter.java,v 1.1 2004/02/09 11:09:18 nicolaken Exp $
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class LineOutputWriter
 * 
 * @author 
 * @version %I%, %G%
 */
public class LineOutputWriter extends HTMLOutputWriter {

    /** Field MAXCOLS */
    public static int MAXCOLS = 5;

    /**
     * The max number of line number digits.
     * Sadly, the pipe pattern doesn't let us use the real value
     * discovered at runtime.
     * No class file should have more than 9999 lines anyway.
     */
    public static final int MAX_LINE_NUMBER_DIGITS = 5;

    /**
     * Constructor LineOutputWriter
     * 
     * @param output 
     */
    public LineOutputWriter(OutputStream output) {

        super(output);

        _lineNumber = 1;
        _firstLine = true;
        _oldLength = 1;
        _spaceString = "";
    }

    /**
     * Method write
     * 
     * @param c 
     * @throws IOException 
     */
    public void write(int c) throws IOException {

        if (_firstLine) {
            _firstLine = false;

            writeLineNumber();
        }

        super.write(c);

        if (c == '\n') {
            writeLineNumber();
        }
    }

    /**
     * Method writeHTML
     * 
     * @param c 
     * @throws IOException 
     */
    public void writeHTML(int c) throws IOException {

        if (_firstLine) {
            _firstLine = false;

            writeLineNumber();
        }

        super.writeHTML(c);

        if (c == '\n') {
            writeLineNumber();
        }
    }

    /**
     * Method write
     * 
     * @param s 
     * @throws IOException 
     */
    public void write(String s) throws IOException {

        int length = s.length();

        for (int i = 0; i < length; i++) {
            this.write(s.charAt(i));
        }
    }

    /**
     * Method writeHTML
     * 
     * @param s 
     * @throws IOException 
     */
    public void writeHTML(String s) throws IOException {

        int length = s.length();

        for (int i = 0; i < length; i++) {
            this.writeHTML(s.charAt(i));
        }
    }

    /**
     * Method writeLineNumber
     * 
     * @throws IOException 
     */
    public void writeLineNumber() throws IOException {

        String lineString = Integer.toString(_lineNumber);
        String spacing = "";

        for (int i = 0; i < MAX_LINE_NUMBER_DIGITS - lineString.length(); i++) {
            spacing += " ";
        }

        // add linenum tag
        lineString = "<a href=\"#" + _lineNumber + "\" name=" + lineString + " class=\"linenum\">"
                + lineString;
        lineString += "</a>";
        lineString += spacing;

        this.write(lineString);

        _lineNumber++;
    }

    /** Field _lineNumber */
    private int _lineNumber;

    /** Field _firstLine */
    private boolean _firstLine;

    /** Field _oldLength */
    private int _oldLength;

    /** Field _spaceString */
    private String _spaceString;
}
