<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- This is not used by Forrest but makes it possible to debug the 
       stylesheet in standalone editors -->
  <xsl:output method = "text"  omit-xml-declaration="yes"  />

<!--
  If the skin doesn't override this, at least aural styles 
  and extra-css are present 
-->
  <xsl:template match="skinconfig">
/* $Id: forrest.css.xslt,v 1.2 2004/01/13 08:19:53 nicolaken Exp $ */

   <xsl:call-template name="aural"/>
   <xsl:call-template name="a-external"/>
   <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="colors">
   <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template name="aural">

/* ==================== aural ============================ */

@media aural {
  h1, h2, h3, h4, h5, h6 { voice-family: paul, male; stress: 20; richness: 90 }
  h1 { pitch: x-low; pitch-range: 90 }
  h2 { pitch: x-low; pitch-range: 80 }
  h3 { pitch: low; pitch-range: 70 }
  h4 { pitch: medium; pitch-range: 60 }
  h5 { pitch: medium; pitch-range: 50 }
  h6 { pitch: medium; pitch-range: 40 }
  li, dt, dd { pitch: medium; richness: 60 }
  dt { stress: 80 }
  pre, code, tt { pitch: medium; pitch-range: 0; stress: 0; richness: 80 }
  em { pitch: medium; pitch-range: 60; stress: 60; richness: 50 }
  strong { pitch: medium; pitch-range: 60; stress: 90; richness: 90 }
  dfn { pitch: high; pitch-range: 60; stress: 60 }
  s, strike { richness: 0 }
  i { pitch: medium; pitch-range: 60; stress: 60; richness: 50 }
  b { pitch: medium; pitch-range: 60; stress: 90; richness: 90 }
  u { richness: 0 }
  
  :link { voice-family: harry, male }
  :visited { voice-family: betty, female }
  :active { voice-family: betty, female; pitch-range: 80; pitch: x-high }
}
  </xsl:template>
  
  <xsl:template name="a-external">
a.external  {
        padding: 0 20px 0px 0px;
	display:inline; display:inline-block;
        background-repeat: no-repeat;
	background-position: center right;
	background-image: url(/skin/images/external-link.gif);
}
  </xsl:template>
  
  <xsl:template match="extra-css">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="*"></xsl:template>
  <xsl:template match="text()"></xsl:template>

</xsl:stylesheet>