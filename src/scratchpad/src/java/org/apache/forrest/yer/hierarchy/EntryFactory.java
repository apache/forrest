/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache Forrest" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.forrest.yer.hierarchy;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;

/** Class <code>org.apache.forrest.yer.hierarchy.EntryFactory</code> functions as
 *  an Abstract Factory for retrieving a concrete Entry Factory.
 * 
 * @author $Author: jefft $
 * @version CVS $Id: EntryFactory.java,v 1.3 2002/11/05 05:52:40 jefft Exp $
 */
public abstract class EntryFactory implements Composable
{
  /** Constant for full qualified class name of the default EntryFactory */
  public static final String DEFAULT_FACTORY =
                                "org.apache.forrest.yer.impl.fs.FileEntryFactory";

  /** Creates a new Instance of the default EntryFactory
   * @return the new instance. */
  public static EntryFactory newInstance(){
    return newInstance(DEFAULT_FACTORY);
  }

  /** Creates a new instance of the specified EntryFactory.
   * @param fcqn full qualified class name of the EntryFactory to create.
   * @return the new instance.
   *  Remarks: the specified class must be in the classpath.  Also it must
   *  be a subclass of <code>org.apache.forrest.yer.hierarchy.EntryFactory</code>.
   *  Finally the class must have a public default constructor (no arguments).
   */
  public static EntryFactory newInstance(String fqcn) {
    try {
      Class c = Class.forName(fqcn);
      Object o = c.newInstance();
      return (EntryFactory)o;
    } catch (ClassNotFoundException cnfe) {
      System.out.println("Could not load class " + fqcn);
    } catch (InstantiationException ie) {
      System.out.println("Could not instantiate class " + fqcn );
    } catch (IllegalAccessException iae) {
      System.out.println("No access to create class " + fqcn );
    } catch (ClassCastException cce) {
      System.out.println("Class " + fqcn + " is not a EntryFactory");
    }
    return null;
  }

  /** Returns the Entry of this hierarchy-implementation that corresponds
   *  to the specified identiefier.
   * @param pathIdentifier implementation specific notation identifying a
   *  root entry of a hierarchy. (often a URI)
   * @return the rootEntr corresponding to the identifier.
   */
  public abstract Entry getRootEntry(String pathIdentifier);

  public abstract void compose(ComponentManager mngr);
}
