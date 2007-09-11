<?xml version="1.0"?>
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
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/"
    exclude-result-prefixes="rdf dc foaf">

    <xsl:template match="/">
      <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="rdf:seeAlso">
      <xsl:variable name="seeAlsoFile"><xsl:value-of select="@rdf:resource"/></xsl:variable>
      <xsl:apply-templates select="document($seeAlsoFile)/rdf:RDF/foaf:Person/*"/>
    </xsl:template>
    
    <xsl:template match="text()|processing-instruction()|comment()">
      <xsl:copy>
        <xsl:apply-templates/>
      </xsl:copy>
    </xsl:template>
    
    <xsl:template match="@*|node()">
       <xsl:if test="not(node()) or not(preceding-sibling::node()[.=string(current()) and name()=name(current())])">
        <xsl:copy>
          <xsl:apply-templates/>
        </xsl:copy>
      </xsl:if>
    </xsl:template>
</xsl:stylesheet>