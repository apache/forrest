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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                version="1.0">

  <xsl:param name="documentPath"/>

  <xsl:template match="glossary">
    <xsl:variable name="pathToRoot">
      <xsl:call-template name="dotdots">
        <xsl:with-param name="path">
          <xsl:value-of select="$documentPath"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="descendant::rdf:Description/dc:title">
          <xsl:value-of select="descendant::rdf:Description/dc:title[1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- FIXME: il8n -->
          <xsl:message terminate="no">
            <xsl:text>WARNING: No title found for the glossary (</xsl:text>
            <xsl:value-of select="concat($pathToRoot, @href-noext, '.rdf')"/>
            <xsl:text>).</xsl:text>
          </xsl:message>
          <xsl:text>Untitled Glossary</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <li>
      <p>
        <a>
          <xsl:attribute name="href">
            <xsl:value-of select="concat($pathToRoot, @href-noext, '.html')"/>
          </xsl:attribute>
          <xsl:value-of select="$name"/>
        </a>
      </p>
    </li>
  </xsl:template>

  <!-- Taken from DOAP plugin as it was; It's recursive, BTW -->
  <!-- FIXME: this should come from include of dotdots.xsl in forest core -->
  <xsl:template name="dotdots">
    <xsl:param name="path"/>
    <xsl:variable name="dirs" select="normalize-space(translate(concat($path, 'x'), ' /\', '_  '))"/>
    <!-- The above does the following
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
