/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

import org.apache.forrest.forrestdoc.java.src.xref.JavaToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Enumeration;
import java.util.Vector;

/**
 * An extended Vector class to provide simple lookup and type resolution
 * methods
 */
public class JavaVector extends java.util.Vector
        implements java.io.Externalizable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** prevent nested resolutions... */
    private boolean resolvingRefs = false;

    /** Field resolvingTypes */
    private boolean resolvingTypes = false;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a new Java vector
     */
    public JavaVector() {
        super();
    }

    /**
     * Add a new element to the vector (used for debugging)
     * 
     * @param o 
     */
    public void addElement(Definition o) {

        super.addElement(o);

        if (o == null) {
            throw new IllegalArgumentException("null element added to vector");
        }
    }

    /**
     * get an element from the
     * 
     * @param name 
     * @return 
     */
    public Definition getElement(String name) {

        Enumeration e = elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            if (d.getName().equals(name)) {
                return d;
            }
        }

        return null;
    }

    /**
     * Write information about each element in the vector to the report,
     * 
     * @param tagList 
     */
    void generateTags(HTMLTagContainer tagList) {

        Enumeration e = elements();

        while (e.hasMoreElements()) {
            ((Taggable) e.nextElement()).generateTags(tagList);
        }
    }

    /**
     * Method generateReferences
     * 
     * @param output 
     */
    void generateReferences(FileWriter output) {

        Enumeration e = elements();

        while (e.hasMoreElements()) {
            Object element = e.nextElement();

            if (element instanceof Definition) {
                Definition d = (Definition) element;

                d.generateReferences(output);
            }
        }
    }

    /**
     * Resolve references that are stored as JavaTokens
     * 
     * @param symbolTable 
     */
    public void resolveRefs(SymbolTable symbolTable) {

        // System.out.println("JavaVector:resolveRefs");
        if (!resolvingRefs) {
            resolvingRefs = true;

            // resolve each element in the list
            Enumeration e = elements();

            while (e.hasMoreElements()) {
                JavaToken t = (JavaToken) e.nextElement();

                // System.out.println("!resolve "+t.getText()+" file="+t.getFile().getAbsolutePath()+":"+t.getLine());
                Definition d = symbolTable.lookup(t.getText(),
                        t.getParamCount(), null);

                if (d == null) {
                    d = symbolTable.findPackage(t.getText());
                }

                if (d != null) {

                    // System.out.println("Found reference:"+d.getQualifiedName());
                    d.addReference(new Occurrence(t.getFile(), t.getLine(),
                            t.getColumn(),
                            t.getPackageName(),
                            t.getClassName(),
                            t.getMethodName()));
                    d.resolveRefs(symbolTable);
                }

                // else
                // System.out.println("Could not resolve "+t.getText());
            }
        }
    }

    /**
     * Resolve the types of dummy elements in the vector
     * 
     * @param symbolTable 
     */
    public void resolveTypes(SymbolTable symbolTable) {

        if (!resolvingTypes) {
            resolvingTypes = true;

            Enumeration e = elements();

            while (e.hasMoreElements()) {
                Definition d = (Definition) e.nextElement();

                if (d instanceof DummyClass) {
                    String pkg = ((DummyClass) d).getPackage();

                    // System.out.println("JavaVector.resolveTypes: resolving pkg="+pkg+" name="+d.getName());
                    Definition newD = symbolTable.lookupDummy(d);

                    if (newD != null) {

                        // System.out.println("JavaVector.resolveTypes: resolved pkg="+pkg+" name="+d.getName());
                        newD.addReference(d.getOccurrence());
                        removeElement(d);
                        addElement(newD);
                    }
                } else {
                    d.resolveTypes(symbolTable);
                }
            }
        }
    }

    /**
     * Method writeExternal
     * 
     * @param out 
     * @throws IOException 
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        Vector vOut = new Vector();
        Enumeration e = elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            if ((d instanceof ClassDef)
                    && !((d instanceof ClassDefProxy)
                    || (d instanceof PrimitiveDef)
                    || (d instanceof DummyClass))) {
                d = new ClassDefProxy((ClassDef) d);
            }

            vOut.addElement(d);
        }

        out.writeObject(vOut);
    }

    /**
     * Method readExternal
     * 
     * @param in 
     * @throws IOException            
     * @throws ClassNotFoundException 
     */
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        Vector v = (Vector) in.readObject();

        addAll(v);
    }

    /**
     * Visitor design pattern.  Call accept method of each
     * element.
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor) {

        Enumeration enum = elements();

        while (enum.hasMoreElements()) {
            Definition def = (Definition) enum.nextElement();

            def.accept(visitor);
        }
    }
}
