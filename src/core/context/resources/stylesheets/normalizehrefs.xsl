<?xml version="1.0"?>
<!--
Stylesheet to normalize the paths of href attributes, e.g.
href="somedir/../someotherdir/index.html" ==> href="someotherdir/index.html"
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

  <xsl:include href="../../skins/common/xslt/html/pathutils.xsl" />
  <xsl:include href="copyover.xsl" />
  
  <xsl:template match="@href">
    <xsl:if test="normalize-space(.)!=''">
      <xsl:attribute name="href">
	      <xsl:call-template name="normalize" >
	        <xsl:with-param name="path" select="." />
      	</xsl:call-template>
      </xsl:attribute>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
