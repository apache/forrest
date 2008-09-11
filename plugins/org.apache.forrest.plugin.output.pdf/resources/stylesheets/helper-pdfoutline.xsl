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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="1.0">
  <xsl:template
        match="document"
        mode="outline">
    <xsl:if test="count(body/section) &gt; 0">
      <fo:bookmark-tree>
        <xsl:if test="$disable-toc != 'true' and $toc-max-depth > 0">
          <fo:bookmark internal-destination="__toc__">
            <fo:bookmark-title>
              <!-- insert i18n stuff here -->
              <xsl:text>Table of contents</xsl:text>
            </fo:bookmark-title>
          </fo:bookmark>
        </xsl:if>
        <xsl:apply-templates
          select="body/section"
          mode="outline" />
      </fo:bookmark-tree>
    </xsl:if>
  </xsl:template>
  <xsl:template
        match="section"
        mode="outline">
    <fo:bookmark>
      <xsl:attribute
                name="internal-destination">
        <xsl:choose>
          <xsl:when
                        test="normalize-space(@id)!=''">
            <xsl:value-of
                            select="@id" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of
                            select="generate-id()" />
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <fo:bookmark-title>
        <xsl:variable name="section-nr">
          <xsl:number count="section" format="1.1.1.1.1.1.1" level="multiple"/>
        </xsl:variable>
        <xsl:if test="not(starts-with(title, $section-nr))">
          <xsl:value-of select="$section-nr"/>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:value-of
                    select="normalize-space(title)" />
      </fo:bookmark-title>
      <xsl:apply-templates
                select="section"
                mode="outline" />
    </fo:bookmark>
  </xsl:template>
</xsl:stylesheet>
