/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 15:48:57 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

import org.outerj.yer.hierarchy.SimpleEntryList;
import org.outerj.yer.hierarchy.EntryList;

/** Interface <code>org.outerj.yer.libre.EntryFilter</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: EntryFilter.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface EntryFilter
{
  public EntryList filter (EntryList entries);
}

