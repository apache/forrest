<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

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
<!--
site2xhtml.xsl is the final stage in HTML page production.  It merges HTML from
document2html.xsl, tab2menu.xsl and book2menu.xsl, and adds the site header,
footer, searchbar, css etc.  As input, it takes XML of the form:

<site>
  <div class="menu">
    ...
  </div>
  <div class="tab">
    ...
  </div>
  <div class="content">
    ...
  </div>
</site>

$Id: site2xhtml.xsl,v 1.19 2003/12/26 00:46:04 mroch Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/site2xhtml.xsl"/>
<!--+
	|Overall site template
	+-->
  <xsl:template match="site">
    <!--html lang="en" xml:lang="en"-->
    <html>
<!--+
	|HTML-head
	+-->
        <head>
<!--+
   	|generator meta
   	+-->
            <xsl:call-template name="html-meta"/>
<!--+
	|title
	+-->
            <title>
                <xsl:value-of select="div[@id='content']/h1"/>
            </title>
<!--+
	|stylesheets
	+-->
            <link rel="stylesheet" href="{$root}skin/basic.css" type="text/css" 
                />
            <link rel="stylesheet" href="{$root}skin/screen.css" 
                type="text/css" media="screen" />
            <link rel="stylesheet" href="{$root}skin/print.css" type="text/css" 
                media="print" />
            <link rel="stylesheet" href="{$root}skin/profile.css" 
                type="text/css" />
<!--+
	|favicon
	+-->
            <xsl:if test="//skinconfig/favicon-url">
                <link rel="shortcut icon">
                    <xsl:attribute name="href">
                        <xsl:value-of 
                            select="concat($root,//skinconfig/favicon-url)"/>
                    </xsl:attribute>
                </link>
            </xsl:if>
        </head>
<!--+
	|HTML-body
	+-->
      <body>
<!--+Site structure
 	 +++++++++++++++++++++++++++
     +=========================+
     |       topstrip          |
     +=========================+
     |                         |
     |       centerstrip       |
     |                         |
     |                         |
     +=========================+
     |       bottomstrip       |
     +=========================+
     +++++++++++++++++++++++++++
     +-->
<!--+
   	|topstrip with logo's and search box 
   	+-->
    <div id="top">
<!--breadcrumbs org location-->
<xsl:if test="not ($config/trail/@location='alt')">
<xsl:comment>+
    |breadtrail
    +</xsl:comment>
      <div class="breadtrail">
          <xsl:call-template name="breadcrumbs"/>
      </div>
</xsl:if>
<xsl:comment>+
    |header
    +</xsl:comment>
	  <div class="header">          
<xsl:comment>+
    |start group logo
    +</xsl:comment> 
        <xsl:if test="$config/group-url">
          <div class="grouplogo">
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="$config/group-name"/>
              <xsl:with-param name="url" select="$config/group-url"/>
              <xsl:with-param name="logo" select="$config/group-logo"/>
              <xsl:with-param name="root" select="$root"/>
              <xsl:with-param name="description" select="$config/group-description"/>
            </xsl:call-template>
          </div>
        </xsl:if>
<xsl:comment>+
    |end group logo
    +</xsl:comment>
<xsl:comment>+
    |start Project Logo
    +</xsl:comment> 
   <xsl:variable name="xtest">
       <xsl:choose>
                <xsl:when 
                    test="$config/group-url and $config/search and not($config/search/@box-location = 'alt')">
                     <xsl:text>true</xsl:text></xsl:when>
                <xsl:otherwise><xsl:text>false</xsl:text></xsl:otherwise>
             </xsl:choose>
   </xsl:variable>
		<div class="projectlogo">
            <xsl:if test="$xtest='false'" >
                    <xsl:attribute name="class">
                        <xsl:text>projectlogoA1</xsl:text>
                    </xsl:attribute>      
            </xsl:if>
          <xsl:call-template name="renderlogo">
            <xsl:with-param name="name" select="$config/project-name"/>
            <xsl:with-param name="url" select="$config/project-url"/>
            <xsl:with-param name="logo" select="$config/project-logo"/>
            <xsl:with-param name="root" select="$root"/>
            <xsl:with-param name="description" select="$config/project-description"/>
	      </xsl:call-template>
	    </div>
<xsl:comment>+
    |end Project Logo
    +</xsl:comment> 

        <xsl:if 
            test="$config/search and not($config/search/@box-location = 'alt')">
<xsl:comment>+
    |start Search
    +</xsl:comment> 
            <div class="searchbox">
                <form method="get" action="http://www.google.com/search"> 
                    <p><label for="query">Search the <xsl:value-of 
                    select="$config/searchsite-name"/> site:</label></p> <input 
                    type="hidden" name="sitesearch" 
                    value="{$config/searchsite-domain}"/> <input type="text" 
                    id="query" name="q" size="15"/>&#160; <input type="submit" 
                    value="Search" name="Search"/> </form>
            </div>
<xsl:comment>+
    |end search
    +</xsl:comment> 
        </xsl:if>
<xsl:comment>+
    |start Tabs
    +</xsl:comment>
        <xsl:apply-templates select="ul[@id='tabs']"/>
