/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/VariableDef.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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
