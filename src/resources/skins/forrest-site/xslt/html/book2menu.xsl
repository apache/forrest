<?xml version="1.0"?>
<!--
book2menu.xsl generates the HTML menu. It outputs XML/HTML of the form:
  <div class="menu">
     ...
  </div>
which is then merged with other HTML by site2xhtml.xsl

$Id: book2menu.xsl,v 1.5 2002/11/02 10:09:39 jefft Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="path"/>

  <xsl:include href="pathutils.xsl"/>
  <xsl:variable name="filename">
    <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
 
  <xsl:template match="book">
    <div class="menu">
      <ul>
        <xsl:apply-templates select="menu"/>
      </ul>
    </div>
  </xsl:template>
  <xsl:template match="menu">
    <li>
      <font color="#CFDCED"><xsl:value-of select="@label"/></font>
      <ul>
        <xsl:apply-templates/>
      </ul>
    </li>
  </xsl:template>
  <xsl:template match="menu-item">
    <li>
       <xsl:choose>
         <xsl:when test="starts-with(@href, $filename)">
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
         <xsl:when test="starts-with(@href, $filename)">
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
