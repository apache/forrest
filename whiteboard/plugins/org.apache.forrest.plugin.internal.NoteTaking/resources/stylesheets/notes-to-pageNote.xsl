<?xml version='1.0'?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dyn="http://exslt.org/dynamic">
  
  <!-- the page for which we want to get the notes -->
  <xsl:param name="path"/>
  
  <xsl:template match="notes">
    <xsl:apply-templates select="dyn:evaluate($path)"/>
  </xsl:template>
  
  <xsl:template match="note">
    <note>
      <xsl:apply-templates/>
    </note>
  </xsl:template>
</xsl:stylesheet>
