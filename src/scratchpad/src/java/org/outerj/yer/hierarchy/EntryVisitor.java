/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 16:40:17 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

/** Interface <code>org.outerj.yer.hierarchy.EntryVisitor</code> specifies
 *  a very simple contract for a class that wants to perform an operation on
 *  an Entry.  It's main use is in combination with the <code>EntryList</code>
 *  class to easily perform the same operation on all the members in a list
 *  of Entries.
 *
 *  Common use will often be as anonymous inner class:
 *  <code>
 *  EntryList el;
 *  el.visitEntries(new EntryVisitor(){
 *    public void visit(Entry theEntry) {
 *      // here comes your operation on the Entry...
 *    }
 *  });
 *  </code>
 *
 * @author $Author: stevenn $
 * @version CVS $Id: EntryVisitor.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface EntryVisitor{
  public void visit(Entry theEntry);
}