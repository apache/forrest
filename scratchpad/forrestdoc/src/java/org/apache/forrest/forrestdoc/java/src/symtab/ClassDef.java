/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/ClassDef.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

/**
 * Definition of a Java class OR interface
 * These are merged together because there are places where we just don't
 * know if something is an interface or class (because we are not looking
 * at the classes/interfaces that are imported.)
 */
public class ClassDef extends HasImports implements Externalizable {

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** Field CLASS */
    static final int CLASS = 0;

    /** Field INTERFACE */
    static final int INTERFACE = 1;

    /** Field EITHER */
    static final int EITHER = 2;

    /**
     * The type of object this represents
     * We may not initially know, as a statement like
     * import java.awt.Color
     * _could_ be referring to a class _or_ interface.
     * Of course a full implementation of a cross-reference
     * tool would either parse the imports at this point,
     * or read information from the class file...
     */
    private int classOrInterface = EITHER;

    /**
     * The class from which this class was extended
     * (This only applies if this represents a CLASS)
     */
    private ClassDef superClass;

    /**
     * A list of classes that extend this class, OR
     * interfaces that extend this interface
     */
    private JavaVector subClasses;

    /**
     * A list of interfaces that this class implements, OR
     * a list of super interfaces for this interface
     */
    private JavaVector interfaces;

    /**
     * A list of classes that implement this interface
     * (This only applies if this represents an INTERFACE)
     */
    private JavaVector implementers;

    /** Field packageName */
    protected String packageName;

    /** Field allClassDefs */
    private static Hashtable allClassDefs = new Hashtable();

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Method findLoadedClass
     * 
     * @param packageName 
     * @param name        
     * @return 
     */
    protected static ClassDef findLoadedClass(String packageName, String name) {

        String qualifiedName = packageName + "." + name;

        // System.out.println("looking for ClassDef "+qualifiedName);
        // Enumeration keys = allClassDefs.keys();
        // System.out.print("allClassDefs={");
        // while (keys.hasMoreElements()) {
        // System.out.print(((String)keys.nextElement())+", ");
        // }
        // System.out.println("}");
        return (ClassDef) allClassDefs.get(qualifiedName);
    }

    /**
     * Default constructor needs to be public for deserialization.
     */
    public ClassDef() {
    }

    /**
     * Constructor to set up a class
     * 
     * @param name        
     * @param occ         
     * @param superClass  
     * @param interfaces  
     * @param parentScope 
     */
    ClassDef(String name, // thhe name of the class
             Occurrence occ, // where it was defined
             ClassDef superClass, // its superclass (if applicable)
             JavaVector interfaces, // interfaces that it implements
             ScopedDef parentScope) {    // which scope owns it

        super(name, occ, parentScope);

        // if a superclass was specified, set it
        if (superClass != null) {
            this.superClass = superClass;
        }

        // keep track of implemented interfaces
        this.interfaces = interfaces;
    }

    /**
     * Adds a reference to the list of classes that implement this interface
     * 
     * @param def 
     */
    void addImplementer(ClassDef def) {

        getImplementers().addElement(def);
        setType(INTERFACE);
        def.setType(CLASS);
    }

    /**
     * Add a reference to a class that extends this class
     * (or an interface that extends this interface
     * 
     * @param subclass 
     */
    void addSubclass(ClassDef subclass) {
        getSubClasses().addElement(subclass);
    }

    /**
     * get the list of classes that implement this interface
     * 
     * @return 
     */
    JavaVector getImplementers() {

        if (implementers == null) {    // lazy instantiation
            implementers = new JavaVector();
        }

        return implementers;
    }

    /**
     * Return the list of interfaces that this class implements
     * (or the interfaces that this interface extends)
     * 
     * @return 
     */
    public JavaVector getInterfaces() {
        return interfaces;
    }

    /**
     * return a list of all subclasses/subinterfaces of this
     * 
     * @return 
     */
    JavaVector getSubClasses() {

        if (subClasses == null) {
            subClasses = new JavaVector();
        }

        return subClasses;
    }

