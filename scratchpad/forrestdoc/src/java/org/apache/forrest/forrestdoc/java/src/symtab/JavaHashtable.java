/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/JavaHashtable.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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

import java.util.Enumeration;

/**
 * An extension of the java.util.Hashtable that is used to
 * add some simple looup and type resolution
 */
class JavaHashtable extends java.util.Hashtable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** prevent nested resolutions... */
    private boolean resolving = false;

    /** Field resolvingRefs */
    private boolean resolvingRefs = false;

    /** Field CLASS */
    private static final int CLASS = 0;

    /** Field INTERFACE */
    private static final int INTERFACE = 1;

    /** Field EITHER */
    private static final int EITHER = 2;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a new java hash table
     */
    JavaHashtable() {
        super();
    }

    /**
     * Generate tag info for elements
     * 
     * @param tagList 
     */
    void tagElements(HTMLTagContainer tagList) {

        Enumeration e = elements();

        while (e.hasMoreElements()) {
            ((Taggable) e.nextElement()).generateTags(tagList);
        }
    }

    /**
     * Resolve the types of dummy elements in the hash table
     * 
     * @param symbolTable 
     */
    void resolveTypes(SymbolTable symbolTable) {

        if (!resolving) {
            resolving = true;

            // walk through each element in the hash table
            Enumeration e = elements();

            while (e.hasMoreElements()) {
                Definition d = (Definition) e.nextElement();

                // System.out.println("JHT:resolving "+d.getName());
                // System.out.println("className="+d.getClass().getName());
                // if the element is a Dummy class or dummy interface, we
                // will replace it with the real definition
                if (d instanceof DummyClass) {
                    if (d.getName().endsWith("SymbolTable")) {
                        new Exception().printStackTrace();
                    }

                    System.out.println("Resolving DummyClass:" + d.getName());

                    Definition newD;

                    // get its package name and look up the class/interace
                    String pkg = ((DummyClass) d).getPackage();

                    System.out.println("pkg " + pkg);

                    newD = symbolTable.lookupDummy(d);

                    System.out.println("newD = " + newD);

                    // if we found the class/interface,
                    // add a reference to it, and replace the current def
                    // with the one we found
                    if (newD != null) {
                        newD.addReference(d.getOccurrence());
                        remove(d.getName());
                        put(d.getName(), newD);
                    }
                }

                // otherwise, ask it if it needs resolution
                else {
                    d.resolveTypes(symbolTable);
                }
            }
        }
    }

    /**
     * Resolve the types of dummy elements in the hash table
     * 
     * @param symbolTable 
     */
    void resolveRefs(SymbolTable symbolTable) {

        // System.out.println("JavaHashTable:resolveRefs");
        if (!resolvingRefs) {
            resolvingRefs = true;

            // walk through each element in the hash table
            Enumeration e = elements();

            while (e.hasMoreElements()) {
                Definition d = (Definition) e.nextElement();

                d.resolveRefs(symbolTable);
            }
        }
    }

    /**
     * Accept a visitor (Visitor design pattern).  Calls
     * accept method on each element.
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor) {

        Enumeration e = elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            d.accept(visitor);
        }
    }

    // // DEBUG METHOD
    // public Object put(Object key, Object value)
    // {
    // if (value instanceof ClassDef) {
    // System.out.println("Adding "+key+" to "+this);
    // }
    // return super.put(key,value);
    // }
}
