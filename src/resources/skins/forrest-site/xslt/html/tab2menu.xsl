<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="resource" select="'UNDEFINED'"/>
  <xsl:param name="dir" select="'UNDEFINED'"/>
  <xsl:include href="dotdots.xsl"/>

  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$dir"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>

  <xsl:template name="spacer">
    <td width="8">
      <img src="{$root}skin/images/spacer.gif" width="8" height="8" alt=""/>
    </td>
  </xsl:template>
  
  <xsl:template name="not-selected">
      <td valign="bottom">
        <table cellspacing="0" cellpadding="0" border="0" height="25" summary="non selected tab">
          <tr>
            <td bgcolor="#B2C4E0" width="5" valign="top"><img src="{$skin-img-dir}/tab-left.gif" alt="" width="5" height="5" /></td>
            <td bgcolor="#B2C4E0" valign="middle">
              <a>
                <xsl:attribute name="href">
                  <xsl:value-of select="$root"/>
                  <xsl:if test="@dir != ''">
                    <xsl:value-of select="concat(translate(normalize-space(translate(@dir, ' /', '/ ')), ' /', '/ '), '/')"/>
                    <!-- The above expression strips duplicate and trailing /'s. Cunning :) :) -->
                  </xsl:if>
                </xsl:attribute>
                <font face="Arial, Helvetica, Sans-serif" size="2"><xsl:value-of select="@label"/></font></a>
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
      <xsl:when test="$resource!='' and @dir=''">
        <xsl:call-template name="not-selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="starts-with($resource,@dir)">
           <xsl:call-template name="selected"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="not-selected"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
