<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="section/document">
      <title><xsl:value-of select="header/title"/></title>
      <xsl:copy-of select="body/node()" />
  </xsl:template>

  <xsl:include href="copyover.xsl"/>

</xsl:stylesheet>
