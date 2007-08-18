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
  xmlns:doap="http://usefulinc.com/ns/doap#"
  xmlns:dc="http://purl.org/dc/elements/1.1/">
  <xsl:include href="descriptorIndex-to-indexByCommon.xsl"/>
  <xsl:key name="kDistinctCategory" match="doap:category" use="@rdf:resource"/>
  <xsl:param name="filter"/>
  <xsl:template match="/">
    <document>
      <header>
        <title>Projects Indexed by Category</title>
      </header>
      <body>
        <xsl:choose>
          <xsl:when test="$filter">
            <section>
              <title>Index of projects in the category of <xsl:value-of select="$filter"/></title>
              <xsl:apply-templates select="//descriptor[descendant::doap:category/@rdf:resource = $filter]"/>
            </section>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="//doap:category[generate-id() = generate-id(key('kDistinctCategory', @rdf:resource))]">
              <xsl:sort select="@rdf:resource"/>
              <xsl:variable name="category" select="@rdf:resource"/>
              <xsl:if test="//descriptor[descendant::doap:category[@rdf:resource = $category]]">
                  <section>
                    <title>
                        <xsl:choose>
                          <xsl:when test="//projectDetails/categories/doap:category[@rdf:resource = $category]/@dc:title">
                            <xsl:value-of select="//projectDetails/categories/doap:category[@rdf:resource = $category]/@dc:title"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:choose>
                              <xsl:when test="@dc:title">
                                <xsl:value-of select="@dc:title"/>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:value-of select="@rdf:resource"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:otherwise>
                        </xsl:choose>
                    </title>
                    <xsl:apply-templates select="//descriptor[descendant::doap:category[@rdf:resource = $category]]"/>
                  </section>
              </xsl:if>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="//descriptor[not(descendant::doap:category)]">
          <section>
            <title>Uncategorised</title>
            <xsl:apply-templates select="//descriptor[not(descendant::doap:category)]"/>
          </section>
        </xsl:if>
      </body>
    </document>
  </xsl:template>
</xsl:stylesheet>
