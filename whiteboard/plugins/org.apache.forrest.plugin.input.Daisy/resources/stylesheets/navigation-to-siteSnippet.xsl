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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:d="http://outerx.org/daisy/1.0#navigationspec"
    version="1.0">
      
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="d:doc">
    <xsl:choose>
      <xsl:when test="d:doc">
        <group>
          <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
          <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
          <xsl:if test="@nodeId">
            <xsl:attribute name="href"><xsl:value-of select="@nodeId"/>/</xsl:attribute>
          </xsl:if>
          <xsl:choose>
            <xsl:when test="@nodeId">
              <doc>
                <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:attribute name="label">Section Home</xsl:attribute>
                <xsl:attribute name="href">../<xsl:value-of select="@nodeId"/>.html</xsl:attribute>
              </doc>
            </xsl:when>
            <xsl:otherwise>
              <doc>
                <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:attribute name="label">Section Home</xsl:attribute>
                <xsl:attribute name="href"><xsl:value-of select="@id"/>.html</xsl:attribute>
              </doc>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:apply-templates/>
        </group>
      </xsl:when>
      <xsl:when test="d:link">
        <group>
          <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
          <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
          <xsl:if test="@nodeId">
            <xsl:attribute name="href"><xsl:value-of select="@nodeId"/>/</xsl:attribute>
          </xsl:if>
          <xsl:apply-templates/>
        </group>
      </xsl:when>
      <xsl:otherwise>
        <doc>
          <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
          <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
          <xsl:choose>
            <xsl:when test="@nodeId">
              <xsl:attribute name="href"><xsl:value-of select="@nodeId"/>.html</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href"><xsl:value-of select="@id"/>.html</xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        </doc>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="d:link">
    <link>
      <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
      <xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute>
    </link>
  </xsl:template>
    
  <xsl:template match="d:group">
    <group>
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
      <xsl:attribute name="href">
        <xsl:choose>
          <xsl:when test="@nodeId"><xsl:value-of select="@nodeId"/>/</xsl:when>
          <xsl:otherwise><xsl:value-of select="@id"/>/</xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates/>
    </group>
  </xsl:template>
</xsl:stylesheet>
