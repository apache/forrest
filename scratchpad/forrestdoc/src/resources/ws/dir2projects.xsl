<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method = "html" encoding="Windows-1252" />
	
	<xsl:template match="/">	
<html><head><link rel="stylesheet" type="text/css" href="styles.css"/></head><body>
<h3 class="packageListItem">Projects</h3>
<p class="packageListItem"><A HREF="projects-summary.html" TARGET="projects-summaryFrame">Summary</A></p>
<br/>
			
			  <xsl:apply-templates/>
</body></html>
	</xsl:template>
	
	<xsl:template match="pathelement">	
       <xsl:variable name="dir" select="substring(.,2)" />
       <xsl:variable name="url" select="concat(substring(.,2),'/index.html')" />
         <p class="packageListItem"><a href="{$url}" TARGET="projects-summaryFrame"><xsl:value-of select="$dir" /></a></p>
    </xsl:template>
	
</xsl:stylesheet>