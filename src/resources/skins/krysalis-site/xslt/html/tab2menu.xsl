<?xml version="1.0"?>
<!--
This stylesheet generates 'tabs' at the top left of the Forrest skin.  Tabs are
visual indicators that a certain subsection of the URI space is being browsed.
For example, if we had tabs with paths:

Tab1:  ''
Tab2:  'community'
Tab3:  'community/howto'
Tab4:  'community/howto/xmlform/index.html'

Then if the current path was 'community/howto/foo', Tab3 would be highlighted.
The rule is: the tab with the longest path that forms a prefix of the current
path is enabled.

The output of this stylesheet is HTML of the form:
    <div class="tab">
      ...
    </div>

which is then merged by site2xhtml.xsl

$Id: tab2menu.xsl,v 1.1 2002/12/07 16:26:34 nicolaken Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/tab2menu.xsl"/>

  <xsl:template match="tabs">
    <div class="tab">
      <table cellspacing="0" cellpadding="0" border="0" summary="tab bar">
        <tr>
          <xsl:call-template name="base-tabs"/>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template name="pre-separator">
    <xsl:call-template name="separator"/>
  </xsl:template>

  <xsl:template name="post-separator">
  </xsl:template>

  <xsl:template name="separator">
    <td width="8">
      <img src="{$root}skin/images/spacer.gif" width="8" height="8" alt=""/>
    </td>
  </xsl:template>

  <xsl:template name="selected">
    <td valign="bottom">
      <table cellspacing="0" cellpadding="0" border="0" height="22" summary="selected tab">
        <tr>
          <td class="top-left"></td>
          
          <td bgcolor="#a5b6c6" valign="middle">
            <span class="tab">
              <b>
                <xsl:call-template name="base-selected"/>
              </b>
             </span>
           </td>
          
          <td class="top-right"></td>
        </tr>
      </table>
    </td>
  </xsl:template>

  <xsl:template name="not-selected">
    <td valign="bottom">
      <table cellspacing="0" cellpadding="0" border="0" height="18" summary="non selected tab">
        <tr>
        
          <td class="top-left-tab"></td>
          
          <td bgcolor="#cedfef" valign="middle">
            <span class="tab">
              <xsl:call-template name="base-not-selected"/>
            </span>              
          </td>

          <td class="top-right-tab"></td>

        </tr>
      </table>
    </td>
  </xsl:template>

</xsl:stylesheet>