/* File Creation Info [User: mpo | Date: 16-mei-02 | Time: 13:58:12 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.excalibur.xml.Parser;
import org.apache.avalon.excalibur.xml.EntityResolver;
import org.apache.xml.resolver.tools.CatalogResolver;
//import org.apache.cocoon.components.resolver.Resolver;
import org.outerj.yer.hierarchy.HierarchyConfig;
import org.outerj.yer.hierarchy.Entry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
//import org.xml.sax.EntityResolver;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
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

/** Class <code>org.outerj.yer.libre.LibreConfigHelper</code> ...
 * 
 * @author $Author: crossley $
 * @version CVS $Id: LibreConfigHelper.java,v 1.2 2002/07/12 07:10:14 crossley Exp $
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
        Parser parser = getExcaliburParser();
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

  public Parser getExcaliburParser(){
    if (this.manager == null) return null;
    try {
      return (Parser)this.manager.lookup(Parser.ROLE);
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
      SAXParser sp = spf.newSAXParser();
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
