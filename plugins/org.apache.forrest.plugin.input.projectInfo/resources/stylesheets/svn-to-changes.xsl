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
<!-- $Id$ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:key name="month" match="logentry" use="substring(date, 1, 7)"/>
  <xsl:variable name="mapper" select="/svn/paths"/>
  <xsl:template match="/">
    <status>
      <xsl:apply-templates select="svn/log"/>
    </status>
  </xsl:template>
  <xsl:template match="log">
    <changes>
      <xsl:for-each 
        select="//logentry[generate-id(.)=generate-id(key('month',substring(date, 1, 7)))]">
        <xsl:sort select="date" order="descending"/>
        <release version="{substring(date, 1, 7)}">
          <xsl:for-each select="key('month',substring(date, 1, 7))">
            <xsl:apply-templates select="."/>
          </xsl:for-each>
        </release>
      </xsl:for-each>
    </changes>
  </xsl:template>
  <xsl:template match="logentry">
    <action> <xsl:attribute name="dev"><xsl:value-of 
      select="author"/></xsl:attribute> <xsl:variable name="type"> <xsl:choose> 
      <xsl:when test="contains(paths/path[1]/@action, 'A')">add</xsl:when> 
      <xsl:when test="contains(paths/path[1]/@action, 'M')">update</xsl:when> 
      <xsl:when test="contains(paths/path[1]/@action, 'R')">update</xsl:when> 
      <xsl:when test="contains(paths/path[1]/@action, 'D')">remove</xsl:when> 
      </xsl:choose> </xsl:variable> <xsl:attribute name="type"><xsl:value-of 
      select="$type"/></xsl:attribute> <xsl:variable name="version"> 
      <xsl:call-template name="context"> <xsl:with-param name="path"> 
      <xsl:value-of select="paths/path[1]"/> </xsl:with-param> 
      </xsl:call-template> </xsl:variable> <xsl:variable name="path"> 
      <xsl:value-of select="paths/path[1]"/> </xsl:variable> <xsl:attribute 
      name="context"> <xsl:value-of select="$version"/> 
      </xsl:attribute>[<xsl:value-of select="$version"/>] <xsl:value-of 
      select="msg"/><xsl:text> </xsl:text><link 
      href="http://svn.apache.org/viewcvs.cgi?rev={@revision}&amp;view=rev">Diff</link><xsl:text> 
      </xsl:text> </action>
  </xsl:template>
  <xsl:template name="context">
    <xsl:param name="path"/>
    <xsl:choose>
      <xsl:when test="$mapper//path[starts-with($path,text())]">
        <xsl:value-of 
          select="$mapper//path[starts-with($path,text())]/@context"/>
      </xsl:when>
      <xsl:otherwise>No context matched</xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
