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

package org.apache.forrest.forrestdoc.ant.doc;

import org.apache.tools.ant.BuildException;

import java.util.Enumeration;
import java.util.Vector;

/**
 * &lt;attrstmt&gt; nested element handler. &lt;attrstmt&gt; represents
 * attr_stmt in <a href="http://www.research.att.com/~erg/graphviz/info/lang.html" target="_blank">the DOT language</a>.
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
        Enumeration enumList = attrs.elements();
        while (enumList.hasMoreElements()) {
            VizAttr a = (VizAttr) enumList.nextElement();
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
        Enumeration enumList = attrstmt.getAttributes();
        while (enumList.hasMoreElements()) {
            addAttribute((VizAttr) enumList.nextElement());
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
        Enumeration enumList = getAttributes();
        while (enumList.hasMoreElements()) {
            buffer.append(enumList.nextElement().toString()).append(" ");
        }
        buffer.append("]");
        return buffer.toString();
    }
}



