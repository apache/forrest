/* File Creation Info [User: mpo | Date: 30-apr-02 | Time: 8:52:32 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.sax;

//std
import java.util.Properties;

//jaxp
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;

/** Set of static methods that make SAX-life easier.
 * 1st Checkin: $Date: 2002/06/11 13:19:20 $
 * @author  $Author: stevenn $
 * @version CVS $Id: SAXConvenience.java,v 1.1 2002/06/11 13:19:20 stevenn Exp $
 */
public class SAXConvenience {
	/** Makes a standard empty TransformerHandler that can be used as a
	 *  ContentHandler that sinks the SAXEvents to a <code>
	 *  javax.xml.transform.Result</code> of your choice.  Typical use:
	 <code>
	 XMLReader parser;
	 Result out = new StreamResult(System.out);
	 TransformerHandler th = SAXConvenience.getSAXSink(out);
	 parser.setContentHandler(th);
	 parser.parse(inXML);
	 </code>
	 * @param out <code>Result</code> object where to sink to.
	 * @returns the requested <code>TransformerHandler</code> or
	 *   <code>null</code> if there were any configuration errors.
	 */
	public static TransformerHandler getSAXSink(Result out){
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			if(tf.getFeature(SAXTransformerFactory.FEATURE)) {
				SAXTransformerFactory stf;
				TransformerHandler th;
				stf = (SAXTransformerFactory)tf;
				th = stf.newTransformerHandler();
				Properties outProps = new Properties();
				outProps.put(OutputKeys.METHOD, "xml");
				outProps.put(OutputKeys.INDENT, "yes");
				th.getTransformer().setOutputProperties(outProps);
				th.setResult(out);
				return th;
			}
		} catch (TransformerConfigurationException tce) {
			tce.printStackTrace();
		}
		return null;
	}

	/** Assembles prefix and suffix parts of a qname.  Handy if somenone
	 *  outside your control sets the prefix (and thus possibly to null)
	 * @param lname unprefixed name
	 * @param prefix that needs to be added to the lname, with a
	 *    separating ':' iff the prefix isn't null or ""
	 * @returns the combined qname.  Note that it can be == to lname*/
	public static String makeQname(String lname, String prefix) {
		if (prefix == null || "".equals(prefix)){
			return lname;
		}
		return prefix + ":" + lname;
	}
}