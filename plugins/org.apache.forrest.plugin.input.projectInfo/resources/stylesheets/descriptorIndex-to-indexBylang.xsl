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
  
  <xsl:include href="descriptorIndex-to-indexByCommon.xsl"/>
  
  <xsl:key name="kDistinctProgLang" match="doap:programming-language" use="."/>
  
  <xsl:param name="language"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>Projects Indexed by Language</title>
      </head>
      <body>
        <xsl:choose>
          <xsl:when test="$language">
            <h1>Index of projects using <xsl:value-of select="$language"/></h1>
            <xsl:apply-templates select="//descriptor[descendant::doap:programming-language = $language]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="//doap:programming-language[generate-id() = generate-id(key('kDistinctProgLang',.))]">
              <xsl:sort select="."/>
              <xsl:variable name="language" select="."/>
              <h1><xsl:value-of select="$language"/></h1>
              <xsl:apply-templates select="//descriptor[descendant::doap:programming-language = $language]"/>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
                
        <xsl:if test="//descriptor[not(descendant::doap:programming-language)]">
          <h1>Unkown Language</h1>
          <xsl:apply-templates select="//descriptor[not(descendant::doap:programming-language)]"/>
        </xsl:if>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>

