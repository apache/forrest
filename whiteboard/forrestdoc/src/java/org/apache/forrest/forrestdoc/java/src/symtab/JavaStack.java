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
 * An extended Stack class to provide simple lookup and type
 * resolution methods
 */
class JavaStack extends java.util.Stack {

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * A constructor for the Java stack
     */
    JavaStack() {
    }

    /**
     * Find the class definition in the stack closest to the top
     * 
     * @return 
     */
    Definition findTopmostClass() {

        // unfortunately, the enumeration of a stack returns it
        // in the reverse order in which we want to traverse it...
        // So we must walk manually...
        for (int i = size() - 1; i > -1; i--) {
            if ((elementAt(i)) instanceof ClassDef) {
                return (Definition) elementAt(i);
            }
        }

        return null;
    }

    /**
     * a wrapper to lookup a non-method -- calls the real lookup
     * method passing -1 for num parameters (meaning no parameters)
     * 
     * @param name 
     * @param type 
     * @return 
     */
    Definition lookup(String name, Class type) {
        return lookup(name, -1, type);
    }

    /**
     * Lookup a method in the stack
     * 
     * @param name      
     * @param numParams 
     * @param type      
     * @return 
     */
    Definition lookup(String name, int numParams, Class type) {

        // unfortunately, the enumeration of a stack returns it
        // in the reverse order in which we want to traverse it...
        // So we must walk manually...
        for (int i = size() - 1; i > -1; i--) {
            Definition ld = (Definition) elementAt(i);

            // System.out.println("JavaStack:looking in "+ld.getName());
            Definition d = ld.lookup(name, numParams, type);

            if (d != null) {

                // System.out.println("JavaStack.lookup returning "+d+" type="+type);
                return d;
            }
        }

        // System.out.println("JavaStack.lookup returning null");
        return null;
    }
}
