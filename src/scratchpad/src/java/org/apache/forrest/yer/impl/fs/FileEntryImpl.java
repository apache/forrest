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
package org.apache.forrest.yer.impl.fs;

import org.apache.forrest.yer.hierarchy.Entry;
import org.apache.forrest.yer.hierarchy.SimpleEntryList;
import org.apache.forrest.yer.libre.LibreConfig;
import org.apache.forrest.yer.hierarchy.AttributeReader;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/** Class <code>org.apache.forrest.yer.impl.fs.FileEntryImpl</code> is a base class
 *  for the ItemImpl and CollectionImpl classes that implement the
 *  org.apache.forrest.yer.hierarchy.* contracts for describing an underlaying
 *  file-system structure.
 *  This implementation chooses to use the org.apache.forrest.yer.libre configuration
 *  mechanism to add some artificial yet semi-automatic sequence information
 *  to the file system.
 *
 * @author $Author: jefft $
 * @version CVS $Id: FileEntryImpl.java,v 1.3 2002/11/05 05:52:40 jefft Exp $
 */
public abstract class FileEntryImpl implements Entry
{
  /** Holds the underlaying user object that is wrapped by this Entry Implementation */
  protected File wrappedFile = null;
  /** Holds read and set attributes from the underlaying user object */
  private Properties attributes = new Properties();
  /** Holds the hierarchical parent object of this Entry */
  private FileEntryImpl parent = null;
  /** Holds the used libre filename for this Entry so it can be inherited
   *  down in the hierarcy.
   * [FIXME: maybe we should consider putting this in the fallback LibConfig
   *  stuff itself??]
   */
  private String libreConfigFileName = FileEntryConstants.DEFAULT_LIBRE_CONFIG;

  /** Gets a previously set Attribute value.
   * @param attrName name of the attribute to get
   * @return the attribute value. (can be null)
   */
  public String getAttribute(String attrName)
  {
    return (String)this.attributes.get(attrName);
  }

  /** Sets (overwrites) the value of the attribute with the specified name.
   * @param attrName name of the attribute to set
   * @param attrValue value of the attribute to set
   */
  public void setAttribute(String attrName, String attrValue)
  {
    this.attributes.put(attrName, attrValue);
  }

  /** Constructor
   * @param userObject the File object to wrap into this EntryImpl
   * @param the parent of this new Entry object that will be created.
   */
  protected FileEntryImpl(File userObject, FileEntryImpl parent) {
    setWrappedFile(userObject);
    setParent(parent);
  }
  /** Returns an object that enables to uniquely identify this EntryImpl
   *  locally among it's siblings.  Of course this is based on the underlaying
   *  userObject of this EntryImpl.
   *
   * [FIXME: mmm looks like lasyness now, but this aspect of things should
   *  come from the libre package not the hierarchy package (now looks like
   *  a new interface to implement, yet still do the same]
   */
  public Object getLocalUniqueKey()
  {
    return this.wrappedFile.getName();
  }
  /** For this implementation the userObject is the wrapped File Object.
   * @see org.apache.forrest.yer.hierarchy.Entry
   */
  public Object getUserObject()
  {
    return this.wrappedFile;
  }
  /** Sets the wrappedFile object for this EntryImpl */
  private void setWrappedFile(File f){
    this.wrappedFile = f;
  }
  /**
   * @see org.apache.forrest.yer.hierarchy.Entry#getParent()
   */
  public Entry getParent()
  {
    return this.parent;
  }
  /**
   * @see org.apache.forrest.yer.hierarchy.Entry
   */
  public void setParent(Entry parent)
  {
    this.parent = (FileEntryImpl)parent;
  }
  /**
   * Gets the libre.xml alternative that was used to build this EntryImpl object
   * [FIXME: check if this method and corresponding member variable can't simply
   *  move down to the level of the CollectionImpl ]
   */
  public String getLibreConfigFileName(){
    return this.libreConfigFileName;
  }
  /**
   * Sets the libre.xml alternative to use for this EntryImpl object
   */
  public void setLibreConfigFileName(String fileName){
    this.libreConfigFileName = fileName;
  }
}
