<?xml version="1.0"?>
<!--
Stylesheet to inherit @tab attributes if a node doesn't have one itself. Eg, given as input:

<site href="">
  <index href="index.html"/>
  <community href="community/" tab="community">
    <faq href="faq.html">
      <how_can_I_help href="#help"/>
    </faq>
    <howto tab="howto">
      <cvs href="cvs-howto.html"/>
    </howto>
  </community>
</site>

Output would be:

<site href="">
  <index href="index.html"/>
  <community tab="community" href="community/">
    <faq tab="community" href="faq.html">
      <how_can_I_help tab="community" href="#help"/>
    </faq>
    <howto tab="howto">
      <cvs tab="howto" href="cvs-howto.html"/>
    </howto>
  </community>
</site>

Jeff Turner <jefft@apache.org>
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:l="http://apache.org/forrest/linkmap/1.0">

  <!-- Return a value for a node's @tab, either using an existing @tab or the first ancestor's -->
  <xsl:template name="gettab">
    <xsl:param name="node"/>
    <xsl:choose>
      <xsl:when test="$node/@tab">
        <xsl:value-of select="$node/@tab"/>
      </xsl:when>
      <xsl:when test="$node/..">
        <xsl:call-template name="gettab">
          <xsl:with-param name="node" select="$node/.."/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="//*">
    <xsl:variable name="newtab">
      <xsl:call-template name="gettab">
        <xsl:with-param name="node" select="."/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:copy>
      <!-- <xsl:if test="not(normalize-space($newtab)='')"> -->
        <xsl:attribute name="tab">
          <xsl:value-of select="$newtab"/>
        </xsl:attribute>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="@*|node()" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
