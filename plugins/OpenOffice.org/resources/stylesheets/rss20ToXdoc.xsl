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
<!--
Stylesheet for generating an XDoc from an RSS 1.0 feed.
-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  

  <xsl:output method="xml" version="1.0" encoding="UTF-8"
    doctype-public="-//APACHE//DTD Documentation V2.0//EN" 
    doctype-system="http://apache.org/forrest/dtd/document-v20.dtd"/>

  <xsl:template match="/">
    <document>
      <xsl:apply-templates/>
    </document>
  </xsl:template>
  
  <xsl:template match="rss">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="channel">
    <header>
      <title><xsl:value-of select="title"/></title>
    </header>
    <body>
      <xsl:apply-templates select="item"/>
      <section>
        <title>Source</title>
        <p>The above entries are syndicated from 
          <xsl:element name="a">
            <xsl:attribute name="href"><xsl:value-of select="link"/></xsl:attribute>
            <xsl:value-of select="title"/>
          </xsl:element>.</p>
      </section>
    </body>
  </xsl:template>
  
  <xsl:template match="item">
    <section>
      <title><xsl:value-of select="title"/></title>
      <xsl:apply-templates select="description"/>
      <xsl:if test="guid">
        <p>
        <xsl:element name="a">
          <xsl:attribute name="href"><xsl:value-of select="guid"/></xsl:attribute>
          Source
        </xsl:element>
        </p>
      </xsl:if>
      <p><xsl:value-of select="copyright"/></p>
      <p>(Published: <xsl:value-of select="pubDate"/>)</p>
    </section>
  </xsl:template>
  
  <xsl:template match="description">
    <p><xsl:value-of select="." disable-output-escaping="yes"/></p>
  </xsl:template>
</xsl:stylesheet>
