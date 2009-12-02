<?xml version="1.0" encoding="utf-8"?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:xhtml="http://www.w3.org/1999/xhtml"
xmlns:dir="http://apache.org/cocoon/directory/2.0"
xmlns:session="http://apache.org/cocoon/session/1.0"
xmlns:forrest="http://apache.org/forrest/templates/1.0">
  <xsl:param name="requestedContract" select="'false'" />
  <xsl:param name="less" select="'false'" />
  <xsl:template match="/">
    <xsl:if test="$less='false'">
      <html>
        <head>
          <title>ls.contract 
          <xsl:if test="$requestedContract='false'">s</xsl:if>
          <xsl:if test="$requestedContract!='false'">&#160; 
          <xsl:value-of select="$requestedContract" /></xsl:if></title>
        </head>
        <body>
          <xsl:choose>
            <xsl:when test="$requestedContract='false'">
              <xsl:apply-templates />
            </xsl:when>
            <xsl:otherwise>
              <xsl:apply-templates select=".//forrest:contract[@name=$requestedContract]" />
            </xsl:otherwise>
          </xsl:choose>
        </body>
      </html>
    </xsl:if>
    <xsl:if test="$less='true'">
      <xsl:apply-templates select=".//forrest:contract[@name=$requestedContract]" />
    </xsl:if>
  </xsl:template>
  <xsl:template match="forrest:theme">
    <xsl:variable select="@name" name="name" />
    <p id="{$name}">
      <title>theme name: 
      <xsl:value-of select="@name" /></title>
      <xsl:apply-templates select="./forrest:contract">
        <xsl:with-param select="$name" name="theme" />
      </xsl:apply-templates>
    </p>
  </xsl:template>
  <xsl:template match="forrest:contract">
    <xsl:param name="theme" select="common" />
    <xsl:choose>
      <xsl:when test="$requestedContract!='false'">
        <p id="{@name}">
          <xsl:call-template name="innerBodyLs" />
        </p>
      </xsl:when>
      <xsl:otherwise>
        <section id="{$theme}-{@name}">
          <xsl:call-template name="innerBodyLs" />
        </section>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="innerBodyLs">
    <title>
      <xsl:value-of select="@name" />
    </title>
    <p class="file">
      <strong>file-name:</strong>
      <br />
      <xsl:value-of select="@file-name" />
    </p>
    <p class="description">
      <strong>description:</strong>
      <br />
      <xsl:if test="./description/*">
        <xsl:copy-of select="./description/*" />
      </xsl:if>
      <xsl:if test="not(./description/*)">
        <xsl:value-of select="./description" />
      </xsl:if>
    </p>
    <p class="usage">
      <strong>usage:</strong>
    </p>
    <pre>
      
<xsl:value-of select="./usage" />
    
</pre>
    <p class="template-definition">
      <strong>forrest-template definition:</strong>
    </p>
    <pre>
&lt;forrest:template
<xsl:apply-templates select="./forrest:template/@*" />/&gt; 
</pre>
  </xsl:template>
  <xsl:template match="forrest:template/@*">&#160; 
  <xsl:value-of select="name()" />=" 
  <xsl:value-of select="." />"</xsl:template>
</xsl:stylesheet>
