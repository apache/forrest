/* File Creation Info [User: mpo | Date: 14-mei-02 | Time: 17:03:23 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

/** Interface <code>org.outerj.yer.hierarchy.EntryList</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: EntryList.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface EntryList
{
  /** Adds a entry to the list.
   * @param newEntry the Entry to be added.
   */
  void addEntry(Entry newEntry);

  /** Allows for the passed object to perform an operation on all elements
   *  of the list. Mainly this function just iterates through the elements
   *  in the list, and passes each element Entry through the EntryVisitor
   *  passed as the argument to the method.
   * @param theVisitor the <code>EntryVisitor</code> that performs an action
   *  on the List.
   */
  void visitEntries(EntryVisitor theVisitor);

  void copyEntries(EntryList otherEntries);

  java.util.Collection getMembersAsCollection();
}

