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

import java.util.Hashtable;

/**
 * DOT attr_stmt type.
 */
public class VizASType {
    private final String name;
    private final String typeName;
    private static final Hashtable types = new Hashtable();

    public static final VizASType GRAPH =
            new VizASType("graph", "graph");

    public static final VizASType EDGE =
            new VizASType("edge", "edge");

    public static final VizASType EDGE_ANT =
            new VizASType("edge.ant", "edge");

    public static final VizASType EDGE_ANTCALL =
            new VizASType("edge.antcall", "edge");

    public static final VizASType EDGE_DEPENDS =
            new VizASType("edge.depends", "edge");

    public static final VizASType NODE =
            new VizASType("node", "node");

    public static final VizASType NODE_DEFAULT =
            new VizASType("node.default", "node");

    private VizASType(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
        types.put(name, this);
    }

    /**
     * get type instance.
     */
    public static VizASType get(String name) throws BuildException {
        VizASType result = (VizASType) types.get(name);
        if (result == null) {
            throw new BuildException(name + " is not a legal value for attrstmt type");
        }
        return result;
    }

    /**
     * get attr_stmt type (node|graph|edge).
     */
    public String getType() {
        return typeName;
    }

    public String toString() {
        return name;
    }
}


