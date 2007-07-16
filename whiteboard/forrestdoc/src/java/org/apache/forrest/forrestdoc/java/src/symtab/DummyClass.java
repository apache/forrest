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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A stub symbol that is used to temporarily hold the name of a class
 * until it can be properly resolved
 *
 * @version $Id: $
 */
public class DummyClass extends ClassDef implements Externalizable {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( DummyClass.class );

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================
    // (none)
    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a placeholder class object
     * This version provides a means to set the package containing the class
     */
    public DummyClass() {

        super();

        this.packageName = "java.lang";
    }

    /**
     * Constructor to create a placeholder class object
     *
     * @param name
     * @param occ
     */
    public DummyClass(String name, Occurrence occ) {
        super(name, occ, null, null, null);
    }

    /**
     * Constructor to create a placeholder class object
     * This version provides a means to set the package containing the class
     *
     * @param name
     * @param occ
     * @param packageName
     */
    public DummyClass(String name, Occurrence occ, String packageName) {

        super(name, occ, null, null, null);

        this.packageName = packageName;
    }

    /**
     * Get the name of the package in which this class is defined
     *
     * @return
     */
    public String getPackage() {
        return packageName;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#generateTags(org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer)
     */
    public void generateTags(HTMLTagContainer tagList) {
        if ( log.isInfoEnabled() )
        {
            log.info( getQualifiedName() + " (Undefined Class/Interface)");
            log.info( getReferences());
        }
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        if (log.isDebugEnabled())
        {
            log.debug("writeExternal(ObjectOutput) - getName()" + getName());
        }

        out.writeObject(getName());
        out.writeObject(packageName);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        setName((String) in.readObject());

        packageName = (String) in.readObject();
    }
}
