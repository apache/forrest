<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href = "corner-imports.svg.xslt" />

  <!-- Rounded corner -->
  <xsl:template name="figure">
		<g transform="translate(0.5 0.5)">
			<ellipse cx="{$smallersize}" cy="{$smallersize}" rx="{$smallersize}" ry="{$smallersize}"
				 style="{$fill}{$stroke}stroke-width:1"/>
		</g>
  </xsl:template>
      
</xsl:stylesheet>

