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
import org.apache.forrest.yer.libre.LibreConfigHelper;
import org.apache.forrest.yer.hierarchy.Collection;
import org.apache.forrest.yer.hierarchy.HierarchyConfig;
import org.apache.forrest.yer.hierarchy.EntryList;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

/** Class <code>org.apache.forrest.yer.impl.fs.CollectionImpl</code> makes an
 *  implementation of the <code>org.apache.forrest.yer.hierarchy.Collection</code>
 *  interface that helps represent the hierarchy of a filesystem directory
 *  structure. As such the wrapped UserObject of this Entry is a java.io.File
 *  of which isDirectory() is true.
 *
 *  It uses the <code>libre</code> package to add configurable support for
 *  filtering, sorting and manual organization which is typically not available
 *  in the file system by itself.
 *
 * @author $Author: mpo $
 * @version CVS $Id: CollectionImpl.java,v 1.4 2003/01/09 00:06:45 mpo Exp $
 */
public class CollectionImpl extends FileEntryImpl implements Collection
{

  /** Points to the Libre Configuration object used for this Collection. */
  private LibreConfig libre = null;
  private LibreConfigHelper helper = null;

  /**
   * @see org.apache.forrest.yer.hierarchy.Entry
   * @return true to indicate that this Entry is in fact a collection that
   *  'can' hold child Entries.  Note that it can be the case that there are
   *  no actual children in this Collection. */
  public boolean hasChildEntries()
  {
    return true;
  }

  /**
   * @see org.apache.forrest.yer.hierarchy.Collection
   *  Creates and returns either an ItemImpl or CollectionImpl object that
   *  wraps the Child File object that reflects the Child File specified by
   *  the location of the wrapped directory of this CollectionImpl.
   * @param location is the local name of the child file of the wrapped directory
   * @param cfg is the configuration object to fallback to for configuration
   *   information it can't otherwise find out about.
   * @return the ItemImpl or CollectionImpl object that wraps that specified
   *   child file.
   */
  public Entry createChildEntry(String location, HierarchyConfig cfg)
  {
    return createChildEntry(new File(this.wrappedFile, location), cfg);
  }


  /**
   * @see org.apache.forrest.yer.hierarchy.Collection
   *  Creates and returns either an ItemImpl or CollectionImpl object that
   *  wraps the Child File object that reflects the Child File specified
   *  which should be a child of the wrapped directory of this CollectionImpl.
   * @param wrappedFile is the child file of the wrapped directory
   * @param cfg is the configuration object to fallback to for configuration
   *   information it can't otherwise find out about.
   * @return the ItemImpl or CollectionImpl object that wraps that specified
   *   child file.
   */
  private Entry createChildEntry(File wrappedFile, HierarchyConfig cfg)
  {
    if(wrappedFile.exists()) {
      if (wrappedFile.isDirectory()){
        return new CollectionImpl(wrappedFile, this, cfg);
      } else {
        return  new ItemImpl(wrappedFile, this);
      }
    }
    return null;
  }

  /**
   * @see org.apache.forrest.yer.hierarchy.Collection
   *  Creates and returns the List of ItemImpl and CollectionImpl objects
   *  that wrap the childFiles of the wrapped Directory of this CollectionImpl.
   * @param cfg is the configuration object to fallback to for configuration
   *   information it can't otherwise find out about.
   * @return the EntryList of ItemImpl or CollectionImpl objects that wrap
   *  the different child Files which are in their turnn children of the
   *  directory wrapped by this CollectionImpl
   */
  public EntryList createChildList(HierarchyConfig cfg)
  {
    final File[] subFiles = this.wrappedFile.listFiles(new FilenameFilter(){
      public boolean accept(File dir, String name)
      {
        return (!name.equals(CollectionImpl.this.getLibreConfigFileName()));
      }
    });
    final EntryList entries = new SimpleEntryList();
    final int len = subFiles.length;
    for(int i=0; i<len; i++) {
      Entry newEntry = createChildEntry(subFiles[i], cfg);
      // no extra check for null, since the File really should exist
      entries.addEntry(newEntry);
    }
    return entries;
  }

  /**
   * @see org.apache.forrest.yer.hierarchy.Entry
   */
  public EntryList getChildEntries()
  {
    return libre.makeChildList(this);
  }

  /**
   * @see org.apache.forrest.yer.hierarchy.Entry
   */
  public InputStream getUserXMLStream() throws IOException
  {
    //FIXME: should be considered to open an inputStream on the embeded libre.xml file !?
    // but what about directories with inherited libre.xml?
    return null;
  }

  /**
   * @see org.apache.forrest.yer.hierarchy.Entry
   */
  public String[] getAvaliableAttributeNames()
  {
    return FileEntryConstants.COLLECTION_ATTRIBUTE_NAMES;
  }

  /** Constructor that will inherit the alternative libre.xml filename
   *  from it's parent.
   * @param dirFile the File object that will be wrapped in this CollectionImpl
   * @param parent (should not be null, if it is, take an overloaded version
   *   of this constructor that doesn't require this)
   * @param cfg the configuration info to fall back onto when creating this
   *   CollectionImpl object.
   */
  public CollectionImpl(File dirFile, CollectionImpl parent,
                        HierarchyConfig fallBackConfig)
  {
    this(dirFile, parent, parent.getLibreConfigFileName(), fallBackConfig, parent.helper);
  }

  /** Constructor that just takes the alternative libre.xml filename
   *  as specified in the 3rd argument.
   * @param dirFile the File object that will be wrapped in this CollectionImpl
   * @param parent (should not be null, if it is, take an overloaded version
   *   of this constructor that doesn't require this)
   * @param libreConfig the alternative name of the libre.xml file to use
   *   for configuration purposes.
   * @param cfg the configuration info to fall back onto when creating this
   *   CollectionImpl object.
   */
  public CollectionImpl(File dirFile, CollectionImpl parent,
                        String libreConfigLocation, HierarchyConfig parentDef,
                        LibreConfigHelper lch)
  {
    super(dirFile, parent);
    this.helper = lch;
    LibreConfig lbc = null;
    try {
      lbc = lch.createConfig(new FileInputStream(
          new File(dirFile,libreConfigLocation)), parentDef);
    } catch(FileNotFoundException e) {
      lbc = lch.createInheritConfig(parentDef);
    }
    this.libre = lbc;
  }
}
