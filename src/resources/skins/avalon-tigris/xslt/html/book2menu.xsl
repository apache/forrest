<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="path"/>

  <xsl:include href="pathutils.xsl"/>

  <xsl:variable name="filename-noext">
    <xsl:call-template name="filename-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="path-noext">
    <xsl:call-template name="path-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:template match="book">
    <div class="menuBar">
      <xsl:apply-templates select="menu"/>
    </div>
  </xsl:template>

  <xsl:template match="menu">
    <div class="menu">
      <span class="menuLabel"><xsl:value-of select="@label"/></span>
       <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="menu-item">
    <div class="menuItem">
      <xsl:variable name="href-noext">
        <xsl:call-template name="path-noext">
          <xsl:with-param name="path" select="@href"/>
        </xsl:call-template>
      </xsl:variable>

       <xsl:choose>
        <xsl:when test="$href-noext = $path-noext">
          <span class="menuSelected"><xsl:value-of select="@label"/></span>
        </xsl:when>
        <xsl:otherwise>
          <a href="{@href}"><xsl:value-of select="@label"/></a>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="external">
    <li>
       <xsl:choose>
        <xsl:when test="starts-with(@href, $filename-noext)">
         <span class="externalSelected"><xsl:value-of select="@label"/></span>
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
