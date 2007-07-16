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

import org.apache.forrest.forrestdoc.java.src.util.JSCollections;
import org.apache.forrest.forrestdoc.java.src.util.SortableString;
import org.apache.forrest.forrestdoc.java.src.xref.JavaToken;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A SymbolTable object keeps track of all symbols encountered while
 * parsing a file.  It's main components are a list of all packages
 * that have been parsed or imported, and a stack of symbols that represent
 * the syntactical scope of a source file as it is being parsed.
 *
 * @version $Id: $
 */
public class SymbolTable {

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( SymbolTable.class );

    /** Field singleton */
    public static SymbolTable singleton = null;

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** a dummy scope to hold things like primitive types */
    private BlockDef baseScope = null;

    /** the "default" package to hold classes w/o package definitions */
    private PackageDef defaultPackage = null;

    /** A list of all strings encountered in the source */
    private StringTable names = new StringTable();

    /** A stack of currently-active scopes */
    private JavaStack activeScopes;

    /** A list of all packages that have been parsed or imported */
    private JavaHashtable packages;

    /**
     * A specific scope to look in if the source code contains
     * an explicitly-qualified identifier
     */
    private Definition qualifiedScope;

    /** The file that is currently being parsed */
    private File currentFile;

    /**
     * The method header that is currently being parsed.
     * This is used to associate variable definitions as
     * parameters to a method
     */
    private MethodDef currentMethod;

    /** A list of packages that are being imported on demand */
    private JavaVector demand;

    /** A list of classes that have been explicitly imported */
    private JavaHashtable importedClasses;

    /** The java.lang package */
    private PackageDef javaLang;

    /** The "java.lang.Object" class */
    private ClassDef object;

    /** Path to the output directory */
    private String outDirPath = null;

    /** Hashtable holds Vectors of ClassDefs for files */
    private static Hashtable _fileClassDefs;

    /** Hashtable holds Vectors mapping symbol references to files */
    private static Hashtable _fileReferences = new Hashtable();

    /** Hashtable holds Vectors mapping comments to files */
    private static Hashtable _fileComments = new Hashtable();

    /** Hashtable holds Vectors mapping literals to files */
    private static Hashtable _fileLiterals = new Hashtable();

    /** Hashtable holds Vectors mapping keywords to files */
    private static Hashtable _fileKeywords = new Hashtable();

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Method getPackages
     *
     * @return
     */
    public Hashtable getPackages() {
        return packages;
    }

    /**
     * Method createReferenceTags
     *
     * @param f
     * @param tagList
     */
    public static void createReferenceTags(File f, Vector tagList) {

        Vector v = (Vector) _fileReferences.get(f);

        if (v != null) {
            Enumeration enumList = v.elements();

            while (enumList.hasMoreElements()) {
                Occurrence occ = (Occurrence) enumList.nextElement();
                HTMLTag occTag = occ.getOccurrenceTag();

                if (occTag != null) {
                    tagList.addElement(occTag);
                }
            }
        }
    }

    /**
     * Method addFileReference
     *
     * @param occ
     */
    public static void addFileReference(Occurrence occ) {

        File f = occ.getFile();
        Vector v = (Vector) _fileReferences.get(f);

        if (v == null) {
            v = new Vector();

            _fileReferences.put(f, v);
        }

        v.add(occ);
    }

    /**
     * Method getCommentTags
     *
     * @param f
     * @param tagList
     */
    public static void getCommentTags(File f, Vector tagList) {

        Vector commentList = (Vector) _fileComments.get(f);

        if (commentList != null) {
            tagList.addAll(commentList);
        }
    }

    /**
     * Method getLiteralTags
     *
     * @param f
     * @param tagList
     */
    public static void getLiteralTags(File f, Vector tagList) {

        Vector literalList = (Vector) _fileLiterals.get(f);

        if (literalList != null) {
            tagList.addAll(literalList);
        }
    }

    /**
     * Method getKeywordTags
     *
     * @param f
     * @param tagList
     */
    public static void getKeywordTags(File f, Vector tagList) {

        Vector keywordList = (Vector) _fileKeywords.get(f);

        if (keywordList != null) {
            tagList.addAll(keywordList);
        }
    }

    /**
     * Method getImports
     *
     * @return
     */
    public JavaHashtable getImports() {
        return importedClasses;
    }

    /**
     * Method setImports
     *
     * @param imp
     */
    public void setImports(JavaHashtable imp) {
        importedClasses = imp;
    }

    /**
     * Method getDemand
     *
     * @return
     */
    public JavaVector getDemand() {
        return demand;
    }

    /**
     * Method setDemand
     *
     * @param dem
     */
    public void setDemand(JavaVector dem) {
        demand = dem;
    }

