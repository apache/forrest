/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/HTMLTag.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:12 $
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
package org.apache.forrest.forrestdoc.java.src.symtab;

import org.apache.forrest.forrestdoc.java.src.util.JSComparable;

import java.io.File;
import java.util.StringTokenizer;

/**
 * An occurrence of an HTML Tag in a file
 */
public class HTMLTag implements JSComparable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The file containing the occurrence */
    private File file;

    /** The line number containing the occurrence */
    private int line;

    /** The start column of the occurrence */
    private int startColumn;

    /** The end column of the occurrence */
    private int endColumn;

    /** Character num of start of token */
    private int charNum = 0;

    /** The text for the tag */
    private String replaceText;

    /** Field packageName */
    private String packageName;

    /** Field length */
    private int length;

    public static int TYPE_NONE = 0;
    public static int TYPE_COMMENT = 1;
    public static int TYPE_LITERAL = 2;
    public static int TYPE_KEYWORD = 3;
    private int type = TYPE_NONE;

    /** Field numBreaks */
    private int numBreaks;

    /** Field origLength */
    private int origLength;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Method getLine
     * 
     * @return 
     */
    public int getLine() {
        return line;
    }

    /**
     * Method getStartColumn
     * 
     * @return 
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Method getEndColumn
     * 
     * @return 
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Method getFile
     * 
     * @return 
     */
    public File getFile() {
        return file;
    }

    /**
     * Method getText
     * 
     * @return 
     */
    public String getText() {
        return replaceText;
    }

    /**
     * Method getPackageName
     * 
     * @return 
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Method getLength
     * 
     * @return 
     */
    public int getLength() {
        return length;
    }

    /**
     * Method isComment
     * 
     * @return 
     */
    public boolean isComment() {
        return (type == TYPE_COMMENT);
    }

    /**
     * Method isLiteral
     * 
     * @return 
     */
    public boolean isLiteral() {
        return (type == TYPE_LITERAL);
    }
    
    /**
     * Method isKeyword
     * 
     * @return 
     */
    public boolean isKeyword() {
        return (type == TYPE_KEYWORD);
    }
    
    /**
     * Method getNumBreaks
     * 
     * @return 
     */
    public int getNumBreaks() {
        return numBreaks;
    }

    /**
     * Method getOrigLength
     * 
     * @return 
     */
    public int getOrigLength() {
        return origLength;
    }

    /**
     * Constructor to define a new tag
     * 
     * @param o           
     * @param origText    
     * @param replaceText 
     */
    HTMLTag(Occurrence o, String origText, String replaceText) {

        // if (origText.equals("SymbolTable"))
        // new Exception("occ="+o+" origText="+origText+" replaceText="+replaceText).printStackTrace();
        this.file = o.getFile();
        this.line = o.getLine();
        this.startColumn = o.getColumn();
        this.endColumn = startColumn + origText.length() - 1;
        this.replaceText = replaceText;
        this.packageName = o.getPackageName();
        this.numBreaks = 0;

        if (origText.indexOf("\r\n") > 0) {
            StringTokenizer st = new StringTokenizer(origText, "\r\n");

            numBreaks = Math.max(st.countTokens() - 1, 1);
        }

        this.origLength = origText.length();
    }

    /**
     * Constructor to define a new tag
     * 
     * @param f           
     * @param l           
     * @param c           
     * @param packageName 
     * @param origText    
     * @param replaceText 
     */
    HTMLTag(File f, int l, int c, String packageName, String origText,
            String replaceText) {

        // if (origText.equals("SymbolTable"))
        // new Exception().printStackTrace();
        this.file = f;
        this.line = l;
        this.startColumn = c;
        this.charNum = charNum;
        this.endColumn = startColumn + origText.length() - 1;
        this.replaceText = replaceText;
        this.packageName = packageName;
        this.numBreaks = 0;

        if (origText.indexOf("\r\n") > 0) {
            StringTokenizer st = new StringTokenizer(origText, "\r\n");

            numBreaks = Math.max(st.countTokens() - 1, 1);
        }

        this.origLength = origText.length();
    }

    /**
     * Constructor for comment.  Doesn't store text
     * 
     * @param f           
     * @param l           
     * @param c           
     * @param packageName 
     * @param length      
     */
    HTMLTag(File f, int l, int c, String packageName, int length) {

        this.file = f;
        this.line = l;
        this.startColumn = c;
        this.charNum = charNum;
        this.length = length;
        this.packageName = packageName;
        this.type = TYPE_COMMENT;
    }

    /**
     * Constructor for comment.  Doesn't store text
     * 
     * @param f           
     * @param l           
     * @param c           
     * @param packageName 
     * @param length      
     */
    HTMLTag(File f, int l, int c, String packageName, int length, int type) {

        this.file = f;
        this.line = l;
        this.startColumn = c;
        this.charNum = charNum;
        this.length = length;
        this.packageName = packageName;
        this.type = type;
    }
    
    /**
     * return a string representation of the occurrence
     * 
     * @return 
     */
    public String toString() {
        return "HTMLTag [" + file + "," + line + "," + startColumn + "-"
                + endColumn + "," + replaceText + "]";
    }

    /**
     * Method compareTo
     * 
     * @param o 
     * @return 
     */
    public int compareTo(Object o) {

        HTMLTag t = (HTMLTag) o;

        if (getLine() < t.getLine()) {
            return -1;
        } else if (getLine() > t.getLine()) {
            return 1;
        }

        if (getStartColumn() < t.getStartColumn()) {
            return -1;
        } else if (getStartColumn() > t.getStartColumn()) {
            return 1;
        }

        return 0;
    }
}
