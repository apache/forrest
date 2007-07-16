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

/**
 * An abstract class representing a symbol that can import packages.
 *
 * @version $Id: $
 */
abstract class HasImports extends ScopedDef {
    /**
    * Logger for this class
    */
    private static final Logger log = Logger.getLogger( HasImports.class );

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** A table of all packages imported by this symbol */
    private JavaHashtable imports;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Default constructor is public for deserialization.
     */
    public HasImports() {
    }

    /**
     * Constructor to set up an object that can have imports
     *
     * @param name
     * @param occ
     * @param parentScope
     */
    HasImports(String name, // name of the symbol
               Occurrence occ, // where it's defined
               ScopedDef parentScope) {    // which scope owns it
        super(name, occ, parentScope);
    }

    /**
     * Tell the symbol table that we are done with our imports
     *
     * @param symbolTable
     */
    void closeImports(SymbolTable symbolTable) {

        if (log.isDebugEnabled())
        {
            log.debug("closeImports(SymbolTable) - SymbolTable symbolTable=" + symbolTable);
        }

        symbolTable.closeImports();
    }

    /**
     * Ask if this is a toplevel class or not
     * This is true if the parentScope is a package
     *
     * @return
     */
    boolean isTopLevel() {
        return (getParentScope() instanceof PackageDef);
    }

    /**
     * Tell the symbol table that we need to import some classes
     *
     * @param symbolTable
     */
    void openImports(SymbolTable symbolTable) {

        if (log.isDebugEnabled())
        {
            log.debug("openImports(SymbolTable) - SymbolTable symbolTable=" + symbolTable);
        }

        symbolTable.openImports(imports);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#resolveTypes(org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable)
     */
    void resolveTypes(SymbolTable symbolTable) {

        if (log.isDebugEnabled())
        {
            log.debug("resolveTypes(SymbolTable) - SymbolTable symbolTable=" + symbolTable);
        }

        // this was commented out.  Why?
        // if (imports != null)            // resolve imported classes/packages
        // imports.resolveTypes(symbolTable);
        if (isTopLevel()) {
            openImports(symbolTable);    // make them available for lookup
        }

        super.resolveTypes(symbolTable);    // resolve class/interface contents

        // closeImports() is done in class resolution
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#resolveRefs(org.apache.forrest.forrestdoc.java.src.symtab.SymbolTable)
     */
    void resolveRefs(SymbolTable symbolTable) {

        if (log.isDebugEnabled())
        {
            log.debug("resolveRefs(SymbolTable) - SymbolTable symbolTable=" + symbolTable);
        }

        // this was commented out.  Why?
        // if (imports != null)            // resolve imported classes/packages
        // imports.resolveRefs(symbolTable);
        if (isTopLevel()) {
            openImports(symbolTable);    // make them available for lookup
        }

        super.resolveRefs(symbolTable);    // resolve class/interface contents

        // closeImports() is done in class resolution
    }

    /**
     * Set the list of imported classes/packages
     *
     * @param imports
     */
    void setImports(JavaHashtable imports) {
        this.imports = imports;
    }
}
