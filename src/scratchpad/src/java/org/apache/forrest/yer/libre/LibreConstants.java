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
package org.apache.forrest.yer.libre;

/** Interface <code>org.apache.forrest.yer.libre.LibreConstants</code> holds
 *  constants for different element and attributenames of the libre.xml files.
 * 
 * @author $Author: jefft $
 * @version CVS $Id: LibreConstants.java,v 1.3 2002/11/05 05:52:41 jefft Exp $
 */
public interface LibreConstants
{
  public static final String NS_URI = "http://outerx.org/libre/config/0.1";
  public static final String LIBRE_ELM = "libre";
  public static final String ENTRY_ELM = "entry";

  public static final String LOCATION_ATT = "location";
  public static final String AUTO_ELM = "auto";

  public static final String FILTER_ELM = "filter";
  public static final String SORTER_ELM = "sorter";
  public static final String XPATH_ELM = "xpath";
  public static final String PROPERTY_ELM = "property";

  public static final String LOGIC_ATT = "logic";
  public static final String LOGIC_VAL_INVERSE = "inverse";
  public static final String LOGIC_VAL_NORMAL  = "normal";

  public static final String CLEAR_ATT = "clear";
  public static final String CLEAR_VAL_TRUE = "yes";
  public static final String CLEAR_VAL_FALSE = "no";

  public static final String ORDER_ATT = "order";
  public static final String ORDER_VAL_ASCENDING = "ascending";
  public static final String ORDER_VAL_DESCENDING = "descending";
  public static final String EXPRESSION_ATT = "expression";
  public static final String NAME_ATT = "name";
  public static final String MASK_ATT = "mask";
  public static final String REGEX_ATT = "regex";
  public static final String SUBSTITUTE_ATT = "substitute";
}

