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
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.text.GlobCompiler;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/** Class <code>org.apache.forrest.yer.libre.PropertyAttributeReader</code> ...
 *
 * @author $Author: jefft $
 * @version CVS $Id: PropertyAttributeReader.java,v 1.3 2002/11/05 05:52:41 jefft Exp $
 */
public class PropertyAttributeReader  implements LibreAttributeReader
{
  private String name;
  private String substitute;
  private Pattern regexPattern = null; // set either by setRegex or by setGlob
  public Pattern getPattern(){ //laisy compilation of the regex.
    return this.regexPattern;
  }
  public void setRegex(String regex) {
    // would be better to get them from Avalon or so?
    PatternCompiler regexCompiler = new Perl5Compiler();
     if (regex != null){
      try {
        this.regexPattern = regexCompiler.compile(regex);
      } catch (MalformedPatternException e){
        this.regexPattern = null;
      }
    }
  }
  public void setGlob(String mask) {
    // would be better to get them from Avalon or so?
   PatternCompiler globCompiler = new GlobCompiler();
    if (mask != null){
      try {
        this.regexPattern = globCompiler.compile(mask);
      } catch (MalformedPatternException e){
        this.regexPattern = null;
      }
    }
  }

  public PropertyAttributeReader(String propName, String propMask, String propRegex)
  {
    this(propName, propMask, propRegex, null);
  }
  public PropertyAttributeReader(String propName, String propMask, String propRegex, String propSubstitute)
  {
    this.name  = propName;
    setGlob(propMask);
    setRegex(propRegex); // regex rules over mask (will overwrite pattern)
    this.substitute = propSubstitute;
  }

  public String getAttributeValue(Entry entryToInspect)
  {
    //FIXME: this needs regex substitution posibilities!
    String bareValue = getBareAttributeValue(entryToInspect);
    String substitutedValue;
    if (getPattern() == null || this.substitute == null) {
      substitutedValue = bareValue;
    } else{
      Perl5Substitution ps = new Perl5Substitution(this.substitute);
      PatternMatcher mtch = new Perl5Matcher();
      substitutedValue = Util.substitute(mtch, getPattern(), ps, bareValue,
                                         Util.SUBSTITUTE_ALL );
    }
    return substitutedValue;
  }
  private String getBareAttributeValue(Entry entryToInspect)
  {
    //TODO: maybe some future version can think about recursively get
    // getX().getY().getZ() etc etc if the name would be <property name="x.y.z" />
    String retVal = "";
    Object userObject = entryToInspect.getUserObject();
    try {
      Class inspectClass = userObject.getClass();
      Method m = inspectClass.getMethod("get" + initCap(name), null);
      Object val = m.invoke(userObject, null);
      retVal =  val.toString();
    } catch(NoSuchMethodException e) {
    } catch(SecurityException e) {
    } catch(IllegalAccessException e) {
    } catch(IllegalArgumentException e) {
    } catch(InvocationTargetException e) {
    }
    return retVal;
  }

  public boolean isAttributeValue(Entry entryToInspect)
  {
    String value = getBareAttributeValue(entryToInspect);
    Pattern pat = getPattern();
    if (pat != null) {
      PatternMatcher mtch = new Perl5Matcher();
      final boolean testValue = ( pat != null && mtch.contains(value, pat));
      return this.inverseLogic != testValue;
    }
    final boolean testValue = "true".equals(value);
    return this.inverseLogic != testValue;
  }

  public String initCap(String s){
    char[] c = s.toCharArray();
    c[0] = Character.toUpperCase(c[0]);
    return new String(c);
  }

  private boolean inverseOrder = false;
  public void setOrderDescending(boolean descending)
  {
    this.inverseOrder = descending;
  }

  public boolean isOrderDescending()
  {
    return inverseOrder;
  }

  private boolean inverseLogic = false;
  public void setInverseLogic(boolean inverse)
  {
    this.inverseLogic = inverse;
  }

}
