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

import java.util.Hashtable;

/**
 * An abstract class representing a symbol that provides a scope that
 * contains other symbols.
 */
public abstract class ScopedDef extends Definition {

    /** Field debug */
    public static final boolean debug = false;

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** A table of symbols "owned" by this symbol */
    protected JavaHashtable elements;

    /** A list of yet-to-be-resolved references */
    private JavaVector unresolvedStuff;

    /**
     * Is this scope one of the following:
     * <dl>
     * <dt>Base scope
     * <dd>the scope that contains primitive types
     * <dt>Default package
     * <dd>where Java classes reside that are not explicitly in another scope.
     * </dl>
     */
    private boolean iAmDefaultOrBaseScope = false;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Default constructor is public for deserialization.
     */
    public ScopedDef() {

        // Create a new hashtable for fast element lookup
        elements = new JavaHashtable();
    }

    /**
     * Constructor to create the base part of a scoped definition
     * 
     * @param name        
     * @param occ         
     * @param parentScope 
     */
    ScopedDef(String name, // the scoped name
              Occurrence occ, // where it's defined
              ScopedDef parentScope) {    // scope containing the def

        super(name, occ, parentScope);

        // Create a new hashtable for fast element lookup
        elements = new JavaHashtable();
    }

    /**
     * Method getSymbols
     * 
     * @return 
     */
    public Hashtable getSymbols() {
        return elements;
    }

    /**
     * Add a symbol to our scope
     * 
     * @param def 
     */
    void add(Definition def) {

        // Check to see if we already have a definition
        Definition oldDef = (Definition) elements.get(def.getName());

        // If so, we'll create a MultiDef to hold them
        if (oldDef != null) {

            // If the symbol there so far was not a MultiDef
            if (!(oldDef instanceof MultiDef)) {

                // remove the old definition
                elements.remove(oldDef);

                // create a new MultiDef
                MultiDef newMulti = new MultiDef(def.getName(), oldDef);

                // add the old symbol to the MultiDef
                newMulti.addDef(oldDef);

                oldDef = newMulti;

                // add the MultiDef back into the scope
                elements.put(def.getName(), oldDef);
            }

            // We now have a multidef, so add the new symbol to it
            ((MultiDef) oldDef).addDef(def);
        }

        // Otherwise, just add the new symbol to the scope
        else {
            elements.put(def.getName(), def);
            def.setParentScope(this);
        }
    }

    /**
     * Add a token to the list of unresolved references
     * 
     * @param t 
     */
    void addUnresolved(JavaToken t) {

        // be lazy in our creation of the reference vector
        // (many definitions might not contain refs to other symbols)
        // System.out.println("Adding unresolved reference to:"+getQualifiedName());
        if (unresolvedStuff == null) {
            unresolvedStuff = new JavaVector();
        }

        unresolvedStuff.addElement(t);
    }

    /**
     * Return whether or not this scope actually contains any elements
     * 
     * @return 
     */
    public JavaHashtable getElements() {
        return elements;
    }

    /**
     * Return whether or not this scope actually contains any elements
     * 
     * @return 
     */
    boolean hasElements() {
        return !elements.isEmpty();
    }

    /**
     * Return if this is a base or default scope.  This is used when printing
     * information to the report so we won't prefix elements in these
     * scopes.
     * 
     * @return 
     */
    public boolean isDefaultOrBaseScope() {
        return iAmDefaultOrBaseScope;
    }

    /**
     * Lookup a method in the scope
     * This is usually just a hashtable lookup, but if the element returned
     * is a MultiDef, we need to ask it to find the best match
     * 
     * @param name      
     * @param numParams 
     * @param type      
     * @return 
     */
    Definition lookup(String name, int numParams, Class type) {

        // Try to find the name in our scope
        Definition d = (Definition) elements.get(name);

        // if we found multiple defs of the same name, ask the multidef
        // to do the resolution for us.
        if (d instanceof MultiDef) {
            d = d.lookup(name, numParams, type);
        }

        // if we got a method back, check to see that the params apply
        else if (d instanceof MethodDef) {
            if (((MethodDef) d).getParamCount() != numParams) {
                d = null;
            }
        }

        if ((d != null) && !d.isA(type)) {
            d = null;
        }

        return d;
    }

    /**
     * Create tag info about elements
     * 
     * @param tagList 
     */
    void tagElements(HTMLTagContainer tagList) {

        if (elements != null) {
            elements.tagElements(tagList);
        }
    }

    /** Field resolveLevel */
    static private int resolveLevel = 0;

    /**
     * Resolve referenced names
     * 
     * @param symbolTable 
     */
    void resolveTypes(SymbolTable symbolTable) {

        // for (int i=0; i<resolveLevel; i++) {
        // System.out.print("\t");
        // }
        // System.out.println("resolving types for "+this.getQualifiedName());
        resolveLevel++;

        symbolTable.pushScope(this);           // push the current scope
        elements.resolveTypes(symbolTable);    // resolve elements in this scope
        symbolTable.popScope();                // pop back out of the scope
        super.resolveTypes(symbolTable);       // let superclass resolve if needed

        resolveLevel--;
    }

    /**
     * Resolve referenced names
     * 
     * @param symbolTable 
     */
    void resolveRefs(SymbolTable symbolTable) {

        // for (int i=0; i<resolveLevel; i++) {
        // System.out.print("\t");
        // }
        // System.out.println("resolving refs for "+this.getQualifiedName());
        resolveLevel++;

        // System.out.println("!ScopedDef:resolveRefs:"+getQualifiedName());
        symbolTable.pushScope(this);    // push the current scope

        if (unresolvedStuff != null) {    // resolve refs to other syms
            unresolvedStuff.resolveRefs(symbolTable);

            unresolvedStuff = null;
        }

        elements.resolveRefs(symbolTable);
        symbolTable.popScope();            // pop back out of the scope
        super.resolveRefs(symbolTable);    // let superclass resolve if needed

        resolveLevel--;
    }

    /**
     * Indicate that this scope is a base scope or default package
     * 
     * @param val 
     */
    void setDefaultOrBaseScope(boolean val) {
        iAmDefaultOrBaseScope = val;
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
