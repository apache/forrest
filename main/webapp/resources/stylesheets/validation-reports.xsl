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
  <xsl:template match="/">
    <html>
      <head>
        <title>Validation reports</title>
      </head>
      <body>
        <h1>Validation reports</h1>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="reports">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="report">
    <xsl:variable name="fn">
      <xsl:value-of select="substring-before(@filename, '.validation.xml')"/>
    </xsl:variable>
    <p><strong><xsl:value-of select="$fn"/></strong>.validation.xml</p>
    <ul>
      <xsl:choose>
        <xsl:when test="count(error)=0">
          <li>No errors</li>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </ul>
  </xsl:template>
  <xsl:template match="error">
    <li>
      <xsl:apply-templates/>
    </li>
  </xsl:template>
</xsl:stylesheet>
