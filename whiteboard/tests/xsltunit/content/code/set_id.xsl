<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:param name="id"/>

<xsl:template match="/*">
  <xsl:copy>
  	<xsl:copy-of select="@*"/>
    <xsl:attribute name="id">
      <xsl:value-of select="$id"/>
      <xsl:if test="@id">
        <xsl:value-of select="concat(': ', @id)"/>
      </xsl:if>
    </xsl:attribute>
    <xsl:copy-of select="node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
