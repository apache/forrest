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

/**
 * Definition of a curly-brace-delimited block in a file.
 */
public class BlockDef extends ScopedDef {

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to set up a block
     * 
     * @param name        
     * @param occ         
     * @param parentScope 
     */
    BlockDef(String name, // name of the block (dummy)
             Occurrence occ, // where it's defined
             ScopedDef parentScope) {    // which scope owns it

        super(name, occ, parentScope);

        // System.out.println("new BlockDef");
    }

    /**
     * Write information about this block to the tagList
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {
        tagElements(tagList);
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
