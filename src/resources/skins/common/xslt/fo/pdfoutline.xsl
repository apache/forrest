<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY copy "&#169;">
<!ENTITY nbsp "&#160;">
<!ENTITY degr "&#186;">
<!ENTITY sup2 "&#178;">
<!ENTITY sup3 "&#179;">]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xml.apache.org/fop/extensions" xmlns:documentation="http://sealcorp.com.au/documentation">

<xsl:template match="document" mode="outline">
  <fox:outline internal-destination="{generate-id()}">
    <fox:label>
      <xsl:value-of select="header/title"/>
      <xsl:text> - </xsl:text>
      <xsl:value-of select="header/authors/person/@name"/>
    </fox:label>
    <xsl:apply-templates select="body/section" mode="outline"/>
  </fox:outline>
</xsl:template>

<xsl:template match="section" mode="outline">
  <fox:outline internal-destination="{generate-id()}">
    <fox:label>
      <xsl:value-of select="title"/>
    </fox:label>
    <xsl:apply-templates select="section" mode="outline"/>
  </fox:outline>
</xsl:template>

</xsl:stylesheet>
