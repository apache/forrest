<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  xmlns:doap="http://usefulinc.com/ns/doap#">
  
  <xsl:template match="descriptor">
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="descendant::doap:Project/doap:shortname">
          <xsl:value-of select="descendant::doap:Project/doap:shortname"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="descendant::doap:Project/doap:name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
      
    <dl>
      <dt>
        <a>
          <xsl:attribute name="href"><xsl:value-of select="@href-noext"/>.html</xsl:attribute>
          <xsl:value-of select="$name"/>
        </a>
      </dt>
      <dd>
        <xsl:choose>
          <xsl:when test="descendant::doap:Project/doap:shortdesc">
            <xsl:value-of select="descendant::doap:Project/doap:shortdesc"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="descendant::doap:Project/doap:description"/>
          </xsl:otherwise>
        </xsl:choose>
      </dd>
    </dl>
  </xsl:template>
</xsl:stylesheet>

