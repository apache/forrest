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

/**
 * A label that appears in the source file.
 */
class LabelDef extends Definition {

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a new label symbol
     * 
     * @param name        
     * @param occ         
     * @param parentScope 
     */
    LabelDef(String name, // name of the label
             Occurrence occ, // where it was defined
             ScopedDef parentScope) {    // scope containing the def
        super(name, occ, parentScope);
    }

    /**
     * Write information about the label to the tagList
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {

        // state that this is a label
        // System.out.println(getQualifiedName() + " (Label) " + getDef());
        // list all references to this label
        // listReferences(System.out);
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
