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

$Id: tab2menu.xsl,v 1.6 2002/11/09 11:37:44 jefft Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="path"/>
  <xsl:param name="dir_index" select="'index.html'"/>
  <xsl:include href="dotdots.xsl"/>
  <xsl:include href="pathutils.xsl"/>

  <!-- NOTE: Xalan has a bug (race condition?) where sometimes $root is only half-evaluated -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>

  <xsl:template name="spacer">
    <td width="8">
      <img src="{$root}skin/images/spacer.gif" width="8" height="8" alt=""/>
    </td>
  </xsl:template>

  <!--
    The longest path of any tab, whose path is a subset of the current URL.  Ie,
    the path of the 'current' tab.
  -->
  <xsl:variable name="longest-dir">
    <xsl:for-each select="/tabs/tab[starts-with($path, @dir|@href)]">
      <xsl:sort select="string-length(@dir|@href)"
                data-type="number" order="descending"/>
      <xsl:if test="position()=1">
        <xsl:value-of select="@dir|@href"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:variable>


  <xsl:template match="tabs">
    <div class="tab">
      <table cellspacing="0" cellpadding="0" border="0" summary="tab bar">
        <tr>
          <xsl:apply-templates/>
        </tr>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="tab">
    <xsl:call-template name="spacer"/>
    <xsl:choose>
      <xsl:when test="@dir = $longest-dir or @href = $longest-dir">
        <xsl:call-template name="selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="not-selected"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="not-selected">
    <td valign="bottom">
      <table cellspacing="0" cellpadding="0" border="0" height="25" summary="non selected tab">
        <tr>
          <td bgcolor="#B2C4E0" width="5" valign="top"><img src="{$skin-img-dir}/tab-left.gif" alt="" width="5" height="5" /></td>
          <td bgcolor="#B2C4E0" valign="middle">
            <a>
              <xsl:attribute name="href">
                <xsl:if test="starts-with(@href, 'http')">  <!-- Absolute URL -->
                  <xsl:value-of select="@href"/>
                </xsl:if>
                <xsl:if test="not(starts-with(@href, 'http'))">  <!-- Root-relative path -->
                  <xsl:variable name="backpath">
                    <xsl:call-template name="dotdots">
                      <xsl:with-param name="path" select="$path"/>
                    </xsl:call-template>
                    <xsl:text>/</xsl:text>
                    <xsl:value-of select="@dir|@href"/>
                    <!-- If we obviously have a directory, add /index.html -->
                    <xsl:if test="@dir or substring(@href, string-length(@href),
                      string-length(@href)) = '/'">
                      <xsl:text>/</xsl:text>
                      <xsl:value-of select="$dir_index"/>
                    </xsl:if>
                  </xsl:variable>

                  <xsl:value-of
                    select="translate(normalize-space(translate($backpath, ' /', '/ ')), ' /', '/ ')"/>
                  <!-- Link to backpath, normalizing slashes -->
                </xsl:if>
              </xsl:attribute>
              <font face="Arial, Helvetica, Sans-serif" size="2"><xsl:value-of select="@label"/></font>
            </a>
          </td>
          <td bgcolor="#B2C4E0" width="5" valign="top"><img src="{$skin-img-dir}/tab-right.gif" alt="" width="5" height="5" />
          </td>
        </tr>
      </table>
    </td>
  </xsl:template>

  <xsl:template name="selected">
    <td valign="bottom">
      <table cellspacing="0" cellpadding="0" border="0" height="30" summary="selected tab">
        <tr>
          <td bgcolor="#4C6C8F" width="5" valign="top"><img src="{$skin-img-dir}/tabSel-left.gif" alt="" width="5" height="5" /></td>
          <td bgcolor="#4C6C8F" valign="middle">
            <font face="Arial, Helvetica, Sans-serif" size="2" color="#ffffff"><b><xsl:value-of select="@label"/></b></font>
          </td>
          <td bgcolor="#4C6C8F" width="5" valign="top"><img src="{$skin-img-dir}/tabSel-right.gif" alt="" width="5" height="5" /></td>
        </tr>
      </table>
    </td>
  </xsl:template>

</xsl:stylesheet>
