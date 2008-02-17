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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <xsl:template match="anchor">
    <fo:block>
      <xsl:copy-of select="@id"/>
    </fo:block>
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="section">
    <xsl:param name="level">0</xsl:param>
    <xsl:variable name="size">
      <xsl:value-of select="12-number($level)"/>
    </xsl:variable>
    <fo:block font-family="sans-serif" font-size="{$size}pt" font-weight="bold"
      space-before="12pt" space-after="8pt" margin="0">
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="normalize-space(@id)!=''">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:if test="$heading-type = 'boxed'">
        <xsl:attribute name="background-color">
          <xsl:value-of select="$heading-color"/>
        </xsl:attribute>
        <xsl:attribute name="padding-left">3pt</xsl:attribute>
        <xsl:attribute name="padding-top">4pt</xsl:attribute>
      </xsl:if>
      <xsl:if test="$heading-type = 'underlined'">
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-width">
          <xsl:value-of select="2+(2-number($level))*2"/>pt</xsl:attribute>
        <xsl:attribute name="border-bottom-color">
          <xsl:value-of select="$heading-color"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if
        test="$numbersections = 'true' and number($level) &lt; $numbering-max-depth+1">
        <xsl:number format="1.1.1.1.1.1.1" count="section" level="multiple"/>
        <xsl:text>. </xsl:text>
      </xsl:if>
      <!-- For sections 4  or more nestings deep, indent instead of number -->
      <xsl:if test="number($level) &gt; $numbering-max-depth+1">
        <xsl:attribute name="start-indent">
          <xsl:value-of select="4+number($level)"/>
          <xsl:text>pt</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:value-of select="title"/>
    </fo:block>
    <fo:block background-color="{$background-color}">
      <xsl:apply-templates>
        <xsl:with-param name="level" select="number($level)+1"/>
      </xsl:apply-templates>
    </fo:block>
  </xsl:template>
  <xsl:template match="title">
    <!-- do nothing as titles are handled in their parent templates -->
  </xsl:template>
  <xsl:template match="subtitle">
    <xsl:param name="level">0</xsl:param>
    <xsl:variable name="size" select="16-(number($level)*1.5)"/>
    <fo:block font-weight="bold" font-size="{$size}pt">
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  <xsl:template match="p">
    <xsl:choose>
      <xsl:when test="ancestor::li and not(preceding-sibling::*)">
        <fo:block space-after="4pt" font-family="serif">
          <xsl:copy-of select="@id"/>
          <xsl:call-template name="insertPageBreaks"/>
          <xsl:apply-templates/>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <fo:block space-before="4pt" space-after="4pt" font-family="serif">
          <xsl:copy-of select="@id"/>
          <xsl:call-template name="insertPageBreaks"/>
          <xsl:apply-templates/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
