/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 10:14:45 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.impl.fs;

import org.outerj.yer.hierarchy.Entry;
import org.outerj.yer.hierarchy.SimpleEntryList;
import org.outerj.yer.hierarchy.EntryList;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;

/** Class <code>org.outerj.yer.impl.fs.ItemImpl</code> represents a simple
 *  FileEntryImpl that wraps a single file from a file system.
 *
 * @author $Author: stevenn $
 * @version CVS $Id: ItemImpl.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
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
   * @see org.outerj.yer.hierarchy.Entry#hasChildEntries
   * @return false to indicate that this is not a Collection of child Entries
   */
  public boolean hasChildEntries()
  {
    return false;
  }

  /**
   * @see org.outerj.yer.hierarchy.Entry#getAvaliableAttributeNames
   * @return the list of AtributeNames that are available for read on this
   *  EntryImpl
   */
  public String[] getAvaliableAttributeNames()
  {
    return FileEntryConstants.ITEM_ATTRIBUTE_NAMES;
  }

  /**
   * @see org.outerj.yer.hierarchy.Entry#getChildEntries
   * @return null since this Impl doesn't have any children
   */
  public EntryList getChildEntries()
  {
    return null;
  }

  /**
   * @see org.outerj.yer.hierarchy.Entry#getUserXMLStream
   * @return the FileInputStream on the wrapped File userObject.
   */
  public InputStream getUserXMLStream() throws IOException
  {
    return new FileInputStream(this.wrappedFile);
  }
}