    /**
     * Return a reference to the superclass of this class
     * 
     * @return 
     */
    public ClassDef getSuperClass() {
        return superClass;
    }

    /**
     * Does this represent a Java class?
     * 
     * @return 
     */
    public boolean isClass() {
        return classOrInterface == CLASS;
    }

    /**
     * Does this represent a Java interface?
     * 
     * @return 
     */
    public boolean isInterface() {
        return classOrInterface == INTERFACE;
    }

    // debugging stuff -- see below

    /** Field goals */
    private static Set goals = new HashSet();

    /**
     * Lookup a method in the class or its superclasses
     * 
     * @param name      
     * @param numParams 
     * @param type      
     * @return 
     */
    Definition lookup(String name, int numParams, Class type) {

        String goal = name + "|" + getQualifiedName() + "|" + numParams + "|"
                + type;

        if (goals.contains(goal)) {
            System.out.println("detected infinite loop: " + goal);
            new Exception().printStackTrace();

            return null;
        }

        goals.add(goal);

        // try to look it up in our scope
        Definition d = super.lookup(name, numParams, type);

        // if not found, look it up in our superclass (if we have one)
        if (d == null) {
            if (getSuperClass() != null) {
                setType(CLASS);
                getSuperClass().setType(CLASS);

                // System.out.println("ClassDef.lookup: "+getName()+" looking for name="+name+" numParams="+numParams+" type="+type);
                d = getSuperClass().lookup(name, numParams, type);
            }
        }

        // if still not found, look for it in any of our implemented interfaces
        if ((d == null) && (interfaces != null)) {
            Enumeration e = interfaces.elements();

            while ((d == null) && e.hasMoreElements()) {
                d = ((ClassDef) e.nextElement()).lookup(name, numParams, type);
            }
        }

        goals.remove(goal);

        return d;
    }

    // 
    // Generate all references to this class (and to things in this class).
    // 

