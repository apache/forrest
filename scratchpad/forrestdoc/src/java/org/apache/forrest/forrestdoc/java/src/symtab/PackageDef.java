/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/PackageDef.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Definition of a package.
 */
public class PackageDef extends ScopedDef implements java.io.Serializable {

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Constructor to create a package object
     * 
     * @param name        
     * @param occ         
     * @param parentScope 
     */
    PackageDef(String name, // the name of the package
               Occurrence occ, // where it was defined (NULL)
               ScopedDef parentScope) {    // which scope owns it

        super(name, occ, parentScope);

        String relPath = name.replace('.', File.separatorChar);

        persistedDefs = new Hashtable();

        // HACK -- because java.lang PackageDef is created in SymbolTable constructor!
        if (!name.equals("java.lang")) {
            File outputDir =
                    new File(SymbolTable.getSymbolTable().getOutDirPath()
                    + File.separatorChar + relPath);

            // System.out.println("checking for def files in "+outputDir.getAbsolutePath());
            FilenameFilter filter = new FilenameFilter() {

                public boolean accept(File dir, String defName) {
                    return defName.endsWith(".def");
                }
            };
            File[] files = outputDir.listFiles(filter);

            if (files != null) {

                // System.out.println("def files are:");
                for (int i = 0; i < files.length; i++) {
                    String filename = files[i].getName();

                    // System.out.println("\t"+filename);
                    String className = filename.substring(0, filename.length()
                            - ".def".length());

                    // System.out.println("className="+className);
                    persistedDefs.put(className, files[i]);
                }
            }
        }
    }

    /**
     * Method getDefinitions
     * 
     * @return 
     */
    public Hashtable getDefinitions() {
        return getElements();
    }

