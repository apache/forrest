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
 * Definition of a primitive type. We subclass ClassDef here as a shortcut
 * for method parameter lookups.  By defining primitives in a hierarchy
 * we get the primitive type promotion for free.  For this cross-reference
 * tool we don't actually perform any type conversions, but we thought it
 * would be a good example of how some constructs can be used in similar
 * ways, even though it might not appear like the proper model.
 */
class PrimitiveDef extends ClassDef {

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a primitive type object
     * 
     * @param name        
     * @param superClass  
     * @param parentScope 
     */
    PrimitiveDef(String name, // the name of the primitive
                 ClassDef superClass, // the superclass (if applicable)
                 ScopedDef parentScope) {    // which scope owns it
        super(name, null, superClass, null, parentScope);
    }

    /**
     * Write information about this primitive type to the tagList
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {

        /*
         *   out.println(getName() + " (Primitive type)");
         *   listReferences(out);
         */
    }
}
