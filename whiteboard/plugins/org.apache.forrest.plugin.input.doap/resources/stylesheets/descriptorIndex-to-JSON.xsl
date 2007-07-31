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
  <xsl:template match="/">{ "items": [<xsl:apply-templates select="descriptors"/>]} 
  </xsl:template>
  
  <xsl:template match="descriptors"><xsl:apply-templates select="descriptor"/></xsl:template>
  
  <xsl:template match="descriptor">
    {<xsl:apply-templates select="doap:Project|rdf:RDF|atom:feed"/>
    }<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>

  <xsl:template match="atom:feed"><xsl:apply-templates select="atom:entry/atom:content/doap:Project"/></xsl:template>
  
  <xsl:template match="rdf:RDF"><xsl:apply-templates select="doap:Project"/></xsl:template>
  
  <xsl:template match="doap:Project">
    "label":"<xsl:value-of select="doap:name"/>",
    <xsl:call-template name="categories"/>
    <xsl:apply-templates select="doap:name|doap:shortdesc|doap:homepage"/>
  </xsl:template>
  
  <xsl:template match="doap:homepage">
    "<xsl:value-of select="local-name(.)"/>":"<xsl:apply-templates select="@rdf:resource"/>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
  
  <xsl:template name="categories">
    <xsl:if test="doap:category">
      "Category" : [<xsl:apply-templates select="doap:category"/>],
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:category">
    "<xsl:apply-templates select="@rdf:resource"/>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:*">
    "<xsl:value-of select="local-name(.)"/>":"<xsl:value-of select="normalize-space(.)"/>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
    
</xsl:stylesheet>
