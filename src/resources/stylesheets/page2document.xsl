<?xml version="1.0"?>

<!--+
    | Transforms ServerPages Generator output to document format.
    |
    | CVS $Id: page2document.xsl,v 1.1 2003/09/10 20:21:45 cheche Exp $
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="copyover.xsl"/>

  <xsl:template match="page">
    <document>
    <xsl:apply-templates />
    </document>
  </xsl:template>
  
  <xsl:template match="title">
    <header>
    <title><xsl:value-of select="." /></title>
    </header>
  </xsl:template>
  
  <xsl:template match="content">
    <body>
    <xsl:apply-templates />
    </body>
  </xsl:template>
  
  <xsl:template match="para">
    <p>
    <xsl:apply-templates />
    </p>
  </xsl:template>
 
</xsl:stylesheet>
