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

import org.apache.forrest.yer.hierarchy.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.xpath.XPathAPI;
import org.apache.excalibur.xml.dom.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.IOException;


/** Class <code>org.apache.forrest.yer.libre.XPathAttributeReader</code> ...
 *
 * [FIXME this babe needs serious Avalon enabling in the ]
 * @author $Author: jefft $
 * @version CVS $Id: XPathAttributeReader.java,v 1.3.6.1 2003/01/29 17:25:50 jefft Exp $
 */
public class XPathAttributeReader implements LibreAttributeReader
{
  private String xpathQuery;
  private LibreConfigHelper helper;

  /** Default constructor
   * 
   */
  public XPathAttributeReader(String xpathExpression, LibreConfigHelper helper)
  {
    this.helper = helper;
    this.xpathQuery = xpathExpression;
  }

  public String getAttributeValue(Entry entryToInspect)
  {
    return getBareAttributeValue(entryToInspect);
  }

  public String getBareAttributeValue(Entry entryToInspect) {
    String retVal = null;
    InputStream inXML = null;
    try {// Question: should this exception not be eaten by the server object?
      inXML= entryToInspect.getUserXMLStream();
    } catch(IOException e) {
      e.printStackTrace();
    }
    if (inXML == null) return null;
    Document doc = getDOMDocument(inXML);

    try {
      Node hit = XPathAPI.selectSingleNode(doc, this.xpathQuery);
      if (hit != null) {
        retVal = hit.getNodeValue(); //FIXME: read about getNodeValue seems to give strange result on ElementNodes?
      }
    } catch(TransformerException e) {
      e.printStackTrace();
    } catch(DOMException e) {
      e.printStackTrace();
    }
    return retVal;
  }

  public boolean isAttributeValue(Entry entryToInspect)
  {
    final boolean testValue = (getBareAttributeValue(entryToInspect)!=null);
    return this.inverseLogic != testValue;
  }

  public Document getDOMDocument(InputStream inXML)  {

    Document doc = null;
    DOMParser excaliburParser = this.helper.getExcaliburDOMParser();
    if (excaliburParser != null) {
      try {
        doc = excaliburParser.parseDocument(new InputSource(inXML));
      } catch(SAXException e) {
        e.printStackTrace();;
      } catch(IOException e) {
        e.printStackTrace();
      }
    } else {
      try {
        DocumentBuilder db = this.helper.getDocumentBuilder();
        doc = db.parse(inXML);
      } catch(SAXException e) {
        e.printStackTrace();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    return doc;
  }
  private boolean inverseOrder = false;
  public void setOrderDescending(boolean descending)
  {
    this.inverseOrder = descending;
  }

  public boolean isOrderDescending()
  {
    return this.inverseOrder;
  }

  private boolean inverseLogic = false;
  public void setInverseLogic(boolean inverse)
  {
    this.inverseLogic = inverse;
  }
}
