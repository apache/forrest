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
                xmlns:skos="http://www.w3.org/2004/02/skos/core#"
                xmlns:exsl="http://exslt.org/common"
                extension-element-prefixes="exsl"
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

    <xsl:variable name="concepts">
      <xsl:for-each select="skos:Concept">
        <!-- Sort wrapping sections -->
        <xsl:sort select="skos:prefLabel"/>
	<xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:variable>

    <body>
      <!-- Ignore terms starting with the same letter except the first one. -->
      <xsl:for-each select="exsl:node-set($concepts)/skos:Concept[not(substring(skos:prefLabel, 1, 1)=substring(following-sibling::skos:Concept/skos:prefLabel, 1, 1))]">
        <xsl:variable name="first-char" 
		      select="substring(skos:prefLabel, 1, 1)"/>
        <section id="{$first-char}">
          <title>
  	    <xsl:value-of select="$first-char"/>
	  </title>
          <xsl:apply-templates 
	             select="../*[substring(skos:prefLabel, 1, 1)=$first-char]">
	    <!-- Sort terms inside a wrapping section -->
  	    <xsl:sort select="skos:prefLabel"/>
          </xsl:apply-templates>
	</section>
      </xsl:for-each>
    </body>
  </xsl:template>

  <xsl:template match="skos:Concept">
    <xsl:call-template name="concept"/>
  </xsl:template>

  <xsl:template match="skos:prefLabel">
    <dt>
      <strong>
        <xsl:apply-templates/>
      </strong>
    </dt>
  </xsl:template>

  <xsl:template match="skos:definition">
    <li class="{local-name(.)}">
      <xsl:apply-templates/>
    </li>
  </xsl:template>

  <xsl:template name="concept">
    <dl class="concept" id="{skos:prefLabel[1]/text()}">
      <xsl:apply-templates select="skos:prefLabel[1]"/>
      <dd class="definition">
        <ol>
          <xsl:apply-templates select="skos:definition"/>
        </ol>
      </dd>

      <xsl:if test="skos:related">
        <dd class="related">
          <!-- il8n? -->
	  <p>See Also:</p>
          <ul>
            <xsl:apply-templates select="skos:related"/>
          </ul>
	</dd>
      </xsl:if>
    </dl>
  </xsl:template>

  <xsl:template match="skos:related">
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

</xsl:stylesheet>
