<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:error="http://apache.org/cocoon/error/2.1"
>

<xsl:param name="element"/> <!-- Root element for test suites. -->
<xsl:param name="id"/> <!-- Name of the error suite. -->

<!-- Errors are translated into a testsuite, so they are visible in the report. -->
<xsl:template match="error:notify">
  <xsl:element name="{$element}">
    <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
    <xsl:attribute name="error"><xsl:value-of select="error:title"/></xsl:attribute>
    <xsl:apply-templates select="@*|node()|text()"/>
  </xsl:element>
</xsl:template>

<!-- Throw away extra error information. -->
<xsl:template match="error:extra">
</xsl:template>

<!-- Keep all other elements, process content. -->
<xsl:template match="@*|*">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()|text()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()">
  <xsl:value-of select="normalize-space()"/>
</xsl:template>

</xsl:stylesheet>
