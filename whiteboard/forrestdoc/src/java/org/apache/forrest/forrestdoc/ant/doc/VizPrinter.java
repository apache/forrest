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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A class to print xml graph of dependencies.
 */
public class VizPrinter {
    /** the projects which will be printed */
    private Vector projects = new Vector();
    /** table of attribute statement (VizASType to VizAttrStmt) */
    private Hashtable attrMap;
    /** table of subgraph attribute statement (VizASType to VizAttrStmt) */
    private Hashtable subgraphAttrMap;
    /** output */
    private VizWriter out;
    /** graph ID. The default value is 'G'. */
    private String graphid = "G";
    /** wheter to use cluster or not */
    private boolean noCluster = false;
    /** print only the targets which are depended from this target */
    private String targetFrom = "";
    /** print only the targets which depend on this target */
    private String targetTo = "";
    /** the namespace of node id */
    private IDTable idtable;
    private int indentLevel = 0;
    private final String indent;

    public VizPrinter() {
        attrMap = getDefaultAttrMap();
        subgraphAttrMap = new Hashtable();
        indent = "    ";
    }

    /**
     * return default attributes.
     */
    private Hashtable getDefaultAttrMap() {
        Hashtable result = new Hashtable();
        VizAttrStmt graphAttrs = new VizAttrStmt();
        graphAttrs.setType("graph");
        graphAttrs.addAttribute("rankdir", "LR");
        result.put(graphAttrs.getType(), graphAttrs);
        return result;
    }

    /**
     * add a project which will be printed.
     */
    public void addProject(VizProject project) {
        projects.addElement(project);
    }

    public void setGraphid(String graphid) {
        this.graphid = graphid;
    }

    public void setNocluster(boolean noCluster) {
        this.noCluster = noCluster;
    }

    public void setFrom(String targetName) {
        this.targetFrom = targetName;
    }

    public void setTo(String targetName) {
        this.targetTo = targetName;
    }

    public void setWriter(VizWriter out) {
        this.out = out;
    }

