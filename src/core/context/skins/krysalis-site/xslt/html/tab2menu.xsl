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
    <table class="tab">
      ...
    </table>
    <table class="level2tab">
      ...
    </table>

which is then merged by site2xhtml.xsl

$Id: tab2menu.xsl,v 1.2 2003/12/28 22:54:16 nicolaken Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/tab2menu.xsl"/>
  
  <xsl:template match="tabs">
      <table class="tab" cellspacing="0" cellpadding="0" border="0"> 
        <tr>
          <xsl:call-template name="base-tabs"/>
        </tr>
      </table>
      <xsl:if test="tab[@dir=$longest-dir]/tab">
      <table class="level2tab" cellspacing="0" cellpadding="0" border="0">
          <tr>
            <td>
              <xsl:call-template name="level2tabs"/>
            </td>
          </tr>
        </table>
      </xsl:if>      
  </xsl:template>

  <xsl:template name="pre-separator">
     <td class="tab pre-separator"></td>
  </xsl:template>

  <xsl:template name="post-separator">
  
  </xsl:template>

  <xsl:template name="separator">
     <td class="tab separator"></td>
  </xsl:template>

  <xsl:template name="level2-pre-separator">
     <td class="tab pre-separator"></td>
  </xsl:template>

  <xsl:template name="level2-post-separator">
  
  </xsl:template>

  <xsl:template name="level2-separator">
     <td class="level2tab separator">|</td>
  </xsl:template>

  <xsl:template name="selected">
        <td class="tab selected top-left"></td>
        <td class="tab selected">
           <xsl:call-template name="base-selected"/>
        </td>
        <td class="tab selected top-right"></td>
  </xsl:template>

  <xsl:template name="not-selected">
    <td>
      <table cellspacing="0" cellpadding="0" border="0">
        <tr>
         <td class="tab unselected top-left"></td>
          <td class="tab unselected">
             <xsl:call-template name="base-not-selected"/>
          </td>
          <td class="tab unselected top-right"></td>
        </tr> 
        <tr>
          <td colspan="3" class="spacer"/>
        </tr>
      </table>
    </td>
  </xsl:template>
  
  <xsl:template name="level2-selected">
        <td class="level2tab selected">
           <xsl:call-template name="base-selected"/>
        </td>
  </xsl:template>

  <xsl:template name="level2-not-selected">
    <td class="level2tab unselected">
      <xsl:call-template name="base-not-selected"/> 
    </td>
  </xsl:template>

</xsl:stylesheet>
