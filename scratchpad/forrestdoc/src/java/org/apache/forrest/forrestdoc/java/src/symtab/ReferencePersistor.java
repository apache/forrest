/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/symtab/ReferencePersistor.java,v 1.1 2004/02/09 11:09:12 nicolaken Exp $
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
