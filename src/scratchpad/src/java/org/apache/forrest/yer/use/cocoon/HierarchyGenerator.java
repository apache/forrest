/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 0:34:57 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.use.cocoon;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.generation.ComposerGenerator;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.ResourceNotFoundException;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.xml.sax.SAXException;
import org.outerj.yer.hierarchy.EntryFactory;
import org.outerj.yer.hierarchy.Entry;
import org.outerj.yer.hierarchy.HierarchyReader;

import java.io.IOException;
import java.util.Map;

/** Class <code>org.outerj.yer.use.cocoon.HierarchyGenerator</code> ...
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: HierarchyGenerator.java,v 1.2 2002/07/24 14:46:19 stevenn Exp $
 */
public class HierarchyGenerator
extends ComposerGenerator implements CacheableProcessingComponent, Recyclable {

  /** Default constructor
   * 
   */
  public HierarchyGenerator()
  {
      System.out.println();
  }

  /** The  source */
  private String startLocation;
  private int theDepth = DEPTH_DEFAULT;

  /** Contstant for ... */
  public static final int DEPTH_DEFAULT = -1;
  /** Constant for ... */
  public static final String DEPTH_PARAMETER = "depth";

  /**
   * Recycle this component.
   * All instance variables are set to <code>null</code>.
   */
  public void recycle() {
    super.recycle();
    this.startLocation = null;
    this.theDepth = DEPTH_DEFAULT;
  }

  /**
   * Copy paste en serious cut from cocoons HTML Generator
   */
  public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par)
  throws ProcessingException, SAXException, IOException {
    super.setup(resolver, objectModel, src, par);
    //this is a dirty hack!
      try {
          this.startLocation = resolver.resolveURI(super.source).getSystemId();
          this.theDepth = par.getParameterAsInteger(DEPTH_PARAMETER, DEPTH_DEFAULT);
      } catch (SourceException e) {
          getLogger().error("Can not resolve " + super.source);
          throw SourceUtil.handle("Unable to resolve " + super.source, e);
      }
  }

  /**
   * Generate the unique key.
   * This key must be unique inside the space of this component.
   * This method must be invoked before the generateValidity() method.
   *
   * @return The generated key or <code>null</code> if the component
   *              is currently not cacheable.
   */
  public java.io.Serializable generateKey() {
    //TODO: think about making it cacheable!
    return null;
  }

  /**
   * Generate the validity object.
   * Before this method can be invoked the generateKey() method
   * must be invoked.
   *
   * @return The generated validity object or <code>null</code> if the
   *         component is currently not cacheable.
   */
  public SourceValidity generateValidity() {
    /*
    if (this.inputSource.getLastModified() != 0) {
      return new TimeStampCacheValidity(this.inputSource.getLastModified());
    }
    */
    return null;
  }

  /**
   * Generate XML data.
   */
  public void generate()
  throws ProcessingException {
    try {
      getLogger().debug("hierachy generator start generate()");

      //START hack that needs to be replaced
      //FIXME: get this through Avalon instead!
      String rootFactory = "org.outerj.yer.impl.fs.FileEntryFactory";
      EntryFactory ef = EntryFactory.newInstance(rootFactory);
      ef.compose(this.manager); // the container should of have done this then
      //END hack that needs to be replaced by something like:
      // EntryFactory ef = this.manager.lookup(EntryFactory.ROLE);

      Entry rootEntry = ef.getRootEntry(this.startLocation);
      //get a reader
      HierarchyReader hr = new HierarchyReader();
      hr.setDepth(this.theDepth);
      // get going
      hr.setContentHandler(this.contentHandler);
      hr.startReading(rootEntry);

      getLogger().debug("hierachy generator stop generate()");
    } catch (Exception e){
      getLogger().error("Some strange thing just happened", e);
      throw new ProcessingException("hierarchy.generate()",e);
    }
  }
}
