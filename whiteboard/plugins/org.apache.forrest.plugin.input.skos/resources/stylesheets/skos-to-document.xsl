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
<xsl:stylesheet version = "1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
		exclude-result-prefixes="rdf skos">

  <xsl:template match="/">
    <xsl:apply-templates select="rdf:RDF" />
  </xsl:template>

  <xsl:template match="rdf:RDF">
    <document>
      <xsl:call-template name="header"/>
      <xsl:call-template name="body"/>
    </document>
  </xsl:template>

  <xsl:template name="header">
    <header>
      <title>Glossary</title>
    </header>
  </xsl:template>

  <xsl:template name="body">
    <body>
      <xsl:apply-templates/>
    </body>
  </xsl:template>

  <xsl:template match="skos:Concept">
    <xsl:call-template name="concept"/>
  </xsl:template>

  <xsl:template match="skos:prefLabel">
    <em class="bold">
      <xsl:apply-templates/>
    </em>
  </xsl:template>

  <xsl:template match="skos:definition">
    <li>
      <xsl:apply-templates/>
    </li>
  </xsl:template>

  <xsl:template name="concept">
    <dl class="{local-name(.)}" id="{skos:prefLabel/text()}">
      <dt>
        <xsl:apply-templates select="skos:prefLabel"/>
      </dt>
      <dd class="{local-name(.)}">
        <ol>
          <xsl:apply-templates select="skos:definition"/>
        </ol>
      </dd>
      <xsl:if test="skos:related">
        <dd class="{local-name(skos:related)}">
          <p>See Also: </p>
          <ul>
            <xsl:apply-templates select="skos:related"/>
          </ul>
        </dd>
      </xsl:if>
    </dl>
  </xsl:template>

  <xsl:template match="skos:related">
    <xsl:variable name="relatedTerm" 
     		  select="@rdf:resource"/>
    <xsl:for-each select="../../skos:Concept">
    <xsl:if test="$relatedTerm = @rdf:about">
      <li>
        <link href="#{skos:prefLabel/text()}">
	  <xsl:value-of select="skos:prefLabel/text()"/>
        </link>
      </li>
    </xsl:if>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
