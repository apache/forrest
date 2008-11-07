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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:include href="unescape.xsl"/>
  <xsl:param name="searchForm"/>
  <xsl:variable name="rows" select="//lst[@name='params']/str[@name='rows']/text()"/>
  <xsl:variable name="query" select="//lst[@name='params']/str[@name='q']/text()"/>
  <xsl:variable name="time" select="//lst[@name='responseHeader']/int[@name='QTime']/text()"/>
  <xsl:template match="/">
    <document>
      <header>
        <title><xsl:value-of select="$query"/> - solr search </title>
      </header>
      <body>
        <xsl:apply-templates select="//result"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="result[@name='response']">
    <xsl:variable name="start" select="@start"/>
    <xsl:variable name="found" select="@numFound"/>
    <xsl:apply-templates select="paginator"/>
    <section id="result">
      <title>solr search result </title>
      <p>
        Results
        <xsl:if test="not($found=0)">
          <xsl:value-of select="$start+1"/>
          <xsl:choose>
            <xsl:when test="($start+$rows)&gt;$found and ($start+1 &lt; $found)">
              -
              <xsl:value-of select="$found"/>
            </xsl:when>
            <xsl:when test="($start+$rows)&gt;$found and ($start+1 = $found)"/>
            <xsl:otherwise> -
              <xsl:value-of select="$start+$rows"/>
            </xsl:otherwise>
          </xsl:choose>
          of </xsl:if>
        <xsl:value-of select="$found"/>
        for <code>
        <xsl:value-of select="$query"/>
        </code>. ( <code>
        <xsl:value-of select="$time"/>
        </code> ms)
      </p>
      <xsl:for-each select="doc">
        <xsl:variable name="id" select="str[@name='id']"/>
        <section id="result-{$id}">
          <title><xsl:value-of select="str[@name='title']"/></title>
          <p>
            <a href="{concat(substring-after(substring-before($id,'.xml'),':'),'.html')}">
            <xsl:value-of select="str[@name='title']"/>
            </a>
          </p>
          <xsl:apply-templates
            select="//lst[@name='highlighting']/lst[@name=$id]"/>
        </section>
      </xsl:for-each>
    </section>
  </xsl:template>
  <xsl:template match="paginator">
    <section>
      <title>Result pages</title>
      <p>
        <xsl:for-each select="page">
          <xsl:variable name="current" select="@current"/>
          <xsl:choose>
            <xsl:when test="$current='true'">
<xsl:text> </xsl:text>
              <xsl:value-of select="@id"/>
<xsl:text> </xsl:text>
            </xsl:when>
            <xsl:otherwise><a href="{concat($searchForm,'?',@queryString)}">
              <xsl:value-of select="@id"/></a>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>
      </p>
    </section>
  </xsl:template>
  <xsl:template match="arr[@name='content']">
    <xsl:for-each select="str">
      <p>
        <xsl:call-template name="unescapeEm">
          <xsl:with-param name="val" select="."/>
        </xsl:call-template>
      </p>
    </xsl:for-each>
  </xsl:template>
</xsl:stylesheet>
