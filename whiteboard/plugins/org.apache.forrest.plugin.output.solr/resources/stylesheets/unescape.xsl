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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:template name="unescapeEm">
    <xsl:param name="val" select="''"/>
    <xsl:variable name="preEm" select="substring-before($val, '&lt;')"/>
    <xsl:choose>
      <xsl:when test="$preEm or starts-with($val, '&lt;')">
        <xsl:variable name="insideEm" select="substring-before($val,
'&lt;/')"/>
        <xsl:value-of select="$preEm"/>
        <em>
          <xsl:value-of select="substring($insideEm, string-length($preEm)+5)"/>
        </em>
        <xsl:variable name="leftover"
          select="substring($val,
string-length($insideEm) + 6)"/>
        <xsl:if test="$leftover">
          <xsl:call-template name="unescapeEm">
            <xsl:with-param name="val" select="$leftover"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$val"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
