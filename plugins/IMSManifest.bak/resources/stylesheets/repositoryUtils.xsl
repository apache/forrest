<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
PathUtils.xsl

A set of XSLT templates useful for parsing Burrokeet URI's

-->

<xsl:param name="cmdRepositoryGetSCO">http://repository.burrokeet.org/getSCO/</xsl:param>

<!-- Return the burrokeet command in a path that includes one -->
<xsl:template name="getRepositoryCommand">
  <xsl:param name="path"/>
  <xsl:if test="contains($path, $cmdRepositoryGetSCO)">getSCO</xsl:if>
</xsl:template>

<!-- return the name of the SCO too use -->
<xsl:template name="getSCOName">
  <xsl:param name="path"/>
  <xsl:value-of select="substring-after($path, $cmdRepositoryGetSCO)"/>
</xsl:template>


</xsl:stylesheet>
