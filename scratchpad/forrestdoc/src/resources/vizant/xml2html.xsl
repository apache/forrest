<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method = "html" />

	<xsl:template match="graph">
      <html> 
       <head>
         <meta http-equiv="content-type" content="text/html; charset=ISO8859-1" />
         <title>Vizant - Ant task to visualize buildfiles</title>
         <link rel="stylesheet" href="main.css" media="all" />
        </head>
        <body>
         <h1><xsl:value-of select="@name" /></h1>
         <xsl:apply-templates />
        </body>
      </html>
	</xsl:template>
<!--
	<xsl:template match="attributes">
	 <h3><xsl:value-of select="@type" /></h3> 
       <xsl:apply-templates select="attribute"/>
	</xsl:template>
	
	<xsl:template match="attribute">
	  <xsl:value-of select="@name" />=<xsl:value-of select="@value" /><br />
	</xsl:template>
-->		
	<xsl:template match="node">
	   <h2><a name="{@id}"><xsl:value-of select="@id" /></a></h2>
	</xsl:template>
	
	<xsl:template match="edge">
	  <xsl:value-of select="@from" /> -> <a href="#{@to}"><xsl:value-of select="@to" /></a><br />
	</xsl:template>

	<xsl:template match="subgraph">
  	 <h2>subgraph  
  	   <if select="@numcluster">
  	             "cluster:<xsl:value-of select="@numcluster" />"
  	          </if></h2>
  	    <h3>label"="<xsl:value-of select="@label" /></h3>
         <xsl:apply-templates />  	     
	</xsl:template>
   
</xsl:stylesheet>