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

import org.apache.avalon.framework.configuration.*;

import java.util.*;

/**
 * Represents a sourcetype. A sourcetype has a name and a number of rules
 * which are used to determine if a certain document is of this sourcetype.
 *
 * @author <a href="mailto:bruno@outerthought.org">Bruno Dumon</a>
 */
public class SourceType implements Configurable
{
    protected List rules = new ArrayList();
    protected String name;

    public void configure(Configuration configuration) throws ConfigurationException
    {
        name = configuration.getAttribute("name");

        Configuration[] ruleConfs = configuration.getChildren();
        for (int i = 0; i < ruleConfs.length; i++)
        {
            SourceTypeRule rule;
            if (ruleConfs[i].getName().equals("document-declaration"))
                rule = new DocDeclRule();
            else if (ruleConfs[i].getName().equals("processing-instruction"))
                rule = new ProcessingInstructionRule();
            else if (ruleConfs[i].getName().equals("w3c-xml-schema"))
                rule = new ProcessingInstructionRule();
            else if (ruleConfs[i].getName().equals("document-element"))
                rule = new DocumentElementRule();
            else
                throw new ConfigurationException("Unsupported element " + ruleConfs[i].getName() + " at "
                        + ruleConfs[i].getLocation());

            rule.configure(ruleConfs[i]);
            rules.add(rule);
        }
    }

    public boolean matches(SourceInfo sourceInfo)
    {
        Iterator rulesIt = rules.iterator();
        boolean matches = true;
        while (rulesIt.hasNext())
        {
            SourceTypeRule rule = (SourceTypeRule)rulesIt.next();
            matches = matches && rule.matches(sourceInfo);
            if (!matches)
                return false;
        }
        return matches;
    }

    public String getName()
    {
        return name;
    }
}
