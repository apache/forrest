/*
 * Copyright 1999-2004 The Apache Software Foundation or its licensors,
 * as applicable.
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * A visitor (in the Visitor design pattern sense) for persisting
 * references.  All references to anything in a package are stored
 * in that package's references.txt file.
 */
public class ReferencePersistor implements Visitor, ReferenceTypes {

    /**
     * @param outDirPath the root of the output directory tree
     */
    public ReferencePersistor(String outDirPath) {
        this.outDirPath = outDirPath;
    }

    /**
     * Call this after traversal completes.
     */
    public void done() {

        if (pw != null) {
            closeWriters();
        }

        pw = null;
        bw = null;
        fw = null;
    }

    /**
     * Prepare to process this package.  Close the previous package's file.
     * 
     * @param def 
     */
    public void visit(PackageDef def) {

        if (pw != null) {
            closeWriters();
        }

        String finalDirPath = outDirPath + File.separatorChar
                + def.getName().replace('.', File.separatorChar);

        // System.out.println("visit:finalDirPath="+finalDirPath);
        File dir = new File(finalDirPath);

        dir.mkdirs();

        filePath = finalDirPath + File.separatorChar + "references.txt";

        try {
            boolean append = true;

            fw = new FileWriter(filePath, append);
        } catch (IOException ex) {
            ex.printStackTrace();

            fw = null;    // TBD: fix this hack
        }

        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);

        // ReferentFileClass - name of class whose .java file this referent lives in.
        // ReferentType -- Class Method, or Variable
        // ReferentClass - name of this referent's class.  If that class is an inner class, format is outer.inner
        // ReferentTag - referent's name as used below [TBD: change usage in Pass1]
        // 
        // Referent's URL in source code is {ReferentFileClass}_java.html#{ReferentTag}
        // Referent's URL in referent list is {ReferentFileClass}_java_ref.html#{ReferentTag}
        // Referent class's name in package list is {ReferentClass}
        // 
        pw.println(
                "# ReferentFileClass | ReferentClass | ReferentType | ReferentTag | ReferringPackage | ReferringClass | ReferringMethod | ReferringFile | ReferringLineNumber");
    }

    /**
     * Method getReferentFileClass
     * 
     * @param def 
     * @return 
     */
    private String getReferentFileClass(Definition def) {

        String temp = def.getOccurrence().getFile().getName();    // HACK!

        // System.out.println("name="+def.getClassScopeName()+" temp="+temp);
        return temp.substring(0, temp.length() - ".java".length());
    }

    /**
     * Method visit
     * 
     * @param def 
     */
    public void visit(ClassDef def) {

        if (def instanceof PrimitiveDef) {
            return;
        }

        // System.out.println(def.getName()+" is defined in "+def.getOccurrence().getFile().getName());
        // System.out.println("persist "+def.getName());
        String referentFileClass = getReferentFileClass(def);
        String referentTag = def.getClassScopeName();
        String referentClass;

        if (def.getParentScope() instanceof ClassDef)    // inner class
        {
            String parentScopeName = def.getParentScope().getName();

            referentClass = parentScopeName + "." + def.getName();
        } else {
            referentClass = def.getName();
        }

        persist(referentFileClass, CLASS_REF, referentTag, referentClass, def);
    }

    /**
     * Method visit
     * 
     * @param def 
     */
    public void visit(MethodDef def) {

        String referentClass;
        String referentFileClass = getReferentFileClass(def);
        String referentTag = def.getClassScopeName();
        Definition parentScope = def.getParentScope();
        Definition grandParentScope = parentScope.getParentScope();

        if (grandParentScope instanceof ClassDef)    // inner class
        {
            referentClass = grandParentScope.getName() + "."
                    + parentScope.getName();
        } else {
            referentClass = parentScope.getName();
        }

        persist(referentFileClass, METHOD_REF, referentTag, referentClass, def);
    }

    /**
     * Method visit
     * 
     * @param def 
     */
    public void visit(VariableDef def) {

        String referentClass;
        String referentFileClass = getReferentFileClass(def);
        String referentTag = def.getClassScopeName();
        Definition parentScope = def.getParentScope();
        Definition grandParentScope = parentScope.getParentScope();

        if (parentScope instanceof MethodDef)                 // method variable
        {
            Definition greatGrandParentScope =
                    grandParentScope.getParentScope();

            if (greatGrandParentScope instanceof ClassDef)    // inner class
            {
                referentClass = greatGrandParentScope.getName() + "."
                        + grandParentScope.getName();
            } else {
                referentClass = grandParentScope.getName();
            }
        } else                                                // class variable
        {
            if (grandParentScope instanceof ClassDef)         // inner class
            {
                referentClass = grandParentScope.getName() + "."
                        + parentScope.getName();
            } else {
                referentClass = parentScope.getName();
            }
        }

        persist(referentFileClass, VARIABLE_REF, referentTag, referentClass,
                def);
    }

    /**
     * Method persist
     * 
     * @param referentFileClass 
     * @param referentType      
     * @param referentTag       
     * @param referentClass     
     * @param def               
     */
    private void persist(String referentFileClass, String referentType,
                         String referentTag, String referentClass,
                         Definition def) {

        // System.out.println("persist: referentType="+referentType+" referentTag="+referentTag);
        // boolean debugFlag = referentClass.equals("ReferencePersistor") && referentTag.equals("ReferencePersistor");
        // if (debugFlag) {
        // System.out.println("references "+references);
        // System.out.println("output file "+filePath);
        // }
        JavaVector refs = def.getReferences();

        if (refs != null) {
            Enumeration enum = refs.elements();

            while (enum.hasMoreElements()) {
                Occurrence occ = (Occurrence) enum.nextElement();

                // if (debugFlag) System.out.println("occ="+occ);
                persist(referentFileClass, referentType, referentTag,
                        referentClass, occ);
            }
        }

        persist(referentFileClass, referentType, referentTag, referentClass,
                def.getOccurrence());
    }

    /**
     * Method persist
     * 
     * @param referentFileClass 
     * @param referentType      
     * @param referentTag       
     * @param referentClass     
     * @param occ               
     */
    private void persist(String referentFileClass, String referentType,
                         String referentTag, String referentClass,
                         Occurrence occ) {

        pw.print(referentFileClass);
        pw.print("|");
        pw.print(referentClass);
        pw.print("|");
        pw.print(referentTag);
        pw.print("|");
        pw.print(referentType);
        pw.print("|");
        pw.print(occ.getPackageName());
        pw.print("|");
        pw.print(occ.getClassName());
        pw.print("|");
        pw.print(occ.getMethodName());
        pw.print("|");
        pw.print(occ.getFile().getName());
        pw.print("|");
        pw.println(occ.getLine());
    }

    /**
     * Method closeWriters
     */
    private void closeWriters() {

        try {
            pw.close();
            bw.close();
            fw.close();
        } catch (IOException ex) {
            System.err.println("Hell has frozen over.");
            ex.printStackTrace();
        }
    }

    /** Field filePath */
    private String filePath = null;

    /** Field outDirPath */
    private String outDirPath = null;

    /** Field pw */
    private PrintWriter pw = null;

    /** Field bw */
    private BufferedWriter bw = null;

    /** Field fw */
    private FileWriter fw = null;
}
