<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- FIXME: not yet dealing with <tabs>
-->

<xsl:template match="site">
<html>
 <head>
  <title><xsl:value-of select="div[@class='header']/h1"/></title>
  <link rel="stylesheet" href="skin/style.css" type="text/css"/>
 </head>
 <body>
  <p>
   <a href="@group-logo.href@"><img src="@group-logo.src@" width="220" height="65" alt="@group-logo.alt@" /></a>
   <a href="@project-logo.href@"><img src="@project-logo.src@" width="220" height="65" alt="@project-logo.alt@" /></a>
  </p>
  <h1><xsl:value-of select="div[@class='header']/h1"/></h1>
<!--FIXME: what to do with the sub-heading -->
  <h3><xsl:value-of select="div[@class='header']/h3"/></h3>
  <p>
   <xsl:copy-of select="menu/node()|@*"/>
  </p>
  <hr/>
  <xsl:apply-templates select="div[@class='content']"/>
  <hr/>
  <p><i>Copyright &#x00A9; @year@ @vendor@. All Rights Reserved.</i></p>
  <hr/>
 </body>
</html>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