    /**
     * Method getSymbolTable
     *
     * @return
     */
    public static SymbolTable getSymbolTable() {

        if (singleton == null) {
            singleton = new SymbolTable();
        }

        return singleton;
    }

    /**
     * Constructor to create a new symbol table
     */
    private SymbolTable() {

        // allocate storage for the packages and scope lists
        packages = new JavaHashtable();
        activeScopes = new JavaStack();

        // Create a package object to represent java.lang
        javaLang = new PackageDef(getUniqueName("java.lang"),
                new Occurrence(null, 0), null);

        // Create a block to hold predefined types
        baseScope = new BlockDef(null, null, null);

        pushScope(baseScope);
        baseScope.setDefaultOrBaseScope(true);

        // define the predefined types
        // treat them as being an inheritance hierarchy to make widening
        // conversions automatic. For example, a float can widen to a double,
        // so we treat float as a _subclass_ of double.  This makes
        // parameter matching for method lookup simple!
        // (NOTE: the real parameter lookup routine that would be used
        // for method resolution is not implemented in this simple
        // cross-reference tool.  However, treating the primitives
        // as an inheritance hierarchy is included to show a
        // technique that might be used in a real cross-reference tool.)
        PrimitiveDef pBoolean = new PrimitiveDef(getUniqueName("boolean"),
                getObject(), baseScope);
        PrimitiveDef pDouble = new PrimitiveDef(getUniqueName("double"),
                getObject(), baseScope);
        PrimitiveDef pFloat = new PrimitiveDef(getUniqueName("float"),
                pDouble, baseScope);
        PrimitiveDef pLong = new PrimitiveDef(getUniqueName("long"), pFloat,
                baseScope);
        PrimitiveDef pInt = new PrimitiveDef(getUniqueName("int"), pLong,
                baseScope);
        PrimitiveDef pShort = new PrimitiveDef(getUniqueName("short"), pInt,
                baseScope);
        PrimitiveDef pByte = new PrimitiveDef(getUniqueName("byte"), pShort,
                baseScope);
        PrimitiveDef pChar = new PrimitiveDef(getUniqueName("char"), pInt,
                baseScope);
        PrimitiveDef pVoid = new PrimitiveDef(getUniqueName("void"),
                getObject(), baseScope);

        baseScope.add(pBoolean);
        baseScope.add(pDouble);
        baseScope.add(pFloat);
        baseScope.add(pLong);
        baseScope.add(pInt);
        baseScope.add(pShort);
        baseScope.add(pByte);
        baseScope.add(pChar);
        baseScope.add(pVoid);
    }

    /**
     * Method setOutDirPath
     *
     * @param outDirPath
     */
    public void setOutDirPath(String outDirPath) {
        this.outDirPath = outDirPath;
    }

    /**
     * Method getOutDirPath
     *
     * @return
     */
    public String getOutDirPath() {
        return outDirPath;
    }

    /**
     * Add a package to the list of packages available on demand
     * ("On demand" refers to imports that use an "*" to mean "any class
     * that resides in the package."  For example
     * import java.awt.*;
     * is an on-demand import that says "if we don't find a class anywhere
     * else, try to find it in the java.awt.* package.
     *
     * @param pkg
     */
    void addDemand(PackageDef pkg) {
        demand.addElement(pkg);
    }

