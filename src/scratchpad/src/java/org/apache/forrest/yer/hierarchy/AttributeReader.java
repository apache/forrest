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
package org.apache.forrest.yer.hierarchy;


/** Interface <code>org.apache.forrest.yer.libre.AttributeReader</code> specifies
 *  the contract for specific implentations of Classes that can read out
 *  specific (as coded into the implementation) information from <code>
 *  org.apache.forrest.yer.hierarchy.Entry</code> objects.
 *
 *  Typically these AttributeReaders will call the <code>getUserObject()</code>
 *  or the <code>getUserXMLStream()</code> on the passed Entry first and
 *  retrieve the desired information from those.  Because this tie in to
 *  specific Entry implementations, one will typically find specific
 *  attributeReader implementatiosn close to those Entry implementations.
 *
 * @author $Author: jefft $
 * @version CVS $Id: AttributeReader.java,v 1.3 2002/11/05 05:52:40 jefft Exp $
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

