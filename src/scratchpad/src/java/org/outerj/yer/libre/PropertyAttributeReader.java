/* File Creation Info [User: mpo | Date: 3-mei-02 | Time: 0:56:45 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

import org.outerj.yer.hierarchy.Entry;
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

/** Class <code>org.outerj.yer.libre.PropertyAttributeReader</code> ...
 *
 * @author $Author: stevenn $
 * @version CVS $Id: PropertyAttributeReader.java,v 1.1 2002/06/11 13:19:21 stevenn Exp $
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
