/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 15:48:01 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

import org.outerj.yer.hierarchy.SimpleEntryList;
import org.outerj.yer.hierarchy.EntryList;

/** Interface <code>org.outerj.yer.libre.EntrySorter</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: EntrySorter.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface EntrySorter
{
  public EntryList sort(EntryList entries);
}

