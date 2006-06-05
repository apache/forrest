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

import java.util.Hashtable;
import java.util.Vector;

/**
 * Ant project.
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


