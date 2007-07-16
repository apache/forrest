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

import java.util.Hashtable;

/**
 * Keeps track of all strings encountered in the file that represent
 * identifiers.  This way we only ever keep a single copy of a string
 * and all symbols refer to it.
 *
 * @version $Id: $
 */
class StringTable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /**
     * The hash table that holds all the strings.  Note that if we were tuning
     * this we'd adjust the size of the hash table to find a fairly
     * efficient setting
     */
    private Hashtable names = new Hashtable();

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Get a name from the StringTable
     *
     * @param name
     * @return
     */
    String getName(String name) {

        if (name == null) {
            return null;
        }

        String uniqueName = (String) names.get(name);

        if (uniqueName != null) {
            return uniqueName;
        }

        names.put(name, name);

        return name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "StringTable";
    }
}
