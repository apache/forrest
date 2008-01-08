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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"
                exclude-result-prefixes="rdf skos dc"
                version="1.0">

  <xsl:template match="/">
    <xsl:apply-templates select="rdf:RDF" mode="skos.glossary"/>
  </xsl:template>

  <xsl:template match="rdf:RDF" mode="skos.glossary">
    <document>
      <xsl:call-template name="skos.glossary.header"/>
      <xsl:call-template name="skos.glossary.body"/>
    </document>
  </xsl:template>

  <xsl:template name="skos.glossary.header">
    <header>
      <xsl:call-template name="skos.glossary.title"/>
    </header>
  </xsl:template>

  <xsl:template name="skos.glossary.title">
    <title>
      <xsl:choose>
        <xsl:when test="rdf:Description/dc:title">
          <xsl:value-of select="rdf:Description[1]/dc:title[1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- FIXME: il8n -->
          <xsl:message terminate="no">
            <xsl:text>WARNING: No title found for the glossary.</xsl:text>
          </xsl:message>
          <xsl:text>Glossary</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </title>
  </xsl:template>

  <xsl:template name="skos.glossary.body">
    <xsl:variable name="concepts">
      <xsl:for-each select="skos:Concept">
        <xsl:sort select="skos:prefLabel"/>
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:variable>

    <!-- upper/lower-case letters for case conversion -->
    <xsl:variable name="ucl">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
    <xsl:variable name="lcl">abcdefghijklmnopqrstuvwxyz</xsl:variable>

    <body>
      <xsl:if test="rdf:Description">
        <section>
          <title>
            <!-- FIXME: il8n -->
            <xsl:text>Description</xsl:text>
          </title>
          <xsl:apply-templates select="rdf:Description" mode="skos.glossary"/>
        </section>
      </xsl:if>

      <!-- Ignore terms starting with the same letter except the first one -->
      <!-- FIXME: use of exsl is not necessary -->
      <xsl:for-each select="exsl:node-set($concepts)/skos:Concept[not(translate(substring(skos:prefLabel, 1, 1), $ucl, $lcl)=translate(substring(following-sibling::skos:Concept/skos:prefLabel, 1, 1), $ucl, $lcl))]">
        <xsl:variable name="firstChar" select="substring(skos:prefLabel, 1, 1)"/>

        <section id="{$firstChar}">
          <title>
            <xsl:value-of select="translate($firstChar, $lcl, $ucl)"/>
          </title>
          <xsl:apply-templates select="../*[substring(skos:prefLabel, 1, 1)=$firstChar]" mode="skos.glossary">
            <!-- Sort terms inside a wrapping section -->
            <xsl:sort select="skos:prefLabel"/>
          </xsl:apply-templates>
        </section>
      </xsl:for-each>
    </body>
  </xsl:template>

  <xsl:template match="skos:Concept" mode="skos.glossary">
    <xsl:call-template name="skos.glossary.concept"/>
  </xsl:template>

  <xsl:template match="skos:prefLabel" mode="skos.glossary">
    <strong>
      <xsl:apply-templates/>
    </strong>
    <!-- FIXME: il8n -->
    <xsl:if test="position() != last()">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="skos:definition" mode="skos.glossary">
    <li class="{local-name(.)}">
      <xsl:apply-templates mode="skos.glossary"/>
    </li>
  </xsl:template>

  <xsl:template name="skos.glossary.concept">
    <dl class="concept" id="{skos:prefLabel[1]/text()}">
      <dt>
        <xsl:apply-templates select="skos:prefLabel" mode="skos.glossary"/>
      </dt>
      <dd class="definition">
        <ul>
          <xsl:apply-templates select="skos:definition" mode="skos.glossary"/>
        </ul>
      </dd>
      <xsl:apply-templates select="rdfs:*" mode="skos.glossary"/>
      <xsl:if test="skos:related">
        <dd class="related">
          <!-- FIXME: il8n -->
          <p>See Also:</p>
          <ul>
            <xsl:apply-templates select="skos:related" mode="skos.glossary"/>
          </ul>
        </dd>
      </xsl:if>
    </dl>
  </xsl:template>

  <xsl:template match="skos:related" mode="skos.glossary">
    <xsl:variable name="relatedConcept" 
                  select="@rdf:resource"/>

    <xsl:for-each select="../../skos:Concept">
      <xsl:if test="$relatedConcept = @rdf:about">
        <li class="related">
          <link href="#{skos:prefLabel/text()}">
            <xsl:value-of select="skos:prefLabel/text()"/>
          </link>
        </li>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="/*/rdf:Description" mode="skos.glossary">
    <xsl:apply-templates select="*|@*" mode="skos.glossary"/>
  </xsl:template>

  <xsl:template match="@rdf:about[. != '']" mode="skos.glossary">
    <p class="{local-name(.)}">
      <strong>
        <!-- FIXME: il8n -->
        <xsl:text>Identifier: </xsl:text>
      </strong>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="."/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="skos.glossary.title"/>
        </xsl:attribute>
        <xsl:value-of select="."/>
      </a>
    </p>
  </xsl:template>

  <xsl:template match="dc:title" mode="skos.glossary"></xsl:template>

  <xsl:template match="dc:date" mode="skos.glossary">
    <p class="{local-name(.)}">
      <strong>
        <!-- FIXME: il8n -->
        <xsl:text>Date: </xsl:text>
      </strong>
      <span>
        <xsl:apply-templates/>
      </span>
    </p>
  </xsl:template>

  <xsl:template match="dc:description" mode="skos.glossary">
    <p class="{local-name(.)}">
      <xsl:apply-templates/>
    </p>
  </xsl:template>

  <xsl:template match="rdfs:isDefinedBy" mode="skos.glossary">
    <p class="{local-name(.)}">
      <strong>
        <!-- FIXME: il8n -->
        <xsl:text>Source: </xsl:text>
      </strong>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="@rdf:resource"/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="skos.glossary.title"/>
        </xsl:attribute>
        <xsl:value-of select="@rdf:resource"/>
      </a>
    </p>
  </xsl:template>

  <xsl:template match="/*/rdf:Description/dc:rights" mode="skos.glossary">
    <p class="{local-name(.)}">
      <strong>
        <!-- FIXME: il8n -->
        <xsl:text>Legal Notice: </xsl:text>
      </strong>
      <a>
        <xsl:attribute name="href">
          <xsl:value-of select="@rdf:resource"/>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:call-template name="skos.glossary.title"/>
        </xsl:attribute>
        <xsl:value-of select="@rdf:resource"/>
      </a>
    </p>
  </xsl:template>

</xsl:stylesheet>
