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
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;

/**
 * Class ClassDefProxy
 *
 * @version $Id: $
 */
public class ClassDefProxy extends ClassDef implements Externalizable {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( ClassDefProxy.class );

    /**
     * Default constructor is public for deserialization.
     */
    public ClassDefProxy() {
    }

    /**
     * Constructor ClassDefProxy
     *
     * @param ref
     */
    public ClassDefProxy(ClassDef ref) {

        if (ref.getName().equals("RuntimeException")) {
            log.error( "ClassDefProxy(ClassDef) - what's up with this: " + ref );
        }

        if (log.isDebugEnabled())
        {
            log.debug("ClassDefProxy(ClassDef) - ClassDef ref=" + ref);
        }
        setName(ref.getName());

        this.packageName = ref.getPackageName();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#getSuperClass()
     */
    public ClassDef getSuperClass() {

        load();

        return ref.getSuperClass();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#isClass()
     */
    public boolean isClass() {

        load();

        return ref.isClass();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#isInterface()
     */
    public boolean isInterface() {

        load();

        return ref.isInterface();
    }

    /**
     * Method getOccurenceTag
     *
     * @param occ
     * @return
     */
    public HTMLTag getOccurenceTag(Occurrence occ) {

        load();

        return ref.getOccurrenceTag(occ);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getSourceName()
     */
    public String getSourceName() {

        load();

        return ref.getSourceName();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getRefName()
     */
    public String getRefName() {

        load();

        return ref.getRefName();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getRelativePath(org.apache.forrest.forrestdoc.java.src.symtab.Occurrence)
     */
    String getRelativePath(Occurrence occ) {

        load();

        return ref.getRelativePath(occ);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getOccurrence()
     */
    public Occurrence getOccurrence() {

        load();

        return ref.getOccurrence();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getParentScope()
     */
    public ScopedDef getParentScope() {

        load();

        return ref.getParentScope();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getPackagePath()
     */
    public String getPackagePath() {

        load();

        return ref.getPackagePath();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#getReferences()
     */
    public JavaVector getReferences() {

        load();

        return null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#lookup(java.lang.String, int, java.lang.Class)
     */
    Definition lookup(String name, int numParams, Class type) {

        load();

        if (log.isDebugEnabled())
        {
            log.debug("lookup(String, int, Class) - " + getName()+" lookup name="+name+" numParams="+numParams+" type="+type);
        }

        return ref.lookup(name, numParams, type);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#getSymbols()
     */
    public Hashtable getSymbols() {
        return null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#getElements()
     */
    public JavaHashtable getElements() {

        load();

        return ref.getElements();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#hasElements()
     */
    boolean hasElements() {

        load();

        return ref.hasElements();
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ScopedDef#isDefaultOrBaseScope()
     */
    public boolean isDefaultOrBaseScope() {

        load();

        return ref.isDefaultOrBaseScope();
    }

    /**
     * Method resolveTypes
     */
    void resolveTypes() {
    }

    /**
     * Method resolveRefs
     */
    void resolveRefs() {
    }

    /**
     * Method closeImports
     */
    void closeImports() {
        if (log.isDebugEnabled())
        {
            log.debug("closeImports() - closed");
        }
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.HasImports#isTopLevel()
     */
    boolean isTopLevel() {
        return packageName != null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.HasImports#setImports(org.apache.forrest.forrestdoc.java.src.symtab.JavaHashtable)
     */
    void setImports(JavaHashtable imports) {
        load();
        ref.setImports(imports);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#addImplementer(org.apache.forrest.forrestdoc.java.src.symtab.ClassDef)
     */
    void addImplementer(ClassDef def) {

        // load();
        // ref.addImplementer(def);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#addSubclass(org.apache.forrest.forrestdoc.java.src.symtab.ClassDef)
     */
    void addSubclass(ClassDef subclass) {

        // load();
        // ref.addSubclass(subclass);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#getImplementers()
     */
    public JavaVector getImplementers() {
        return null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#getSubClasses()
     */
    JavaVector getSubClasses() {
        return null;
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#generateReferences(java.io.FileWriter)
     */
    public void generateReferences(FileWriter output) {
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#generateTags(org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer)
     */
    public void generateTags(HTMLTagContainer tagList) {
        load();
        ref.generateTags(tagList);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Definition#addReference(org.apache.forrest.forrestdoc.java.src.symtab.Occurrence)
     */
    public void addReference(Occurrence occ) {
        load();
        ref.addReference(occ);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#accept(org.apache.forrest.forrestdoc.java.src.symtab.Visitor)
     */
    public void accept(Visitor visitor) {
        load();
        ref.accept(visitor);
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.ClassDef#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        if (log.isDebugEnabled())
        {
            log.debug("writeExternal(ObjectOutput) - getName()=" + getName());
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

        if (packageName == null) {
            throw new IOException("null packagename: " + getName());
        }
    }

    /**
     * load the class definition
     */
    private void load() {

        if (packageName == null) {
            log.error( "null packageName, name=" + getName());
        }

        if (ref == null) {
            ref = ClassDef.findLoadedClass(packageName, getName());

            if (ref == null) {

                if (log.isDebugEnabled())
                {
                    log.debug("load() - apparently not yet loaded: "+packageName+"."+getName());
                }
                PackageDef packageDef =
                        SymbolTable.getSymbolTable().lookupPackage(packageName);

                ref = packageDef.loadClassDef(getName());
            }
        }

        if (ref == null) {
            log.error("cannot load ClassDef for " + getName());
        }
    }

    /** Field ref */
    private ClassDef ref;
}
