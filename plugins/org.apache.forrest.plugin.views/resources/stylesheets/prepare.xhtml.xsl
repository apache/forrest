<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
  as applicable.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:forrest="http://apache.org/forrest/templates/1.0"  
  xmlns:xi="http://www.w3.org/2001/XInclude"
  xmlns:alias="http://www.w3.org/1999/XSL/TransformAlias"
  >
  <xsl:namespace-alias stylesheet-prefix="alias" result-prefix="xsl"/>
  <xsl:param name="request"/>
  <xsl:template match="/">
    <alias:stylesheet version="1.0">
      <alias:import href="cocoon:/commonSite2xhtml"/>
      <alias:import href="cocoon:/dotdots"/>
		  <alias:import href="cocoon:/pathutils"/>
		  <alias:import href="cocoon:/renderlogo"/>
      <alias:include href="cocoon:/prepare.include.{$request}"/>
			  <alias:param name="path"/>
			  <alias:param name="request"/>
        <alias:variable name="config" select="//skinconfig"/>
			  <alias:variable name="minitoc-location" select="$config/toc/@location"/>
        <!--FIXME: get all additional templates-->
<!--			<xsl:comment>All xhtml additional templates requested by the forrest:template</xsl:comment>
      <xsl:for-each select="/*/forrest:properties/*[@additional='true']">
        <xsl:copy-of select=""/>
      </xsl:for-each>-->
      <xsl:comment>All xhtml head elements requested by the forrest:template</xsl:comment>
			<alias:template name="getHead">
			  <xsl:for-each select="/*/forrest:properties/*[@head='true']">
		      <alias:call-template name="{@name}-head" />
			  </xsl:for-each>
		   </alias:template>
	    <xsl:comment>All xhtml css elements requested by the forrest:template</xsl:comment>
			<alias:template name="getCss">
			  <xsl:for-each select="/*/forrest:properties/*[@css='true']">
		      <alias:call-template name="{@name}-css" />
			  </xsl:for-each>
		   </alias:template>
		   	   
       <xsl:comment>All xhtml body elements requested by the forrest:template</xsl:comment>
			 <alias:template name="getBody">
          <xsl:apply-templates select="/*/forrest:view"/>
		   </alias:template>
       
       <alias:template match="site">
        <xhtml>
      		<head>
    				<alias:call-template name="getHead"/>
            <style type="text/css">
body {
	text-align:center;
	font-family: verdana, helvetica, sans;
	font-size: 8pt;
}
img {border:0;}
hr {border:0px; height: 1px; background-color:#ddd;}

/*============Container and branding=============*/
#container {
	width: 750px;
	text-align:left;
	margin: 0 auto 12px auto;
}
#branding {
	padding: 0;
	height: 75px;
	max-height: 75px;
	background: url(images/header-background.gif) transparent;
	background-repeat: no-repeat;
	position: relative;
}
h1 {font-size: 36pt}
h2 {color: blue}
p {margin-left: 50px}
#spacer {
    clear:both;
}
<alias:call-template name="getCss"/>
</style>
            <!--<link rel="stylesheet" href="{$root}skin/basic.css" type="text/css" 
                />-->
						<title>
                <alias:value-of select="div[@id='content']/h1"/>
            </title>
          </head>
          <body onload="init()">
            <alias:call-template name="getBody"/>
          </body>
         </xhtml>
       </alias:template>
      
		</alias:stylesheet>
	</xsl:template>
	
  <xsl:template match="forrest:view">
    <xsl:apply-templates/>
  </xsl:template>
	
	<xsl:template match="forrest:hook">
    <div id="{@name}">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="forrest:contract">
    <xsl:variable name="name" select="@name"/>
    
    <!--Test whether there is a body template needed-->
    <xsl:if test="/*/forrest:properties/*[@body='true' and @name=$name]">
      <!--If next son is not forrest:properties go on-->
      <xsl:choose>
        <xsl:when test="not(forrest:properties[@contract=$name])">
          <xsl:apply-templates/>
          <alias:call-template name="{@name}-body" />
        </xsl:when>
        <xsl:when test="forrest:properties[@contract=$name]">
          <alias:call-template name="{@name}-body" >
          <xsl:for-each select="forrest:properties[@contract=$name]/forrest:property">
            <alias:with-param name="{@name}" select=".//forrest:properties[@contract='{$name}']/forrest:property[@name='{@name}']" />
          </xsl:for-each>
          </alias:call-template>
        </xsl:when>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
