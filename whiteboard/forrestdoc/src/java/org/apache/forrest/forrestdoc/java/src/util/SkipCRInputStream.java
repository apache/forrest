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
package org.apache.forrest.forrestdoc.java.src.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class SkipCRInputStream
 *
 * @version $Id: $
 */
public class SkipCRInputStream extends BufferedInputStream {

    /**
     * Constructor SkipCRInputStream
     *
     * @param s
     */
    public SkipCRInputStream(InputStream s) {
        super(s);
    }

    /**
     * Constructor SkipCRInputStream
     *
     * @param s
     * @param size
     */
    public SkipCRInputStream(InputStream s, int size) {
        super(s, size);
    }

    /**
     * @see java.io.BufferedInputStream#read()
     */
    public int read() throws IOException {

        int c = super.read();

        if (c == 13) {
            return super.read();
        }

        return c;
    }
}
