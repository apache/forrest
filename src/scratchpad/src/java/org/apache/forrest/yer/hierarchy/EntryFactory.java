/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 1:07:54 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;

/** Class <code>org.outerj.yer.hierarchy.EntryFactory</code> functions as
 *  an Abstract Factory for retrieving a concrete Entry Factory.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: EntryFactory.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public abstract class EntryFactory implements Composable
{
  /** Constant for full qualified class name of the default EntryFactory */
  public static final String DEFAULT_FACTORY =
                                "org.outerj.yer.impl.fs.FileEntryFactory";

  /** Creates a new Instance of the default EntryFactory
   * @return the new instance. */
  public static EntryFactory newInstance(){
    return newInstance(DEFAULT_FACTORY);
  }

  /** Creates a new instance of the specified EntryFactory.
   * @param fcqn full qualified class name of the EntryFactory to create.
   * @return the new instance.
   *  Remarks: the specified class must be in the classpath.  Also it must
   *  be a subclass of <code>org.outerj.yer.hierarchy.EntryFactory</code>.
   *  Finally the class must have a public default constructor (no arguments).
   */
  public static EntryFactory newInstance(String fqcn) {
    try {
      Class c = Class.forName(fqcn);
      Object o = c.newInstance();
      return (EntryFactory)o;
    } catch (ClassNotFoundException cnfe) {
      System.out.println("Could not load class " + fqcn);
    } catch (InstantiationException ie) {
      System.out.println("Could not instantiate class " + fqcn );
    } catch (IllegalAccessException iae) {
      System.out.println("No access to create class " + fqcn );
    } catch (ClassCastException cce) {
      System.out.println("Class " + fqcn + " is not a EntryFactory");
    }
    return null;
  }

  /** Returns the Entry of this hierarchy-implementation that corresponds
   *  to the specified identiefier.
   * @param pathIdentifier implementation specific notation identifying a
   *  root entry of a hierarchy. (often a URI)
   * @return the rootEntr corresponding to the identifier.
   */
  public abstract Entry getRootEntry(String pathIdentifier);

  public abstract void compose(ComponentManager mngr);
}