<xsl:comment>+
    |end Tabs
    +</xsl:comment>
      </div>

    </div>
<!--+
   	|centerstrip with menu and mainarea
   	+-->
    <div id="main">
<!--breadtrail location='alt'-->
        <xsl:if test="$config/trail/@location='alt'">
<xsl:comment>+
    |breadtrail
    +</xsl:comment>
      <div class="breadtrail">
          <xsl:call-template name="breadcrumbs"/>
      </div>
        </xsl:if>
<xsl:comment>+
    |start Menu, mainarea
    +</xsl:comment>
	    <xsl:if test="div[@id='menu']/ul/li">
		  <xsl:call-template name="menu"/>
	    </xsl:if>
<xsl:comment>+
    |start content
    +</xsl:comment>
	    <xsl:apply-templates select="div[@id='content']"/>
<xsl:comment>+
    |end content
    +</xsl:comment>	    
	    <div class="clearboth">&#160;</div>

	</div>
<!--+
   	|bottomstrip with footer
   	+-->
    <div id="footer">
<xsl:comment>+
    |start bottomstrip
    +</xsl:comment>
		  <div class="lastmodified"><script type="text/javascript"><![CDATA[<!--
			  document.write("Last Published: " + document.lastModified);
			  //  -->]]></script></div>

          <div class="copyright">
 			Copyright &#169;<xsl:text> </xsl:text><xsl:value-of select="$config/year"/><xsl:text> </xsl:text><xsl:value-of select="$config/vendor"/>
          </div>
          
          <xsl:if test="$filename = 'index.html'">
            <div class="logos">
              <!-- W3C logos -->
              <xsl:call-template name="compliancy-logos"/>

              <xsl:if test="$config/credits">
                <xsl:for-each select="$config/credits/credit[not(@role='pdf')]">
                  <xsl:variable name="name" select="name"/>
                  <xsl:variable name="url" select="url"/>
                  <xsl:variable name="image" select="image"/>
                  <xsl:variable name="width" select="width"/>
                  <xsl:variable name="height" select="height"/>
                  <a href="{$url}">
                    <img alt="{$name} logo" border="0">
                      <xsl:attribute name="src">
                        <xsl:if test="not(starts-with($image, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
                        <xsl:value-of select="$image"/>
                      </xsl:attribute>
                      <xsl:attribute name="style">
                        <xsl:if test="$width">width: <xsl:value-of select="$width"/>px;</xsl:if>
                        <xsl:if test="$height">height: <xsl:value-of select="$height"/>px;</xsl:if>
                      </xsl:attribute>
                    </img>
                  </a>
                </xsl:for-each>
              </xsl:if>
            </div>
          </xsl:if>
          <xsl:if test="$config/host-logo and not($config/host-logo = '')">
            <div class="host">
              <xsl:call-template name="renderlogo">
                <xsl:with-param name="name" select="$config/host-name"/>
                <xsl:with-param name="url" select="$config/host-url"/>
                <xsl:with-param name="logo" select="$config/host-logo"/>
                <xsl:with-param name="root" select="$root"/>
              </xsl:call-template>
            </div>
          </xsl:if>
<xsl:comment>+
    |end bottomstrip
    +</xsl:comment>
    </div>


      </body>
    </html>
  </xsl:template>

  <!-- Add links to any standards-compliance logos -->
  <xsl:template name="compliancy-logos">
    <xsl:if test="$config/disable-compliance-links = 'false'">
      <a href="http://validator.w3.org/check/referer"><img class="logoImage" 
          src="{$skin-img-dir}/valid-html401.png"
          alt="Valid HTML 4.01!" style="height: 31px; width: 88px;" /></a>
          
      <a href="http://jigsaw.w3.org/css-validator/"><img class="logoImage" 
          src="{$skin-img-dir}/vcss.png" 
          alt="Valid CSS!" style="height: 31px; width: 88px;"/></a>
    </xsl:if>
  </xsl:template>

  <xsl:template name="menu">
<xsl:comment>+
    |start Menu
    +</xsl:comment>
	<xsl:apply-templates select="div[@id='menu']"/>
<xsl:comment>+
    |end Menu
    +</xsl:comment>
  </xsl:template>
  <!-- Generates the PDF link -->
  <xsl:template match="div[@id='skinconf-pdflink']">
    <xsl:if test="not($config/disable-pdf-link) or $disable-pdf-link = 'false'"> 
      <div class="pdflink"><a href="{$filename-noext}.pdf" class="dida">
        <img class="skin" src="{$skin-img-dir}/pdfdoc.gif" alt="PDF"/><br/>
        PDF</a>
      </div>
    </xsl:if>
  </xsl:template>
  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
<xsl:template name="html-meta">
<!--+
   	|generator meta
   	+-->
<xsl:comment>+
    |start generator meta
    +</xsl:comment>
    	<meta name="Generator" content="Apache Forrest"/>
		<meta name="Forrest-version" content="SVN-Head (0.6-dev)"/>
      	<meta name="Forrest-skin-name" content="css-style"/>
<xsl:comment>+
    |end generator meta
    +</xsl:comment>
</xsl:template>
</xsl:stylesheet>
