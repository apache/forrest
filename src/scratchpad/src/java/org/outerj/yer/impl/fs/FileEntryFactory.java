/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 1:27:43 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.impl.fs;

import org.outerj.yer.hierarchy.EntryFactory;
import org.outerj.yer.hierarchy.Entry;
import org.outerj.yer.libre.LibreConfigHelper;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;


/** Class <code>org.outerj.yer.impl.fs.FileEntryFactory</code> makes a simple
 *  implementation of the AbstractFactory for yer hierarchy factories.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: FileEntryFactory.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public class FileEntryFactory extends EntryFactory
{
  /**
   * @see org.outerj.yer.hierarchy.EntryFactory#getRootEntry
   */
  public Entry getRootEntry(final String pathIdentifier)
  {
    final File localDir= resolveToLocalDir(pathIdentifier);

    //FIXME: should be able to replace
    LibreConfigHelper lch = new LibreConfigHelper();
    try {
      lch.compose(this.manager);
    } catch(ComponentException e) {
      e.printStackTrace();
    }
    //with something down the lines of:
    // LibreConfigHelper lch = this.manager.lookup(LibreConfigHelper.ROLE);

    CollectionImpl root = new CollectionImpl(localDir, null,
        FileEntryConstants.DEFAULT_LIBRE_CONFIG, null, lch);
    return root;
  }

  public File resolveToLocalDir(final String pathIdentifier) {
    String localIdentifier = null;
    if (!pathIdentifier.startsWith("file:")){
      localIdentifier = pathIdentifier;
    } else {
      try {
        URL u = new URL(pathIdentifier);
        localIdentifier = u.getFile();
      } catch(MalformedURLException e) {
        ;
      }
    }
    return new File(localIdentifier);
  }

  ComponentManager manager;
  public void compose(ComponentManager mngr)
  {
    this.manager = mngr;
  }
}
