<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output indent="yes"/>

<xsl:template match="defaults">
  <wortel>
    <xsl:for-each select="*">
      <xsl:variable name="elementname">
        <xsl:value-of select="name()"/>
        <xsl:if test="@type">
          <xsl:text>.</xsl:text>
          <xsl:value-of select="@type"/>
        </xsl:if>
      </xsl:variable>
      <xsl:element name="{$elementname}">
        <xsl:for-each select="*">
          <xsl:element name="{name()}">
            <xsl:value-of select="@name"/>
          </xsl:element>
        </xsl:for-each> 
      </xsl:element>
    </xsl:for-each>
  </wortel>
</xsl:template>



</xsl:stylesheet>
