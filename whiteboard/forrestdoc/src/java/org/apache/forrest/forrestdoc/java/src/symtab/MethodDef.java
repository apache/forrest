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

import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A definition of a method in a class
 *
 * @version $Id: $
 */
public class MethodDef extends ScopedDef implements TypedDef, Externalizable {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( MethodDef.class );

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The return type of the method.  null for constructors. */
    private ClassDef type = null;

    /** A list of formal parameters to the method */
    private JavaVector parameters;

    /** A list of exceptions that can be thrown */
    private JavaVector exceptions;

    /** Field className */
    private String className;

    /** Field _uniqueMethodString */
    private String _uniqueMethodString;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Default constructor needs to be public for deserialization.
     */
    public MethodDef() {
    }

    /**
     * Constructor to create a method definition object
     *
     * @param name
     * @param occ
     * @param type
     * @param parentScope
     */
    MethodDef(String name, // the name of the method
              Occurrence occ, // where it was defined
              ClassDef type, // the return type of the method
              ScopedDef parentScope)    // which scope owns it
    {

        super(name, occ, parentScope);

        this.type = type;
    }

    /**
     * Constructor to create a method definition object
     *
     * @param name
     * @param className
     * @param occ
     * @param type
     * @param parentScope
     */
    MethodDef(String name, // the name of the method
              String className, // className (for constructor methods)
              Occurrence occ, // where it was defined
              ClassDef type, // the return type of the method
              ScopedDef parentScope) {    // which scope owns it

        this(name, occ, type, parentScope);

        if (className != null) {
            this.className = className.intern();
        } else {
            this.className = className;
        }
    }

    /**
     * Method getParameters
     *
     * @return
     */
    public Vector getParameters() {
        return parameters;
    }

