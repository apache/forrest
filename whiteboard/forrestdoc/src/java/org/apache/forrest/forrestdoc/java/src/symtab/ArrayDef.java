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

/**
 * Definition of an array type.  Note that this is not currently used in the
 * cross reference tool, but you would define something like this if you
 * wanted to make the tool complete.
 *
 * @version $Id: $
 */
class ArrayDef extends Definition implements TypedDef {

    private static final long serialVersionUID = -6748362770029804744L;

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The base type for the Array */
    private Definition type;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a new array type
     *
     * @param name
     * @param occ
     * @param parentScope
     */
    ArrayDef(String name, // the name of the symbol
             Occurrence occ, // the location of its def
             ScopedDef parentScope) {    // scope containing the def

        super(name, occ, parentScope);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.TypedDef#getType()
     */
    public Definition getType() {
        return type;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#generateTags(org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer)
     */
    public void generateTags(HTMLTagContainer tagList) {

        /*
         * out.println(getQualifiedName() + "[]  (Array) " + getDef());
         * listReferences(out);
         */
    }

    /**
     * Resolves references to other symbols used by this symbol
     */
    void resolveTypes() {

        // would need to lookup the base type in the symbol table
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#toString()
     */
    public String toString() {
        return "ArrayDef [" + type.getQualifiedName() + "]";
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getOccurrenceTag(org.apache.forrest.forrestdoc.java.src.symtab.Occurrence)
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {
        return null;
    }
}
