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
This stylesheet contains templates for converting documentv11 to HTML.  See the
imported document2html.xsl for details.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="lm://transform.xdoc.html" />
  <xsl:include href="lm://transform.xml.generateId" />
  <xsl:variable name="skin-img-dir"
  select="concat(string($root), 'themes/images')" />
  <xsl:template match="body" mode="toc" />
  <xsl:template match="document">
    <div id="content">
      <xsl:if test="normalize-space(header/title)!=''">
        <h1>
          <xsl:value-of select="header/title" />
        </h1>
      </xsl:if>
      <xsl:if test="normalize-space(header/subtitle)!=''">
        <h3>
          <xsl:value-of select="header/subtitle" />
        </h3>
      </xsl:if>
      <!--
      <xsl:apply-templates select="header/type"/>
      <xsl:apply-templates select="header/notice"/>
      <xsl:apply-templates select="header/abstract"/>
      <xsl:apply-templates select="body"/>

      <div class="attribution">
        <xsl:apply-templates select="header/authors"/>
        <xsl:if test="header/authors and header/version">
          <xsl:text>; </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="header/version"/>
      </div>
    -->
      <div id="content-main">
        <xsl:apply-templates select="body" />
      </div>
    </div>
  </xsl:template>
  <xsl:template match="body">
    <xsl:apply-templates />
  </xsl:template>
  <xsl:template name="tocLinkGenerator">
    <a>
      <xsl:attribute name="name">
        <xsl:call-template name="generate-id" />
      </xsl:attribute>
      <xsl:attribute name="title">
        <xsl:value-of select="title" />
      </xsl:attribute>
      <xsl:text>
 
</xsl:text>
    </a>
  </xsl:template>
  <!--<xsl:template match="@id">
    <xsl:apply-imports/>
  </xsl:template>-->
  <!-- Generate a <a name="..."> tag for an @id -->
  <!--<xsl:template match="@id">
    <xsl:if test="normalize-space(.)!=''">
      <a name="{.}">&#160;</a>
    </xsl:if>
  </xsl:template>-->
  <xsl:template match="section">
    <xsl:call-template name="tocLinkGenerator" />
    <!-- <xsl:apply-templates select="@id"/>-->
    <xsl:variable name="level" select="count(ancestor::section)+1" />
    <xsl:choose>
      <xsl:when test="$level=1">
        <div class="skinconf-heading-{$level}">
          <h1>
            <xsl:value-of select="title" />
          </h1>
        </div>
        <div class="section">
          <xsl:apply-templates select="*[not(self::title)]" />
        </div>
      </xsl:when>
      <xsl:when test="$level=2">
        <div class="skinconf-heading-{$level}">
          <h2>
            <xsl:value-of select="title" />
          </h2>
        </div>
        <div class="section">
          <xsl:apply-templates select="*[not(self::title)]" />
        </div>
      </xsl:when>
      <!-- If a faq, answer sections will be level 3 (1=Q/A, 2=part) -->
      <xsl:when test="$level=3 and $notoc='true'">
        <h4 class="faq">
          <xsl:value-of select="title" />
        </h4>
        <div align="right">
          <a href="#{@id}-menu">^</a>
        </div>
        <div style="margin-left: 15px">
          <xsl:apply-templates select="*[not(self::title)]" />
        </div>
      </xsl:when>
      <xsl:when test="$level=3">
        <h4>
          <xsl:value-of select="title" />
        </h4>
        <xsl:apply-templates select="*[not(self::title)]" />
      </xsl:when>
      <xsl:otherwise>
        <h5>
          <xsl:value-of select="title" />
        </h5>
        <xsl:apply-templates select="*[not(self::title)]" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="figure">
    <xsl:apply-templates select="@id" />
    <div style="text-align: center;">
        <xsl:if test="@id">
          <xsl:attribute name="id">
	    <xsl:value-of select="@id"/>
	  </xsl:attribute>
        </xsl:if>
      <img src="{@src}" alt="{@alt}" class="figure">
        <xsl:if test="@id">
          <xsl:attribute name="id">
          <xsl:value-of select="@id"/>-figure</xsl:attribute>
        </xsl:if>
        <xsl:if test="@height">
          <xsl:attribute name="height">
            <xsl:value-of select="@height" />
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="@width">
          <xsl:attribute name="width">
            <xsl:value-of select="@width" />
          </xsl:attribute>
        </xsl:if>
      </img>
    </div>
  </xsl:template>
  <xsl:template match="note | warning | fixme">
    <xsl:apply-templates select="@id" />
    <div class="{local-name()}">
      <div class="label">
        <xsl:choose>
          <!-- FIXME: i18n Transformer here -->
          <xsl:when test="@label">
            <xsl:value-of select="@label" />
          </xsl:when>
          <xsl:when test="local-name() = 'note'">Note</xsl:when>
          <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
          <xsl:otherwise>Fixme ( 
          <xsl:value-of select="@author" />)</xsl:otherwise>
        </xsl:choose>
      </div>
      <div class="content">
        <xsl:apply-templates />
      </div>
    </div>
  </xsl:template>
</xsl:stylesheet>
