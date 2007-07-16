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

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * HTMLTagContainer contains a mapping from files to lists of HTMLTags.
 *
 * @version $Id: $
 */
public class HTMLTagContainer {

    /**
     * Constructor HTMLTagContainer
     */
    public HTMLTagContainer() {
        _fileTable = new Hashtable(10);
    }

    /**
     * Method getFileTable
     *
     * @return
     */
    public Hashtable getFileTable() {
        return _fileTable;
    }

    /**
     * Method elements
     *
     * @return
     */
    public Enumeration elements() {
        return (_fileTable.elements());
    }

    /**
     * Method addElement
     *
     * @param t
     */
    public void addElement(HTMLTag t) {

        File f = t.getFile();
        Vector tagList = (Vector) _fileTable.get(f);

        if (tagList == null) {
            tagList = new Vector(20);

            _fileTable.put(f, tagList);
        }

        tagList.addElement(t);
    }

    /** Field _fileTable */
    private Hashtable _fileTable;
}
