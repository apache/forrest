/* File Creation Info [User: mpo | Date: 1-mei-02 | Time: 12:49:49 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

/** Interface <code>org.outerj.yer.hierarchy.HierarchyConfig</code> functions
 *  as a tagger interface (no methods defined).  It allows to specify the passing
 *  of an implementation specific Configuration object even on the more abstract
 *  level of Hierarchies and entries.
 *  At some point is also should enable us to intermingle implementation trees.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: HierarchyConfig.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface HierarchyConfig
{
  public void inheritDefinition(HierarchyConfig cfg);
}

