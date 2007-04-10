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
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  xmlns:doap="http://usefulinc.com/ns/doap#">
  <xsl:param name="documentPath"/>
  <xsl:template match="descriptor">
    <xsl:variable name="pathToRoot">
      <xsl:call-template name="dotdots">
        <xsl:with-param name="path">
          <xsl:value-of select="$documentPath"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="descendant::doap:Project/doap:shortname">
          <xsl:value-of select="descendant::doap:Project/doap:shortname"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="descendant::doap:Project/doap:name"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <div>
      <p>
        <a>
        <xsl:attribute name="href">
          <xsl:value-of select="$pathToRoot"/>
          <xsl:value-of select="@href-noext"/>.html</xsl:attribute>
        <xsl:value-of select="$name"/>
        </a>
<xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="descendant::doap:Project/doap:shortdesc">
            <xsl:value-of select="descendant::doap:Project/doap:shortdesc"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="descendant::doap:Project/doap:description"/>
          </xsl:otherwise>
        </xsl:choose>
      </p>
    </div>
  </xsl:template>
<!-- FIXME: this should come from include of dotdots.xsl in forest core -->
  <xsl:template name="dotdots">
    <xsl:param name="path"/>
    <xsl:variable name="dirs" select="normalize-space(translate(concat($path, 'x'), ' /\', '_  '))"/>
<!-- The above does the following:
       o Adds a trailing character to the path. This prevents us having to deal
         with the special case of ending with '/'
       o Translates all directory separators to ' ', and normalize spaces,
         cunningly eliminating duplicate '//'s. We also translate any real
         spaces into _ to preserve them.
    -->
    <xsl:variable name="remainder" select="substring-after($dirs, ' ')"/>
    <xsl:if test="$remainder">
<xsl:text>../</xsl:text>
      <xsl:call-template name="dotdots">
        <xsl:with-param name="path" select="translate($remainder, ' ', '/')"/>
<!-- Translate back to /'s because that's what the template expects. -->
      </xsl:call-template>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