    public void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            out.print(indent);
        }
    }

    /**
     * print indent + string.
     */
    public void print(String str) {
        printIndent();
        out.print(str);
    }

    /**
     * print indent + string + line break.
     */
    public void println(String str) {
        printIndent();
        out.println(str);
    }

    /**
     * add attributes
     * 
     * @param attrstmt attribute statement
     */
    public void addAttributeStatement(VizAttrStmt attrstmt) {
        addAttributeStatement(attrMap, attrstmt);
    }

    /**
     * add subgraph attributes
     * 
     * @param attrstmt attribute statement
     */
    public void addSubgraphAttributeStatement(VizAttrStmt attrstmt) {
        addAttributeStatement(subgraphAttrMap, attrstmt);
    }

    /**
     * add attributes to the attribute table.
     * 
     * @param map      attribute table
     * @param attrstmt attribute statement
     */
    protected void addAttributeStatement(Hashtable table,
                                         VizAttrStmt attrstmt) {
        VizASType type = attrstmt.getType();
        VizAttrStmt oldAttrstmt =
                (VizAttrStmt) table.get(type);
        if (oldAttrstmt == null) {
            table.put(type, attrstmt);
        } else {
            oldAttrstmt.addAttribute(attrstmt);
        }
    }

    /**
     * escape special characters in the ID.
     */
    private String escapeId(String id) {
        id = replace(id, "\\", "\\\\");
        id = replace(id, "\"", "\\\"");
        return id;
    }

    private String getQuotedId(String id) {
        return "\"" + escapeId(id) + "\"";
    }

    public void printAttrStmt(VizAttrStmt as) {
        Enumeration enumList = as.getAttributes();
        while (enumList.hasMoreElements()) {
            VizAttr attr = (VizAttr) enumList.nextElement();

            // The attribute value must be raw. DO NOT ESCAPE.
            // Some escape sequence exists: 
            // justification: \r \l \n
            // conversion: \N
            // \xT \xt \xD \xd \xf \xb \xx
            out.print("<attribute name=" + getQuotedId(attr.getName()) + " value=\"" + attr.getValue() + "\" />");
        }
    }

    public boolean printAttrIfExists(Hashtable map, VizASType type) {
        if (map.get(type) != null) {
            printAttrStmt((VizAttrStmt) map.get(type));
            return true;
        }
        return false;
    }

    private void filterReferences() {
        if (!"".equals(targetFrom)) {
            Vector targets = new Vector();
            VizTarget from =
                    ((VizProject) projects.elementAt(0)).getTarget(targetFrom);
            if (from != null)
                addReferredTargets(from, targets, false);
            eraseNotContainsTargets(targets);
        }

        if (!"".equals(targetTo)) {
            Vector targets = new Vector();
            VizTarget to =
                    ((VizProject) projects.elementAt(0)).getTarget(targetTo);
            if (to != null)
                addReferredTargets(to, targets, true);
            eraseNotContainsTargets(targets);
            filterReferences(targets);
        }
    }

    private void filterReferences(Vector targets) {
        Enumeration enumList = projects.elements();
        while (enumList.hasMoreElements()) {
            Enumeration targetEnum = ((VizProject) enumList.nextElement())
                    .getOrderedTargets().elements();
            while (targetEnum.hasMoreElements()) {
                VizTarget target = (VizTarget) targetEnum.nextElement();
                target.filterReferences(targets);
            }
        }
    }

    private void eraseNotContainsTargets(Vector targets) {
        Vector newProjects = new Vector();
        Enumeration enumList = projects.elements();
        while (enumList.hasMoreElements()) {
            VizProject oldp = (VizProject) enumList.nextElement();
            VizProject newp = new VizProject();
            oldp.copyAttributes(newp);
            Enumeration targetEnum = oldp.getOrderedTargets().elements();
            while (targetEnum.hasMoreElements()) {
                VizTarget target = (VizTarget) targetEnum.nextElement();
                if (targets.contains(target)) {
                    target.setProject(newp);
                    newp.appendTarget(target);
                }
            }
            newProjects.addElement(newp);
        }
        projects = newProjects;
    }

    private void addReferredTargets(VizTarget target, Vector set,
                                    boolean backward) {
        if (set.contains(target)) {
            return;
        }
        set.addElement(target);
        Vector refs;
        if (backward) {
            refs = target.getReferencesIn();
        } else {
            refs = target.getReferencesOut();
        }
        Enumeration enumList = refs.elements();
        while (enumList.hasMoreElements()) {
            VizReference ref = (VizReference) enumList.nextElement();
            VizTarget t = (backward) ? ref.getFrom() : ref.getTo();
            addReferredTargets(t, set, backward);
        }
    }

    protected void printBaseAttributes(Hashtable map) {
        printAttributeStatement(map, VizASType.GRAPH);
        printAttributeStatement(map, VizASType.NODE);
        printAttributeStatement(map, VizASType.EDGE);
    }

    public void printAttributeStatement(Hashtable map, VizASType type) {
        if (map.get(type) != null) {
            print("<attributes type=\"" + type.getType() + "\">");
            printAttrIfExists(map, type);
            out.println("</attributes>");
        }
    }

    public void printDefaultNodeAttributes(boolean isSubgraph) {
        if (isSubgraph &&
                printAttrIfExists(subgraphAttrMap, VizASType.NODE_DEFAULT)) {
            return;
        }
        printAttrIfExists(attrMap, VizASType.NODE_DEFAULT);
    }

    public IDTable createIDTable(Vector projects) {
        return new IDTable(projects);
    }

    public void print() {
        filterReferences();
        idtable = createIDTable(projects);

        out.println("<?xml version=\"1.0\"?>\n<graph name=" + getQuotedId(graphid) + ">\n");
        indentLevel++;

        printBaseAttributes(attrMap);

        int subgraphNum = 0;
        Vector clusterRefs = new Vector();

        Enumeration enumList = projects.elements();
        while (enumList.hasMoreElements()) {
            printProject((VizProject) enumList.nextElement(),
                    clusterRefs, subgraphNum);
            subgraphNum++;
        }
        printClusterRefs(clusterRefs);

        indentLevel--;
        out.println("</graph>");
    }

    private void printProject(VizProject project,
                              Vector clusterRefs,
                              int subgraphNum) {
        if (0 < subgraphNum) {
            println("<subgraph "
                    + (noCluster ? "" : "numcluster=\"")
                    + subgraphNum
                    + "\"");
            indentLevel++;
            println(" label="
                    + getQuotedId(project.getDir() + " "
                    + project.getFile())
                    + " ");
            printBaseAttributes(subgraphAttrMap);
            println(">");
        }
        Enumeration targetEnum = project.getOrderedTargets().elements();
        while (targetEnum.hasMoreElements()) {
            printTarget((VizTarget) targetEnum.nextElement(), project,
                    clusterRefs, (0 < subgraphNum));
        }
        if (0 < subgraphNum) {
            indentLevel--;
            println("</subgraph>");
        }
    }

    private void printTarget(VizTarget target,
                             VizProject project,
                             Vector clusterRefs,
                             boolean isSubgraph) {
        String id = idtable.getId(target);
        print("<node id=" + getQuotedId(id));

        String label = target.getId();
        label = ("".equals(label)) ? "(default)" : label;
        if (isSubgraph && noCluster) {
            label = escapeId(project.getDir() + " "
                    + project.getFile())
                    + "\\n" + escapeId(label);
            out.print(" label=\"" + label + "\"");
        } else if (!id.equals(label)) {
            out.print(" label=" + getQuotedId(label) + " ");
        }
        if (target.isDefault()) {
            printDefaultNodeAttributes(isSubgraph);
        }
        out.println("/>");

        Enumeration refs = target.getReferencesOut().elements();
        while (refs.hasMoreElements()) {
            VizReference ref = (VizReference) refs.nextElement();
            if (ref.getType() == VizReference.ANT) {
                clusterRefs.addElement(ref);
            } else {
                String refid = idtable.getId(ref.getTo());
                print("<edge from=" + getQuotedId(id) + " to=" + getQuotedId(refid) + " ");
                if (ref.getType() == VizReference.DEPENDS) {
                    printAttrIfExists(attrMap, VizASType.EDGE_DEPENDS);
                } else if (ref.getType() == VizReference.ANTCALL) {
                    printAttrIfExists(attrMap, VizASType.EDGE_ANTCALL);
                }
                out.println("/>\n");
            }
        }
    }

    private void printClusterRefs(Vector clusterRefs) {
        Enumeration enumList = clusterRefs.elements();
        while (enumList.hasMoreElements()) {
            VizReference ref = (VizReference) enumList.nextElement();
            String from = idtable.getId(ref.getFrom());
            String to = idtable.getId(ref.getTo());
            print("<edge from=" + getQuotedId(from) + " to=" + getQuotedId(to) + " ");
            printAttrIfExists(attrMap, VizASType.EDGE_ANT);
            print("/>\n");
        }
    }

    /**
     * replace string.
     */
    private String replace(String in, String before, String after) {
        if (in.length() == 0)
            return "";
        if (before.length() == 0)
            return in;

        int start = 0;
        int end = -1;
        int len = before.length();
        StringBuffer result = new StringBuffer(in.length());

        while ((end = in.indexOf(before, start)) != -1) {
            result.append(in.substring(start, end));
            result.append(after);
            start = end + len;
        }
        result.append(in.substring(start));
        return result.toString();
    }

    public class IDTable {
        private Hashtable ids;

        public IDTable(Vector projects) {
            this.ids = getIdTable(projects);
        }

        public String getId(VizTarget target) {
            return (String) ids.get(target);
        }

        public String toString() {
            return ids.toString();
        }

        private Hashtable getIdTable(Vector projects) {
            Hashtable result = new Hashtable();
            Vector idSet = new Vector();

            Enumeration enumList = projects.elements();
            while (enumList.hasMoreElements()) {
                VizProject project = (VizProject) enumList.nextElement();
                Vector targetlist = project.getOrderedTargets();
                Enumeration targetEnum = targetlist.elements();
                while (targetEnum.hasMoreElements()) {
                    VizTarget target = (VizTarget) targetEnum.nextElement();
                    String label = target.getId();
                    label = ("".equals(label)) ? "(default)" : label;
                    String id = label;
                    int idSuffixNum = 0;
                    while (idSet.contains(id)) {
                        id = label + "-" + (++idSuffixNum);
                    }
                    idSet.addElement(id);
                    result.put(target, id);
                }
            }
            return result;
        }
    }
}



