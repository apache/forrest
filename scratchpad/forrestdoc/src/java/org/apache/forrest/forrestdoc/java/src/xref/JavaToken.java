/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/xref/JavaToken.java,v 1.1 2004/02/09 11:09:16 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:16 $
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
package org.apache.forrest.forrestdoc.java.src.xref;

import java.io.File;

/**
 * A simple token that is used to relay information from the scanner to
 * the parser.  We've extended it to save information about the file from
 * which the token was created, and the number of parameters (telling if the
 * symbol looked like a method invocation or some other symbol reference.)
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
     * Method getColumn
     * 
     * @return 
     */
    public int getColumn() {
        return column;
    }

    /**
     * Method setColumn
     * 
     * @param c 
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
     * Method toString
     * 
     * @return 
     */
    public String toString() {

        return "[\"" + getText() + "\",type:<" + getType() + ">,line:"
                + getLine() + ",col:" + getColumn() + ",file:"
                + getFile().getName() + "]";
    }
}
