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
Stylesheet for generating an aggregated feed from multple feeds.
-->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rss="http://purl.org/rss/1.0/">
  <xsl:output method="xml" version="1.0" encoding="UTF-8"/>
  <xsl:template match="feedDescriptor">
    <rss version="2.0">
      <xsl:apply-templates/>
    </rss>
  </xsl:template>
  
  <xsl:template match="feed">
    <xsl:variable name="url" select="url"/>
    <xsl:variable name="feed" select="document($url)"/>
    <xsl:choose>
        <!--  Standard RSS Feed  -->
        <xsl:when test="$feed/rss">
            <xsl:apply-templates select="$feed/rss/channel"/>
        </xsl:when>
        <!--  RSS as RDF as used in, for example, del.icio.us -->
        <xsl:when test="$feed/rdf:RDF">
            <xsl:apply-templates select="$feed/*" mode="delicious"/>
        </xsl:when>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="rdf:RDF" mode="delicious">
    <channel>
      <xsl:apply-templates select="rss:channel" mode="delicious"/>
      <xsl:apply-templates select="rss:item" mode="delicious"/>
    </channel>
  </xsl:template>
  
  <xsl:template match="rss:channel" mode="delicious">
    <title><xsl:value-of select="rss:title"/></title>
    <link><xsl:value-of select="rss:link"/></link>
    <description><xsl:value-of select="rss:description"/></description>
  </xsl:template>
  
  <xsl:template match="rss:item" mode="delicious">
    <item>
      <title><xsl:value-of select="rss:title"/></title>
      <link><xsl:value-of select="rss:link"/></link>
    </item>
  </xsl:template>
  
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
