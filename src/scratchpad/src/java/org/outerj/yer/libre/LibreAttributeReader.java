/* File Creation Info [User: mpo | Date: 9-mei-02 | Time: 13:54:58 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

import org.outerj.yer.hierarchy.AttributeReader;

/** Interface <code>org.outerj.yer.libre.LibreAttributeReader</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: LibreAttributeReader.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface LibreAttributeReader extends AttributeReader
{
  /**
   * Set if the isAttribute() returnValue should be inversed or not before return
   * @param inverse insists on inversion when true, lets normal value true when false
   *
   * Implementation advice to inverse your logic: Store the argument of
   * this method as some this.inverseLogic member.  Whenever the isAttribute
   * is actually called you simply store the normal value as some
   * final boolean testValue and then return this.inverseLogic != testValue
   *
   * This approach gives you the required return table:
   * <table>
   * <tr><td>testValue</td><td>inverseLogic</td><td>Return (!=)</td></tr>
   * <tr><td>      0  </td><td>         0  </td><td>        0  </td></tr>
   * <tr><td>      1  </td><td>         0  </td><td>        1  </td></tr>
   * <tr><td>      0  </td><td>         1  </td><td>        1  </td></tr>
   * <tr><td>      1  </td><td>         1  </td><td>        0  </td></tr>
   * </table>
   */
  public void setInverseLogic(boolean inverse);

  public void setOrderDescending(boolean descending);
  public boolean isOrderDescending();
}

