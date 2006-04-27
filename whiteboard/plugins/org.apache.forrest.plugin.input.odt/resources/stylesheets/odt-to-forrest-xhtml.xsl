<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns="http://www.w3.org/1999/xhtml" 
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:dc="http://purl.org/dc/elements/1.1/" >
  <xsl:import href="lm://transform.odt.xhtml"/>
  <xsl:param name="filename" select="'odt file name'"/>
  <xsl:template match="/">
    <xsl:apply-templates select="/odt/content/*"/>
  </xsl:template>
  <xsl:template match="office:document-content">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>
          <xsl:choose>
            <!-- display title element from ODT File:Properties -->
            <xsl:when 
              test="//odt/meta/office:document-meta/office:meta/dc:title">
              <xsl:value-of 
                select="//odt/meta/office:document-meta/office:meta/dc:title"/>
            </xsl:when>
            <!-- if no title element, display 1st h1 -->
            <xsl:when 
              test="not(//odt/meta/office:document-meta/office:meta/dc:title) and
        office:body/office:text/text:h[1] != ''">
              <xsl:value-of select="office:body/office:text/text:h[1]"/>
            </xsl:when>
            <xsl:otherwise>
              <!-- if no title element or h1 elements, display $filename -->
              <xsl:value-of select="$filename"/>
            </xsl:otherwise>
          </xsl:choose>
        </title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <xsl:apply-templates select="office:automatic-styles"/>
      </head>
      <body>
        <xsl:apply-templates select="office:body/office:text"/>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>