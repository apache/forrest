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

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/site2xhtml.xsl"/>

  <xsl:template match="site">
      <html>
         <head>
            <xsl:comment>*** This is a generated file.  Do not edit.  ***</xsl:comment>
            <link type="text/css" href="{$root}skin/tigris.css" rel="stylesheet" />
            <link type="text/css" href="{$root}skin/mysite.css" rel="stylesheet" />
            <link type="text/css" href="{$root}skin/site.css"   rel="stylesheet" />
            <link type="text/css" href="{$root}skin/print.css"  rel="stylesheet" media="print" />
            <xsl:if test="//skinconfig/favicon-url">
              <link rel="shortcut icon">
                <xsl:attribute name="href">
                  <xsl:value-of select="concat($root,//skinconfig/favicon-url)"/>
                </xsl:attribute>
              </link>
            </xsl:if>

            <title>
              <xsl:value-of select="/site/document/title" />
            </title>
         </head>
    
  <body class="composite" bgcolor="white">
    
    <xsl:comment>================= start Banner ==================</xsl:comment>
      <div id="banner">
        <table border="0" summary="banner" cellspacing="0" cellpadding="8" width="100%">
         <tbody>        
          <tr>
            <xsl:comment>================= start Group Logo ==================</xsl:comment>
            <xsl:if test="//skinconfig/group-name">
            <td align="left">
              <div class="groupLogo">
                <xsl:call-template name="renderlogo">
                  <xsl:with-param name="name" select="//skinconfig/group-name"/>
                  <xsl:with-param name="url" select="//skinconfig/group-url"/>
                  <xsl:with-param name="logo" select="//skinconfig/group-logo"/>
                  <xsl:with-param name="root" select="$root"/>
                </xsl:call-template>
              </div>
            </td>
            </xsl:if>
            <xsl:comment>================= end Group Logo ==================</xsl:comment>
            <xsl:comment>================= start Project Logo ==================</xsl:comment>
            <td align="right">
              <div class="projectLogo">
                <xsl:call-template name="renderlogo">
                  <xsl:with-param name="name" select="//skinconfig/project-name"/>
                  <xsl:with-param name="url" select="//skinconfig/project-url"/>
                  <xsl:with-param name="logo" select="//skinconfig/project-logo"/>
                  <xsl:with-param name="root" select="$root"/>
                </xsl:call-template>
              </div>
            </td>
            <xsl:comment>================= end Project Logo ==================</xsl:comment>
          </tr>
         </tbody>          
        </table>
      </div>
    <xsl:comment>================= end Banner ==================</xsl:comment>

    <xsl:comment>================= start Main ==================</xsl:comment>
    <table id="breadcrumbs" summary="nav" border="0" cellspacing="0" cellpadding="0" width="100%">
     <tbody>    
      <xsl:comment>================= start Status ==================</xsl:comment>
      <tr class="status">
        <td>
          <xsl:comment>================= start BreadCrumb ==================</xsl:comment>
            <a href="{//skinconfig/trail/link1/@href}"><xsl:value-of select="//skinconfig/trail/link1/@name" /></a> 
            <xsl:if test = "(//skinconfig/trail/link2/@name)and(normalize-space(//skinconfig/trail/link2/@name)!='')"><xsl:text> | </xsl:text></xsl:if>                                 
            <a href="{//skinconfig/trail/link2/@href}"><xsl:value-of select="//skinconfig/trail/link2/@name" /></a>
            <xsl:if test = "(//skinconfig/trail/link3/@name)and(normalize-space(//skinconfig/trail/link3/@name)!='')"><xsl:text> | </xsl:text></xsl:if>                                 
            <a href="{//skinconfig/trail/link3/@href}"><xsl:value-of select="//skinconfig/trail/link3/@name" /></a>
          <!-- useful when we have <link> elements instead of link(n:=1..3)  
          <xsl:for-each select="//skinconfig/trail/link">
            <xsl:if test="position()!=1">|</xsl:if>
            <a href="{@href}"><xsl:value-of select="@name"/></a>
          </xsl:for-each>
          -->
          <xsl:comment>================= end BreadCrumb ==================</xsl:comment>
        </td>
        <td id="tabs">
          <xsl:comment>================= start Tabs ==================</xsl:comment>
          <xsl:apply-templates select="div[@class='tab']"/>
          <xsl:comment>================= end Tabs ==================</xsl:comment>
        </td>
      </tr>
     </tbody>      
    </table>      
      <xsl:comment>================= end Status ==================</xsl:comment>


    <table border="0" summary="" cellspacing="0" cellpadding="8" width="100%" id="main">
     <tbody>
      <tr valign="top">
        <xsl:comment>================= start Menu ==================</xsl:comment>
        <td id="leftcol">
          <div id="navcolumn">
            <xsl:apply-templates select="div[@class='menuBar']"/>
          </div>
        </td>
        <xsl:comment>================= end Menu ==================</xsl:comment>

        <xsl:comment>================= start Content ==================</xsl:comment>
        <td>
          <div id="bodycol">
            <div class="app">
              <div align="center">
                <h1><xsl:value-of select="/site/document/title" /></h1>
                <xsl:if test="/site/document/subtitle">
                  <h2><xsl:value-of select="/site/document/subtitle" /></h2>
                </xsl:if>
               </div>
                <div class="h3">
                   <xsl:copy-of select="/site/document/body/node()|@*" />
                </div>
              </div>
            </div>
        </td>
        <xsl:comment>================= end Content ==================</xsl:comment>
      </tr>
     </tbody>      
    </table>
    <xsl:comment>================= end Main ==================</xsl:comment>

    <xsl:comment>================= start Footer ==================</xsl:comment>
    <div id="footer">
    <table border="0" width="100%" cellpadding="4" cellspacing="0" summary="footer">
     <tbody>
      <tr>
        <xsl:comment>================= start Copyright ==================</xsl:comment>
        <td colspan="2">
          <div align="center">
            <div class="copyright">
              <xsl:choose>
                <xsl:when test="$config/copyright-link">
                  <a>
                    <xsl:attribute name="href">
                      <xsl:value-of select="$config/copyright-link"/>
                    </xsl:attribute>
                  Copyright &#169; <xsl:value-of select="$config/year"/>&#160;
                  <xsl:value-of select="$config/vendor"/>
                  </a>
                </xsl:when>
                <xsl:otherwise>
                  Copyright &#169; <xsl:value-of select="$config/year"/>&#160;
                  <xsl:value-of select="$config/vendor"/>
                </xsl:otherwise>
              </xsl:choose>
              All rights reserved.
            </div>
          </div>
        </td>
        <xsl:comment>================= end Copyright ==================</xsl:comment>
      </tr>
      <tr>
      <td align="left">
        <xsl:comment>================= start Host ==================</xsl:comment>
        <xsl:if test="//skinconfig/host-logo and not(//skinconfig/host-logo = '')">
          <div align="left">
            <div class="host">
              <xsl:call-template name="renderlogo">
                <xsl:with-param name="name" select="//skinconfig/host-name"/>
                <xsl:with-param name="url" select="//skinconfig/host-url"/>
                <xsl:with-param name="logo" select="//skinconfig/host-logo"/>
                <xsl:with-param name="root" select="$root"/>
              </xsl:call-template>
            </div>
          </div>
        </xsl:if>
        <xsl:comment>================= end Host ==================</xsl:comment>
      </td>
      <td align="right">
        <xsl:comment>================= start Credits ==================</xsl:comment>
        <div align="right">
          <div class="credit">
            <xsl:if test="$filename = 'index.html'">
              <xsl:call-template name="compliancy-logos"/>
              <xsl:if test="//skinconfig/credits">
                <xsl:for-each select="//skinconfig/credits/credit[not(@role='pdf')]">
                  <xsl:call-template name="renderlogo">
                    <xsl:with-param name="name" select="name"/>
                    <xsl:with-param name="url" select="url"/>
                    <xsl:with-param name="logo" select="image"/>
                    <xsl:with-param name="root" select="$root"/>
                    <xsl:with-param name="width" select="width"/>
                    <xsl:with-param name="height" select="height"/>
                  </xsl:call-template>
                </xsl:for-each>
              </xsl:if>
            </xsl:if>
          </div>
        </div>
        <xsl:comment>================= end Credits ==================</xsl:comment>
        </td>
      </tr>
     </tbody>
    </table>
    </div>
    <xsl:comment>================= end Footer ==================</xsl:comment>

      </body>
    </html>
    </xsl:template>

</xsl:stylesheet>
