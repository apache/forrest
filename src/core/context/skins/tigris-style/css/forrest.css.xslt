<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- This is not used by Forrest but makes it possible to debug the 
       stylesheet in standalone editors -->
  <xsl:output method = "text"  omit-xml-declaration="yes"  />

  <xsl:template match="colors">
   <xsl:apply-templates/>
  </xsl:template>

<xsl:template match="group-logo">
  <xsl:if test="'@width' and '@height'">
#cn {
	background-image: url('../<xsl:value-of select="."/>');
	display: block;
	height: <xsl:value-of select="@height"/>px;
	width: <xsl:value-of select="@width"/>px;
}
  </xsl:if>
</xsl:template>

<xsl:template match="host-logo">
  <xsl:if test="'@width' and '@height'">
#poweredby {
	background-image: url('../<xsl:value-of select="."/>');
	display: block;
	height: <xsl:value-of select="@height"/>px;
	width: <xsl:value-of select="@width"/>px;
}
  </xsl:if>
</xsl:template>

<xsl:template match="project-logo">
  <xsl:if test="'@width' and '@height'">
#sc {
	background-image: url('../<xsl:value-of select="."/>');
	display: block;
	height: <xsl:value-of select="@height"/>px;
	width: <xsl:value-of select="@width"/>px;
}
  </xsl:if>
</xsl:template>


<xsl:template match="color[@name='header']">
#banner, #banner td, #toptabs { background-color: <xsl:value-of select="@value"/>;} </xsl:template>

<xsl:template match="color[@name='tab-selected']"> 
.tabs th      { background-color: <xsl:value-of select="@value"/>;} 
#mytools .label, #projecttools .label, #admintools .label, #communitytools .label {
	background-color: <xsl:value-of select="@value"/>;}
#mytools, #projecttools, #admintools, #communitytools {
	background-color: <xsl:value-of select="@value"/>;}
.tabs {	border-bottom: 6px <xsl:value-of select="@value"/> solid; }</xsl:template> 
<xsl:template match="color[@name='tab-unselected']"> 
.tabs td      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='subtab-selected']"> 
#breadcrumbs {background-color: <xsl:value-of select="@value"/>;}</xsl:template> 
<xsl:template match="color[@name='subtab-unselected']"> 
#breadcrumbs {background-color: <xsl:value-of select="@value"/>;}</xsl:template> 

<xsl:template match="color[@name='heading']"> 
.app h3 {background-color: <xsl:value-of select="@value"/>;}</xsl:template> 
<xsl:template match="color[@name='subheading']"> 
.app h4  { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 

<xsl:template match="color[@name='navstrip']"> 
#topmodule    { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='toolbox']"> 
.toolgroup .label {	background: <xsl:value-of select="@value"/>; } </xsl:template> 

<xsl:template match="color[@name='menu']"> 
#mytools .body, #projecttools .body, #admintools .body, #communitytools .body {
	background-color: <xsl:value-of select="@value"/>;}</xsl:template> 
<xsl:template match="color[@name='dialog']"> 
.toolgroup { background: <xsl:value-of select="@value"/>;}    </xsl:template> 

<xsl:template match="color[@name='body']">
body         { background-color: <xsl:value-of select="@value"/>;} </xsl:template>

<xsl:template match="color[@name='footer']"> 
#footer      { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 


<!-- ==================== other colors ============================ -->
<xsl:template match="color[@name='highlight']"> 
.highlight        { background-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='fixme']"> 
.warningmessage {border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='note']"> 
.infomessage {  border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='warning']"> 
.errormessage{  border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='code']"> 
.code, code, pre        { border-color: <xsl:value-of select="@value"/>;} </xsl:template> 
<xsl:template match="color[@name='table']"> 
body .grid td {
	border-top: 1px solid <xsl:value-of select="@value"/>;
	border-left: 1px solid <xsl:value-of select="@value"/>;} 
.app th {
	background-color: <xsl:value-of select="@value"/>;}</xsl:template> 
<xsl:template match="color[@name='table-cell']"> 
body .grid td {
	background-color: <xsl:value-of select="@value"/>;
} </xsl:template> 



  <xsl:template match="skinconfig">
/* $Id: forrest.css.xslt,v 1.2 2004/01/03 15:36:29 nicolaken Exp $ */
   <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="extra-css">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="*"></xsl:template>
  <xsl:template match="text()"></xsl:template>

</xsl:stylesheet>