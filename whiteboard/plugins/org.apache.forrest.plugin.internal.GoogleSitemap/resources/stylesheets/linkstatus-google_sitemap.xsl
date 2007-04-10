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
<!--the input for this stylesheet is the Cocoon LinkStatusGenerator XML output-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:java="java" exclude-result-prefixes="java">
  <xsl:output method="xml" indent="yes" />
  <xsl:param name="request-root"/>
  <xsl:param name="site-root"/>
  <xsl:variable name="ns.uri.google-sitemap" value="'http://www.google.com/schemas/sitemap/0.84'"/>
  <xsl:template match="/">
    <urlset xmlns="$ns.uri.google-sitemap">
      <xsl:apply-templates select="//*[boolean(@href)]"/>
    </urlset>
  </xsl:template>
  <xsl:template match="//*[boolean(@href)]">
    <url>
      <loc>
        <xsl:variable name="relativeURI" select="substring-after(@href, $request-root)"/>
        <xsl:choose>
<!--used when generating a static site and the site uri needs to replace the local uri-->
          <xsl:when test="string-length($site-root) &gt; 1 and $site-root != 'null'">
            <xsl:value-of select="concat($site-root, $relativeURI)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@href"/>
          </xsl:otherwise>
        </xsl:choose>
      </loc>
<!--
			TODO-Think: below the user needs a means of controlling the advanced google sitemaps options (elements and values)
			like <changefreq>daily</changefreq>
			SEE: http://www.google.com/webmasters/sitemaps/docs/en/protocol.html#xmlTagDefinitions 
			-->
      <lastmod>
<!--
			TODO-Replace: this is obviously a HACK, hopefully one day this will be provided from the LinkStatusGenerator xml ouput
			which is the input for this stylsheet. We should then take that result and format it as 'yyyy-MM-dd' per Google's specs
			-->
        <xsl:value-of select="java:format(java:text.SimpleDateFormat.new('yyyy-MM-dd'), java:util.Date.new())"/>
      </lastmod>
    </url>
  </xsl:template>
<!--do nothing with text nodes-->
  <xsl:template match="*/text()"/>
</xsl:stylesheet>
