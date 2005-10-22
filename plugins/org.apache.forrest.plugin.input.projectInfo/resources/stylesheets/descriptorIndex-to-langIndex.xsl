<?xml version="1.0" encoding="UTF-8"?>
<!--
  Convert the descriptor index to a list of projects using a speciic language.
-->
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  xmlns:doap="http://usefulinc.com/ns/doap#">
  
  <xsl:param name="language"/>
  
  <xsl:template match="descriptors">
    <html>
      <head>
        <title>Index of Projects using the <xsl:value-of select="$language"/> Programming language</title>
      </head>
      <body>
        <h1>Index of Projects using the <xsl:value-of select="$language"/> Programming language</h1>
        <ul>
          <xsl:for-each select="//doap:Project[doap:programming-language = $language]/doap:name">
            <li>
              <a>
                <xsl:attribute name="href">/projectDetails.<xsl:value-of select="."/>.html</xsl:attribute>
                <xsl:value-of select="."/>
              </a>
            </li>
          </xsl:for-each>
        </ul>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>

