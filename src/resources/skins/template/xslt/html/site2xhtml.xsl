<?xml version="1.0"?>
<!--
site2xhtml.xsl is the final stage in HTML page production.  It merges HTML from
document2html.xsl, tab2menu.xsl and book2menu.xsl, and adds the site header,
footer, searchbar, css etc.  As input, it takes XML of the form:

<site>
  <div class="menu">
    ...
  </div>
  <div class="tab">
    ...
  </div>
  <div class="content">
    ...
  </div>
</site>

$Id: site2xhtml.xsl,v 1.2 2003/02/10 06:46:03 jefft Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/site2xhtml.xsl"/>
 
 <!-- Override this template to define a new skin.  The contents of the
 overridden common site2xhtml file can be used as a template 

  <xsl:template match="site">
  ...
  </xsl:template>
  -->
 
</xsl:stylesheet>
