/*
 * Copyright 1999-2004 The Apache Software Foundation or its licensors,
 * as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.Externalizable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;

/**
 * Definition of a variable in a source file.
 * This can be member data in class,
 * a local variable or a method parameter.
 */
public class VariableDef extends Definition
        implements TypedDef, Externalizable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The type of the variable */
    private ClassDef type = null;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Default constructor needs to be public for deserialization.
     */
    public VariableDef() {
    }

    /**
     * Constructor to create a new variable symbol
     * 
     * @param name        
     * @param occ         
     * @param type        
     * @param parentScope 
     */
    VariableDef(String name, // the variable's name
                Occurrence occ, // where it was defined
                ClassDef type, // the type of the variable
                ScopedDef parentScope) {    // which scope owns it

        super(name, occ, parentScope);

        this.type = type;
    }

    /**
     * get the type of the variable
     * 
     * @return 
     */
    public Definition getType() {
        return type;
    }

    /**
     * Method generateReferences
     * 
     * @param output 
     */
    public void generateReferences(FileWriter output) {

        String linkString;
        String linkFileName;

        try {
            output.write("<p class=\"variableReflist\">");

            String nameString =
                    "<p class=\"variableReflistHeader\">Variable: <a name="
                    + getName() + " href=" + getSourceName() + "#"
                    + getClassScopeName() + ">" + getName() + "</a></p>";

            output.write(nameString);

            JavaVector v = getReferences();
            Enumeration e = v.elements();

            while (e.hasMoreElements()) {
                Occurrence o = (Occurrence) e.nextElement();

                if (o != null) {
                    linkFileName = getOccurrencePath(o) + o.getLinkReference();
                    linkString = "<p class=\"variableRefItem\"><a href="
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
        }
        ;
    }

    /**
     * Method generateTags
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {

        String linkString;
        String linkFileName;
        String nameString = "<a class=\"varDef\" name=" + getClassScopeName()
                + " href=" + getRefName() + "#"
                + getClassScopeName() + ">" + getName() + "</a>";

        // generate tag for this method
        if (getOccurrence() == null) {
            return;
        }

        HTMLTag t = new HTMLTag(getOccurrence(), getName(), nameString);

        tagList.addElement(t);
    }

    /**
     * Method getOccurrenceTag
     * 
     * @param occ 
     * @return 
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {

        String linkString;
        String linkFileName;

        // System.out.println("Occurrence:"+o.getLine());
        linkFileName = getRelativePath(occ) + getSourceName();
        linkString = "<a class=\"varRef\" title=\"" + getType().getName()
                + "\" " + "href=" + linkFileName + "#"
                + getClassScopeName() + ">" + getName() + "</a>";

        HTMLTag t = new HTMLTag(occ, getName(), linkString);

        return t;
    }

    /**
     * Resolve referenced symbols used by this variable
     * 
     * @param symbolTable 
     */
    void resolveTypes(SymbolTable symbolTable) {

        if ((type != null) && (type instanceof DummyClass)) {

            // resolve the type of the variable
            ClassDef newType = (ClassDef) symbolTable.lookupDummy(type);

            if (newType != null) {
                newType.addReference(type.getOccurrence());

                type = newType;
            }
        }

        super.resolveTypes(symbolTable);
    }

    /**
     * Method getClassScopeName
     * 
     * @return 
     */
    String getClassScopeName() {

        String result;
        Definition parentScope = getParentScope();
        Definition grandParentScope = parentScope.getParentScope();

        if (parentScope instanceof MethodDef)                 // method variable
        {
            Definition greatGrandParentScope =
                    grandParentScope.getParentScope();

            if (greatGrandParentScope instanceof ClassDef)    // inner class
            {
                result = greatGrandParentScope.getName() + "."
                        + grandParentScope.getName();
            } else {
                result = grandParentScope.getName();
            }

            result += "." + parentScope.getName();
        } else                                                // class variable
        {
            if (grandParentScope instanceof ClassDef)         // inner class
            {
                result = grandParentScope.getName() + "."
                        + parentScope.getName();
            } else {
                result = parentScope.getName();
            }
        }

        result += "." + getName();

        return result;
    }

    /**
     * Method writeExternal
     * 
     * @param out 
     * @throws java.io.IOException 
     */
    public void writeExternal(ObjectOutput out) throws java.io.IOException {

        // System.out.println("persisting VariableDef "+getQualifiedName());
        out.writeObject(getName());

        Definition parentScopeOut = getParentScope();

        if (!(parentScopeOut instanceof MethodDef)) {
            parentScopeOut = new ClassDefProxy((ClassDef) parentScopeOut);
        }

        out.writeObject(parentScopeOut);
        out.writeObject(getOccurrence());

        ClassDef typeOut;

        if (type instanceof DummyClass) {
            typeOut = type;
        } else {
            typeOut = new ClassDefProxy(type);
        }

        out.writeObject(typeOut);
    }

    /**
     * Method readExternal
     * 
     * @param in 
     * @throws java.io.IOException    
     * @throws ClassNotFoundException 
     */
    public void readExternal(ObjectInput in)
            throws java.io.IOException, ClassNotFoundException {

        setName((String) in.readObject());
        SymbolTable.startReadExternal("VariableDef " + getName());
        setParentScope((ScopedDef) in.readObject());
        setOccurrence((Occurrence) in.readObject());

        type = (ClassDef) in.readObject();

        setupFileNames();    // TBD: still need this?
        SymbolTable.endReadExternal();
    }

    /**
     * Visitor design pattern.  Let the visitor visit this definition.
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
