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
package org.apache.forrest.yer.impl.fs;

import org.apache.forrest.yer.hierarchy.EntryFactory;
import org.apache.forrest.yer.hierarchy.Entry;
import org.apache.forrest.yer.libre.LibreConfigHelper;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;


/** Class <code>org.apache.forrest.yer.impl.fs.FileEntryFactory</code> makes a simple
 *  implementation of the AbstractFactory for yer hierarchy factories.
 * 
 * @author $Author: jefft $
 * @version CVS $Id: FileEntryFactory.java,v 1.3 2002/11/05 05:52:40 jefft Exp $
 */
public class FileEntryFactory extends EntryFactory
{
  /**
   * @see org.apache.forrest.yer.hierarchy.EntryFactory#getRootEntry
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
