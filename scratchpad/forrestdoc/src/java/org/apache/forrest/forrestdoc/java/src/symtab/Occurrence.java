/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/Occurrence.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An occurrence of an indentifier in a file
 */
public class Occurrence implements java.io.Externalizable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The file containing the occurrence */
    private File file;

    /** The line number containing the occurrence */
    private int line;

    /** The column number containing the occurrence */
    private int column;

    /** Field _packageName */
    private String _packageName = "";

    /** Field _className */
    private String _className = "";

    /** Field _methodName */
    private String _methodName = "";

    /** The definition I reference */
    private Definition _myDefinition;

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
     * Method getColumn
     * 
     * @return 
     */
    public int getColumn() {
        return column;
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
     * Method getPackageName
     * 
     * @return 
     */
    public String getPackageName() {
        return _packageName;
    }

    /**
     * Method getClassName
     * 
     * @return 
     */
    public String getClassName() {
        return _className;
    }

    /**
     * Method getMethodName
     * 
     * @return 
     */
    public String getMethodName() {
        return _methodName;
    }

    /**
     * Method getDefinition
     * 
     * @return 
     */
    public Definition getDefinition() {
        return _myDefinition;
    }

    /**
     * Method setPackageName
     * 
     * @param packageName 
     */
    public void setPackageName(String packageName) {

        if (packageName != null) {
            _packageName = packageName.intern();
        } else {
            _packageName = packageName;
        }
    }

    /**
     * Method setClassName
     * 
     * @param className 
     */
    public void setClassName(String className) {

        if (className != null) {
            _className = className.intern();
        } else {
            _className = className;
        }
    }

    /**
     * Method setMethodName
     * 
     * @param methodName 
     */
    public void setMethodName(String methodName) {

        if (methodName != null) {
            _methodName = methodName.intern();
        } else {
            _methodName = methodName;
        }
    }

    /**
     * Method setDefinition
     * 
     * @param def 
     */
    public void setDefinition(Definition def) {
        _myDefinition = def;
    }

    /**
     * Method getOccurrenceTag
     * 
     * @return 
     */
    public HTMLTag getOccurrenceTag() {

        Definition def = getDefinition();

        return def.getOccurrenceTag(this);
    }

    /**
     * Default constructor is public for serialization
     */
    public Occurrence() {
    }

    /**
     * Constructor to define a new occurrence
     * 
     * @param file 
     * @param line 
     */
    Occurrence(File file, int line) {
        this.file = file;
        this.line = line;
    }

    /**
     * Constructor to define a new occurrence
     * 
     * @param file   
     * @param line   
     * @param column 
     */
    Occurrence(File file, int line, int column) {

        this(file, line);

        this.column = column;
    }

    /**
     * Constructor to define a new occurrence
     * 
     * @param file        
     * @param line        
     * @param column      
     * @param packageName 
     */
    Occurrence(File file, int line, int column, String packageName) {

        this(file, line, column);

        if (packageName != null) {
            _packageName = packageName.intern();
        } else {
            _packageName = packageName;
        }
    }

    /**
     * Constructor to define a new occurrence
     * 
     * @param file        
     * @param line        
     * @param column      
     * @param packageName 
     * @param className   
     * @param methodName  
     */
    Occurrence(File file, int line, int column, String packageName,
               String className, String methodName) {

        this(file, line, column, packageName);

        if (className != null) {
            _className = className.intern();
        } else {
            _className = className;
        }

        if (methodName != null) {
            _methodName = methodName.intern();
        } else {
            _methodName = methodName;
        }
    }

    /**
     * Method getLinkReference
     * 
     * @return 
     */
    public String getLinkReference() {

        String name = getFile().getName();

        name = name.replace('.', '_');
        name = name + ".html";
        name = name + "#" + Integer.toString(line);

        return (name);
    }

    /**
     * return a string representation of the occurrence
     * 
     * @return 
     */
    public String getLocation() {
        return "[" + file + ":" + line + ":" + column + "]";
    }

    /**
     * return a string representation of the occurrence
     * 
     * @return 
     */
    public String toString() {
        return "Occurrence [" + file + "," + line + "," + column + "]";
    }

    /**
     * serialize
     * 
     * @param out 
     * @throws IOException 
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeObject(file.getAbsolutePath());
        out.writeInt(line);
        out.writeInt(column);
        out.writeObject(_packageName);
        out.writeObject(_className);
        out.writeObject(_methodName);
        out.writeObject(_myDefinition);
    }

    /* deserialize */

    /**
     * Method readExternal
     * 
     * @param in 
     * @throws IOException            
     * @throws ClassNotFoundException 
     */
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        String filepath = (String) in.readObject();

        file = new File(filepath);
        line = in.readInt();
        column = in.readInt();
        _packageName = (String) in.readObject();
        _className = (String) in.readObject();
        _methodName = (String) in.readObject();
        _myDefinition = (Definition) in.readObject();
    }
}
