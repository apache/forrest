/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 1:29:58 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.impl.fs;

import org.outerj.yer.hierarchy.Entry;
import org.outerj.yer.hierarchy.SimpleEntryList;
import org.outerj.yer.libre.LibreConfig;
import org.outerj.yer.hierarchy.AttributeReader;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/** Class <code>org.outerj.yer.impl.fs.FileEntryImpl</code> is a base class
 *  for the ItemImpl and CollectionImpl classes that implement the
 *  org.outerj.yer.hierarchy.* contracts for describing an underlaying
 *  file-system structure.
 *  This implementation chooses to use the org.outerj.yer.libre configuration
 *  mechanism to add some artificial yet semi-automatic sequence information
 *  to the file system.
 *
 * @author $Author: stevenn $
 * @version CVS $Id: FileEntryImpl.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
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
   * @see org.outerj.yer.hierarchy.Entry
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
   * @see org.outerj.yer.hierarchy.Entry#getParent()
   */
  public Entry getParent()
  {
    return this.parent;
  }
  /**
   * @see org.outerj.yer.hierarchy.Entry
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
