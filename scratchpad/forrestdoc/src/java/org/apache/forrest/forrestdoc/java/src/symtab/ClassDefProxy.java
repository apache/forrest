/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/ClassDefProxy.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;

/**
 * Class ClassDefProxy
 * 
 * @author 
 * @version %I%, %G%
 */
public class ClassDefProxy extends ClassDef implements Externalizable {

    /** Field whereCreated */
    private Throwable whereCreated = null;

    /**
     * Default constructor is public for deserialization.
     */
    public ClassDefProxy() {
        whereCreated = new Throwable();
    }

    /**
     * Constructor ClassDefProxy
     * 
     * @param ref 
     */
    public ClassDefProxy(ClassDef ref) {

        whereCreated = new Throwable();

        if (ref.getName().equals("RuntimeException")) {
            new Exception("what's up with this: " + ref).printStackTrace();
        }

        // System.out.println("ref="+ref);
        setName(ref.getName());

        this.packageName = ref.getPackageName();
    }

    /**
     * Method getSuperClass
     * 
     * @return 
     */
    public ClassDef getSuperClass() {

        load();

        return ref.getSuperClass();
    }

    /**
     * Method isClass
     * 
     * @return 
     */
    public boolean isClass() {

        load();

        return ref.isClass();
    }

    /**
     * Method isInterface
     * 
     * @return 
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
     * Method getSourceName
     * 
     * @return 
     */
    public String getSourceName() {

        load();

        return ref.getSourceName();
    }

    /**
     * Method getRefName
     * 
     * @return 
     */
    public String getRefName() {

        load();

        return ref.getRefName();
    }

    /**
     * Method getRelativePath
     * 
     * @param occ 
     * @return 
     */
    String getRelativePath(Occurrence occ) {

        load();

        return ref.getRelativePath(occ);
    }

    /**
     * Method getOccurrence
     * 
     * @return 
     */
    public Occurrence getOccurrence() {

        load();

        return ref.getOccurrence();
    }

    /**
     * Method getParentScope
     * 
     * @return 
     */
    public ScopedDef getParentScope() {

        load();

        return ref.getParentScope();
    }

    /**
     * Method getPackagePath
     * 
     * @return 
     */
    public String getPackagePath() {

        load();

        return ref.getPackagePath();
    }

    /**
     * Method getReferences
     * 
     * @return 
     */
    public JavaVector getReferences() {

        load();

        return null;
    }

    /**
     * Method lookup
     * 
     * @param name      
     * @param numParams 
     * @param type      
     * @return 
     */
    Definition lookup(String name, int numParams, Class type) {

        load();

        // System.out.println("ClassDefProxy.lookup: "+getName()+" lookup name="+name+" numParams="+numParams+" type="+type);
        return ref.lookup(name, numParams, type);
    }

    /**
     * Method getSymbols
     * 
     * @return 
     */
    public Hashtable getSymbols() {
        return null;
    }

    /**
     * Method getElements
     * 
     * @return 
     */
    public JavaHashtable getElements() {

        load();

        return ref.getElements();
    }

    /**
     * Method hasElements
     * 
     * @return 
     */
    boolean hasElements() {

        load();

        return ref.hasElements();
    }

    /**
     * Method isDefaultOrBaseScope
     * 
     * @return 
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
        new Exception().printStackTrace();
    }

    /**
     * Method isTopLevel
     * 
     * @return 
     */
    boolean isTopLevel() {
        return packageName != null;
    }

    /**
     * Method setImports
     * 
     * @param imports 
     */
    void setImports(JavaHashtable imports) {
        load();
        ref.setImports(imports);
    }

    /**
     * Method addImplementer
     * 
     * @param def 
     */
    void addImplementer(ClassDef def) {

        // load();
        // ref.addImplementer(def);
    }

    /**
     * Method addSubclass
     * 
     * @param subclass 
     */
    void addSubclass(ClassDef subclass) {

        // load();
        // ref.addSubclass(subclass);
    }

    /**
     * Method getImplementers
     * 
     * @return 
     */
    public JavaVector getImplementers() {
        return null;
    }

    /**
     * Method getSubClasses
     * 
     * @return 
     */
    JavaVector getSubClasses() {
        return null;
    }

    /**
     * Method generateReferences
     * 
     * @param output 
     */
    public void generateReferences(FileWriter output) {
    }

    /**
     * Method generateTags
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {
        load();
        ref.generateTags(tagList);
    }

    /**
     * Method addReference
     * 
     * @param occ 
     */
    public void addReference(Occurrence occ) {
        load();
        ref.addReference(occ);
    }

    /**
     * Visitor design pattern.  Allow visitor to visit this definition,
     * then call accept method on its elements.
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor) {
        load();
        ref.accept(visitor);
    }

    /**
     * serialize
     * 
     * @param out 
     * @throws IOException 
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        // System.out.println("writeExternal "+getName());
        out.writeObject(getName());
        out.writeObject(packageName);
    }

    /**
     * deserialize
     * 
     * @param in 
     * @throws IOException            
     * @throws ClassNotFoundException 
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
            System.out.println("null packageName, name=" + getName());
            System.exit(1);
        }

        if (ref == null) {
            ref = ClassDef.findLoadedClass(packageName, getName());

            if (ref == null) {

                // System.out.println("apparently not yet loaded: "+packageName+"."+getName());
                PackageDef packageDef =
                        SymbolTable.getSymbolTable().lookupPackage(packageName);

                ref = packageDef.loadClassDef(getName());
            }
        }

        if (ref == null) {
            new Exception("cannot load ClassDef for "
                    + getName()).printStackTrace();
        }
    }

    /** Field ref */
    private ClassDef ref;
}
