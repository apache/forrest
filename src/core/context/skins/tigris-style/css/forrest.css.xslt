<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method = "text"  omit-xml-declaration="yes"  />
    
  <xsl:template match="skinconfig">
  <!-- insert CSS here -->
/* $Id: forrest.css.xslt,v 1.1 2003/12/30 00:04:35 nicolaken Exp $ */

/* colors */

/* TODO */
#toptabs td, #toptabs th {
	background-image: url(../images/nw_min_036.gif);
}

.app h3, #banner, #banner td, #toptabs {
	background-color: #036;
	color: #fff;
}

body #banner td a, .app h3 a, .app h4 a {
	color: #fff !important;
}

#banner {
	border-top: 1px solid #369;
}

#mytools .label, #projecttools .label, #admintools .label, #communitytools .label {
	background-color: #ddd;
}

#mytools .body, #projecttools .body, #admintools .body, #communitytools .body {
	background-color: #fff;
	border-top: 1px solid #999;
}

#mytools, #projecttools, #admintools, #communitytools {
	background-color: #ddd;
	border-right: 1px solid #666;
	border-bottom: 1px solid #666;
}

#helptext {
	background-color: #ffc;
}

#helptext .label {
	border-bottom: 1px solid #996;
	border-right: 1px solid #996;
	background-color: #cc9;
}

#helptext .body {
	border-bottom: 1px solid #cc9;
	border-right: 1px solid #cc9;
}

#topmodule {
	background-color: #ddd;
	border-top: 1px solid #fff;
	border-bottom: 1px solid #aaa;
}

#topmodule #issueid {
	border-right: 1px solid #aaa;
}

#login a:link, #login a:visited {
	color: white;
}

#banner a:active, #banner a:hover {
	color: #f90 !important;
}

#toptabs td {
	border-bottom: 1px solid #666;
	border-right: 1px solid #333;
	border-left: 1px solid #036;
}

#toptabs th {
	border-left: 1px solid #036;
}
     
  <!-- end CSS here -->
  </xsl:template>

  <xsl:template match="*"></xsl:template>
  <xsl:template match="text()"></xsl:template>

</xsl:stylesheet>