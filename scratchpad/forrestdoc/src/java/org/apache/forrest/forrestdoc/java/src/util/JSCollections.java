/*
 * $Header: /home/fitz/forrest/xml-forrest/scratchpad/forrestdoc/src/java/org/apache/forrest/forrestdoc/java/src/util/JSCollections.java,v 1.1 2004/02/09 11:09:20 nicolaken Exp $
 * $Revision: 1.1 $
 * $Date: 2004/02/09 11:09:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Alexandria", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.forrest.forrestdoc.java.src.util;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Class JSCollections
 * 
 * @author 
 * @version %I%, %G%
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

        Object element;
        Vector sortedItems;
        JSComparable value;

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

// Comparator.java
// delegate object to hand to QuickSort for callback compare

/**
 * Class QSComparator
 * 
 * @author 
 * @version %I%, %G%
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
        } else {
            return false;
        }
    }
}
