/* File Creation Info [User: mpo | Date: 29-apr-02 | Time: 16:37:05 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */

package org.outerj.yer.hierarchy;

import java.io.InputStream;
import java.io.IOException;

/** Interface <code>org.outerj.yer.hierarchy.Entry</code> defines what
 *  information <code>Entry</code>'s should be able to provide.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: Entry.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
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
   * @see org.outerj.outerj.hierarchy.*
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