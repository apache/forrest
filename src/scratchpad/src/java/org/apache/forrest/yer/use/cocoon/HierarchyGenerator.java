/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache Forrest" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.forrest.yer.use.cocoon;

import org.apache.avalon.excalibur.pool.Recyclable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.generation.ComposerGenerator;

import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceValidity;
import org.xml.sax.SAXException;
import org.apache.forrest.yer.hierarchy.EntryFactory;
import org.apache.forrest.yer.hierarchy.Entry;
import org.apache.forrest.yer.hierarchy.HierarchyReader;

import java.io.IOException;
import java.util.Map;

/** Class <code>org.apache.forrest.yer.use.cocoon.HierarchyGenerator</code> ...
 * 
 * @author $Author: jefft $
 * @version CVS $Id: HierarchyGenerator.java,v 1.6 2003/03/15 06:18:29 jefft Exp $
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
          this.startLocation = resolver.resolveURI(super.source).getURI();
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
      String rootFactory = "org.apache.forrest.yer.impl.fs.FileEntryFactory";
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
