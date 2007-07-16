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

import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Because methods can be overloaded and member data can have the same name
 * as a method, we provide this dummy definition to hold a list of all
 * definitions in a scope with the same name.
 *
 * @version $Id: $
 */
public class MultiDef extends Definition {

    private static final long serialVersionUID = -4453495874767512434L;

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** A list of the various definitions for this symbol name */
    private JavaVector defs;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a new multidef object.
     * This version takes its information from a symbol that
     * it will be replacing.
     * This is just a convenience form of the real constructor
     * that takes a Definition as the base for the new MultiDef
     *
     * @param name
     * @param oldDef
     */
    MultiDef(String name, // the name of the definition
             Definition oldDef) {    // a standing def with its name
        this(name, oldDef.getOccurrence(), oldDef.getParentScope());
    }

    /**
     * Constructor to create a new multidef object
     *
     * @param name
     * @param occ
     * @param parentScope
     */
    MultiDef(String name, // the name of the definition
             Occurrence occ, // where it was defined
             ScopedDef parentScope) {    // the overall symbol table

        super(name, occ, parentScope);

        // Create the list to store the definitions
        defs = new JavaVector();
    }

    /**
     * Add a definition to the list of symbols with the same name
     *
     * @param def
     */
    void addDef(Definition def) {
        defs.addElement(def);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#lookup(java.lang.String, int, java.lang.Class)
     */
    Definition lookup(String name, // the name to locate
                      int numParams, // number of params
                      Class type) {

        // note that the name isn't used...  all definitions contained
        // by the MultiDef have the same name
        // walk through the list of symbols
        Enumeration e = defs.elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            // If the symbol is a method and it has the same number of
            // parameters as what we are calling, assume it's the match
            if ((d instanceof MethodDef) && d.isA(type)) {
                if (((MethodDef) d).getParamCount() == numParams) {
                    return d;
                }
            }

            // otherwise, if it's not a method, AND we're not looking for
            // a method, return the definition found.
            else if ((numParams == -1) && d.isA(type)) {
                return d;
            }
        }

        // If we didn't find a match return null
        return null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#generateTags(org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer)
     */
    public void generateTags(HTMLTagContainer tagList) {
        defs.generateTags(tagList);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#generateReferences(java.io.FileWriter)
     */
    public void generateReferences(FileWriter output) {
        defs.generateReferences(output);
    }

    /**
     * Method getDefs
     *
     * @return
     */
    public Vector getDefs() {
        return defs;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#resolveTypes(org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable)
     */
    void resolveTypes(SymbolTable symbolTable) {

        defs.resolveTypes(symbolTable);    // resolve all the definitions

        // DO NOT resolve anything else! (ie don't call super.resolveTypes() )
        // this is just a placeholder for a group of symbols with the same name
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#resolveRefs(org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable)
     */
    void resolveRefs(SymbolTable symbolTable) {

        Enumeration e = defs.elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            d.resolveRefs(symbolTable);
        }
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getOccurrenceTag(org.apache.forrest.forrestdoc.java.src.symtab.Occurrence)
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {
        return null;
    }
}
