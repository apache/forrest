/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 0:49:37 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

/** Interface <code>org.outerj.yer.hierarchy.HierarchyConstants</code> holds a
 *  number of constants related to the XML representation of the Hierarchy.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: HierarchyConstants.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface HierarchyConstants
{
  /** The namespace URI used int the XML output of the hierarchy */
  public static final String NS_URI = "http://outerx.org/yer/hierarchy/0.1";
  /** The namespace prefix used in the XML Output of the hierarchy */
  public static final String NS_PREFIX = null;
  /** The element name used for collections. */
  public static final String COLLECTION_ELM = "collection";
  /** The element name used for items. */
  public static final String ITEM_ELM = "item";
}

