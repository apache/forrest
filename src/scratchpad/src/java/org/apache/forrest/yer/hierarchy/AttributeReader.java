/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 14:46:13 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;


/** Interface <code>org.outerj.yer.libre.AttributeReader</code> specifies
 *  the contract for specific implentations of Classes that can read out
 *  specific (as coded into the implementation) information from <code>
 *  org.outerj.yer.hierarchy.Entry</code> objects.
 *
 *  Typically these AttributeReaders will call the <code>getUserObject()</code>
 *  or the <code>getUserXMLStream()</code> on the passed Entry first and
 *  retrieve the desired information from those.  Because this tie in to
 *  specific Entry implementations, one will typically find specific
 *  attributeReader implementatiosn close to those Entry implementations.
 *
 * @author $Author: stevenn $
 * @version CVS $Id: AttributeReader.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public interface AttributeReader
{
  /** Reads the specific attribute (from the passed Entry object) this
   *  Reader is designed for.
   * @param entryToInspect is the entry object that will be inspected.
   *   Typically this inspection involves calling getUserObject on this
   *   parameter(and then either downcast or introspect that returned beast)
   *   or calling getUserXMLStream (and doing some XML operations on that)
   * @return a string value holding the attribute value or null if the
   *   inspection couldn't find anything.
   */
  public String getAttributeValue(Entry entryToInspect);
  /** Reads the specific attribute (from the passed Entry object) this
   *  Reader is designed for.
   * @param entryToInspect is the entry object that will be inspected.
   *   Typically this inspection involves calling getUserObject on this
   *   parameter(and then either downcast or introspect that returned beast)
   *   or calling getUserXMLStream (and doing some XML operations on that)
   * @return a boolean value holding the yes/no state of the attribute this
   *   Reader is designed for. Typically it will return false if it wasn't able
   *   to locate the attribute (check the actual impl documentation for this)
   */
  public boolean isAttributeValue(Entry entryToInspect);
}

