/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/ant/doc/VizProject.java,v 1.1 2004/02/09 11:09:21 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:21 $
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

package org.apache.forrest.forrestdoc.ant.doc;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Ant project.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizProject {
    private String dir = "";
    private String file = "";
    private Hashtable allTargets = new Hashtable();
    private Vector orderedTargets = new Vector();

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void copyAttributes(VizProject project) {
        project.setDir(getDir());
        project.setFile(getFile());
    }

    public void appendTarget(VizTarget target) {
        if (!orderedTargets.contains(target)) {
            orderedTargets.addElement(target);
            if (!allTargets.contains(target))
                allTargets.put(target.getId(), target);
        }
    }

    public VizTarget getTarget(String id) {
        VizTarget target = (VizTarget) allTargets.get(id);
        if (target == null) {
            target = new VizTarget();
            target.setId(id);
            target.setProject(this);
            allTargets.put(target.getId(), target);
        }
        return target;
    }

    public Vector getOrderedTargets() {
        return orderedTargets;
    }

    public String toString() {
        return "VizProject:"
                + " dir:" + dir
                + " file:" + file
                + " allTargets:" + allTargets
                + " orderedTargets:" + orderedTargets;
    }

}


