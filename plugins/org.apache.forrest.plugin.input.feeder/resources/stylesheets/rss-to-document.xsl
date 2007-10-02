<?xml version='1.0'?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:strip-space elements="*"/>
  <xsl:param name="title"/>
  
  <xsl:template match="rss">
    <document>
      <header>
        <xsl:choose>
          <xsl:when test="$title">
            <title><xsl:value-of select="$title"/></title>
          </xsl:when>
          <xsl:otherwise>
            <title><xsl:value-of select="channel/title"/></title>
          </xsl:otherwise>
        </xsl:choose>
      </header>
      <body>
        <xsl:apply-templates select="channel"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="channel">
    <section>
      <title><xsl:value-of select="title" disable-output-escaping="yes"/></title>
      <xsl:apply-templates select="item"/>
    </section>
  </xsl:template>
  <xsl:template match="item">
    <p class="itemTitle">
      <xsl:value-of select="title" disable-output-escaping="yes"/>
    </p>
    <p  class="itemLink">
      <link href="{link}">
      <xsl:value-of select="link"/>
      </link>
    </p>
    <p  class="itemDescription">
      <xsl:value-of select="description" disable-output-escaping="yes"/>
    </p>
  </xsl:template>
</xsl:stylesheet>
