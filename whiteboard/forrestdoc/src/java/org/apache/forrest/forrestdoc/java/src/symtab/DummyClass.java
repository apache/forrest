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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A stub symbol that is used to temporarily hold the name of a class
 * until it can be properly resolved
 */
public class DummyClass extends ClassDef implements Externalizable {

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

        // if (name.equals("SymbolTable"))
        // new Exception("occ="+occ).printStackTrace();
        // System.out.println("DummyClass packageName="+packageName);
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
     * Write information about this unresolved class to the tagList
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {
        System.out.println(getQualifiedName() + " (Undefined Class/Interface)");
        System.out.println(getReferences());
    }

    /**
     * Persist this object
     * 
     * @param out 
     * @throws IOException 
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        // System.out.println("persisting DummyClass "+getName());
        out.writeObject(getName());
        out.writeObject(packageName);
    }

    /**
     * Restore this object
     * 
     * @param in 
     * @throws IOException            
     * @throws ClassNotFoundException 
     */
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        setName((String) in.readObject());

        packageName = (String) in.readObject();
    }
}
