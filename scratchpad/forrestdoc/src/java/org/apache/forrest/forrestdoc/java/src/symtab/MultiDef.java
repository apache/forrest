/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/MultiDef.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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

import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Because methods can be overloaded and member data can have the same name
 * as a method, we provide this dummy definition to hold a list of all
 * definitions in a scope with the same name.
 */
public class MultiDef extends Definition {

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
     * Lookup a symbol in the list of symbols
     * This is a rather lame approximation that just returns the first match
     * based on number of parameters.  A real routine to perform this would
     * use the best-fit parameter type matching algorithm described
     * in the Java Language Specification
     * 
     * @param name      
     * @param numParams 
     * @param type      
     * @return 
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
     * Write information about all the definitions contained within this to
     * the tagList
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {
        defs.generateTags(tagList);
    }

    /**
     * Let definitions generate references
     * 
     * @param output 
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
     * Resolve references to other symbols
     * 
     * @param symbolTable 
     */
    void resolveTypes(SymbolTable symbolTable) {

        defs.resolveTypes(symbolTable);    // resolve all the definitions

        // DO NOT resolve anything else! (ie don't call super.resolveTypes() )
        // this is just a placeholder for a group of symbols with the same name
    }

    /**
     * Resolve references to other symbols
     * 
     * @param symbolTable 
     */
    void resolveRefs(SymbolTable symbolTable) {

        Enumeration e = defs.elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            d.resolveRefs(symbolTable);
        }
    }

    /**
     * Method getOccurrenceTag
     * 
     * @param occ 
     * @return 
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {
        return null;
    }
}
