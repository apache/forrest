<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!--
Generates a lucene:index for the whole site with CInclude elements where lucene:documents should be pulled in.
Input is expected to be in standard book.xml format. @hrefs should be normalized, although unnormalized hrefs can be
handled by uncommenting the relevant section.
-->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  xmlns:lucene="http://apache.org/cocoon/lucene/1.0">
<!-- The extension of the lucene index fragments. -->
  <xsl:param name="extension" select="'solr'"/>
<!-- Creates the lucene:index root element from the Forrest
  book. -->
  <xsl:template match="book">
    <add>
      <xsl:apply-templates select="menu|menu-item"/>
    </add>
  </xsl:template>
<!-- Recursively processes menu elements. -->
  <xsl:template match="menu">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="menu-item[@type='hidden']"/>
<!-- Ignore hidden items -->
  <xsl:template match="menu-item[contains(@href, '#')]"/>
<!-- Ignore #frag-id items -->
  <xsl:template match="menu-item[starts-with(@href, 'http:')]"/>
<!-- Ignore absolute http urls -->
  <xsl:template match="menu-item[starts-with(@href, 'https:')]"/>
<!-- Ignore absolute https urls -->
  <xsl:template match="menu-item[starts-with(@href, 'ext:')]"/>
<!-- Ignore ext: urls -->
  <xsl:template match="menu-item[starts-with(@href, 'site:')]"/>
<!-- Ignore site: urls -->
<!-- For entries whose @href ends in "/", refer to @href/index.lucene -->
  <xsl:template match="menu-item[substring(@href, string-length(@href) - string-length('/') + 1) = '/']">
    <cinclude:include>
      <xsl:attribute name="src">
<xsl:text>cocoon://</xsl:text>
        <xsl:value-of select="concat(@href, 'index.', $extension)"/>
      </xsl:attribute>
    </cinclude:include>
  </xsl:template>
<!-- Inserts a cinclude:include element for document referenced by
  menu item. -->
  <xsl:template match="menu-item">
    <cinclude:include>
      <xsl:attribute name="src">
<xsl:text>cocoon://</xsl:text>
        <xsl:call-template name="strip-extension">
          <xsl:with-param name="the-string" select="@href"/>
        </xsl:call-template>
        <xsl:value-of select="$extension"/>
      </xsl:attribute>
    </cinclude:include>
  </xsl:template>
<!-- Strips the extension from a filename. Works for filenames with
  multiple dots. -->
  <xsl:template name="strip-extension">
    <xsl:param name="the-string"/>
    <xsl:value-of select="substring-before($the-string, '.')"/>
    <xsl:if test="substring-after($the-string, '.') != ''">
<xsl:text>.</xsl:text>
      <xsl:call-template name="strip-extension">
        <xsl:with-param name="the-string" select="substring-after($the-string, '.')"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
