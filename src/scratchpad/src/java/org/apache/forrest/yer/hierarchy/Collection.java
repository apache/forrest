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

/** Interface <code>org.apache.forrest.yer.hierarchy.Collection</code> defines the
 *  extra methods that should be offered by those Entry implementations that
 *  want to be the hierarchical parrent of a set of child Entries.
 *
 *  The extra methods are factory methods for creating childnodes.
 *
 *  [QUESTION reflecting how 'they' did it with DOM trees, there the
 *  factory methods are only on the Document node, while all nodes allow to
 *  get a hold of that one.  We could achieve the same with some EntryFactory
 *  just can't quite grasp the advantages that would offer (performance?)
 *  Sticked to this approach currently cause it reads nicer, also the created
 *  Children are from <u>this</u> Collection so it makes sense in that respect. ]
 * 
 * @author $Author: jefft $
 * @version CVS $Id: Collection.java,v 1.3 2002/11/05 05:52:40 jefft Exp $
 */
public interface Collection extends Entry
{
  /** Creates and returns a child Entry (i.e. an Entry that is either a simple
   *  Item or a Collection of more child Entries)
   * @param location specifies (in a -can be relative- way <u>this</u> collection
   *   undrstands) which child Entry to create.  Note that it's the underlaying
   *   Collection implementation that has the responsibility to actually choose
   *   if that child is a Collection rather then an Item.  Note also that this
   *   location thing probably relates more to finding the wrapped userObject.
   * @param parentCfg is the HierarchyConfiguration object that was used to
   *   create the parent.  Passing this down allows for the child to inherit
   *   some of it's settings.  It's up to the actual implementation to choose
   *   (and document) which, why and how.
   * @return the specified childEntry in a way that the getParent() on it
   *   returns the Collection you called this method on.  Returns null if the
   *   location String could not be resolved to an actual child Entry.
   */
  public Entry createChildEntry(String location, HierarchyConfig parentCfg);
  /** Creates and returns a EntryList of children of this collection.
   * @param parentCfg is the HierarchyConfiguration object that was used to
   *   create the parent.  Passing this down allows for the child to inherit
   *   some of it's settings.  It's up to the actual implementation to choose
   *   (and document) which, why and how.
   * @return the EntryList of children in a way that the getParent() on the
   *   Entry children will return the Collection you called this method on.
   *   Returns null if the Collection happens to have no children.
   */
  public EntryList createChildList(HierarchyConfig cfg);
}

