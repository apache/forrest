<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="resource"/>
  
  <xsl:template name="spacer">
    <td width="8">
      <img src="skin/images/spacer.gif" width="8" height="8" alt=""/>
    </td>
  </xsl:template>
  
  <xsl:template name="not-selected">
      <td valign="bottom">
        <table cellspacing="0" cellpadding="0" border="0" height="25" summary="non selected tab">
          <tr>
            <td bgcolor="#B2C4E0" width="5" valign="top"><img src="skin/images/tab-left.gif" alt="" width="5" height="5" /></td>
            <td bgcolor="#B2C4E0" valign="middle">
              <a href="/xml-forrest/{@dir}"><font face="Arial, Helvetica, Sans-serif" size="2"><xsl:value-of select="@label"/></font></a>
            </td>
            <td bgcolor="#B2C4E0" width="5" valign="top"><img src="skin/images/tab-right.gif" alt="" width="5" height="5" />
            </td>
          </tr>
        </table>
      </td>
  </xsl:template>
  
  <xsl:template name="selected">
      <td valign="bottom">
        <table cellspacing="0" cellpadding="0" border="0" height="30" summary="selected tab">
          <tr>
            <td bgcolor="#4C6C8F" width="5" valign="top"><img src="skin/images/tabSel-left.gif" alt="" width="5" height="5" /></td>
            <td bgcolor="#4C6C8F" valign="middle">
              <font face="Arial, Helvetica, Sans-serif" size="2" color="#ffffff"><b><xsl:value-of select="@label"/></b></font>
            </td>
            <td bgcolor="#4C6C8F" width="5" valign="top"><img src="skin/images/tabSel-right.gif" alt="" width="5" height="5" /></td>
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
