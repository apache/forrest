<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:lm="http://apache.org/forrest/locationmap/1.0"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0">

  <xsl:template match="lm:locationmap">
    <descriptors>
      <xsl:apply-templates/>
    </descriptors>
  </xsl:template>
  
  <xsl:template match="lm:locator/lm:match[starts-with(@pattern, 'project.descriptor')]">
    <xsl:variable name="href-noext">
      <xsl:choose>
        <xsl:when test="@pattern = 'project.descriptor'">/projectDetails</xsl:when>
        <xsl:otherwise>/projectDetails.<xsl:value-of select="substring-after(@pattern, 'project.descriptor.')"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <descriptor>
      <xsl:attribute name="id"><xsl:value-of select="@pattern"/></xsl:attribute>
      <xsl:attribute name="href-noext"><xsl:value-of select="$href-noext"/></xsl:attribute>
      <cinclude:include>
        <xsl:attribute name="src">cocoon:<xsl:value-of select="$href-noext"/>.source.xml</xsl:attribute>
      </cinclude:include>
    </descriptor>
  </xsl:template>
</xsl:stylesheet>

