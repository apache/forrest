<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- This is not used by Forrest but makes it possible to debug the 
       stylesheet in standalone editors -->
  <xsl:output method = "text"  omit-xml-declaration="yes"  />
  
  <xsl:template match="colors">
   <xsl:apply-templates/>
  </xsl:template>

<!-- ==================== main block colors ============================ -->
<xsl:template match="color[@name='header']">
.header         { background-color: <xsl:value-of select="@value"/>;} </xsl:template>

<xsl:template match="color[@name='tab-selected']"> 
.tab.selected      { background-color: <xsl:value-of select="@value"/>;} 
.border         { background-color: <xsl:value-of select="@value"/>;} 
.menu           { border-color: <xsl:value-of select="@value"/>;}</xsl:template> 
<xsl:template match="color[@name='tab-unselected']"> 
.tab.unselected      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='subtab-selected']">
<!-- subtab-selected unused -->
</xsl:template> 
<xsl:template match="color[@name='subtab-unselected']">
.level2tabstrip { background-color: <xsl:value-of select="@value"/>;}
.datenote { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 

<xsl:template match="color[@name='heading']">
.heading { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='subheading']">
.subheading { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 

<xsl:template match="color[@name='navstrip']">
.subborder      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='toolbox']">
<!-- toolbox unused -->
</xsl:template> 

<xsl:template match="color[@name='menu']">
.menu      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='dialog']"> 
.dialog      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 

<xsl:template match="color[@name='body']">
body         { background-color: <xsl:value-of select="@value"/>;} </xsl:template>
<xsl:template match="color[@name='footer']"> 
.footer      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 


<!-- ==================== other colors ============================ -->
<xsl:template match="color[@name='highlight']"> 
.highlight        { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='fixme']"> 
.fixme        { border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='note']"> 
.note         { border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='warning']"> 
.warning         { border-color: <xsl:value-of select="@value"/>;} </xsl:template>
<xsl:template match="color[@name='code']"> 
.code         { border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='table']"> 
.content .ForrestTable      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='table-cell']"> 
.content .ForrestTable td   { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 


  <xsl:template match="skinconfig">
/* $Id: forrest.css.xslt,v 1.1 2004/01/03 15:36:08 nicolaken Exp $ */
   <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="extra-css">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="*"></xsl:template>
  <xsl:template match="text()"></xsl:template>

</xsl:stylesheet>