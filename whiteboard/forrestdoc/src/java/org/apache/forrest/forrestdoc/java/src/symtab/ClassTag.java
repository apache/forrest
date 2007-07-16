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

import org.apache.forrest.forrestdoc.java.src.util.JSComparable;

/**
 * An occurrence of an HTML Tag in a file
 *
 * @version $Id: $
 */
public class ClassTag implements JSComparable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The key */
    private String _key;

    /** The tag */
    private String _tag;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Method getKey
     *
     * @return
     */
    public String getKey() {
        return _key;
    }

    /**
     * Method getTag
     *
     * @return
     */
    public String getTag() {
        return _tag;
    }

    /**
     * Constructor to define a new tag
     *
     * @param key
     * @param tag
     */
    ClassTag(String key, String tag) {

        _key = key;
        _tag = tag;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return _tag;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.util.JSComparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {

        ClassTag t = (ClassTag) o;

        return _key.compareTo(t.getKey());
    }
}
