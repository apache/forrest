<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet xmlns:s="http://apache.org/cocoon/lenya/sitetree/1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://apache.org/forrest/linkmap/1.0"
    version="1.0">
  <xsl:output method="xml"/>
  <xsl:template match="/">
    <xsl:apply-templates select="s:site"/>
  </xsl:template>
  <xsl:template match="s:site">
    <site label="MyProj">
      <about label="About">
        <index label="Index" href="index.html" description="Welcome to MyProj"/>
      </about>
      <lenya label="Lenya Site" href="lenya/">
        <xsl:apply-templates select="*"/>
      </lenya>
    </site>
  </xsl:template>
  <xsl:template match="s:node">
    <xsl:element name="{@id}">
      <xsl:attribute name="label">
        <xsl:value-of select="s:label[1]"/>
      </xsl:attribute>
      <xsl:attribute name="href">
        <xsl:value-of select="@id"/>
        <xsl:choose>
          <xsl:when test="s:node">
<xsl:text>/</xsl:text>
          </xsl:when>
          <xsl:otherwise>
<xsl:text>.html</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*"/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="s:label"/>
</xsl:stylesheet>
