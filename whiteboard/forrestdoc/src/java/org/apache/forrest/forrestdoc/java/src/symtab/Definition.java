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

import java.io.File;
import java.io.FileWriter;
import java.util.StringTokenizer;

/**
 * This abstract class represents a symbol definition in a Java source file.
 * All symbols used in our Java symbol table stem from this definition.
 *
 * @version $Id: $
 */
public abstract class Definition implements Taggable, java.io.Serializable {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( Definition.class );

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** A file location where the item was defined */
    private Occurrence definition;

    /** The scope that contains this symbol */
    private ScopedDef parentScope;

    /** A list of references to this symbol */
    private JavaVector references;

    /** The name of the symbol */
    private String name;

    /** Field _sourceName */
    private String _sourceName;

    /** Field _refName */
    private String _refName;

    /**
     * Method getSourceName
     *
     * @return
     */
    public String getSourceName() {
        return _sourceName;
    }

    /**
     * Method getRefName
     *
     * @return
     */
    public String getRefName() {
        return _refName;
    }

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Default constructor is public for deserialization.
     */
    public Definition() {

        // create a new vector to keep track of references to this symbol
        this.references = new JavaVector();
    }

    /**
     * Constructor for the base of a symbol definition
     *
     * @param name
     * @param occ
     * @param parentScope
     */
    Definition(String name, // the symbol name
               Occurrence occ, // the location of its definition
               ScopedDef parentScope) {    // scope containing the def

        this();

        this.definition = occ;
        this.parentScope = parentScope;

        if (name != null) {
            this.name = name.intern();
        } else {
            this.name = name;
        }

        setupFileNames(occ);
    }

    /**
     * Method setupFileNames
     */
    protected void setupFileNames() {
        setupFileNames(definition);
    }

    /**
     * Setup source and reference file names.
     *
     * @param occ
     */
    private void setupFileNames(Occurrence occ) {

        if ((occ != null) && (occ.getFile() != null)) {
            String fileName = occ.getFile().toString();
            String baseName =
                    fileName.substring(fileName.lastIndexOf(File.separatorChar)
                    + 1, fileName.length());

            baseName = baseName.replace('.', '_');
            _refName = baseName + "_ref.html";
            _sourceName = baseName + ".html";

            // Intern strings
            _refName = _refName.intern();
            _sourceName = _sourceName.intern();
        }
    }

    /**
     * Add a location of a reference to the symbol to our reference list
     *
     * @param occ
     */
    void addReference(Occurrence occ) {

        if (log.isDebugEnabled())
        {
            log.debug("addReference(Occurrence) - Adding reference in:"+occ.getFile()+" to "+getQualifiedName());
        }

        if (occ != null) {
            occ.setDefinition(this);
            references.addElement(occ);
            SymbolTable.addFileReference(occ);
        }
    }

    /**
     * Subclasses override this method to create tags for references to this definition
     *
     * @param occ
     * @return
     */
    public abstract HTMLTag getOccurrenceTag(Occurrence occ);

    /**
     * Get a String representation of the location where this symbol
     * was defined
     *
     * @return
     */
    String getDef() {

        if (definition != null) {
            return definition.getLocation();
        }

        return "";
    }

    /**
     * Set the basic name of this symbol
     *
     * @param name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Get the basic name of the symbol
     *
     * @return
     */
    public String getName() {

        if (name == null) {
            return "~NO NAME~";
        }

        return name;
    }

    /**
     * Get the information about where the symbol was defined
     *
     * @return
     */
    public Occurrence getOccurrence() {

        if ((definition != null) && (definition.getPackageName() == null)) {
            definition.setPackageName(getPackageName());
        }

        return definition;
    }

    /**
     * Method setOccurrence
     *
     * @param definition
     */
    protected void setOccurrence(Occurrence definition) {
        this.definition = definition;
    }

    /**
     * Get the symbol that contains the definition of this symbol
     *
     * @return
     */
    public ScopedDef getParentScope() {
        return parentScope;
    }

    /**
     * Get the fully-qualified name of the symbol
     * Keep building the name by recursively calling the parentScope's
     * getQualifiedName() method...
     *
     * @return
     */
    public String getQualifiedName() {

        String nameToUse = name;

        if (name == null) {
            nameToUse = "~NO NAME~";
        }

        if ((getParentScope() != null)
                && !getParentScope().isDefaultOrBaseScope()) {
            return getParentScope().getQualifiedName() + "." + nameToUse;
        }

        return nameToUse;
    }

    /**
     * Get the package name by going up the list of parents till we find the package
     *
     * @return
     */
    String getPackageName() {

        ScopedDef d = getParentScope();

        if (d == null) {
            return null;
        }

        while (!(d instanceof PackageDef) && (d.getParentScope() != null)) {
            d = d.getParentScope();
        }

        return (d.getName());
    }

    /**
     * Get the name for the class without the package,but with outer classes
     *
     * @return
     */
    String getScopedClassName() {

        String name;
        ScopedDef d = getParentScope();

        if (!(d instanceof ClassDef)) {
            return getName();
        }

        name = d.getScopedClassName() + "." + getName();

        return (name);
    }

    /**
     * Method getPackagePath
     *
     * @return
     */
    public String getPackagePath() {

        String packageName = getPackageName();

        if (packageName != null) {
            packageName = packageName.replace('.', File.separatorChar);
        }

        return packageName;
    }

