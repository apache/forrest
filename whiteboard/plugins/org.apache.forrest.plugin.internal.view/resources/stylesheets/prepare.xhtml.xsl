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

<xsl:stylesheet version="1.0" xmlns:alias="http://www.w3.org/1999/XSL/TransformAlias" xmlns:forrest="http://apache.org/forrest/templates/1.0" xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:namespace-alias result-prefix="xsl" stylesheet-prefix="alias"/>
    <xsl:param name="request"/>
    <xsl:template match="/">
    	<!--Create the final stylesheet (alias:)-->
        <alias:stylesheet version="1.0">
            <alias:import href="cocoon://commonSite2xhtml"/>
            <alias:import href="cocoon://dotdots"/>
            <alias:import href="cocoon://pathutils"/>
            <alias:import href="cocoon://renderlogo"/>
            <alias:include href="cocoon://prepare.include.xhtml.{$request}"/>
            <alias:param name="path"/>
            <xsl:comment>All xhtml head elements requested by the forrest:template</xsl:comment>
            <alias:template name="getHead">
                <xsl:for-each select="/*/forrest:properties/*[@head='true']">
                    <alias:call-template name="{@name}-head"/>
                </xsl:for-each>
            </alias:template>
            <xsl:comment>All xhtml body elements requested by the forrest:template</xsl:comment>
            <alias:template name="getBody">
                <xsl:apply-templates select="/*/forrest:views/forrest:view"/>
            </alias:template>
        	<!--default entry point into the presentation model 'site'-->
            <alias:template match="site">
                <html>
                    <head>
                        <alias:call-template name="getHead"/>
                    	<!--Test whether there is an own css implemention requested by the view-->
                    	<!--*No* forrest:css found in the view-->
                        <xsl:if test="not(/*/forrest:views/forrest:view/forrest:css)">
                            <link rel="stylesheet" type="text/css">
                                <xsl:attribute name="href">{$root}skin/basic.css</xsl:attribute>
                            </link>
                            <link rel="stylesheet" type="text/css">
                                <xsl:attribute name="href">{$root}skin/contracts-<xsl:value-of select="$request"/>.css</xsl:attribute>
                            </link>
                            <link rel="stylesheet" type="text/css">
                                <xsl:attribute name="href">{$root}skin/profiling.css</xsl:attribute>
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
    <xsl:template match="forrest:hook[@name]">
        <div id="{@name}">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    <xsl:template match="forrest:hook[@class]">
        <div class="{@class}">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    <xsl:template match="forrest:css[@url]">
        <link rel="stylesheet" type="text/css">
            <xsl:attribute name="href">{$root}skin/<xsl:value-of select="@url"/>
            </xsl:attribute>
        </link>
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
                            <alias:with-param name="{@name}" select=".//forrest:properties[@contract='{$name}']/forrest:property[@name='{@name}']"/>
                        </xsl:for-each>
                    </alias:call-template>
                </xsl:when>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
