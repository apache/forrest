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
import java.util.Vector;

/**
 * Ant target.
 */
public class VizTarget {
    private String id;
    private Vector referencesIn = new Vector();
    private Vector referencesOut = new Vector();
    private VizProject project;
    private boolean defaultTarget = false;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDefault(boolean defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    public boolean isDefault() {
        return defaultTarget;
    }

    public void setProject(VizProject project) {
        this.project = project;
    }

    public VizProject getProject() {
        return project;
    }

    public Vector getReferencesIn() {
        return referencesIn;
    }

    public Vector getReferencesOut() {
        return referencesOut;
    }

    public void addReferenceIn(VizReference ref, boolean unique) {
        if (unique && referencesIn.contains(ref))
            return;
        if (!this.equals(ref.getTo()))
            return;
        referencesIn.addElement(ref);
    }

    public void addReferenceOut(VizReference ref, boolean unique) {
        if (unique && referencesOut.contains(ref))
            return;
        if (!this.equals(ref.getFrom()))
            return;
        referencesOut.addElement(ref);
    }

    public void filterReferences(Vector targets) {
        referencesIn = filterReferences(targets, referencesIn);
        referencesOut = filterReferences(targets, referencesOut);
    }

    private Vector filterReferences(Vector targets,
                                    Vector references) {
        Vector ret = new Vector();
        Enumeration enumList = references.elements();
        while (enumList.hasMoreElements()) {
            VizReference r = (VizReference) enumList.nextElement();
            if (targets.contains(r.getFrom()) &&
                    targets.contains(r.getTo())) {
                ret.addElement(r);
            }
        }
        return ret;
    }

    public String toString() {
        return "VizTarget: id:" + id
                + " referencesIn:" + referencesIn
                + " referencesOut:" + referencesOut
                + " default:" + defaultTarget;
    }
}


