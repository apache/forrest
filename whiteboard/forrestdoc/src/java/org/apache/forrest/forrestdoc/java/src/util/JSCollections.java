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

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Class JSCollections
 *
 * @version $Id: $
 */
public class JSCollections {

    /**
     * Method sortEnumeration
     *
     * @param unsortedItems
     * @return
     */
    public static Vector sortEnumeration(Enumeration unsortedItems) {

        Object element;
        Vector sortedItems;
        JSComparable value;

        if (unsortedItems == null) {
            return (null);
        }

        sortedItems = new Vector();

        while (unsortedItems.hasMoreElements()) {
            element = unsortedItems.nextElement();

            if (element instanceof JSComparable == false) {
                return (null);
            }

            value = (JSComparable) element;

            sortedItems.addElement(value);
        }

        Object[] a = new Object[sortedItems.size()];

        for (int i = 0; i < a.length; i++) {
            a[i] = sortedItems.elementAt(i);
        }

        QSComparator compare = new QSComparator();

        QuickSort.sort(a, compare);
        sortedItems.removeAllElements();

        for (int i = 0; i < a.length; i++) {
            sortedItems.addElement(a[i]);
        }

        return (sortedItems);
    }

    /**
     * Method sortVector
     *
     * @param unsortedItems
     * @return
     */
    public static Object[] sortVector(Vector unsortedItems) {

        if (unsortedItems == null) {
            return (null);
        }

        Object[] a = new Object[unsortedItems.size()];

        for (int i = 0; i < a.length; i++) {
            a[i] = unsortedItems.elementAt(i);
        }

        QSComparator compare = new QSComparator();

        QuickSort.sort(a, compare);

        return (a);
    }
}

/**
 * Class QSComparator
 * delegate object to hand to QuickSort for callback compare
 *
 * @version $Id: $
 */
class QSComparator implements Comparator {

    /**
     * Method compare
     *
     * @param a
     * @param b
     * @return
     */
    public int compare(Object a, Object b) {
        return (((JSComparable) a).compareTo((JSComparable) b));
    }

    /**
     * Method equals
     *
     * @param a
     * @param b
     * @return
     */
    public boolean equals(Object a, Object b) {

        if (((JSComparable) a).compareTo((JSComparable) b) == 0) {
            return true;
        }

        return false;
    }
}
