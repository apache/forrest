/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 1:43:24 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;


import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.ArrayList;

/** Class <code>org.outerj.yer.hierarchy.SimpleEntryList</code> implements a
 *  type-aware List of Entry objects.  It just wraps a <code>java.util.List
 *  </code> and does the necessary typecasting for you.  Also it offers a
 *  visitor mechanism so user of the list don't need to write their own
 *  iteration logic for visiting all the elements in the SimpleEntryList.
 *
 * @author $Author: stevenn $
 * @version CVS $Id: SimpleEntryList.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public class SimpleEntryList implements EntryList
{

  /** Holds the actual Entry objects. */
  private List entries = new ArrayList();

  /** Default constructor
   *
   */
  public SimpleEntryList()
  {
  }

  /** Adds a entry to the list.
   * @param newEntry the Entry to be added.
   */
  public void addEntry(Entry newEntry){
    this.entries.add(newEntry);
  }

  /** Gets the entry at the specified index position in the List.  Index
   *  starts at 0.
   * @param index the index-position of the Entry to get.
   * @return the Entry at the specified position in the list.
   */
  public Entry getEntry(int index){
    return (Entry)this.entries.get(index);
  }

  /** Allows for the passed object to perform an operation on all elements
   *  of the list. Mainly this function just iterates through the elements
   *  in the list, and passes each element Entry through the EntryVisitor
   *  passed as the argument to the method.
   * @param theVisitor the <code>EntryVisitor</code> that performs an action
   *  on the List.
   */
  public void visitEntries(EntryVisitor theVisitor) {
    Iterator iterator = this.entries.iterator();
    while (iterator.hasNext()) {
      theVisitor.visit((Entry)iterator.next());
    }
  }

  public void copyEntries(EntryList otherEntries){
    this.entries.addAll(otherEntries.getMembersAsCollection());
/*
    otherEntries.visitEntries(new EntryVisitor(){
      public void visit(Entry theEntry) {
        SimpleEntryList.this.addEntry(theEntry);
      }
    });
    */
  }

  public java.util.Collection getMembersAsCollection() {
    return this.entries;
  }
}
