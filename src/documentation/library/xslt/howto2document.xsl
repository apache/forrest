<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

 <xsl:import href="copyover.xsl"/>

  <xsl:template match="howto">
   <document>
     <xsl:copy-of select="header"/>
     <body><xsl:apply-templates select="*[not(name()='header')]"/></body>
   </document>
  </xsl:template>
  
  <xsl:template match="overview | purpose | prerequisites | audience | steps | extension | tips | references">
    <section>
      <xsl:choose>
        <xsl:when test="normalize-space(@title)!=''">
          <xsl:attribute name="title"><xsl:value-of select="@title"/></xsl:attribute>
        </xsl:when>
        <xsl:otherwise>
          <xsl:attribute name="title"><xsl:value-of select="name()"/></xsl:attribute>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
</xsl:stylesheet>