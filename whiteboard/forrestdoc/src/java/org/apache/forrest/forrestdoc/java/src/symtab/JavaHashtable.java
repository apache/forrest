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
