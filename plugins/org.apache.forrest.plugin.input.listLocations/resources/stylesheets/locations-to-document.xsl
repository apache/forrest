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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
  <xsl:template match="/">
    <xsl:apply-templates select="//locations"/>
  </xsl:template>
  <xsl:template match="locations">
    <document>
      <header>
        <title><xsl:choose>
            <xsl:when test="@title!=''">
              <xsl:value-of select="@title"/>
            </xsl:when>
            <xsl:otherwise>
<xsl:text>Locations</xsl:text>
            </xsl:otherwise>
          </xsl:choose></title>
      </header>
      <body>
        <xsl:apply-templates/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="introduction">
    <section id="introduction">
      <title><xsl:choose>
          <xsl:when test="@title!=''">
            <xsl:value-of select="@title"/>
          </xsl:when>
          <xsl:otherwise>
<xsl:text>Introduction</xsl:text>
          </xsl:otherwise>
        </xsl:choose></title>
      <p>
        <xsl:value-of select="."/>
      </p>
    </section>
  </xsl:template>
  <xsl:template match="location">
    <section id="{id}">
      <title><xsl:value-of select="title"/></title>
      <p>
        <xsl:choose>
          <xsl:when test="url"><a>
            <xsl:attribute name="href">
              <xsl:value-of select="url"/>
            </xsl:attribute>
            <xsl:value-of select="title"/></a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="title"/>
          </xsl:otherwise>
        </xsl:choose>
        at
        <xsl:value-of select="place"/>
      </p>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  <xsl:template match="notes">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="note">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>
  <xsl:template match="geoPoint">
    <p>
      Latitude:
      <xsl:apply-templates select="latitude"/>
      Longitude:
      <xsl:apply-templates select="longitude"/>
    </p>
  </xsl:template>
  <xsl:template match="id | title | place | url"/>
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
