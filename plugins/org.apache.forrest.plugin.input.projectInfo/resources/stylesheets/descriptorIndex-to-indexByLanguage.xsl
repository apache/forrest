<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  xmlns:doap="http://usefulinc.com/ns/doap#">
  
  <xsl:key name="kDistinctProgLang" match="doap:programming-language" use="."/>
  
  <xsl:param name="language"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>Projects Indexed by Language</title>
      </head>
      <body>
        <xsl:choose>
          <xsl:when test="$language">
            <h1>Index of projects using <xsl:value-of select="$language"/></h1>
            <ul>
              <xsl:apply-templates select="//descriptor[descendant::doap:programming-language = $language]"/>
            </ul>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="//doap:programming-language[generate-id() = generate-id(key('kDistinctProgLang',.))]">
              <xsl:sort select="."/>
              <xsl:variable name="language" select="."/>
              <h1><xsl:value-of select="$language"/></h1>
              <ul>
                <xsl:apply-templates select="//descriptor[descendant::doap:programming-language = $language]"/>
              </ul>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>
  
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
      
    <li>
      <a>
        <xsl:attribute name="href"><xsl:value-of select="@href-noext"/>.html</xsl:attribute>
        <xsl:value-of select="$name"/>
      </a>
    </li>
  </xsl:template>
</xsl:stylesheet>

