<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xslt="xslt"
>

<xsl:namespace-alias stylesheet-prefix="xslt" result-prefix="xsl"/>

<xsl:param name="id"/>
<xsl:param name="xslt"/>
<xsl:param name="output"/>

<xsl:template match="/*">
  <xslt:stylesheet version="1.0" xmlns:xsltu="http://xsltunit.org/0/">
    <xslt:import href="cocoon://test/xsltunit.xsl"/>
    <xslt:import href="{$xslt}"/>
    <xslt:strip-space elements="*"/>
    <xsl:apply-templates select="parameter"/>
    <xslt:template match="/">
      <xslt:call-template name="xsltu:assertEqual">
        <xslt:with-param name="id" select="'{$id}: {@name}'"/>
        <xslt:with-param name="nodes1"><xslt:apply-imports/></xslt:with-param>
        <xslt:with-param name="nodes2" select="document('{$output}')"/>
      </xslt:call-template>
    </xslt:template>
  </xslt:stylesheet>	
</xsl:template>

<xsl:template match="parameter">
	<xslt:param name="{@name}" select="'{@value}'"/>
</xsl:template>

</xsl:stylesheet>