    /**
     * Method getExceptions
     *
     * @return
     */
    public Vector getExceptions() {
        return exceptions;
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
     * Add a thrown exception to the method's exception list
     *
     * @param excep
     */
    void add(ClassDef excep) {

        if (exceptions == null) {    // lazy instantiation
            exceptions = new JavaVector();
        }

        exceptions.addElement(excep);
    }

    /**
     * Add a parameter to the method's parameter list
     *
     * @param param
     */
    void add(VariableDef param) {

        if (parameters == null) {    // lazy instantiation
            parameters = new JavaVector();
        }

        parameters.addElement(param);
    }

    /**
     * Method getFullName
     *
     * @return
     */
    String getFullName() {

        if (_uniqueMethodString != null) {
            return (_uniqueMethodString);
        }

        _uniqueMethodString = getName() + "(";

        if (parameters != null) {
            Enumeration e = parameters.elements();

            while (e.hasMoreElements()) {
                VariableDef d = (VariableDef) e.nextElement();

                _uniqueMethodString += d.getType().getName();

                if (e.hasMoreElements()) {
                    _uniqueMethodString += ",";
                }
            }
        }

        _uniqueMethodString += ")";

        return (_uniqueMethodString);
    }

    /**
     * Find out how many parameters this method has
     *
     * @return
     */
    int getParamCount() {

        if (parameters == null) {
            return 0;
        }

        return parameters.size();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.TypedDef#getType()
     */
    public Definition getType() {
        return type;
    }

    /**
     * lookup the name as a local variable or local class in this class
     *
     * @param name
     * @param numParams
     * @param type
     * @return
     */
    Definition lookup(String name, int numParams, Class type) {

        if (log.isDebugEnabled())
        {
            log.debug("lookup(String, int, Class) - String name=" + name);
            log.debug("lookup(String, int, Class) - String numParams=" + numParams);
        }

        if (numParams == -1) {

            // look for it in the method's scope
            Definition d = super.lookup(name, numParams, type);

            if (d != null) {
                return d;
            }

            // otherwise, look in the parameters for the method
            if (parameters != null) {
                Enumeration e = parameters.elements();

                while (e.hasMoreElements()) {
                    d = (Definition) e.nextElement();

                    if (d.getName().equals(name) && d.isA(type)) {
                        return d;
                    }
                }
            }
        }

        return null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#generateReferences(java.io.FileWriter)
     */
    public void generateReferences(FileWriter output) {

        String linkString;
        String linkFileName;
        String methodName = getName();

        if ((methodName != null)
                && (methodName.indexOf("~constructor~") >= 0)) {
            methodName = this.className;
        }

        try {
            output.write("<p class=\"methodReflist\">");

            String nameString =
                    "<p class=\"methodReflistHeader\">Method: <a name="
                    + getFullName() + " href=" + getSourceName() + "#"
                    + getClassScopeName() + ">" + getFullName() + "</a></p>";

            output.write(nameString);

            JavaVector v = getReferences();
            Enumeration e = v.elements();

            while (e.hasMoreElements()) {
                Occurrence o = (Occurrence) e.nextElement();

                if (o != null) {
                    linkFileName = getOccurrencePath(o) + o.getLinkReference();
                    linkString = "<p class=\"methodRefItem\"><a href="
                            + linkFileName + ">" + getName() + " in "
                            + o.getPackageName() + "."
                            + o.getClassName() + "." + o.getMethodName()
                            + " (" + o.getFile().getName() + ":"
                            + Integer.toString(o.getLine())
                            + ")</a></p>\n";

                    output.write(linkString);
                }
            }

            output.write("</p>");
        } catch (IOException e) {
            log.error( "IOException: " + e.getMessage(), e );
        }
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#generateTags(org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer)
     */
    public void generateTags(HTMLTagContainer tagList) {

        String methodName = getName();

        if ((methodName != null)
                && (methodName.indexOf("~constructor~") >= 0)) {
            methodName = this.className;
        }

        if ((methodName != null) && (methodName.indexOf("~class-init~") >= 0)) {
            methodName = "";
        }

        String nameString = "<a class=\"methodDef\" name="
                + getClassScopeName() + " href=" + getRefName()
                + "#" + getClassScopeName() + ">" + methodName
                + "</a>";

        // generate tag for this method
        if (getOccurrence() == null) {
            return;
        }

        HTMLTag t = new HTMLTag(getOccurrence(), methodName, nameString);

        tagList.addElement(t);

        if(parameters != null) {
            parameters.generateTags(tagList);
        }

        if(elements != null){
            elements.tagElements(tagList);
        }
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#getOccurrenceTag(org.apache.forrest.forrestdoc.java.src.symtab.Occurrence)
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {

        String methodName = getName();
        String linkString;
        String linkFileName;

        if ((methodName != null)
                && (methodName.indexOf("~constructor~") >= 0)) {
            methodName = this.className;
        }

        if ((methodName != null) && (methodName.indexOf("~class-init~") >= 0)) {
            methodName = "";
        }

        String definerName = getOccurrence().getClassName();

        linkFileName = getRelativePath(occ) + getSourceName();
        linkString = "<a class=\"methodRef\" title=\"" + definerName + "\" "
                + "href=" + linkFileName + "#" + getClassScopeName()
                + ">" + methodName + "</a>";

        HTMLTag t = new HTMLTag(occ, methodName, linkString);

        return t;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#resolveTypes(org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable)
     */
    void resolveTypes(SymbolTable symbolTable) {

        if (log.isDebugEnabled())
        {
            log.debug("resolveTypes(SymbolTable) - SymbolTable symbolTable=" + symbolTable);
        }

        // if we have parameters and/or exceptions, resolve them
        if (parameters != null) {
            parameters.resolveTypes(symbolTable);
        }

        if (exceptions != null) {
            exceptions.resolveTypes(symbolTable);
        }

        // if we have a return type, resolve it
        if ((type != null) && (type instanceof DummyClass)) {
            ClassDef newType = symbolTable.lookupDummy(type);

            if (newType != null) {
                newType.addReference(type.getOccurrence());

                type = newType;
            }
        }

        // perform resolution for our superclass
        super.resolveTypes(symbolTable);
    }

    /**
     * set the list of exceptions that this method can throw
     *
     * @param exceptions
     */
    void setExceptions(JavaVector exceptions) {
        this.exceptions = exceptions;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getClassScopeName()
     */
    String getClassScopeName() {

        String result;
        Definition parentScope = getParentScope();
        Definition grandParentScope = parentScope.getParentScope();

        if (grandParentScope instanceof ClassDef)    // inner class
        {
            result = grandParentScope.getName() + "." + parentScope.getName();
        } else {
            result = parentScope.getName();
        }

        result += "." + getFullName();

        return result;
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws java.io.IOException {

        out.writeObject(getName());
        out.writeObject(getParentScope());
        out.writeObject(getOccurrence());

        ClassDef typeOut = type;

        if ((type != null) && !(type instanceof DummyClass)) {
            typeOut = new ClassDefProxy(type);
        }

        out.writeObject(typeOut);
        out.writeObject(exceptions);
        out.writeObject(parameters);
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in)
            throws ClassNotFoundException, IOException {

        setName((String) in.readObject());
        SymbolTable.startReadExternal("MethodDef " + getName());
        setParentScope((ScopedDef) in.readObject());
        setOccurrence((Occurrence) in.readObject());

        type = (ClassDef) in.readObject();
        exceptions = (JavaVector) in.readObject();
        parameters = (JavaVector) in.readObject();

        setupFileNames();
        SymbolTable.endReadExternal();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#accept(org.apache.forrest.forrestdoc.java.src.symtab.Visitor)
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
        elements.accept(visitor);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#toString()
     */
    public String toString() {

        String str = getClass().getName() + " [" + getQualifiedName()
                + "]";
        JavaHashtable ht = getElements();
        Enumeration e = ht.elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            str += "\n        " + d;
        }

        return str;
    }
}
