/* File Creation Info [User: mpo | Date: 3-mei-02 | Time: 9:21:10 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

import org.outerj.yer.hierarchy.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.apache.xpath.XPathAPI;
import org.apache.avalon.excalibur.xml.Parser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.IOException;


/** Class <code>org.outerj.yer.libre.XPathAttributeReader</code> ...
 *
 * [FIXME this babe needs serious Avalon enabling in the ]
 * @author $Author: stevenn $
 * @version CVS $Id: XPathAttributeReader.java,v 1.1 2002/06/11 13:19:21 stevenn Exp $
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
    Parser excaliburParser = this.helper.getExcaliburParser();
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
