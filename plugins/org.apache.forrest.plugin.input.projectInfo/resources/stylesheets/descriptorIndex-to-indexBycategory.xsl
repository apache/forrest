<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  xmlns:doap="http://usefulinc.com/ns/doap#">
  
  <xsl:include href="descriptorIndex-to-indexByCommon.xsl"/>
  
  <xsl:key name="kDistinctCategory" match="doap:category" use="@rdf:resource"/>
  
  <xsl:param name="category"/>
  <xsl:param name="categoryURL">http://projects.apache.org/category/</xsl:param>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>Projects Indexed by Category</title>
      </head>
      <body>
        <xsl:choose>
          <xsl:when test="$category">
            <h1>Index of projects in the category of <xsl:value-of select="$category"/></h1>
            <xsl:apply-templates select="//descriptor[descendant::doap:category/@rdf:resource = concat($categoryURL, $category)]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="//doap:category[generate-id() = generate-id(key('kDistinctCategory', @rdf:resource))]">
              <xsl:sort select="@rdf:resource"/>
              <xsl:variable name="category" select="@rdf:resource"/>
              <h1><xsl:value-of select="substring-after($category, $categoryURL)"/></h1>
              <xsl:apply-templates select="//descriptor[descendant::doap:category[@rdf:resource = $category]]"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
        
        <xsl:if test="//descriptor[not(descendant::doap:category)]">
          <h1>Uncategorised</h1>
          <xsl:apply-templates select="//descriptor[not(descendant::doap:category)]"/>
        </xsl:if>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>

