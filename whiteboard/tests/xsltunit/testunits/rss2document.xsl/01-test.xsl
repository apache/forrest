<?xml version="1.0" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsltu="http://xsltunit.org/0/"
>

  <xsl:import href="cocoon://code/xsltunit.xsl"/>

  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <xsl:call-template name="xsltu:assertEqual">
      <xsl:with-param name="id" select="'Title for the channel'"/>
      <xsl:with-param name="nodes1" select="."/>
      <xsl:with-param name="nodes2"  select="document('01-output.xml')"/>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
