<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method = "html" encoding="Windows-1252" />
	
	<xsl:template match="/">	
<html><head>
<link rel="stylesheet" type="text/css" href="styles.css"/></head><body>
<h2>Forrestdoc run for Gump Workspace</h2>
<table class="summary">
<thead>
<tr>
<th>Projects</th>
</tr>
</thead>
<tbody>
			
			  <xsl:apply-templates/>
</tbody>
</table>


          <hr></hr>Made with Apache ForrestDoc.
</body></html>
	</xsl:template>
	
	<xsl:template match="pathelement">	
       <xsl:variable name="dir" select="substring(.,2)" />
       <xsl:variable name="url" select="concat(substring(.,2),'/index.html')" />
       <tr><td><a href="{$url}"><xsl:value-of select="$dir" /></a></td></tr>
    </xsl:template>
	
</xsl:stylesheet>