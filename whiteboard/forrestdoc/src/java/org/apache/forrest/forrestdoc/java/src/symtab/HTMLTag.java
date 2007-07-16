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
package org.apache.forrest.forrestdoc.java.src.symtab;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.forrest.forrestdoc.java.src.util.JSComparable;

/**
 * An occurrence of an HTML Tag in a file
 *
 * @version $Id: $
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

        this.file = f;
        this.line = l;
        this.startColumn = c;
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
        this.length = length;
        this.packageName = packageName;
        this.type = type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "HTMLTag [" + file + "," + line + "," + startColumn + "-"
                + endColumn + "," + replaceText + "]";
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.util.JSComparable#compareTo(java.lang.Object)
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
