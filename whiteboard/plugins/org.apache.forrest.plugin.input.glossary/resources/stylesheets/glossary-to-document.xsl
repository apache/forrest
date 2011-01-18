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
  <xsl:import href="copyover.xsl"/>
  <xsl:template match="glossary">
    <document>
      <header>
        <xsl:choose>
          <xsl:when test="title">
            <title><xsl:value-of select="title"/></title>
          </xsl:when>
          <xsl:otherwise>
            <title>Glossary</title>
          </xsl:otherwise>
        </xsl:choose>
      </header>
      <body>
        <xsl:if test="introduction">
          <section>
            <title>Introduction</title>
            <xsl:apply-templates select="introduction"/>
          </section>
        </xsl:if>
        <xsl:apply-templates select="part"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="introduction">
    <xsl:apply-templates />
  </xsl:template>
  <xsl:template match="part">
    <section>
      <xsl:attribute name="id">
        <xsl:value-of select="@id"/>
      </xsl:attribute>
      <title><xsl:value-of select="title"/></title>
      <table>
        <tr>
          <th>Term</th>
          <th>Definitions</th>
          <th>Notes</th>
        </tr>
        <xsl:apply-templates select="item"/>
      </table>
    </section>
  </xsl:template>
  <xsl:template match="item">
    <xsl:variable name="id">
      <xsl:call-template name="generate-id"/>
    </xsl:variable>
    <tr>
      <xsl:choose>
        <xsl:when test="acronym">
          <td class="term" id="{$id}">
            <xsl:apply-templates select="term"/> (<xsl:apply-templates select="acronym"/>)</td>
        </xsl:when>
        <xsl:otherwise>
          <td class="term" id="{$id}">
            <xsl:apply-templates select="term"/>
          </td>
        </xsl:otherwise>
      </xsl:choose>
      <td class="definitions">
        <xsl:apply-templates select="definitions"/>
        <xsl:apply-templates select="definition"/>
        <xsl:if test="see">
          <br/>See Also: <xsl:apply-templates select="see"/>
        </xsl:if>
      </td>
      <td class="notes">
        <xsl:if test="notes">
          <xsl:apply-templates select="notes"/>
        </xsl:if>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="term">
    <xsl:value-of select="."/>
    <xsl:if test="not(position() = last())">, </xsl:if>
  </xsl:template>
  <xsl:template match="acronym">
    <xsl:value-of select="."/>
  </xsl:template>
  <xsl:template match="definitions">
    <p>
      <xsl:apply-templates/>
    </p>
  </xsl:template>
  <xsl:template match="definition">
    <xsl:number/>. <xsl:apply-templates/>
    <br />
    <xsl:if test="@cite">
        (Source: <link role="citation">
      <xsl:attribute name="href">
        <xsl:value-of select="@cite"/>
      </xsl:attribute>
      <xsl:value-of select="@cite"/></link>)
      </xsl:if>
  </xsl:template>
  <xsl:template match="see">
<!-- FIXME: FOR-858 --><link role="glossary">
    <xsl:attribute name="href">#<xsl:value-of select="id"/>
    </xsl:attribute>
    <xsl:value-of select="text"/></link>
    <xsl:if test="not(position() = last())">, </xsl:if>
  </xsl:template>
  <xsl:template match="notes">
    <ul>
      <xsl:apply-templates/>
    </ul>
  </xsl:template>
  <xsl:template match="item-note">
    <li><xsl:apply-templates/></li>
  </xsl:template>
  <xsl:template name="generate-id">
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:value-of select="@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(concat(local-name(.), '-'), generate-id(.))"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
