<?xml version="1.0"?>

<!--
This stylesheet strips the 'file:' prefix from HTML links.
It should be applied after document2html.xsl.
This stripping cannot be done in document2html.xsl, because the 'links' view is
based on the unstripped document2html output.
-->

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="1.0">

  <xsl:template match="@src|@href|@background">
    <xsl:choose>
      <xsl:when test="starts-with(., 'file:')">
        <xsl:attribute name="{name()}">
          <xsl:value-of select="substring(., 6)"/>
        </xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="."/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|node()">
   <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
  </xsl:template>


</xsl:stylesheet>
