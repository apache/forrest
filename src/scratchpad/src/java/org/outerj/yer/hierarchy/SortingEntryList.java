/* File Creation Info [User: mpo | Date: 14-mei-02 | Time: 23:53:41 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Iterator;

/** Class <code>org.outerj.yer.hierarchy.SortingEntryList</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: SortingEntryList.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public class SortingEntryList implements EntryList
{
  TreeSet sortedSet;
  /** Default constructor
   * 
   */
  public SortingEntryList(Comparator c)
  {
    this.sortedSet = new TreeSet(c);
  }

  /** Adds a entry to the list.
   * @param newEntry the Entry to be added.
   */
  public void addEntry(Entry newEntry)
  {
    this.sortedSet.add(newEntry);
  }

  public void copyEntries(EntryList otherEntries)
  {
     this.sortedSet.addAll(otherEntries.getMembersAsCollection());
  }

  public Collection getMembersAsCollection()
  {
    return this.sortedSet;
  }

  /** Allows for the passed object to perform an operation on all elements
   *  of the list. Mainly this function just iterates through the elements
   *  in the list, and passes each element Entry through the EntryVisitor
   *  passed as the argument to the method.
   * @param theVisitor the <code>EntryVisitor</code> that performs an action
   *  on the List.
   */
  public void visitEntries(EntryVisitor theVisitor)
  {
    Iterator iterator = this.sortedSet.iterator();
    while (iterator.hasNext()) {
      theVisitor.visit((Entry)iterator.next());
    }
  }
}