    /**
     * Method persistDefinitions
     * 
     * @param path 
     */
    public void persistDefinitions(String path) {

        // System.out.println("\tpath="+path);
        Enumeration e;
        Definition d;
        Occurrence o;
        String newPath;
        File currentFile = null;
        Vector files = new Vector(20, 20);

        if (hasElements()) {
            JavaHashtable ht = getElements();

            e = ht.elements();

            while (e.hasMoreElements()) {
                d = (Definition) e.nextElement();
                newPath = path + File.separatorChar + d.getPackagePath();

                if (!((d instanceof ClassDefProxy)
                        || (d instanceof DummyClass))) {
                    try {
                        String fileName = newPath + File.separatorChar
                                + d.getName() + ".def";

                        currentFile = new File(fileName);

                        new File(currentFile.getParent()).mkdirs();

                        // System.out.println("\tWriting "+currentFile.getAbsolutePath());
                        FileOutputStream fos =
                                new FileOutputStream(currentFile);
                        ObjectOutputStream output = new ObjectOutputStream(fos);

                        output.writeObject(d);
                        output.flush();
                        output.close();
                        fos.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Method persistReferences
     * 
     * @param path 
     */
    public void persistReferences(String path) {

        ReferencePersistor rp = new ReferencePersistor(path);

        accept(rp);

        // Enumeration enum = getElements().elements();
        // while (enum.hasMoreElements()) {
        // ClassDef def = (ClassDef)enum.nextElement();
        // String newPath = path + File.separatorChar + d.getPackagePath();
        // if (!(def instanceof DummyClass || def instanceof PrimitiveDef) ) {
        // String filename = newPath + File.separatorChar + def.getName() + ".ref";
        // }
        // }
    }

    /**
     * Method accept
     * 
     * @param visitor 
     */
    public void accept(Visitor visitor) {

        visitor.visit(this);

        if (elements != null) {
            elements.accept(visitor);
        }
    }

    // /**
    // * The reference files are the HTML files that contain links to all references to
    // * a file's definitions.  There's one reference file per source file, regardless
    // * of how many classes a source file defines.
    // */
    // public void generateReferenceFiles(String path)
    // {
    // Enumeration e;
    // Definition d;
    // Occurrence o;
    // String newPath;
    // File currentFile=null;
    // // Since more than one class may live in the same file,
    // // each time we process a class we need to figure out whether
    // // we already created a reference file for that class's source
    // // file.  The files Vector contains a File object per reference file.
    // Vector files=new Vector(20,20);
    // if (hasElements()) {
    // JavaHashtable ht = getElements();
    // e = ht.elements();
    // // iterate over this package's classes
    // while (e.hasMoreElements()) {
    // d = (Definition) e.nextElement();
    // newPath=path+File.separatorChar+d.getPackagePath();
    // //              System.out.println("newPath="+newPath);
    // if (d instanceof ClassDef) {
    // try {
    // String refName = ((ScopedDef) d).getRefName();
    // String fileName=newPath+File.separatorChar+refName;
    // currentFile=new File(fileName);
    // FileWriter output;
    // if (files.contains(currentFile)) {
    // output = new FileWriter(currentFile.getPath(),true);
    // }
    // else {
    // SymbolTable.createDirs(currentFile);
    // output = new FileWriter(currentFile);
    // files.addElement(currentFile);
    // output.write("<html><head><link rel=\"stylesheet\" " +
    // "type=\"text/css\"" +
    // "href=\"" +
    // javasrc.app.JavaSrc.getBackupPath(d.getPackageName()) +
    // "styles.css\"></head><body>");
    // }
    // ((ClassDef) d).generateReferences(output);
    // output.write("</body></html>");
    // output.flush();
    // output.close();
    // }
    // catch (Exception ex) {ex.printStackTrace(); }
    // }
    // }
    // }
    // }

    /**
     * Method generateClassList
     * 
     * @return 
     */
    public Vector generateClassList() {

        Enumeration e;
        Definition d;
        Occurrence o;
        ClassTag tag;
        Vector tagList = null;
        String tagText;

        // List all classes in this package
        if (hasElements()) {
            tagList = new Vector(2);

            JavaHashtable ht = getElements();

            e = ht.elements();

            String baseName;
            String link;

            while (e.hasMoreElements()) {
                d = (Definition) e.nextElement();

                if (d instanceof ClassDef) {
                    o = d.getOccurrence();

                    SymbolTable.addFileClassDef(o.getFile(), (ClassDef) d);

                    baseName = o.getFile().getName();
                    baseName = baseName.substring(0, baseName.lastIndexOf("."));
                    link = baseName + "_java.html";

                    if (!baseName.equals(d.getName())) {
                        link += "#" + o.getLine();
                    }

                    tagText = "<p class=\"classListItem\"><a href=\"" + link
                            + "\"" + "TARGET=\"classFrame\">" + d.getName()
                            + "</a></p>";
                    tag = new ClassTag(((ClassDef) d).getScopedClassName(),
                            tagText);

                    tagList.addElement(tag);
                    ((ClassDef) d).generateClassList(tagList);
                }
            }
        }

        return (tagList);
    }

    /**
     * Generate HTML tags for this package
     * 
     * @param tagList 
     */
    public void generateTags(HTMLTagContainer tagList) {

        Enumeration e;
        Definition d;
        Occurrence o;

        // if we found any definitions in this package, report them
        if (hasElements()) {
            tagElements(tagList);
        }
    }

    /**
     * Method getOccurrenceTag
     * 
     * @param occ 
     * @return 
     */
    public HTMLTag getOccurrenceTag(Occurrence occ) {
        return null;
    }

    // TBD: reimplement to search outputdir for this class if the class isn't already loaded

    /**
     * Method lookup
     * 
     * @param name 
     * @param type 
     * @return 
     */
    Definition lookup(String name, Class type) {

        boolean debug = false;    // name.endsWith("SymbolTable");

        // if (debug)      System.out.println("PackageDef.lookup "+name);
        Definition result = super.lookup(name, type);

        // if (debug) System.out.println("PackageDef.lookup:super.lookup returned "+result);
        if ((result == null) || (result instanceof DummyClass)) {

            // System.out.println(getName()+": PackageDef.lookup(\""+name+"\", \""+type.getName()+"\") returned "+(result == null ? "null" : result.getClass().getName()));
            ClassDef classDef = ClassDef.findLoadedClass(getName(), name);

            if (classDef == null) {
                classDef = loadClassDef(name);
            }

            if (classDef != null) {
                result = new ClassDefProxy(classDef);

                elements.put(name, result);
            }
        }

        // if (result == null) {
        // System.out.println("not found: "+name);
        // }
        return result;
    }

    /**
     * Method loadClassDef
     * 
     * @param name 
     * @return 
     */
    public ClassDef loadClassDef(String name) {

        ClassDef result = null;
        File defFile = (File) persistedDefs.get(name);

        if (defFile != null) {
            try {

                // new Exception("traceback").printStackTrace();
                System.out.println("loading " + name + " from " + defFile);
                System.out.flush();

                FileInputStream fis = new FileInputStream(defFile);
                ObjectInputStream ois = new ObjectInputStream(fis);

                result = (ClassDef) ois.readObject();

                ois.close();
                fis.close();

                // System.out.println("loaded Class "+name); System.out.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Method writeObject
     * 
     * @param out 
     * @throws java.io.IOException 
     */
    private void writeObject(ObjectOutputStream out)
            throws java.io.IOException {

        JavaHashtable saveElements = elements;

        elements = null;

        // super.writeObject(out);
        out.defaultWriteObject();

        elements = saveElements;
    }

    /**
     * Method toString
     * 
     * @return 
     */
    public String toString() {

        String str = "------- Package -------\n";

        str += super.toString() + "\n";

        if (hasElements()) {
            JavaHashtable ht = getElements();
            Enumeration e = ht.elements();

            while (e.hasMoreElements()) {
                Definition d = (Definition) e.nextElement();

                str += d.toString() + "\n";
            }
        }

        return str;
    }

    // // DEBUG METHOD
    // void add(Definition def)
    // {
    // System.out.println("adding "+def+" to package "+getName());
    // super.add(def);
    // }

    /** Field _currentPackPath */
    private static String _currentPackPath;

    /**
     * Hashtable of File objects representing definition files
     * that live in this package's output directory.
     */
    private Hashtable persistedDefs;
}
