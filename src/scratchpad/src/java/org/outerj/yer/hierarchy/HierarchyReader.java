/* File Creation Info [User: mpo | Date: 29-apr-02 | Time: 17:39:18 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.hierarchy;

//sax
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.outerj.sax.SAXConvenience;

/** Class <code>org.outerj.yer.hierarchy.HierarchyReader</code> will
 *  traverse the presented structure hanging under the rootEntry.
 *  Comparable to a <code>org.xml.sax.XMLReader</code> this class will
 *  generate SAXEvents to the registered contentHandler.
 *
 *  At this stage the option has been not to let this class implement the
 *  <code>org.xml.sax.XMLReader</code> interface.  While much of it's
 *  behaviour does resemble that particular type of being, the full <code>
 *  XMLReader</code> also brings in quite a number of responsibilities that
 *  were considered outside class scope for the moment.
 *
 *  Responsibility: Bind the abstract hierarchy structure to an XML
 *  representation. (This class can't make assumptions on the nature of the
 *  hierarchy it is traversing.)
 *
 *  Namespace behaviour: this class will read out namespace information in
 *  all possible ways the SAX Callbacks are offering:  <ul>
 *  <li> prefixMapping events</li>
 *  <li> uri and lname in Element events</li>
 *  <li> qname in Element events</li>
 *  <li> xmlns attributes in the startElement event</li></ul>
 *  This behavior is equal to having both namespace related features:
 *  <code>http://xml.org/sax/features/namespaces</code> and <code>
 *  http://xml.org/sax/features/namespace-prefixes</code> set to true.
 *  @link http://saxproject.org/?selected=namespaces
 *
 * [FIXME: consider implementing org.apache.avalon.excalibur.xml in some
 *  sister class of this or something... more catchup on avalon needed.]
 * @author $Author: stevenn $
 * @version CVS $Id: HierarchyReader.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public class HierarchyReader
{
  /** Holds the contentHandler that will listen to what this class Reads
   *  out to it.
   */
  private ContentHandler contentHandler;
  /** Stores how deep the traversing needs to go.  Default is -1 (unlimited)
   */
  private int depth = -1;
  /** Remembers if the xmlns attribute has already been set or not. */
  private boolean xmlnsNotSet = true;

  /** Sets this class ContentHandler. The contentHandler is where this
   *  class reads it's SAXEvents to.
   */
  public void setContentHandler(ContentHandler theContentHandler)
  {
    this.contentHandler = theContentHandler;
  }

  /** Sets the recursing depth that will be applied when traversing the
   *  hierarchy.
   * @param depth holds the integer value of how many levels deep the
   *  hierarchy will get traversed. 0 means only current directory, any
   *  negative depth level will cause the traversing to go down to the
   *  deepest available level in the hierarchy.
   */
  public void setDepth(int depth)
  {
    this.depth = depth;
  }
  /** Sets the recursing depth that will be applied when traversing the
   *  hierarchy.
   * @param depth holds the string value of how many levels deep the
   *  hierarchy will get traversed. "." or "0" means only the current
   *  directory, "*" or any negative number as a string will cause the
   *  traversing to go down to the deepest available level in the hierarchy.
   */
  public void setDepth(String depthStr)
  {
    if ("*".equals(depthStr)) {
      setDepth(-1);
    } else if (".".equals(depthStr)) {
      setDepth(0);
    } else {
      try {
        setDepth(Integer.parseInt(depthStr));
      } catch (Exception e) {
        setDepth(0);
      }
    }
  }

  /** Starts the traversing of the hierarchy and the emission of the
   *  appropriate SAXEvents while that goes about.  Traversing will be
   *  as deep down in the hierarchy as the <code>setDepth()</code> indicates.
   *  SAXEvents will be sent to the class registered with <code>
   *  setContentHandler()</code>
   * @param rootEntry is the <code>Entry</code> where the traversing starts
   */
  public void startReading(Entry rootEntry)
  {
    notifyStartReading();
    recurse(rootEntry, this.depth);
    notifyEndReading();
  }

  /** Recursively (depth first) traverses the hierarchy, decrementing the
   *  currentDepth and taking up childEntries as we go down.
   * @param currentEntry the current position in the traversed hierarchy
   * @param currentDepth decrementing parameter.  When equal to zero the
   *  traversing ends.
   */
  public void recurse(final Entry currentEntry, final int currentDepth)
  {
    if (currentEntry.hasChildEntries()){
      notifyStartCollection(currentEntry);
      if (currentDepth != 0) {
        EntryList theChildren = currentEntry.getChildEntries();
        if(theChildren != null) {
          theChildren.visitEntries(new EntryVisitor(){
            public void visit(Entry theEntry)
            {
              HierarchyReader.this.recurse(theEntry, currentDepth - 1);
            }
          });
        }
      }
      notifyEndCollection();
    } else {
      notifyResource(currentEntry);
    }
  }

  /** Helper method to notify that reading of the hierarchy has begun. */
  private void notifyStartReading() {
    try {
      this.contentHandler.startDocument();
    } catch (SAXException se) {
      se.printStackTrace();
    }
  }
  /** Helper method to notify that reading of the hierarchy has ended. */
  private void notifyEndReading() {
    try {
      this.contentHandler.endDocument();
    } catch(SAXException se) {
      se.printStackTrace();
    }
  }
  /** Helper method to notify that a new Collection in the hierarchy has
   *  started.
   */
  private void notifyStartCollection(Entry collectionEntry)
  {
    startElm(HierarchyConstants.COLLECTION_ELM,
        getEntryAttributes(collectionEntry));
  }
  /** Helper method to notify that a new Collection in the hierarchy has
   *  ended.
   */
  private void notifyEndCollection()  {
    endElm(HierarchyConstants.COLLECTION_ELM);
  }
  /** Helper method to notify that a resource in the hierachy is passed. */
  private void notifyResource(Entry resourceEntry)  {
    startElm(HierarchyConstants.ITEM_ELM,
        getEntryAttributes(resourceEntry));
    endElm(HierarchyConstants.ITEM_ELM);
  }
  /** Helper method that gets the Entry Attributes to set */
  private AttributesImpl getEntryAttributes(Entry theEntry){
    AttributesImpl atts = new AttributesImpl();
    final String[] attrNames = theEntry.getAvaliableAttributeNames();
    final int len = attrNames.length;
    for (int i=0; i<len; i++ ){
      String attrName = attrNames[i];
      String attrValue = theEntry.getAttribute(attrName);
      if (attrValue != null && !"".equals(attrValue)) {
        atts.addAttribute("", attrName, attrName, "CDATA", attrValue );
      }
    }
    return atts;
  }

  /** Helper method sends the SAXEvent startElement() */
  private void startElm(String lname, AttributesImpl atts){
    try {
      String xmlnsAttQname, xmlnsAttLname;
      if (this.xmlnsNotSet){
        if (HierarchyConstants.NS_URI != null) {
          if (HierarchyConstants.NS_PREFIX != null) {
            xmlnsAttQname = "xmlns:" + HierarchyConstants.NS_PREFIX;
            xmlnsAttLname = HierarchyConstants.NS_PREFIX;
          } else {
            xmlnsAttQname = "xmlns";
            xmlnsAttLname = "";
          }
          atts.addAttribute("http://www.w3.org/2000/xmlns/", xmlnsAttLname,
                            xmlnsAttQname,"CDATA", HierarchyConstants.NS_URI);
        }
        this.xmlnsNotSet = false;
      }
      this.contentHandler.startElement(HierarchyConstants.NS_URI, lname,
          SAXConvenience.makeQname(lname, HierarchyConstants.NS_PREFIX), atts);
    } catch(SAXException se) {
      se.printStackTrace();
    }
  }

  /** Helper method sends the SAXEvent endElement() */
 private void endElm( String lname){
   try {
     this.contentHandler.endElement(HierarchyConstants.NS_URI, lname,
         SAXConvenience.makeQname(lname, HierarchyConstants.NS_PREFIX));
   } catch(SAXException se) {
     se.printStackTrace();
   }
 }
}
