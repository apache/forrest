<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="resource"/>
  <xsl:template match="book">
    <div class="menu">
      <ul>
        <xsl:apply-templates select="menu"/>
      </ul>
    </div>
  </xsl:template>
  <xsl:template match="menu">
    <li>
      <font color="white"><xsl:value-of select="@label"/></font>
      <ul>
        <xsl:apply-templates/>
      </ul>
    </li>
  </xsl:template>
  <xsl:template match="menu-item">
    <li>
       <xsl:choose>
         <xsl:when test="starts-with(@href, $resource)">
          <span class="sel"><font color="#ffcc00"><xsl:value-of select="@label"/></font></span>
        </xsl:when>
        <xsl:otherwise>
          <a href="{@href}"><xsl:value-of select="@label"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>
  <xsl:template match="external">
    <li>
       <xsl:choose>
         <xsl:when test="starts-with(@href, $resource)">
          <font color="#ffcc00"><xsl:value-of select="@label"/></font>
        </xsl:when>
        <xsl:otherwise>
          <a href="{@href}" target="_blank"><xsl:value-of select="@label"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>
  <xsl:template match="menu-item[@type='hidden']"/>
  <xsl:template match="external[@type='hidden']"/>
</xsl:stylesheet>
