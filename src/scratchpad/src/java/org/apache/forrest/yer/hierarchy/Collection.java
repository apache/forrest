/* File Creation Info [User: mpo | Date: 1-mei-02 | Time: 10:48:32 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

/** Interface <code>org.outerj.yer.hierarchy.Collection</code> defines the
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
 * @author $Author: stevenn $
 * @version CVS $Id: Collection.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
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