    /**
     * Method generateReferences
     * 
     * @param output 
     */
    public void generateReferences(FileWriter output) {

        // System.out.println("Generating references for:"+getName());
        try {
            output.write("<p class=\"classReflist\">");

            String nameString =
                    "<p class=\"classReflistHeader\">Class: <a name=" + getName()
                    + " href=" + getSourceName() + "#" + getClassScopeName() + ">"
                    + getScopedClassName() + "</a></p>";
            String linkFileName;
            String linkString;

            output.write(nameString);

            JavaVector v = getReferences();
            Enumeration e = v.elements();

            while (e.hasMoreElements()) {
                Occurrence o = (Occurrence) e.nextElement();

                if (o != null) {
                    linkFileName = getOccurrencePath(o) + o.getLinkReference();
                    linkString = "<p class=\"classRefItem\"><a href="
                            + linkFileName + ">" + getName() + " in "
                            + o.getPackageName() + "."
                            + o.getClassName() + "." + o.getMethodName()
                            + " (" + o.getFile().getName() + ":"
                            + Integer.toString(o.getLine())
                            + ")</a></p>\n";

                    output.write(linkString);
                }
            }

            JavaHashtable ht = getElements();

            // Variables
            Enumeration el = ht.elements();

            while (el.hasMoreElements()) {
                Object d = el.nextElement();

                if (d instanceof VariableDef) {
                    ((Definition) d).generateReferences(output);
                }
            }

            // Methods
            el = ht.elements();

            while (el.hasMoreElements()) {
                Object d = el.nextElement();

                if ((d instanceof MethodDef) || (d instanceof MultiDef)) {
                    ((Definition) d).generateReferences(output);
                }
            }

            output.write("</p>\n");

            // Now inner classes
            el = ht.elements();

            while (el.hasMoreElements()) {
                Object d = el.nextElement();

                if (d instanceof ClassDef) {
                    ((Definition) d).generateReferences(output);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            return;
        }
    }

    /**
     * Write information about the class to the tagList
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {

        String nameString = "<a class=\"classDef\" name=" + getClassScopeName()
                + " href=" + getRefName() + "#"
                + getClassScopeName() + ">" + getName() + "</a>";

        // generate tag for this class
        if (getOccurrence() != null) {
            HTMLTag t = new HTMLTag(getOccurrence(), getName(), nameString);

            tagList.addElement(t);
        }

        tagElements(tagList);
    }

    /**
     * Method getOccurrenceTag
     * 
     * @param occ 
     * @return 
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {

        String linkString;
        String linkFileName;
        HTMLTag t = null;

        if(getSourceName() != null){
            linkFileName = getRelativePath(occ) + getSourceName();
            linkString = "<a class=\"classRef\" href=" + linkFileName + "#"
                    + getClassScopeName() + ">" + getName() + "</a>";
    
            t = new HTMLTag(occ, getName(), linkString);
        }
        return t;
    }

    /**
     * resolve referenced symbols
     * 
     * @param symbolTable 
     */
    void resolveTypes(SymbolTable symbolTable) {

        // System.err.println("resolving: "+getQualifiedName());
        // resolve superclass laundry
        super.resolveTypes(symbolTable);

        // if we have subclasses, resolve them to their symbols
        if (subClasses != null) {
            subClasses.resolveTypes(symbolTable);

            // Make sure we re-open imports
            openImports(symbolTable);
        }

        ClassDef newSuperClass = getSuperClass();

        if ((newSuperClass != null) && (newSuperClass instanceof DummyClass)) {

            // get its package name and look up the class/interace
            String pkg = ((DummyClass) newSuperClass).getPackage();

            newSuperClass = (ClassDef) symbolTable.lookupDummy(newSuperClass);

            if (newSuperClass == null) {
                newSuperClass = new DummyClass(
                        symbolTable.getUniqueName(getSuperClass().getName()), null,
                        symbolTable.getUniqueName(pkg));
            }

            // if we were able to resolve the superclass, add the reference
            // to its reference list and make it this class' superclass
            if (newSuperClass != null) {
                newSuperClass.addReference(getSuperClass().getOccurrence());
                setSuperClass(newSuperClass);
                newSuperClass.addSubclass(this);
                newSuperClass.setType(ClassDef.CLASS);
            }

            setType(CLASS);
        }

        if (interfaces != null) {
            interfaces.resolveTypes(symbolTable);

            // add this class to the list of implementers for each interface
            Enumeration e = interfaces.elements();

            while (e.hasMoreElements()) {
                ((ClassDef) e.nextElement()).addImplementer(this);
            }
        }

        // we're done, so toss the packages (only for top-level classes)
        if (isTopLevel()) {
            closeImports(symbolTable);
        }
    }

    /**
     * Method resolveRefs
     * 
     * @param symbolTable 
     */
    void resolveRefs(SymbolTable symbolTable) {

        JavaHashtable oldImports = symbolTable.getImports();
        JavaVector oldDemand = symbolTable.getDemand();

        super.resolveRefs(symbolTable);

        if (isTopLevel()) {
            closeImports(symbolTable);
            symbolTable.setImports(oldImports);
            symbolTable.setDemand(oldDemand);
        }
    }

    /**
     * Method generateClassList
     * 
     * @param tagList 
     */
    public void generateClassList(Vector tagList) {

        Enumeration e;
        Definition d;
        Occurrence o;
        ClassTag tag;
        String tagText;

        // List all classes in this class
        if (hasElements()) {
            JavaHashtable ht = getElements();

            e = ht.elements();

            String baseName;
            String link;

            while (e.hasMoreElements()) {
                d = (Definition) e.nextElement();

                if (d instanceof ClassDef) {
                    o = d.getOccurrence();
                    baseName = o.getFile().getName();
                    baseName = baseName.substring(0, baseName.lastIndexOf("."));
                    link = baseName + "_java.html";

                    if (!baseName.equals(d.getName())) {
                        link += "#" + o.getLine();
                    }

                    tagText = "<p class=\"classListItem\"><a href=\"" + link
                            + "\"" + "TARGET=\"classFrame\">"
                            + d.getScopedClassName() + "</a></p>";
                    tag = new ClassTag(d.getScopedClassName(), tagText);

                    tagList.addElement(tag);
                    ((ClassDef) d).generateClassList(tagList);
                }
            }
        }
    }

    /**
     * Set the list of interfaces that this class implements
     * 
     * @param interfaces 
     */
    void setInterfaces(JavaVector interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * set the superclass of this class
     * 
     * @param superClass 
     */
    void setSuperClass(ClassDef superClass) {

        this.superClass = superClass;

        setType(CLASS);
        superClass.setType(CLASS);
    }

    /**
     * Specify if this is a class or interface once we know
     * 
     * @param type 
     */
    void setType(int type) {
        classOrInterface = type;
    }

    /**
     * Method getClassScopeName
     * 
     * @return 
     */
    String getClassScopeName() {

        String result;

        if (getParentScope() instanceof ClassDef)    // inner class
        {
            result = getParentScope().getName() + "." + getName();
        } else {
            result = getName();
        }

        return result;
    }

    /**
     * Method writeExternal
     * 
     * @param out 
     * @throws java.io.IOException 
     */
    public void writeExternal(ObjectOutput out) throws java.io.IOException {

        // System.out.println("externalizing "+getName());
        out.writeObject(getName());
        out.writeObject(getPackageName());
        out.writeObject(getOccurrence());

        ScopedDef parentScopeOut = getParentScope();

        if (parentScopeOut instanceof PackageDef) {
            parentScopeOut = null;
        }

        if ((parentScopeOut == null) && (getPackageName() == null)) {
            new Exception().printStackTrace();    // DEBUG
        }

        out.writeObject(parentScopeOut);
        out.writeObject(elements);

        ClassDef superClassOut;

        if ((superClass instanceof ClassDef)
                && !(superClass instanceof ClassDefProxy)
                && !(superClass instanceof DummyClass)) {
            superClassOut = new ClassDefProxy(superClass);
        } else {
            superClassOut = superClass;
        }

        out.writeObject(superClassOut);
        out.writeObject(interfaces);
    }

    /**
     * Method readExternal
     * 
     * @param in 
     * @throws IOException            
     * @throws ClassNotFoundException 
     */
    public void readExternal(ObjectInput in)
            throws IOException, ClassNotFoundException {

        setName((String) in.readObject());
        SymbolTable.startReadExternal("ClassDef " + getName());

        packageName = (String) in.readObject();

        setOccurrence((Occurrence) (in.readObject()));

        ScopedDef parentScopeIn = (ScopedDef) in.readObject();

        elements = (JavaHashtable) in.readObject();
        superClass = (ClassDef) in.readObject();
        interfaces = (JavaVector) in.readObject();

        if (parentScopeIn == null) {
            parentScopeIn =
                    (ScopedDef) SymbolTable.getSymbolTable().findPackage(
                            packageName);
        }

        setParentScope(parentScopeIn);
        setupFileNames();

        String qualifiedName = packageName + "." + getName();

        // System.out.println("ClassDef.readExternal: registering class "+qualifiedName);
        allClassDefs.put(qualifiedName, this);
        SymbolTable.endReadExternal();
    }

    /**
     * Visitor design pattern.  Allow visitor to visit this definition,
     * then call accept method on its elements.
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
        elements.accept(visitor);
    }

    /**
     * Method toString
     * 
     * @return 
     */
    public String toString() {

        String str = "  " + super.toString() + "\n";

        if (interfaces != null) {
            Enumeration e = interfaces.elements();

            while (e.hasMoreElements()) {
                Definition d = (Definition) e.nextElement();

                str += "    " + d + "\n";
            }
        }

        JavaHashtable ht = getElements();
        Enumeration e = ht.elements();

        while (e.hasMoreElements()) {
            Definition d = (Definition) e.nextElement();

            str += "    " + d + "\n";
        }

        return str;
    }
}
