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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="command"/>
  <xsl:template match="/">
    <document>
      <header>
        <xsl:choose>
          <xsl:when test="not(response/lst[@name='responseHeader']/int[@name='status']/text()='0')">
            <title>error in solr operation </title>
          </xsl:when>
          <xsl:otherwise>
            <title>success in solr operation </title>
          </xsl:otherwise>
        </xsl:choose>
      </header>
      <body>
        <xsl:apply-templates select="response"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="response">
    <section id="result">
      <title>solr server response</title>
      <p>
        Command:
        <xsl:value-of select="$command"/>
      </p>
      <xsl:choose>
        <xsl:when test="not(lst[@name='responseHeader']/int[@name='status']/text()='0')">
          <warning>
            There was a problem:
            <xsl:value-of select="."/>
          </warning>
        </xsl:when>
        <xsl:otherwise>
          <p>
            Success.
          </p>
        </xsl:otherwise>
      </xsl:choose>
    </section>
  </xsl:template>
</xsl:stylesheet>