    /**
     * Method getRelativePath
     *
     * @param o
     * @return
     */
    String getRelativePath(Occurrence o) {

        String newPath = "";
        String packageName = null;

        if (log.isDebugEnabled())
        {
            log.debug("getRelativePath(Occurrence) - Package Name for "+getName()+"="+getPackageName());
            log.debug("getRelativePath(Occurrence) - Package Name for occurrence in "+o.getFile()+"="+o.getPackageName());
        }

        if (o != null) {
            packageName = o.getPackageName();
        }

        if ((getPackageName() != null) && (packageName != null)
                && !getPackageName().equals(packageName)) {
            String pathName = getPackagePath();
            StringTokenizer st = new StringTokenizer(packageName, ".");
            String backup = "";
            int dirs = 0;

            dirs = st.countTokens();

            for (int j = 0; j < dirs; j++) {
                backup = backup + "../" ;
            }

            newPath = backup + pathName + '/';

            if (log.isDebugEnabled())
            {
                log.debug("getRelativePath(Occurrence) - Packagename for ["+getName()+"]=["+getPackageName()+"]");
                log.debug("getRelativePath(Occurrence) - Occurrence   in ["+o.getClassName()+"."+o.getMethodName()+"] in File ["+o.getFile()+"]=["+o.getPackageName()+"]");
                log.debug("getRelativePath(Occurrence) - NewPath         ["+newPath+"]");
            }
        }

        return (newPath);
    }

    /**
     * Method getOccurrencePath
     *
     * @param o
     * @return
     */
    String getOccurrencePath(Occurrence o) {

        String occurrencePackageName = o.getPackageName();
        String newPath = "";

        if (log.isDebugEnabled())
        {
            log.debug("getOccurrencePath(Occurrence) - Package Name for "+getName()+"="+getPackageName());
            log.debug("getOccurrencePath(Occurrence) - Package Name for occurrence in "+o.getFile()+"="+o.getPackageName());
        }

        if ((getPackageName() != null) && (occurrencePackageName != null)
                && !getPackageName().equals(occurrencePackageName)) {

            // String occurrancePathName = o.getPackageName().replace('.',_separatorChar);
            // StringTokenizer st = new StringTokenizer(occurrencePackageName,".");
            String occurrancePathName = o.getPackageName().replace('.',
                    File.separatorChar);
            String mePackageName = getPackageName();
            StringTokenizer st =
                    new StringTokenizer(mePackageName, ".");
            String backup = "";
            int dirs = 0;

            dirs = st.countTokens();

            for (int j = 0; j < dirs; j++) {
                backup = backup + ".." + File.separatorChar;
            }

            newPath = backup + occurrancePathName + File.separatorChar;

            if (log.isDebugEnabled())
            {
                log.debug("getOccurrencePath(Occurrence) - Occurrence["+occurrencePackageName+"] me["+getPackageName()+"]");
                log.debug("getOccurrencePath(Occurrence) - newPath "+newPath);
            }
        }

        return (newPath);
    }

    /**
     * Determine if this symbol represents a class that is a superclass of
     * another symbol.  For most symbols, this is false (because most symbols
     * are not classes...).
     * This method will be overridden for classes and interfaces.
     *
     * @param def
     * @return
     */
    boolean isSuperClassOf(Definition def) {
        return false;
    }

    /**
     * return a String representation of this class for printing
     *
     * @return
     */
    public JavaVector getReferences() {
        return references;
    }

    /**
     * The "default" lookup routine.  This is used to search for a name within
     * the scope of another symbol.  This version of the lookup method is a
     * convenience that just passes -1 as the parameter count (meaning
     * look the name up as a non-method symbol
     *
     * @param name
     * @param type
     * @return
     */
    Definition lookup(String name, Class type) {
        return lookup(name, -1, type);
    }

    /**
     * Lookup a method in our scope.  Because this is only a valid
     * operation for scoped definitions, we default this to throw
     * an exception that states so
     *
     * @param name
     * @param numParams
     * @param type
     * @return
     */
    Definition lookup(String name, int numParams, Class type) {
        throw new IllegalArgumentException("Can't lookup in a " + getClass());
    }

    /**
     * @see org.apache.forrest.forrestdoc.java.src.symtab.Taggable#generateTags(org.apache.forrest.forrestdoc.java.src.symtab.HTMLTagContainer)
     */
    public abstract void generateTags(HTMLTagContainer tagList);

    /**
     * Default implementation of accept method (Visitor design pattern).
     *
     * @param visitor
     */
    public void accept(Visitor visitor) {
    }

    /**
     * generateReferences
     *
     * @param output
     */
    public void generateReferences(FileWriter output) {

        // Subclasses should override
    }

    /**
     * This method resolves any references to other symbols.
     * At this level there is nothing to resolve, so do nothing.
     *
     * @param symbolTable
     */
    void resolveTypes(SymbolTable symbolTable) {
    }

    /**
     * This method resolves any references to other symbols.
     * At this level there is nothing to resolve, so do nothing.
     *
     * @param symbolTable
     */
    void resolveRefs(SymbolTable symbolTable) {
    }

    /**
     * Set a reference to the symbol that syntactically
     * contains this symbol.
     *
     * @param parentScope
     */
    void setParentScope(ScopedDef parentScope) {
        this.parentScope = parentScope;
    }

    /**
     * Method getClassScopeName
     *
     * @return
     */
    String getClassScopeName() {
        return getName();
    }

    /**
     * Determine whether this is an instance of exactly the specified Class.
     *
     * @param o the object in question
     * @param c a Class object or null.  If null, always returns true.
     * @return
     */
    protected boolean isA(Class c) {

        if (c == null) {
            return true;
        }

        return getClass() == c;
    }

    /**
     * Serialize
     *
     * @param out
     * @throws java.io.IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {

        JavaVector saveRefs = references;

        references = null;

        out.defaultWriteObject();

        references = saveRefs;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getClass().getName() + " [" + getQualifiedName() + "]";
    }
}
