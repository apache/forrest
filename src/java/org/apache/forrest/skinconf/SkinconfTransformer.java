/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.forrest.skinconf;

import java.awt.Color;
import java.io.IOException;
import java.util.Map;

import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.cocoon.transformation.AbstractTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author <a href="mailto:nicolaken@apache.org">Nicola Ken Barozzi</a>
 * @version CVS $Id: SkinconfTransformer.java,v 1.2 2004/02/19 23:39:51 nicolaken Exp $
 */
public class SkinconfTransformer
  extends AbstractTransformer {

          /**
     * Setup
     */
    public void setup(SourceResolver resolver, Map objectModel,
                      String src, Parameters parameters)
    throws ProcessingException, SAXException, IOException {
        /*final boolean append = parameters.getParameterAsBoolean("append", false);
        final String  logfilename = parameters.getParameter("logfile", null);*/
    }
    
   /**
     * Receive notification of the beginning of an element.
     */
    public void startElement(String uri, String loc, String raw, Attributes a)
    throws SAXException {
        /*
        this.log ("startElement", "uri="+uri+",local="+loc+",raw="+raw);
        for (int i = 0; i < a.getLength(); i++) {
            this.log ("            ", new Integer(i+1).toString()
                 +". uri="+a.getURI(i)
                 +",local="+a.getLocalName(i)
                 +",qname="+a.getQName(i)
                 +",type="+a.getType(i)
                 +",value="+a.getValue(i));
        }
        */
        Attributes outAttributes = a;
        
        if("color".equals(loc)) {
            
            AttributesImpl newAttributes = new AttributesImpl(a);
            
            String value=null;
            String highlight=null;
            String lowlight=null;
            String font=null;
            String link=null;            
            String vlink=null;
            String hlink=null;
            
            for (int i = 0; i < a.getLength(); i++) {
                if("value".equals(a.getLocalName(i))){
                   value=a.getValue(i); 
                }else if("highlight".equals(a.getLocalName(i))){
                   highlight=a.getValue(i); 
                }else if("lowlight".equals(a.getLocalName(i))){
                   lowlight=a.getValue(i); 
                }else if("font".equals(a.getLocalName(i))){
                   font=a.getValue(i); 
                }else if("link".equals(a.getLocalName(i))){
                   link=a.getValue(i); 
                }else if("vlink".equals(a.getLocalName(i))){
                   vlink=a.getValue(i); 
                }else if("hlink".equals(a.getLocalName(i))){
                   hlink=a.getValue(i); 
                }              
            }
            
            if(value==null){
              value="#dd0000";// a default "visible" color
              newAttributes.addAttribute("","value","value","",value);
            }   
            
            Color valueColor = Color.decode(value);
            float brightness = Color.RGBtoHSB(valueColor.getRed(),
                                              valueColor.getGreen(),
                                              valueColor.getBlue(),
                                              null)[2];
            
            if(highlight==null){
                highlight = "#"+Integer.toHexString(valueColor.brighter().getRGB()).substring(2);
                newAttributes.addAttribute("","highlight","highlight","",highlight);
            } 

            if(lowlight==null){
                lowlight = "#"+Integer.toHexString(valueColor.darker().getRGB()).substring(2);
                newAttributes.addAttribute("","lowlight","lowlight","",lowlight);
            }   
            
            if(font==null){
                if(brightness<0.5) {
                    font="#ffffff";
                }
                else{   
                    font="#000000";
                }
                newAttributes.addAttribute("","font","font","",font);
            }    

            if(link==null){
                if(brightness<0.5) {
                     link="#7f7fff";
                }
                else{   
                    link="#0000ff";
                }
                newAttributes.addAttribute("","link","link","",link);
            }    

            if(vlink==null){
                if(brightness<0.5) {
                    vlink="#4242a5";
                }
                else{   
                    vlink="#ffffff";
                }
                newAttributes.addAttribute("","vlink","vlink","",link);
            }   
            
            if(hlink==null){
                if(brightness<0.5) {
                    hlink="#0037ff";
                }
                else{   
                    hlink="#6587ff";
                }
                newAttributes.addAttribute("","hlink","hlink","",link);
            }   
            
            outAttributes = newAttributes;
        }
        
        if (super.contentHandler!=null) {
            super.contentHandler.startElement(uri,loc,raw,outAttributes);
        }
    }
}
