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

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * A Rule which checks the value of the xsi:schemaLocation and xsi:noNamespaceSchemaLocation
 * attributes.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class XmlSchemaRule implements SourceTypeRule
{
    protected String schemaLocation;
    protected String noNamespaceSchemaLocation;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        schemaLocation = configuration.getAttribute("schema-location", null);
        noNamespaceSchemaLocation = configuration.getAttribute("no-namespace-schema-location", null);
        if (schemaLocation == null && noNamespaceSchemaLocation == null)
            throw new ConfigurationException("Missing schema-location and/or no-namespace-schema-location attribute on w3c-xml-schema element at " + configuration.getLocation());
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        if (schemaLocation != null && noNamespaceSchemaLocation != null
                && schemaLocation.equals(sourceInfo.getXsiSchemaLocation())
                && noNamespaceSchemaLocation.equals(sourceInfo.getXsiNoNamespaceSchemaLocation()))
            return true;
        else if (schemaLocation != null && noNamespaceSchemaLocation == null && schemaLocation.equals(sourceInfo.getXsiSchemaLocation()))
            return true;
        else if (schemaLocation == null && noNamespaceSchemaLocation != null && noNamespaceSchemaLocation.equals(sourceInfo.getXsiNoNamespaceSchemaLocation()))
            return true;
        else
            return false;
    }

}
