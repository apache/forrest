<?xml version="1.0"?>
<!--
This stylesheet selects a set of nodes with @tab equal to that of a node whose @href matches an input parameter.  Could
probably be done with 2 lines of XQuery.

Jeff Turner <jefft@apache.org>
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:l="http://apache.org/forrest/linkmap/1.0">

  <xsl:output indent="yes"/>

  <xsl:param name="path" select="'index'"/>

  <xsl:variable name="tab">
    <xsl:value-of select="string(//*[starts-with(@href, $path)]/@tab)"/>
  </xsl:variable>

  <xsl:template match="/*">
    <xsl:message>## path is <xsl:value-of select="$path"/></xsl:message>
    <xsl:message>## tab is <xsl:value-of select="$tab"/></xsl:message>
    <xsl:copy>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="*">
    <xsl:choose>
      <!-- Take out the first test to not duplicate other tabs' content in first menu -->
      <xsl:when test="$tab='' or @tab=$tab">
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="*"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
