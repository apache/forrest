/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.cocoon.components.source.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.excalibur.source.SourceFactory;
import org.apache.excalibur.source.SourceResolver;


/** 
  * ATTENTION: The ZIP protocol is also part of Cocoon 2.1.4 (scratchpad). When
  *            Forrest uses this version or higher this file can be removed!!! (RP)
  *
  * Implementation of a {@link Source} that gets its content from
  * and ZIP archive.
  * 
  * A ZIP source can be reached using the zip:// pseudo-protocol. The syntax is
  * zip://myFile.xml@myZip.zip (zip://[file]@[archive])
  * 
  * @author <a href="http://apache.org/~reinhard">Reinhard Poetz</a>
  * @version CVS $Id: ZipSourceFactory.java,v 1.1 2004/01/13 14:52:09 reinhard Exp $
  * @since 0.6
  */ 
public class ZipSourceFactory extends AbstractLogEnabled
    implements SourceFactory, ThreadSafe, Serviceable {

    protected ServiceManager manager;
    public static String ZIP_SOURCE_SCHEME = "zip:";

    public Source getSource(String location, Map parameters)
        throws IOException, MalformedURLException {

        if ( this.getLogger().isDebugEnabled() ) {
            this.getLogger().debug("Processing " + location);
        }
        
        // syntax checks
        int separatorPos = location.indexOf('@');
        if (separatorPos == -1) {
            throw new MalformedURLException("@ required in URI: " + location);
        }
        int protocolEnd = location.indexOf("://");
        if (protocolEnd == -1) {
            throw new MalformedURLException("URI does not contain '://' : " + location);
        }

        // get the source of the archive and return the ZipSource passing
        // a source retrieved from the SourceResolver
        String documentName = location.substring(protocolEnd + 3, separatorPos);
        Source archive;
        SourceResolver resolver = null;
        try {
            resolver = (SourceResolver)this.manager.lookup( SourceResolver.ROLE );
            archive = resolver.resolveURI(location.substring(separatorPos + 1));            
        } catch (ServiceException se) {
            throw new SourceException("SourceResolver is not available.", se);
        } finally {
            this.manager.release(resolver);
        }
        return new ZipSource(archive, documentName);
    }


    public void release(Source source) {
        // not necessary here
    }

    public void service(ServiceManager manager) throws ServiceException {
        this.manager = manager;
    }

}
