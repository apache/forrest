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
<!--
site2xml.xsl is the final stage in XML page production.  It merges HTML from
document2html.xsl, tab2menu.xsl and book2menu.xsl, and adds the site header,
footer, searchbar, css etc.  As input, it takes XML of the form:

<elements>
  <branding/>
  <search/>
  <menu/>
  <content/>
  <siteinfo/>
</elements>

-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:forrest="http://apache.org/forrest/templates/1.0">
  <xsl:param name="contextPath"/>
  
  <!--+
  |Overall site template
  +-->
  <xsl:template match="/">
    <forrest:xhtml>
    <xsl:comment>context: <xsl:value-of select="$contextPath"/></xsl:comment>
<!--+
  |XHTML-head
  +-->
        <forrest:head>
            <xsl:apply-templates select="forrest:contract" mode="xhtml-head"/>
        </forrest:head>
<!--+
  |XHTML-body
  +-->
      <forrest:body>
        <xsl:apply-templates select="forrest:contract" mode="xhtml-body"/>
      </forrest:body>
    </forrest:xhtml>
  </xsl:template>
  
  <xsl:template match="forrest:contract" mode="xhtml-head">
    <xsl:comment>function name=<xsl:value-of select="@name"/> HEAD</xsl:comment>
    <xsl:copy-of select="xhtml/head/*"/>
  </xsl:template>
  
  <xsl:template match="forrest:contract" mode="xhtml-body">
    <xsl:comment>function name=<xsl:value-of select="@name"/> BODY</xsl:comment>
    <xsl:copy-of select="xhtml/body/*"/>
  </xsl:template>
</xsl:stylesheet>
