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
package org.apache.forrest.sax;

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
 * 1st Checkin: $Date: 2002/11/05 05:52:40 $
 * @author  $Author: jefft $
 * @version CVS $Id: SAXConvenience.java,v 1.3 2002/11/05 05:52:40 jefft Exp $
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
