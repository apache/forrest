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

import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.excalibur.xml.sax.SAXParser;
import org.apache.excalibur.xml.dom.DOMParser;
import org.apache.excalibur.xml.EntityResolver;
import org.apache.xml.resolver.tools.CatalogResolver;
//import org.apache.cocoon.components.resolver.Resolver;
import org.apache.forrest.yer.hierarchy.HierarchyConfig;
import org.apache.forrest.yer.hierarchy.Entry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
//import org.xml.sax.EntityResolver;

import javax.xml.parsers.SAXParserFactory;
// import javax.xml.parsers.SAXParser;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.IOException;


//FIXME: this needs to become an interface defining implementations
// that will govern defaults, read mechanisms et all
// hierarchy implementations that choose to use the libre package can
// provide a specific impl for this interface then!

/** Class <code>org.apache.forrest.yer.libre.LibreConfigHelper</code> ...
 * 
 * @author $Author: jefft $
 * @version CVS $Id: LibreConfigHelper.java,v 1.4.6.1 2003/01/29 17:25:50 jefft Exp $
 */
public class LibreConfigHelper implements Composable
{

  /** Default constructor
   * 
   */
  public LibreConfigHelper()
  {
  }

  ComponentManager manager;
  public void compose(ComponentManager manager) throws ComponentException
  {
    this.manager = manager;
  }

  /** Loads, interprets and builds a LibreConfig file from it's XML representation
   * @param fromStream is the inputStream from where the XML input should be read.
   */
  public LibreConfig createConfig(InputStream fromStream, HierarchyConfig parentCfg){
    LibreConfigBuilder lcb = new LibreConfigBuilder(parentCfg, this);
    if (this.manager != null) {
      try {
        SAXParser parser = getExcaliburSAXParser();
        parser.parse(new InputSource(fromStream), lcb);
      } catch(SAXException e) {
        e.printStackTrace();
      } catch(IOException e) {
        e.printStackTrace();
      }
    } else {
      try {
        XMLReader reader = getXMLReader();
        reader.setContentHandler(lcb);
        reader.parse(new InputSource(fromStream));
      } catch(IOException e) {
        e.printStackTrace();
      } catch(SAXException e) {
        e.printStackTrace();
      }
    }
    return lcb.getLibreConfig();
  }

  public SAXParser getExcaliburSAXParser(){
    if (this.manager == null) return null;
    try {
      return (SAXParser)this.manager.lookup(SAXParser.ROLE);
    } catch(ComponentException e) {
      e.printStackTrace();
    }
    return null;
  }

  public DOMParser getExcaliburDOMParser(){
    if (this.manager == null) return null;
    try {
      return (DOMParser)this.manager.lookup(DOMParser.ROLE);
    } catch(ComponentException e) {
      e.printStackTrace();
    }
    return null;
  }

  //temporarily hack untill everything becomes avalon aware.
  public XMLReader getXMLReader(){
    XMLReader parser = null;

    try {
      //FIXME: following lines need to switch to avalon use of course.
      SAXParserFactory spf = SAXParserFactory.newInstance();
      // non-validating...
      // but namespace-aware.
      spf.setNamespaceAware(true);
      spf.setValidating(false);
      javax.xml.parsers.SAXParser sp = spf.newSAXParser();
      parser = sp.getXMLReader();
      parser.setEntityResolver(getCatalogResolver());
    } catch(FactoryConfigurationError error) {
      error.printStackTrace();
    } catch(ParserConfigurationException e) {
      e.printStackTrace();
    } catch(SAXException e) {
      e.printStackTrace();
    }
    return parser;
  }

    //temporarily hack untill everything becomes avalon aware.
  public DocumentBuilder getDocumentBuilder(){
    DocumentBuilder parser = null;

    try {
      //FIXME: following lines need to switch to avalon use of course.
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      dbf.setValidating(false);
      parser = dbf.newDocumentBuilder();
      parser.setEntityResolver(getCatalogResolver());
    } catch(FactoryConfigurationError error) {
      error.printStackTrace();
    } catch(ParserConfigurationException e) {
      e.printStackTrace();
    }
    return parser;
  }

  public org.xml.sax.EntityResolver getCatalogResolver() {
    if (this.manager != null) {
      try {
        EntityResolver resolver = (EntityResolver)this.manager.lookup(EntityResolver.ROLE);
        return resolver;
      } catch(ComponentException e) {
        e.printStackTrace();
      }
    }
    CatalogResolver catalogResolver = new CatalogResolver();
    try {
      catalogResolver.getCatalog().parseCatalog("src/resources/schema/catalog");
    } catch(IOException e) {
      e.printStackTrace();
    }
    return catalogResolver;
  }

  public LibreAttributeReader getDefaultFilter() {
    return new LibreAttributeReader(){
      public String getAttributeValue(Entry entryToInspect) { return null;}
      public boolean isAttributeValue(Entry entryToInspect) { return true;}
      public void setInverseLogic(boolean inverse) { }
      public void setOrderDescending(boolean descending)  {  }
      public boolean isOrderDescending()  { return false;  }
    };
  }
  public LibreAttributeReader getDefaultSorter() {
    return new PropertyAttributeReader("name", null, null);
  }

  /** Constant for a default libre config to start from. */
  public static final LibreConfig DEFAULT_LIBRE_CONFIG = new LibreConfig(){
    {
      ChildDefinition acd = new AutoChildDefinition();
      acd.setAttributeReader("label", new PropertyAttributeReader("name", null, null, null));
      acd.setAttributeReader("href", new PropertyAttributeReader("path", null, null));
      //keep the filter not set
      //acd.setFilterTest(LibreConfig.getDefaultFilter());
      //keep the sorter not set!
      addChildDefinition(acd);
    }
  };

  public LibreConfig createInheritConfig(HierarchyConfig parentCfg){
    try {
      return new LibreConfig((LibreConfig.ChildDefinition)parentCfg);
    } catch(ClassCastException cce) {
      return DEFAULT_LIBRE_CONFIG;
    }
  }

  public LibreAttributeReader createPropertyAttributeReader(String name,
                            String mask, String regex, String substitute){
    return new PropertyAttributeReader(name, mask, regex, substitute);
  }
  public LibreAttributeReader createXPathAttributeReader(String expression) {
    return new XPathAttributeReader(expression, this);
  }
}
