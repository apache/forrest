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
package org.apache.forrest.libre.yer.hierarchy;

import java.io.InputStream;
import java.io.IOException;

/** Interface <code>org.apache.forrest.libre.yer.hierarchy.Entry</code> defines what
 *  information <code>Entry</code>'s should be able to provide.
 * 
 * @author $Author: jefft $
 * @version CVS $Id: Entry.java,v 1.2 2002/11/05 04:46:14 jefft Exp $
 */
public interface Entry
{
  /** Answers if this entry has any sub entries.  One should use this
   *  method to check if this entry has sub-entries below.  In general this
   *  return is used to distinguish Entries of type Item (simple entries)
   *  from type Collection (parent of child Entries) */
  public boolean hasChildEntries();

  /** Offers the list of available attributes on this Entry.
   * @return a String array of available attributes, can be null,
   *   can be hardcoded in the implementation as something that normally
   *   should be available. So while being 'available' according to its
   *   pressence in the returned String[] the actual read Attribute value
   *   could still be null (refering to being non set/unreadable).  It all
   *   depends on the implementation, right?
   */
  public String[] getAvaliableAttributeNames();

  /** Gets a particular attribute of this Entry.
   * @param the name of the attribute to read.
   * @return the attribute value.
   */
  public String getAttribute(String attrName);

  /**  Sets the value of a particular attribute.  This method at first
   *   looks like a bit awkward, since the responsibility for finding out
   *   how to read the specific attribute value should be INSIDE the
   *   implementation of this interface.  However having this method public
   *   allows for implementations to delegate the actual setting/finding out
   *   of the attributeValue to a generic package (like the libre package)
   * @see org.apache.forrest.libre.outerj.hierarchy.*
   * @param attrName specifies the name of the attribute to set a new value for
   * @param attrValue the value to set.
   */
  public void setAttribute(String attrName, String attrValue);

  /** Gets the ordered and filtered list of entries with readable attributes
   *  for this collection entry.  This means the returned list is ready to
   *  be used in the HierarchyReader to generate the corresponding XML.
   * @return the array of child entries, or null if there are none.
   */
  public EntryList getChildEntries();

  /** Gets the parent of this entry.
   * @returns the parent entry of this one, <code>null</code> if this
   *  is the root entry of a hierarchy.
   */
  public Entry getParent();

  /** Sets the parent of this entry.
   * @param parent the parent of this entry.
   */
  public void setParent(Entry parent);

  /** Gets the user-object that is wrapped in this entry.  The big idea behind
   *  the yer Hierarchy is that it only builds an abstract hierarchy.  Typically
   *  the actually hierarchy implementation will be based on a underlaying
   *  set of interconnected userObjects. (it doesn't need to, and can thus just
   *  return null)
   *  This user-object allows for property retrieval through introspection.
   * @return the user object wrapped in this entry.
   */
  public Object getUserObject();

  /** Gets the XML user-InputStream wrapped in this entry.
   *  This inpustream allows for xml parsing and xpath expression evaluation
   * @return the user-inputstream wrapped in this entry.
   * @throws IOException when the inputStream could not be created.
   */
  public InputStream getUserXMLStream() throws IOException;

  /** Gets a unique key idientifying the wrapped user-object in a unique way
   * @return the object representing the unique key for the user-object.
   */
  public Object getLocalUniqueKey();
}
