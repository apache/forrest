/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/HasImports.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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

/**
 * An abstract class representing a symbol that can import packages.
 */
abstract class HasImports extends ScopedDef {

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

        // System.out.println("Closing imports");
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

        // System.out.println("Opening imports");
        symbolTable.openImports(imports);
    }

    /**
     * Resolve any referenced symbols
     * 
     * @param symbolTable 
     */
    void resolveTypes(SymbolTable symbolTable) {

        // System.out.println("Resolving imports");
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
     * Resolve any referenced symbols
     * 
     * @param symbolTable 
     */
    void resolveRefs(SymbolTable symbolTable) {

        // System.out.println("Resolving imports");
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
