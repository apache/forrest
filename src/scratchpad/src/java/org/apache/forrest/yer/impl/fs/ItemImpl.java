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
package org.apache.forrest.libre.yer.impl.fs;

import org.apache.forrest.libre.yer.hierarchy.Entry;
import org.apache.forrest.libre.yer.hierarchy.SimpleEntryList;
import org.apache.forrest.libre.yer.hierarchy.EntryList;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;

/** Class <code>org.apache.forrest.libre.yer.impl.fs.ItemImpl</code> represents a simple
 *  FileEntryImpl that wraps a single file from a file system.
 *
 * @author $Author: jefft $
 * @version CVS $Id: ItemImpl.java,v 1.2 2002/11/05 04:46:14 jefft Exp $
 */
public class ItemImpl extends FileEntryImpl
{
  /** Contructor
   * @param theFile is the File UserObject that will get wrapped in this EntryImpl
   * @param parent is the CollectionImpl object that wraps the parent directory
   *   of this object.
   *
   * [ FIXME: currently the parent needs to be in the same implementation space.
   *   waiting for a reason why this should change in fact... ]
   */
  public ItemImpl(File theFile, FileEntryImpl parent)
  {
    super(theFile, parent);
  }

  /**
   * @see org.apache.forrest.libre.yer.hierarchy.Entry#hasChildEntries
   * @return false to indicate that this is not a Collection of child Entries
   */
  public boolean hasChildEntries()
  {
    return false;
  }

  /**
   * @see org.apache.forrest.libre.yer.hierarchy.Entry#getAvaliableAttributeNames
   * @return the list of AtributeNames that are available for read on this
   *  EntryImpl
   */
  public String[] getAvaliableAttributeNames()
  {
    return FileEntryConstants.ITEM_ATTRIBUTE_NAMES;
  }

  /**
   * @see org.apache.forrest.libre.yer.hierarchy.Entry#getChildEntries
   * @return null since this Impl doesn't have any children
   */
  public EntryList getChildEntries()
  {
    return null;
  }

  /**
   * @see org.apache.forrest.libre.yer.hierarchy.Entry#getUserXMLStream
   * @return the FileInputStream on the wrapped File userObject.
   */
  public InputStream getUserXMLStream() throws IOException
  {
    return new FileInputStream(this.wrappedFile);
  }
}
