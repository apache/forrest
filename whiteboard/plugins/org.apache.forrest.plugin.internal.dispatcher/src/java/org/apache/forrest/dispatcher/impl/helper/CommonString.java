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
package org.apache.forrest.dispatcher.impl.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CommonString {

  /**
   * @param args
   */
  public static String common(String[] args) {
    List<String> list = Arrays.asList(args);
    Collections.sort(list, STRING_LENGTH_ORDER);
    int length = list.size(), i = 0;
    String common = "";
    for (Iterator<String> iter = list.iterator(); i < length;) {
      i = i + 1;
      String path = iter.next();
      if (i == 1) {
        common = path;
      } else {
        common = common(common, path);
      }
    }
    return common;
  }

  public static final Comparator<String> STRING_LENGTH_ORDER = new StringLengthComparator();

  private static class StringLengthComparator implements Comparator<String>,
      java.io.Serializable {

    private static final long serialVersionUID = -1967432089082711940L;

    public int compare(String s1, String s2) {
      int n1 = s1.length(), n2 = s2.length();
      return n1 - n2;
    }
  }

  public static String common(String s1, String s2) {
    StringBuffer common = new StringBuffer();
    int n1 = s1.length(), n2 = s2.length();
    for (int i1 = 0, i2 = 0; i1 < n1 && i2 < n2; i1++, i2++) {
      char c1 = s1.charAt(i1);
      char c2 = s2.charAt(i2);
      if (c1 != c2) {
        c1 = Character.toUpperCase(c1);
        c2 = Character.toUpperCase(c2);
        if (c1 != c2) {
          c1 = Character.toLowerCase(c1);
          c2 = Character.toLowerCase(c2);
          if (c1 != c2) {
            return new String(common);
          } else {
            common.append(c1);
          }
        } else {
          common.append(c1);
        }
      } else {
        common.append(c1);
      }
    }
    return new String(common);
  }
}
