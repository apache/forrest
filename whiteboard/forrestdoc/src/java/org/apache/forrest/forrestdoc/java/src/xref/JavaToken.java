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
package org.apache.forrest.forrestdoc.java.src.xref;

import java.io.File;

/**
 * A simple token that is used to relay information from the scanner to
 * the parser.  We've extended it to save information about the file from
 * which the token was created, and the number of parameters (telling if the
 * symbol looked like a method invocation or some other symbol reference.)
 *
 * @version $Id: $
 */
public class JavaToken extends antlr.CommonToken {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /**
     * A count of the parameters used to call a method.
     * -1 means the symbol is not a method invocation
     */
    private int paramCount = -1;

    /** A reference to the File that was scanned to create this symbol */
    private File file = null;

    /** Field column */
    protected int column;

    /** Field charNum */
    protected int charNum;

    /** Field packageName */
    protected String packageName;

    /** Field className */
    protected String className;

    /** Field methodName */
    protected String methodName;

    /**
     * @see antlr.CommonToken#getColumn()
     */
    public int getColumn() {
        return column;
    }

    /**
     * @see antlr.CommonToken#setColumn(int)
     */
    public void setColumn(int c) {
        column = c;
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
     * Method setPackageName
     *
     * @param name
     */
    public void setPackageName(String name) {

        if (name != null) {
            packageName = name.intern();
        } else {
            packageName = null;
        }
    }

    /**
     * Method getClassName
     *
     * @return
     */
    public String getClassName() {
        return className;
    }

    /**
     * Method setClassName
     *
     * @param name
     */
    public void setClassName(String name) {

        if (name != null) {
            className = name.intern();
        } else {
            className = null;
        }
    }

    /**
     * Method getMethodName
     *
     * @return
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Method setMethodName
     *
     * @param name
     */
    public void setMethodName(String name) {

        if (name != null) {
            methodName = name.intern();
        } else {
            methodName = null;
        }
    }

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor JavaToken
     */
    public JavaToken() {
    }

    /**
     * Constructor JavaToken
     *
     * @param t
     */
    public JavaToken(JavaToken t) {

        column = t.getColumn();
        file = t.getFile();
        packageName = t.getPackageName();
        paramCount = t.getParamCount();

        this.setLine(t.getLine());

        if (t.getText() != null) {
            this.setText(t.getText().intern());
        } else {
            this.setText(null);
        }

        this.setType(t.getType());
    }

    /**
     * get the File that contained the text scanned for this token
     *
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * get the number of parameters for this token (if it represents a
     * method invocation
     *
     * @return
     */
    public int getParamCount() {
        return paramCount;
    }

    /**
     * Sets the file property of this token
     *
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Sets the parameter count property of this token
     *
     * @param count
     */
    public void setParamCount(int count) {
        paramCount = count;
    }

    /**
     * @see antlr.CommonToken#toString()
     */
    public String toString() {

        return "[\"" + getText() + "\",type:<" + getType() + ">,line:"
                + getLine() + ",col:" + getColumn() + ",file:"
                + getFile().getName() + "]";
    }
}
