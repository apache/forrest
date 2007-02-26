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
  
  <xsl:key name="kDistinctProgLang" match="doap:programming-language" use="."/>
  
  <xsl:template match="/">
    <langIndex>
      <xsl:attribute name="label">Language Indexes</xsl:attribute>
      <xsl:for-each select="//doap:programming-language[generate-id() = generate-id(key('kDistinctProgLang',.))]">
        <xsl:sort select="."/>
        <xsl:variable name="name" select="."/>
        <xsl:element name="{$name}">
          <xsl:attribute name="label"><xsl:value-of select="$name"/></xsl:attribute>
          <xsl:attribute name="href">/projectDetails/index/byLang/<xsl:value-of select="$name"/>.html</xsl:attribute>
          <xsl:attribute name="description">All projects using <xsl:value-of select="$name"/></xsl:attribute>
        </xsl:element>
      </xsl:for-each>
    </langIndex>
  </xsl:template>
  
  <xsl:template match="descriptor">
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
      
    <project>
      <xsl:attribute name="label"><xsl:value-of select="$name"/></xsl:attribute>
      <xsl:attribute name="href"><xsl:value-of select="@href-noext"/>.html</xsl:attribute>
      <xsl:attribute name="description">Project details for <xsl:value-of select="$name"/></xsl:attribute>
    </project>
  </xsl:template>
</xsl:stylesheet>

