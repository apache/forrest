<?xml version="1.0"?>
<!--
book2menu.xsl generates the HTML menu. It outputs XML/HTML of the form:
  <div class="menu">
     ...
  </div>
which is then merged with other HTML by site2xhtml.xsl

$Id: book2menu.xsl,v 1.2 2002/11/16 23:01:48 jefft Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="menu">
    <div class="menu">
      <xsl:call-template name="base-menu"/>
    </div>
  </xsl:template>

  <xsl:template name="base-menu">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:param name="path"/>

  <xsl:include href="pathutils.xsl"/>

  <xsl:variable name="filename-noext">
    <xsl:call-template name="filename-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:template match="book">
    <xsl:apply-templates select="menu"/>
  </xsl:template>

  <xsl:template match="menu-item">
    <xsl:choose>
      <xsl:when test="starts-with(@href, $filename-noext)">
        <xsl:call-template name="selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="unselected"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="selected">
    <xsl:value-of select="@label"/>
  </xsl:template>

  <xsl:template name="unselected">
    <a href="{@href}"><xsl:value-of select="@label"/></a>
  </xsl:template>

  <xsl:template name="print-external">
    <xsl:value-of select="@label"/>
  </xsl:template>

  <xsl:template match="external">
    <li>
      <xsl:choose>
        <xsl:when test="starts-with(@href, $filename-noext)">
          <span class="externalSelected">
            <xsl:call-template name="print-external"/>
          </span>
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
