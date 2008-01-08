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
                exclude-result-prefixes="dc atom rdfs rdf"
                version="1.0">

  <xsl:param name="name">All</xsl:param>
  <xsl:template match="glossaries">
    <xsl:element name="{$name}">
      <xsl:attribute name="label">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="glossary">
    <xsl:variable name="name">
      <xsl:choose>
        <xsl:when test="descendant::rdf:Description/dc:title">
          <xsl:value-of select="descendant::rdf:Description/dc:title"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@id"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <glossary>
      <xsl:attribute name="label">
        <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="href">
        <xsl:value-of select="@href-noext"/>.html</xsl:attribute>
        <xsl:attribute name="description">
        <xsl:value-of select="$name"/>
        <xsl:text> Glossary</xsl:text>
      </xsl:attribute>
    </glossary>
  </xsl:template>
</xsl:stylesheet>
