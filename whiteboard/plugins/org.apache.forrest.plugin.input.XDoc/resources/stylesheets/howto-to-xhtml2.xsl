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
<!--
This stylesheet contains the majority of templates for converting documentv11
to HTML.  It renders XML as HTML in this form:

  <div class="content">
   ...
  </div>

..which site2xhtml.xsl then combines with HTML from the index (book2menu.xsl)
and tabs (tab2menu.xsl) to generate the final HTML.

Section handling
  - <a name/> anchors are added if the id attribute is specified

-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns="http://www.w3.org/2002/06/xhtml2">
  
  <xsl:import href="document-to-xhtml2.xsl"/>
  
  
  <xsl:template match="/howto | /document | /overview">
    <html xmlns="http://www.w3.org/2002/06/xhtml2" xml:lang="en"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd"
    >
      <head>
        <title><xsl:value-of select="header/title"/></title>
        <xsl:apply-templates select="header/meta"/>
        <xsl:apply-templates select="header/version"/>
        <xsl:apply-templates select="header/authors"/>
        <xsl:apply-templates select="header/type | header/subtitle"/>
        <xsl:apply-templates select="header/last-modified-content-date"/>
      </head>
      <body>
        <xsl:apply-templates select="header/abstract"/>
        
        <xsl:apply-templates select="*[not(name()='header')]"/>

      </body>
    </html>
  </xsl:template>

  <xsl:template match="audience | extension | feedback | prerequisites | purpose | references | steps | tips">
    <section>
      <xsl:if test="./@id">
        <xsl:attribute name="xml:id"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="class"><xsl:value-of select="local-name()"/></xsl:attribute>
       <h><xsl:value-of select="./@title"/></h>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="last-modified-content-date">
    <xsl:element name="meta">
      <xsl:attribute name="property">last-modified-content-date</xsl:attribute>
      <xsl:value-of select="./@date"/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="faqs | faq | faqsection | part">
    <section>
      <xsl:if test="./@id">
        <xsl:attribute name="xml:id"><xsl:value-of select="@id"/></xsl:attribute>
      </xsl:if>
      <xsl:attribute name="class"><xsl:value-of select="local-name()"/></xsl:attribute>
      <xsl:if test="./@title">
        <h><xsl:value-of select="./@title"/></h>
      </xsl:if>
      <xsl:apply-templates/>
    </section>
  </xsl:template>

  <xsl:template match="question | answer">
    <section>
      <xsl:attribute name="class"><xsl:value-of select="local-name()"/></xsl:attribute>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
</xsl:stylesheet>

