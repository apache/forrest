<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href = "corner-imports.svg.xslt" />
  
  <!-- Diagonal 45 degrees corner -->
  <xsl:template name="figure">
        <xsl:variable name="biggersize" select="number($size)+number($size)"/>     
		<g transform="translate(0 0.5)">
           <polygon points="0,{$size} {$size},0 {$biggersize},0 {$biggersize},{$biggersize} 0,{$biggersize}"
                    style="{$fill}{$stroke}stroke-width:1"/>
		</g>
  </xsl:template>
      
</xsl:stylesheet>

