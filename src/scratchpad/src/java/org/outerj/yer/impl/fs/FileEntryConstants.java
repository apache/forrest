/* File Creation Info [User: mpo | Date: 1-mei-02 | Time: 11:02:17 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.impl.fs;

/** Interface <code>org.outerj.yer.impl.fs.FileEntryConstants</code> holds
 *  a number of Constants used inside the FileSystem oriented implmentation
 *  of the org.outerj.yer.hierarchy package.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: FileEntryConstants.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface FileEntryConstants
{
  /** Constant for the default filename of the libre file to use */
  public static final String DEFAULT_LIBRE_CONFIG = "libre.xml";

  /** Constant array of AttributeNames that should be set on the ItemImpl objects.  */
  public static final String[] ITEM_ATTRIBUTE_NAMES = {"label", "href", "title"};
  /** Constant array of AttributeNames that should be set on the CollectionImpl objects.  */
  public static final String[] COLLECTION_ATTRIBUTE_NAMES = {"label"};
}
