<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
     <xsl:output method="xml" media-type="image/svg" omit-xml-declaration="yes" indent="yes"/>

  <!-- the skinconf file -->
  <xsl:param name="config-file" select="'../../../../skinconf.xml'"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>
  <!-- Get the section depth to use when generating the minitoc (default is 2) -->
  <xsl:variable name="toc-max-depth" select="number($config/toc/@level)"/>

  <xsl:param name="numbersections" select="'true'"/>

  <!-- Section depth at which we stop numbering and just indent -->
  <xsl:param name="numbering-max-depth" select="'3'"/>
  <xsl:param name="ctxbasedir" select="."/>
  <xsl:param name="xmlbasedir"/>

  <xsl:template match="/">
     <svg width="1305" height="1468" xmlns="http://www.w3.org/2000/svg">
      <g transform="translate(0 0)">
        <xsl:apply-templates/>
      </g>
     </svg>
  </xsl:template>

  <xsl:template match="document">
    <text x="00px" y="30px" style="font-size:20;"><xsl:value-of select="header/title"/></text>
  	<text x="0px" y="50px" style="font-size:12;">
        <xsl:apply-templates/>
  	</text>
  </xsl:template>

                      
</xsl:stylesheet>
