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
package org.apache.forrest.components.sourcetype;

import org.cyberneko.pull.XMLPullParser;
import org.cyberneko.pull.XMLEvent;
import org.cyberneko.pull.event.*;
import org.cyberneko.pull.parsers.Xerces2;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.avalon.framework.configuration.*;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.acting.Action;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.environment.Redirector;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceNotFoundException;

import java.io.InputStream;
import java.util.*;

/**
 * An action that assigns a "sourcetype" to a source. See the external documentation for
 * more information.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class SourceTypeAction extends AbstractLogEnabled implements Configurable, ThreadSafe, Action
{
    protected List sourceTypes = new ArrayList();
    protected static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    public void configure(Configuration configuration) throws ConfigurationException
    {
        Configuration[] sourceTypeConfs = configuration.getChildren("sourcetype");
        for (int i = 0; i < sourceTypeConfs.length; i++)
        {
            SourceType sourceType = new SourceType();
            sourceType.configure(sourceTypeConfs[i]);
            sourceTypes.add(sourceType);
        }
    }

    public Map act(Redirector redirector, SourceResolver sourceResolver, Map objectModel, String src, Parameters parameters)
            throws Exception
    {
        if (src == null || src.equals(""))
            throw new Exception("SourceTypeAction: src attribute should be defined and non-empty.");
        Source source = sourceResolver.resolveURI(src);
        XMLPullParser parser = new Xerces2();
        try {
          InputStream is = source.getInputStream();
          parser.setInputSource(new XMLInputSource(null, src, null, is, null));
        } catch (SourceNotFoundException e) {
          getLogger().warn("Source '"+source+"' not found");
          return null;
        }

        // load nothing external
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        // note: namespace-aware parsing is by default true

        SourceInfo sourceInfo = new SourceInfo();
        // pull-parse the document until we reach the document element and put the collected information
        // into the sourceInfo object
        try
        {
            XMLEvent event;
            while ((event = parser.nextEvent()) != null)
            {
                if (event.type == XMLEvent.DOCTYPE_DECL)
                {
                    DoctypeDeclEvent doctypeDeclEvent = (DoctypeDeclEvent)event;
                    sourceInfo.setPublicId(doctypeDeclEvent.pubid);
                }
                else if (event.type == XMLEvent.PROCESSING_INSTRUCTION)
                {
                    ProcessingInstructionEvent piEvent = (ProcessingInstructionEvent)event;
                    sourceInfo.addProcessingInstruction(piEvent.target, piEvent.data != null ? piEvent.data.toString() : null);
                }
                else if (event.type == XMLEvent.ELEMENT)
                {
                    ElementEvent elementEvent = (ElementEvent)event;
                    sourceInfo.setDocumentElementLocalName(elementEvent.element.localpart);
                    sourceInfo.setDocumentElementNamespace(elementEvent.element.uri);

                    sourceInfo.setXsiSchemaLocation(elementEvent.attributes.getValue(XSI_NAMESPACE, "schemaLocation"));
                    sourceInfo.setXsiNoNamespaceSchemaLocation(elementEvent.attributes.getValue(XSI_NAMESPACE, "noNamespaceSchemaLocation"));

                    // stop parsing after the root element
                    break;
                }
            }
        }
        finally
        {
            // this will also close the inputstream
            parser.cleanup();
        }

        // Run over the SourceTypes until one is found that matches the information collected in sourceInfo
        Iterator sourceTypeIt = sourceTypes.iterator();
        while (sourceTypeIt.hasNext())
        {
            SourceType sourceType = (SourceType)sourceTypeIt.next();
            if (sourceType.matches(sourceInfo))
            {
                HashMap returnMap = new HashMap();
                returnMap.put("sourcetype", sourceType.getName());
                if (getLogger().isDebugEnabled())
                    getLogger().debug("SourceTypeAction: found sourcetype " + sourceType.getName() + " for source " + src);
                return returnMap;
            }
        }
        if (getLogger().isDebugEnabled())
            getLogger().debug("SourceTypeAction: found no sourcetype for source " + src);
        return null;
    }
}

