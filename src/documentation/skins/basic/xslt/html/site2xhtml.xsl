<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
<xsl:template match="/">
<html>
 <head>
  <title><xsl:value-of select="/site/document/title"/></title>
  <link rel="stylesheet" type="text/css" href="skin/style.css" />
 </head>
 <body>
   <a href="@group-logo.href@"><img src="@group-logo.src@"/></a>
   <a href="@project-logo.href@"><img src="@project-logo.src@"/></a>
   
  <h1>
   <xsl:value-of select="/site/document/title"/>
  </h1>

    <hr/><xsl:copy-of select="/site/menu/node()|@*"/>
    <hr/><xsl:copy-of select="/site/document/body/node()|@*"/>

  <hr/><p><i>Copyright &#x00A9; @year@ @vendor@. All Rights Reserved.</i></p>
  <hr/>
  <a href="http://krysalis.org/"><img src="skin/images/krysalis-compatible.jpg" alt="Krysalis Logo"/></a> 
  <a href="http://xml.apache.org/cocoon/"><img src="skin/images/built-with-cocoon.gif" alt="Cocoon Logo"/></a> 
  <a href="http://jakarta.apache.org/ant/"><img src="skin/images/ant_logo_medium.gif" alt="Ant Logo"/></a> 
 
 </body>
</html>
</xsl:template>
</xsl:stylesheet>
