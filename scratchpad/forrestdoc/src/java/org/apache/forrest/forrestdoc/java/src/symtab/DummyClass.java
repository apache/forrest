/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/DummyClass.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:12 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Alexandria", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
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
