/* File Creation Info [User: mpo | Date: 8-mei-02 | Time: 15:13:20 ]
 * (c) 2002, Outerthought bvba. http://outerthought.org
 */
package org.outerj.yer.libre;

/** Interface <code>org.outerj.yer.libre.LibreConstants</code> holds
 *  constants for different element and attributenames of the libre.xml files.
 * 
 * @author $Author: stevenn $
 * @version CVS $Id: LibreConstants.java,v 1.1 2002/06/11 13:19:21 stevenn Exp $
 */
public interface LibreConstants
{
  public static final String NS_URI = "http://outerx.org/libre/config/0.1";
  public static final String LIBRE_ELM = "libre";
  public static final String ENTRY_ELM = "entry";

  public static final String LOCATION_ATT = "location";
  public static final String AUTO_ELM = "auto";

  public static final String FILTER_ELM = "filter";
  public static final String SORTER_ELM = "sorter";
  public static final String XPATH_ELM = "xpath";
  public static final String PROPERTY_ELM = "property";

  public static final String LOGIC_ATT = "logic";
  public static final String LOGIC_VAL_INVERSE = "inverse";
  public static final String LOGIC_VAL_NORMAL  = "normal";

  public static final String CLEAR_ATT = "clear";
  public static final String CLEAR_VAL_TRUE = "yes";
  public static final String CLEAR_VAL_FALSE = "no";

  public static final String ORDER_ATT = "order";
  public static final String ORDER_VAL_ASCENDING = "ascending";
  public static final String ORDER_VAL_DESCENDING = "descending";
  public static final String EXPRESSION_ATT = "expression";
  public static final String NAME_ATT = "name";
  public static final String MASK_ATT = "mask";
  public static final String REGEX_ATT = "regex";
  public static final String SUBSTITUTE_ATT = "substitute";
}

