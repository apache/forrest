<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
      <xsl:choose>
        <xsl:when test="menu-item">
          <ul>
            <xsl:apply-templates select="menu-item"/>
          </ul>
        </xsl:when>
        <xsl:otherwise>
          <ul>
            <xsl:apply-templates select="external"/>
          </ul>
        </xsl:otherwise>
      </xsl:choose>
    </li>
  </xsl:template>
  <xsl:template match="menu-item">
    <li>
      <a href="{@href}"><xsl:value-of select="@label"/></a>
    </li>
  </xsl:template>
  <xsl:template match="external">
    <li>
      <a href="{@href}" target="_blank"><xsl:value-of select="@label"/></a>
    </li>
  </xsl:template>
  <xsl:template match="menu-item[@type='hidden']"/>
  <xsl:template match="external[@type='hidden']"/>
</xsl:stylesheet>
