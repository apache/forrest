/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/JavaVector.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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
