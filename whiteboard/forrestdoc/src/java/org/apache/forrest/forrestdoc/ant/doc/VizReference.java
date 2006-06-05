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

/**
 * Target dependency.
 */
public class VizReference {
    public static final int DEPENDS = 0;
    public static final int ANTCALL = 1;
    public static final int ANT = 2;

    private int type = DEPENDS;
    private VizTarget from = null;
    private VizTarget to = null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFrom(VizTarget from) {
        this.from = from;
    }

    public VizTarget getFrom() {
        return from;
    }

    public void setTo(VizTarget to) {
        this.to = to;
    }

    public VizTarget getTo() {
        return to;
    }

    public boolean equals(Object o) {
        if (!(o instanceof VizReference))
            return false;
        VizReference ref = (VizReference) o;
        if (getType() != ref.getType())
            return false;
        if (getFrom() == null && ref.getFrom() != null)
            return false;
        if (getTo() == null && ref.getTo() != null)
            return false;
        return (getFrom().equals(ref.getFrom()) &&
                getTo().equals(ref.getTo()));
    }

    public int hashCode() {
        int ret = 17;
        ret = 37 * ret + getType();
        ret = 37 * ret + ((getFrom() == null) ? 0 : getFrom().hashCode());
        ret = 37 * ret + ((getTo() == null) ? 0 : getTo().hashCode());
        return ret;
    }
}


