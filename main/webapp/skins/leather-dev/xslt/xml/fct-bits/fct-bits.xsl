<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="contract" mode="xhtml-head">
  <xsl:comment>function name=<xsl:value-of select="@name"/> HEAD</xsl:comment>
  <xsl:copy-of select="xhtml/head/*"/>
</xsl:template>

<xsl:template match="contract" mode="xhtml-body">
  <xsl:comment>function name=<xsl:value-of select="@name"/> BODY</xsl:comment>
  <xsl:copy-of select="xhtml/body/*"/>
</xsl:template>
</xsl:stylesheet>
