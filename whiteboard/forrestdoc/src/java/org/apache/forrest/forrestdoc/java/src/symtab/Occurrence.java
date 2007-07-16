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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An occurrence of an indentifier in a file
 *
 * @version $Id: $
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
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Occurrence [" + file + "," + line + "," + column + "]";
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
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

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
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
