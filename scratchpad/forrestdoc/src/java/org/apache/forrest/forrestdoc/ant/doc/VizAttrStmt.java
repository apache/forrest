/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/ant/doc/VizAttrStmt.java,v 1.1 2004/02/09 11:09:21 nicolaken Exp $
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

import org.apache.tools.ant.BuildException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * &lt;attrstmt&gt; nested element handler. &lt;attrstmt&gt; represents
 * attr_stmt in <a href="http://www.research.att.com/~erg/graphviz/info/lang.html" target="_blank">the DOT language</a>.
 * 
 * @author <a href="mailto:kengo@tt.rim.or.jp">KOSEKI Kengo</a>
 */
public class VizAttrStmt {
    /** attribute statement type */
    private VizASType type = null;
    /** attributes table */
    private Vector attrs = new Vector();

    /**
     * set attribute statement type.
     */
    public void setType(String type) throws BuildException {
        this.type = VizASType.get(type);
    }

    /**
     * get attribute statement type.
     */
    public VizASType getType() {
        return type;
    }

    /**
     * add &lt;attr&gt; nested element.
     */
    public void addConfiguredAttr(VizAttr attr) {
        addAttribute(attr);
    }

    /**
     * add attribute.
     */
    public void addAttribute(VizAttr attr) {
        Enumeration enum = attrs.elements();
        while (enum.hasMoreElements()) {
            VizAttr a = (VizAttr) enum.nextElement();
            if (a.getName().equals(attr.getName())) {
                a.setValue(attr.getValue());
                return;
            }
        }
        attrs.addElement(attr);
    }

    /**
     * add attribute.
     * 
     * @param name  Name of the attribute.
     * @param value Value of the attribute.
     */
    public void addAttribute(String name, String value) {
        VizAttr attr = new VizAttr();
        attr.setName(name);
        attr.setValue(value);
        addAttribute(attr);
    }

    /**
     * add attribute(s) from the other attrstmt.
     */
    public void addAttribute(VizAttrStmt attrstmt) {
        if (!type.equals(attrstmt.getType()))
            return;
        Enumeration enum = attrstmt.getAttributes();
        while (enum.hasMoreElements()) {
            addAttribute((VizAttr) enum.nextElement());
        }
    }

    /**
     * get attributes as Enumeration.
     */
    public Enumeration getAttributes() {
        return attrs.elements();
    }

    protected void checkConfiguration() throws BuildException {
        if (this.type == null)
            throw new BuildException("attrstmt: type attribute is required.");
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(type).append(" [ ");
        Enumeration enum = getAttributes();
        while (enum.hasMoreElements()) {
            buffer.append(enum.nextElement().toString()).append(" ");
        }
        buffer.append("]");
        return buffer.toString();
    }
}