    /**
     * Method createDirs
     *
     * @param f
     */
    public static void createDirs(File f) {

        String parentDir = f.getParent();
        File directory = new File(parentDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Method addFileClassDef
     *
     * @param f
     * @param classDef
     */
    public static void addFileClassDef(File f, ClassDef classDef) {

        if (_fileClassDefs == null) {
            _fileClassDefs = new Hashtable(2);
        }

        Vector v = (Vector) _fileClassDefs.get(f);

        if (v == null) {
            v = new Vector(2, 2);

            _fileClassDefs.put(f, v);
        }

        v.addElement(classDef);
    }

    /**
     * Method getClassList
     *
     * @param f
     * @return
     */
    public static String getClassList(File f) {

        if (_fileClassDefs == null) {
            return null;
        }

        Vector v = (Vector) _fileClassDefs.get(f);

        if (v == null) {
            return null;
        }

        String list = "";

        for (int i = 0; i < v.size(); i++) {
            Definition d = (Definition) v.elementAt(i);
            String name = d.getName();

            list += name;

            if (i < v.size() - 1) {
                list += ", ";
            }
        }

        return (list);
    }

    /**
     * Add a package that has been imported
     *
     * @param tok
     * @param className
     * @param packageName
     */
    public void addImport(JavaToken tok, String className, String packageName) {

        if (log.isDebugEnabled())
        {
            log.debug("addImport(JavaToken, String, String) - String className=" + className);
            log.debug("addImport(JavaToken, String, String) - String packageName=" + packageName);
        }

        if (importedClasses == null) {    // lazy instantiation
            importedClasses = new JavaHashtable();
        }

        // if there is no package name, use the default package
        if (packageName.equals(".") || packageName.equals("")) {
            importedClasses.put(getUniqueName("~default~"),
                    getDefaultPackage());

            return;
        }

        // otherwise, chop the extra "." that the parser adds...
        else {
            packageName = packageName.substring(1);
        }

        // if there is no class, we are importing a package on demand
        // so create a dummy package definition (if one doesn't already
        // exist)
        if (className == null) {
            Definition d = (Definition) packages.get(packageName);

            if (d == null) {
                d = new PackageDef(
                        getUniqueName(packageName),
                        new Occurrence(
                                currentFile, tok.getLine(), tok.getColumn()), null);

                packages.put(packageName, d);
            }

            importedClasses.put(d.getName(), d);

            // reference(tok);
        }

        // otherwise, create a placeholder class for class/interface ref
        else {

            if (log.isDebugEnabled())
            {
                log.debug("addImport(JavaToken, String, String) - Created placeholder for:"+getUniqueName(className));
            }

            importedClasses.put(
                    getUniqueName(className),
                    new DummyClass(
                            getUniqueName(className),
                            new Occurrence(currentFile, tok.getLine(), tok.getColumn()),
                            getUniqueName(packageName)));

            // reference(tok);
        }
    }

    /**
     * Add an element to the current scope
     *
     * @param def
     */
    void addToCurrentScope(Definition def) {

        // add the definition to the current scope
        getCurrentScope().add(def);

        // set the parent scope for the definition
        def.setParentScope(getCurrentScope());
    }

    /**
     * We are done with imports, so clear the list
     */
    void closeImports() {
        demand = null;
        importedClasses = null;
    }

    /**
     * Define a curly-brace-delimited block of code
     *
     * @param tok
     * @return
     */
    public Definition defineBlock(JavaToken tok) {

        // create a new block definition and push it
        // as the current scope
        BlockDef def = new BlockDef(null, getOccurrence(tok),
                getCurrentScope());

        addToCurrentScope(def);

        return pushScope(def);
    }

    /**
     * Define a class object
     *
     * @param theClass
     * @param superClass
     * @param interfaces
     */
    public void defineClass(JavaToken theClass, // class being created
                            JavaToken superClass, // its superclass
                            JavaVector interfaces) {    // interfaces it implements

        // note -- we leave interfaces as a vector of JavaTokens for now
        // we'll resolve them in pass 2.
        // create a new class definition for the class
        ClassDef def = new ClassDef(getUniqueName(theClass),
                getOccurrence(theClass),
                (superClass == null)
                ? null
                : getDummyClass(superClass), interfaces,
                getCurrentScope());

        def.setType(ClassDef.CLASS);

        // add the imported classes/packages to the class
        def.setImports(importedClasses);

        // add the class to the current scope
        addToCurrentScope(def);
        SymbolTable.addFileClassDef(currentFile, def);

        // make the claa be the new current scope
        pushScope(def);
    }

    /**
     * Define an interface object
     *
     * @param theInterface
     * @param superInterfaces
     */
    public void defineInterface(JavaToken theInterface,
                                JavaVector superInterfaces) {

        // note -- we leave superInterfaces as a vector of JavaTokens for now.
        // we'll resolve in pass 2.
        // create the new interface object
        ClassDef def = new ClassDef(getUniqueName(theInterface),
                getOccurrence(theInterface),
                null, // no super class...
                superInterfaces, getCurrentScope());

        def.setType(ClassDef.INTERFACE);

        // add it to the current scope
        addToCurrentScope(def);
        SymbolTable.addFileClassDef(currentFile, def);

        // make the interface the current scope
        pushScope(def);
    }

    /**
     * Define a new label object
     *
     * @param theLabel
     */
    public void defineLabel(JavaToken theLabel) {

        addToCurrentScope(new LabelDef(getUniqueName(theLabel),
                getOccurrence(theLabel),
                getCurrentScope()));
    }

    /**
     * Define a new comment object
     *
     * @param line
     * @param column
     * @param text
     */
    public void defineComment(int line, int column, String text) {

        int length = text.length();

        /*
         * Removing since we now skip carriage returns
         *  if (text.indexOf("\r\n")>0) {
         *  StringTokenizer st = new StringTokenizer(text,"\r\n");
         *  int numBreaks = Math.max(st.countTokens()-1,1);
         *  length=length-numBreaks;
         *  }
         */
        String packageName = getCurrentPackageName();
        HTMLTag t = new HTMLTag(currentFile, line, column,
                packageName, length);
        Vector commentList = (Vector) _fileComments.get(currentFile);

        if (commentList == null) {
            commentList = new Vector();

            _fileComments.put(currentFile, commentList);
        }

        commentList.addElement(t);
    }

    /**
     * Define a new literal object
     *
     * @param line
     * @param column
     * @param text
     */
    public void defineLiteral(int line, int column, String text) {

        int length = text.length();
        String packageName = getCurrentPackageName();
        HTMLTag t = new HTMLTag(currentFile, line, column,
                packageName, length, HTMLTag.TYPE_LITERAL);
        Vector literalList = (Vector) _fileLiterals.get(currentFile);

        if (literalList == null) {
            literalList = new Vector();

            _fileLiterals.put(currentFile, literalList);
        }

        literalList.addElement(t);
    }

    /**
     * Define a new keyword object
     *
     * @param line
     * @param column
     * @param text
     */
    public void defineKeyword(int line, int column, String text) {

        int length = text.length();
        String packageName = getCurrentPackageName();
        HTMLTag t = new HTMLTag(currentFile, line, column,
                packageName, length, HTMLTag.TYPE_KEYWORD);
        Vector keywordList = (Vector) _fileKeywords.get(currentFile);

        if (keywordList == null) {
            keywordList = new Vector();

            _fileKeywords.put(currentFile, keywordList);
        }

        keywordList.addElement(t);
    }

    /**
     * Define a new method object
     *
     * @param theMethod
     * @param type
     */
    public void defineMethod(JavaToken theMethod, JavaToken type) {

        if (log.isDebugEnabled())
        {
            log.debug("defineMethod(JavaToken, JavaToken) - JavaToken theMethod=" + theMethod.getText());
        }

        // if there is no type, this is a constructor
        String name;
        String className = null;

        if (type == null) {

            // name = "~constructor~";
            name = theMethod.getText();
            className = theMethod.getText();
            currentMethod = new MethodDef(getUniqueName(name), className,
                    getOccurrence(theMethod),
                    getDummyClass(type),
                    getCurrentScope());
        }

        // otherwise use its real name
        else {
            if (theMethod == null) {
                theMethod = type;
                type = null;
            }

            name = theMethod.getText();
            currentMethod = new MethodDef(getUniqueName(name),
                    getOccurrence(theMethod),
                    getDummyClass(type),
                    getCurrentScope());
        }

        // add the method to the current scope
        addToCurrentScope(currentMethod);

        // make the method be the current scope
        pushScope(currentMethod);
    }

    /**
     * Define a new package object
     * This is an adapter version to get the name of the package from a token
     *
     * @param tok
     */
    public void definePackage(JavaToken tok) {
        definePackage(getUniqueName(tok));
    }

    /**
     * Define a new package object
     *
     * @param name
     * @return
     */
    PackageDef definePackage(String name) {

        // find/add this package
        PackageDef pkg = lookupPackage(name);

        // make the package be the current scope
        pushScope(pkg);

        return pkg;
    }

    /**
     * create a variable definition
     *
     * @param theVariable
     * @param type
     */
    public void defineVar(JavaToken theVariable, JavaToken type) {

        // create the variable definition
        VariableDef v = new VariableDef(getUniqueName(theVariable),
                getOccurrence(theVariable),
                getDummyClass(type), getCurrentScope());

        // if we are in a method's parameter def, add to its parameters
        if (currentMethod != null) {
            currentMethod.add(v);

            // otherwise, add to the current scope
        } else {
            addToCurrentScope(v);
        }
    }

    /**
     * State that we are done processing the method header
     *
     * @param exceptions
     */
    public void endMethodHead(JavaVector exceptions) {

        // set its thrown exception list
        currentMethod.setExceptions(exceptions);

        // reset the method indicator
        // NOTE:  this is not a problem for inner classes; you cannot define an
        // inner class inside a formal parameter list, so we don't need a
        // stack of methods here...
        currentMethod = null;
    }

    /**
     * look for the name in the import list for this class
     *
     * @param name
     * @param type
     * @return
     */
    Definition findInImports(String name, Class type) {

        if (log.isDebugEnabled())
        {
            log.debug("findInImports(String, Class) - String name=" + name);
            log.debug("findInImports(String, Class) - importedClasses=" + importedClasses);
            log.debug("findInImports(String, Class) - demand=" + demand);
        }

        Definition def = null;

        // look at the stuff we imported
        // (the name could be a class name)
        if (importedClasses != null) {
            def = (Definition) importedClasses.get(name);
        }

        if (log.isDebugEnabled())
        {
            log.debug("findInImports(String, Class) - def=" + def);
        }

        // TBD: best place for this?
        if (def instanceof DummyClass) {
            String packageName = ((DummyClass) def).getPackage();

            if (log.isDebugEnabled())
            {
                log.debug("findInImports(String, Class) - DummyClass's packageName is "+packageName);
            }

            if (packageName != null) {
                PackageDef pd = (PackageDef) packages.get(packageName);

                def = pd.lookup(def.getName(), type);

                if (log.isDebugEnabled())
                {
                    log.debug("findInImports(String, Class) - pd.lookup returned "+def);
                }
            }
        }

        // otherwise, take a look in the import-on-demand packages to
        // see if the class is defined
        if ((def == null) && (demand != null) && (name.charAt(0) != '~')) {
            Enumeration e = demand.elements();

            while ((def == null) && e.hasMoreElements()) {
                PackageDef pd = (PackageDef) e.nextElement();

                if (log.isDebugEnabled())
                {
                    log.debug("findInImports(String, Class) - searching in package "+pd.getName());
                }
                def = pd.lookup(name, type);
            }
        }

        return def;
    }

    /**
     * Lookup a package in the list of all parsed packages
     *
     * @param name
     * @return
     */
    Definition findPackage(String name) {
        return (Definition) packages.get(name);
    }

    /**
     * Retrieve or add a package to the list of packages
     *
     * @param name
     * @return
     */
    PackageDef lookupPackage(String name) {

        PackageDef result = (PackageDef) packages.get(name);

        if (result == null) {
            result = new PackageDef(getUniqueName(name), null, null);

            packages.put(name, result);
        }

        return result;
    }

    /**
     * Return the currently-active scope
     *
     * @return
     */
    ScopedDef getCurrentScope() {

        if (activeScopes.empty()) {
            return null;
        }

        return (ScopedDef) activeScopes.peek();
    }

    /**
     * Define a new package object
     *
     * @return
     */
    PackageDef getDefaultPackage() {

        // if the default package has not yet been defined, create it
        // (lazy instantiation)
        if (defaultPackage == null) {
            defaultPackage = new PackageDef(getUniqueName("~default~"), null,
                    null);

            packages.put(getUniqueName("~default~"), defaultPackage);
            defaultPackage.setDefaultOrBaseScope(true);
        }

        return defaultPackage;
    }

    /**
     * Create a new dummy class object
     *
     * @param tok
     * @return
     */
    public DummyClass getDummyClass(JavaToken tok) {

        if (tok == null) {
            return null;
        }

        return new DummyClass(getUniqueName(tok), getOccurrence(tok));
    }

    /**
     * Get the java.lang.Object object
     *
     * @return
     */
    ClassDef getObject() {

        if (object == null) {    // lazy instantiation
            object = new DummyClass();

            object.setType(ClassDef.CLASS);

            // add it to package java.lang
            javaLang.add(object);
        }

        return object;
    }

    /**
     * Create a new occurrence object
     *
     * @param tok
     * @return
     */
    Occurrence getOccurrence(JavaToken tok) {

        if (tok == null) {
            return new Occurrence(null, 0);
        }

        return new Occurrence(currentFile, tok.getLine(), tok.getColumn(),
                    getCurrentPackageName(),
                    getCurrentClassName(),
                    getCurrentMethodName());
    }

    /**
     * return the current qualified scope for lookup.
     *
     * @return
     */
    Definition getScope() {
        return qualifiedScope;
    }

    /**
     * Get a unique occurrence of a String that has the name we want
     *
     * @param tok
     * @return
     */
    String getUniqueName(JavaToken tok) {
        return getUniqueName(tok.getText());
    }

    /**
     * Get a unique occurrence of a String that has the specified name
     *
     * @param name
     * @return
     */
    String getUniqueName(String name) {
        return names.getName(name);
    }

    /**
     * Lookup a non-method in the symbol table
     * This version of lookup is a convenience wrapper that just passes -1
     * as the parameter count to the real lookup routine
     *
     * @param name
     * @param type
     * @return
     */
    Definition lookup(String name, Class type) {
        return lookup(name, -1, type);
    }

    /**
     * Lookup a name in the symbol table
     *
     * @param name
     * @param numParams
     * @param type
     * @return
     */
    Definition lookup(String name, int numParams, Class type) {

        if (log.isDebugEnabled())
        {
            log.debug("lookup(String, int, Class) - String name=" + name);
            log.debug("lookup(String, int, Class) - int numParams=" + numParams);
        }

        Definition def = null;
        StringTokenizer st = null;
        String afterPackage = null;

        // If we have a dot ('.') in the name, we must first resolve the package,
        // class or interface that starts the name, the progress through the
        // name
        if (name.indexOf('.') > 0) {

            // /NOTE: class names can have the same name as a package and
            // context will determine the result!!!!
            // eg.  pkg com.magelang.xref  and class xref in
            // com.magelang can coexist...
            // The lookup algorithm use here is far simpler, and may
            // resolve improperly (ie, if we have packages
            // com.magelang.xref and com.magelang and the string we are
            // testing is com.magelang.xref, we will always assume that it
            // is a package)
            // A better implementation of this routine would
            // try to find the proper name by trying to match the entire
            // name at each stage rather than just finding the longest
            // valid package name.
            String testName = null;

            // break up the string into an enumerartion of substrings that were
            // separated by dots ('.')
            st = new StringTokenizer(name, ".");

            // We'll walk through to find the longest package name that we
            // know about, then start checking to see if the rest of the
            // elements are validly scoped within that package
            if (log.isDebugEnabled())
            {
                log.debug("lookup(String, int, Class) - String name=" + name);
            }

            boolean doneWithPackage = false;

            while (st.hasMoreElements()) {
                String id = (String) st.nextElement();
                Definition testIt = null;

                if (testName == null) {
                    testName = id;

                    // see if the first part of the name is an imported class
                    if (log.isDebugEnabled())
                    {
                        log.debug("lookup(String, int, Class) - calling findInImports " + testName);
                    }

                    def = findInImports(testName, type);

                    if (log.isDebugEnabled())
                    {
                        log.debug("lookup(String, int, Class) - findInImports returned " + def);
                    }

                    if (def != null) {
                        if (log.isDebugEnabled())
                        {
                            log.debug("lookup(String, int, Class) - Found in imports:"
                                    + def.getName());
                        }

                        doneWithPackage = true;
                        id = (String) st.nextElement();
                    }
                } else {
                    testName += "." + id;
                }

                // keep track of the longest name that is a package
                if (log.isDebugEnabled())
                {
                    log.debug("lookup(String, int, Class) - Looking for package:"+testName);
                }
                testIt = (PackageDef) packages.get(testName);

                if (log.isDebugEnabled())
                {
                    log.debug("lookup(String, int, Class) - doneWithPackage=" + doneWithPackage
                            + " testIt=" + ((testIt == null)
                            ? "null"
                            : testIt.getName()) + " afterPackage="
                            + afterPackage);
                }

                if (!doneWithPackage && (testIt != null)) {
                    def = testIt;
                    afterPackage = null;
                } else if (afterPackage == null) {
                    afterPackage = id;
                } else {
                    afterPackage += "." + id;
                }
            }
        }

        // otherwise, just try to find the name in the imported classes/packages
        else if (numParams == -1) {
            def = findInImports(name, type);

            if (log.isDebugEnabled())
            {
                log.debug("lookup(String, int, Class) - findInImports returned " + def);
                if ((def != null) && (def.getParentScope() != null)) {
                    log.debug("lookup(String, int, Class) - parentScope" + def.getParentScope());
                }
            }

            if (def != null) {

                if (log.isDebugEnabled())
                {
                    log.debug("lookup(String, int, Class) - lookup Returning import:"+def.getQualifiedName());
                }
                return def;
            }
        }

        // At this point, we may have a definition that represents the
        // part of the name to the left of the rightmost dot ('.')
        // If we have such a definition, there might be something after it;
        // a final part of the name. If so, we need to push the scope of the
        // leftmost part of the identifier.  If not, we just want to analyze
        // the entire name as a unit.
        if (log.isDebugEnabled())
        {
            if (def == null) {
                log.debug("lookup(String, int, Class) - def=null");
            } else {
                log.debug("lookup(String, int, Class) - def="+def.getName());
            }

            log.debug("lookup(String, int, Class) - afterpackage=" + afterPackage);
        }

        if ((def != null) && (afterPackage != null)) {
            if (log.isDebugEnabled())
            {
                log.debug("lookup(String, int, Class) - Setting scope=" + def.getName());
            }

            setScope(def);
        } else {
            if (log.isDebugEnabled())
            {
                log.debug("lookup(String, int, Class) - afterPackage=" + name);
            }

            afterPackage = name;
        }

        // Here we know we have more to look at...
        if (afterPackage != null) {    // more to chew on...

            // check to see if we still have any DOTs in the name
            // if so, we'll need to figure out where certain names start/end
            st = new StringTokenizer(afterPackage, ".");

            // find and load the classes up to the last
            while (st.hasMoreElements()) {
                String id = (String) st.nextElement();

                // if a explicit scope qualification was specified, look only there
                if (qualifiedScope != null) {
                    if (log.isDebugEnabled())
                    {
                        log.debug("lookup(String, int, Class) - Checking qualified scope:"
                                + qualifiedScope);
                    }

                    def = qualifiedScope.lookup(id, numParams, type);

                    resetScope();
                }

                // Otherwise, first try a scoped lookup
                else {
                    if (log.isDebugEnabled())
                    {
                        log.debug("lookup(String, int, Class) - Checking activeScopes for:" + id);
                    }

                    def = activeScopes.lookup(id, numParams, type);

                    if (def == null) {
                        def = activeScopes.lookup(id, -1, type);
                    }
                }

                if (def == null) {
                    if (log.isDebugEnabled())
                    {
                        log.debug("lookup(String, int, Class) - " + id + " Not found in activescopes");
                    }

                    break;
                }

                if (st.hasMoreElements()) {
                    setScope(def);
                }
            }
        }

        if (def == null) {
            if (log.isDebugEnabled())
            {
                log.debug("lookup(String, int, Class) - lookup returning null:" + name);
            }
        } else {
            if (log.isDebugEnabled())
            {
                log.debug("lookup(String, int, Class) - lookup returning:"
                        + def.getQualifiedName());
            }
        }

        return def;
    }

    /**
     * Lookup a class based on a placeholder for that class
     *
     * @param d
     * @return
     */
    ClassDef lookupDummy(Definition d) {

        // construct a qualified class name
        String className;
        String pkg = ((DummyClass) d).getPackage();
        Definition newD = null;

        if (pkg == null) {
            className = d.getName();
        } else {
            className = pkg + "." + d.getName();

            if (packages.get(pkg) == null) {
                packages.put(pkg, new PackageDef(pkg, null, null));
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug("lookupDummy(Definition) - String className=" + className);
        }
        newD = lookup(className, ClassDef.class);

        if (log.isDebugEnabled())
        {
            log.debug("lookupDummy(Definition) - String newD=" + newD);
        }
        if ((newD != null) && !(newD instanceof ClassDef)) {
            if (log.isDebugEnabled())
            {
                log.debug("lookupDummy(Definition) - newD is a " + newD);
                log.debug("lookupDummy(Definition) - d="+d + "parentScope = "+d.getParentScope());
            }
        }

        return (ClassDef) newD;
    }

    /**
     * Set up the list of imported packages for use in symbol lookup
     *
     * @param imports
     */
    void openImports(JavaHashtable imports) {

        // start a new demand list
        demand = new JavaVector();

        // add package java.lang to the demand list...
        demand.addElement(javaLang);

        importedClasses = new JavaHashtable();

        // if this class has something to import...
        if (imports != null) {

            // walk through the list of imports
            Enumeration e = imports.elements();

            while (e.hasMoreElements()) {

                // add the package or class to the demand or import list
                // based on the type of import it was
                Definition d = (Definition) e.nextElement();

                if (d instanceof PackageDef) {

                    if (log.isDebugEnabled())
                    {
                        log.debug("openImports(JavaHashtable) - Adding package "+d.getName()+" to imports");
                    }
                    addDemand((PackageDef) d);
                } else {
                    if (d instanceof DummyClass) {

                        if (log.isDebugEnabled())
                        {
                            log.debug("openImports(JavaHashtable) - found DummyClass " + d.getName() + " package "
                                + ( (DummyClass) d ).getPackage() + " in import list");
                        }
                        Definition newD = lookupDummy(d);

                        if (newD != null) {

                            if (log.isDebugEnabled())
                            {
                                log.debug("openImports(JavaHashtable) - Adding class "+newD.getName()+" to imports");
                            }
                            d = newD;
                        }
                    }

                    importedClasses.put(d.getName(), d);
                }
            }
        }
    }

    /**
     * Clear the scope stack (except the base scope for primitives)
     */
    public void popAllScopes() {

        while (activeScopes.peek() != baseScope) {
            activeScopes.pop();
        }

        importedClasses = null;
    }

    /**
     * Pop off the current scope from the stack
     */
    public void popScope() {
        activeScopes.pop();
    }

    /**
     * Push a scope on the stack for symbol lookup
     *
     * @param scope
     * @return
     */
    Definition pushScope(Definition scope) {

        if (!(scope instanceof ScopedDef)) {
            throw new RuntimeException("Not a ScopedDef");
        }

        activeScopes.push(scope);

        return scope;
    }

    /**
     * Get current package scope *
     *
     * @return
     */
    String getCurrentPackageName() {

        String packageName = "";

        if (activeScopes == null) {
            return packageName;
        }

        Enumeration e = activeScopes.elements();

        while (e.hasMoreElements()) {
            ScopedDef d = (ScopedDef) e.nextElement();

            if (d instanceof PackageDef) {
                packageName = d.getName();

                break;
            }
        }

        return packageName;
    }

    /**
     * Get current class scope *
     *
     * @return
     */
    String getCurrentClassName() {

        String className = "?";

        if (activeScopes == null) {
            return className;
        }

        Enumeration e = activeScopes.elements();

        while (e.hasMoreElements()) {
            ScopedDef d = (ScopedDef) e.nextElement();

            if (d instanceof ClassDef) {
                className = d.getName();

                break;
            }
        }

        return className;
    }

    /**
     * Get current method scope *
     *
     * @return
     */
    String getCurrentMethodName() {

        String methodName = "?";

        if (activeScopes == null) {
            return methodName;
        }

        Enumeration e = activeScopes.elements();

        while (e.hasMoreElements()) {
            ScopedDef d = (ScopedDef) e.nextElement();

            if (d instanceof MethodDef) {
                methodName = d.getName();

                break;
            }
        }

        return methodName;
    }

    /**
     * Add an unresolved reference to the current scope
     *
     * @param t
     */
    public void reference(JavaToken t) {

        t.setFile(currentFile);
        t.setPackageName(getCurrentPackageName());
        t.setClassName(getCurrentClassName());
        t.setMethodName(getCurrentMethodName());
        getCurrentScope().addUnresolved(t);
    }

    /**
     * Method generatePackageTags
     *
     * @return
     */
    public Hashtable generatePackageTags() {

        Vector tagList;
        Vector sortedList;
        Hashtable tagTable = new Hashtable(2);

        // Write out information about each package
        Enumeration e = packages.elements();

        while (e.hasMoreElements()) {
            PackageDef p = (PackageDef) e.nextElement();

            tagList = p.generateClassList();

            if (tagList != null) {
                sortedList = JSCollections.sortEnumeration(tagList.elements());

                tagTable.put(new SortableString(p.getScopedClassName()),
                        sortedList);
            }
        }

        return (tagTable);
    }

    // public void generateReferenceFiles(String path)
    // {
    // // Write out information about each package
    // Enumeration e = packages.elements();
    // while(e.hasMoreElements()) {
    // PackageDef p = (PackageDef)e.nextElement();
    // p.generateReferenceFiles(path);
    // }
    // }

    /**
     * unset the qualifiedScope so normal scoped lookup applies on the next
     * name to look up
     */
    void resetScope() {
        setScope((Definition) null);
    }

    /**
     * resolve types of anything that needs resolution in the symbol table
     */
    public void resolveTypes() {

        // for each package, resolve its references
        if (defaultPackage != null) {
            defaultPackage.resolveTypes(this);
        }

        packages.resolveTypes(this);
    }

    /**
     * resolve types of anything that needs resolution in the symbol table
     */
    public void resolveRefs() {

        // for each package, resolve its references
        if (defaultPackage != null) {
            defaultPackage.resolveRefs(this);
        }

        packages.resolveRefs(this);
    }

    /**
     * Method persistRefs
     *
     * @param outDirPath
     */
    public void persistRefs(String outDirPath) {

        ReferencePersistor rp = new ReferencePersistor(outDirPath);

        if (defaultPackage != null) {
            defaultPackage.accept(rp);
        }

        packages.accept(rp);
        rp.done();
    }

    /**
     * Mark the current file that is being parsed
     *
     * @param file
     */
    public void setFile(File file) {
        currentFile = file;
    }

    /**
     * set the lookup scope to the nearest enclosing class (for "this.x")
     */
    public void setNearestClassScope() {

        // find the nearest class scope
        setScope(activeScopes.findTopmostClass());
    }

    /**
     * Set the qualified scope for the next name lookup.  Names will only be
     * searched for within that scope.  This version of setScope looks up
     * the definition to set based on its name as received from a token...
     *
     * @param t
     */
    void setScope(JavaToken t) {

        Definition def = lookup(t.getText(), null);

        if (def != null) {
            def.addReference(getOccurrence(t));
            setScope(def);
        }
    }

    /**
     * Set the qualified scope for the next name lookup.  Names will only be
     * searched for within that scope
     *
     * @param d
     */
    void setScope(Definition d) {

        while ((d != null) && (d instanceof TypedDef)) {
            d = ((TypedDef) d).getType();
        }

        if ((d == null) || (d instanceof ScopedDef)) {
            qualifiedScope = d;
        }
    }

    /**
     * Set the qualified scope for the next name lookup.  Names will only be
     * searched for within that scope.  This version of setScope looks up
     * the definition to set based on its name...
     *
     * @param name
     */
    void setScope(String name) {

        Definition def = lookup(name, null);

        if (def != null) {
            setScope(def);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        String str = "";
        Enumeration e = packages.elements();

        while (e.hasMoreElements()) {
            PackageDef p = (PackageDef) e.nextElement();

            str += p.toString();
        }

        return str;
    }

    /**
     * Used to push the scope of the default package.  This is used by the
     * parser if the source file being parsed does not contain a package
     * specification.
     */
    public void useDefaultPackage() {
        pushScope(getDefaultPackage());
    }

    /** Field readLevel */
    static int readLevel = 0;

    /**
     * Method startReadExternal
     *
     * @param name
     */
    static public void startReadExternal(String name) {

        // for (int i=0; i<readLevel; i++)
        // System.out.print(' ');
        // System.out.println("reading "+name);
        // readLevel++;
    }

    /**
     * Method endReadExternal
     */
    static public void endReadExternal() {

        // readLevel--;
    }
}
