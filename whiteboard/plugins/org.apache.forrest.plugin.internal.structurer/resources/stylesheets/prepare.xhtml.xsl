<?xml version="1.0"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
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

<xsl:stylesheet version="1.0" xmlns:alias="http://www.w3.org/1999/XSL/TransformAlias" xmlns:forrest="http://apache.org/forrest/templates/1.0" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:namespace-alias result-prefix="xsl" stylesheet-prefix="alias"/>
    <!--Include forrest:hook matchers-->
    <xsl:include href="hooksMatcher.xsl"/>
    <xsl:param name="request"/>
    <xsl:param name="forrestContext" select="'test'"/>
    <xsl:key name="head-template" match="forrest:property[@format='xhtml']" use="@name" />
    <xsl:key name="css-includes" match="forrest:css" use="@url" />
    <xsl:template match="/">
    	<!--Create the final stylesheet (alias:)-->
        <alias:stylesheet version="1.0">
            <alias:import href="file:{$forrestContext}/skins/common/xslt/html/site2xhtml.xsl"/> 
            <alias:import href="file:{$forrestContext}/skins/common/xslt/html/dotdots.xsl"/>
            <alias:import href="file:{$forrestContext}/skins/common/xslt/html/pathutils.xsl"/>
            <alias:import href="file:{$forrestContext}/skins/common/xslt/html/renderlogo.xsl"/>
            <alias:include href="cocoon://prepare.include.xhtml.{$request}"/>
            <alias:param name="path"/>
            <xsl:comment>All xhtml head elements requested by the forrest:template</xsl:comment>
            <alias:template name="getHead">
                <xsl:for-each 
                  select="/*/forrest:properties/*[@head='true' and count(. | key('head-template', @name)[1]) = 1]">
                  <xsl:variable name="name" select="@name"/>
                  <xsl:apply-templates mode="head"
                    select="//forrest:contract[@name=$name]"/>
                </xsl:for-each>
            </alias:template>
            <xsl:comment>All xhtml body elements requested by the forrest:template</xsl:comment>
            <alias:template name="getBody">
                <xsl:apply-templates select="/*/forrest:views/forrest:view"/>
            </alias:template>
        	<!--default entry point into the presentation model 'site'-->
            <alias:template match="/">
                <html>
                    <head>
                        <alias:call-template name="getHead"/>
                    	<!--Test whether there is an own css implemention requested by the view-->
                    	<!--*No* forrest:css found in the view-->
                        <xsl:if test="not(/*/forrest:views/forrest:view/forrest:css)">
                            <link rel="stylesheet" type="text/css">
                                <xsl:attribute name="href">{$root}skin/default.css</xsl:attribute>
                            </link>
                        </xsl:if>
                    	<!-- forrest:css *found* in the view-->
                        <xsl:if test="/*/forrest:views/forrest:view/forrest:css">
                            <xsl:apply-templates select="/*/forrest:views/forrest:view/forrest:css"/>
                        </xsl:if>
                        <title>
                            <alias:value-of select="div[@id='content']/h1"/>
                        </title>
                    </head>
                    <body onload="init()">
                        <alias:call-template name="getBody"/>
                    </body>
                </html>
            </alias:template>
        </alias:stylesheet>
    </xsl:template>
    <xsl:template match="forrest:view">
        <xsl:apply-templates select="*[local-name()!='css']"/>
    </xsl:template>
    <xsl:template match="forrest:css[@url and count(. | key('css-includes', @url)[1]) = 1]">
        <link type="text/css">
            <xsl:choose>
              <xsl:when test="@rel">
                <xsl:attribute name="rel"><xsl:value-of select="@rel"/></xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="rel">stylesheet</xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="@title">
                <xsl:attribute name="title"><xsl:value-of select="@title"/></xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="title"><xsl:value-of select="@url"/></xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:attribute name="href">{$root}skin/<xsl:value-of select="@url"/>
            </xsl:attribute>
            <xsl:if test="@media">
              <xsl:attribute name="media"><xsl:value-of select="@media"/></xsl:attribute>
            </xsl:if>
        </link>
    </xsl:template>
    <xsl:template match="forrest:contract" mode="head">
      <xsl:variable name="name" select="@name"/>
      <xsl:if test="/*/forrest:properties/*[@head='true' and @name=$name]">
            <!--If next son is not forrest:properties go on-->
            <xsl:choose>
                <xsl:when test="not(forrest:properties[@contract=$name])">
                    <alias:call-template name="{@name}-head"/>
                </xsl:when>
                <xsl:when test="forrest:properties[@contract=$name]">
                    <alias:call-template name="{@name}-head"  xmlns:forrest="http://apache.org/forrest/templates/1.0">
                        <xsl:for-each select="forrest:properties[@contract=$name]/forrest:property">
                          <xsl:variable name="xpath">
                            <xsl:call-template name="generateXPath"/>
                          </xsl:variable>
                          <alias:with-param name="{@name}" select="{normalize-space($xpath)}"/>
                        </xsl:for-each>
                    </alias:call-template>
                </xsl:when>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="forrest:contract">
        <xsl:variable name="name" select="@name"/>
        <!--Test whether there is a body template needed-->
        <xsl:if test="/*/forrest:properties/*[@body='true' and @name=$name]">
            <!--If next son is not forrest:properties go on-->
            <xsl:choose>
                <xsl:when test="not(forrest:properties[@contract=$name])">
                    <xsl:apply-templates/>
                    <alias:call-template name="{@name}-body"/>
                </xsl:when>
                <xsl:when test="forrest:properties[@contract=$name]">
                    <alias:call-template name="{@name}-body">
                        <xsl:for-each select="forrest:properties[@contract=$name]/forrest:property">
                          <xsl:variable name="xpath">
                            <xsl:call-template name="generateXPath"/>
                          </xsl:variable>
                          <alias:with-param name="{@name}" select="{normalize-space($xpath)}" />
                        </xsl:for-each>
                    </alias:call-template>
                </xsl:when>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    <xsl:template name="generateXPath">
      <xsl:for-each select="ancestor::*[name()!='filter']">
        /<xsl:value-of select="name()"/>[<xsl:number/>]
      </xsl:for-each>
      /<xsl:value-of select="name()"/>[<xsl:number/>]
    </xsl:template> 
</xsl:stylesheet>
