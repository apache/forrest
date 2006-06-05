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
 * &lt;attr&gt; nested element handler. &lt;attr&gt; represents an
 * attribute in the DOT language.
 */
public class VizAttr {
    private String name = null;
    private String value = null;

    /**
     * Set the name of this attirubte.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the value of this attirubte.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get the name of this attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this attribute.
     */
    public String getValue() {
        return value;
    }

    public String toString() {
        return name + "=" + value;
    }
}


