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
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:doap="http://usefulinc.com/ns/doap#"
    xmlns:foaf="http://xmlns.com/foaf/0.1/">
    
    <xsl:output method="text" indent="no" />

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="descriptors">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="descriptor">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="doap:Project">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="doap:maintainer">
      <xsl:if test="starts-with(foaf:Person/foaf:mbox/@rdf:resource, 'mailto:')">
        <xsl:value-of select="normalize-space(../doap:name)" />,<xsl:apply-templates />
<xsl:text>

</xsl:text>
      </xsl:if>
    </xsl:template>
    <xsl:template match="foaf:Person">
        <xsl:apply-templates select="foaf:name" />,<xsl:apply-templates select="foaf:mbox" />
    </xsl:template>
    
    <xsl:template match="foaf:name">
      <xsl:choose>
          <xsl:when test="contains(., ' ')">
            <xsl:value-of select="normalize-space(substring-before(., ' '))"/>,<xsl:value-of select="normalize-space(substring-after(., ' '))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>,</xsl:text><xsl:value-of select="normalize-space(.)"/>
          </xsl:otherwise>
      </xsl:choose>
    </xsl:template>
    
    <xsl:template match="foaf:mbox">
      <xsl:if test="starts-with(@rdf:resource, 'mailto:')">
        <xsl:value-of select="substring(@rdf:resource,8)"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="text()">
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:template>
    
    <xsl:template
        match="@*|*|processing-instruction()|comment()" />
</xsl:stylesheet>
