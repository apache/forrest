<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  exclude-result-prefixes="cinclude">

  <xsl:key name="node-id" match="*" use="@id"/>

  <!-- If we encounter a section with an @id, make that @id globally unique by
  prefixing the id of the current document -->
  <xsl:template match="section/document//@id">
    <xsl:attribute name="id"><xsl:value-of select="concat(ancestor::section/@id, '#', .)"/></xsl:attribute>
  </xsl:template>
  
  <!-- Make #fragment-id references inside each page globally unique -->
  <xsl:template match="section/document//link/@href[starts-with(., '#')]">
    <xsl:attribute name="href"><xsl:value-of select="concat('#', ancestor::section/@id, .)"/></xsl:attribute>
  </xsl:template>

  <!-- Translate relative links like 'index.html' to '#index.html' -->
  <xsl:template match="section/document//link/@href[contains(., '.html')]">
    <xsl:attribute name="href"><xsl:text>#</xsl:text><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <xsl:template match="section/document//img[starts-with(@src, 'my-images')]">
    <!-- Zap my-images/** links, which break as they are not relative to the site root -->
  </xsl:template>
  <xsl:include href="../copyover.xsl"/>

</xsl:stylesheet>
