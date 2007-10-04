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
  <xsl:param name="versionNumber"/>
  <xsl:param name="changes-url"
    select="concat(//skinconfig/project-url, 'changes.html')"/>
  <xsl:param name="project-name" select="//skinconfig/project-name"/>
  <xsl:template match="status">
    <rss version="0.91">
      <channel>
        <xsl:choose>
          <xsl:when test="$versionNumber = 'current'">
            <title><xsl:value-of select="$project-name"/> (<xsl:value-of select="//release[1]/@version"/>) Changes</title>
          </xsl:when>
          <xsl:when test="$versionNumber">
            <title><xsl:value-of select="$project-name"/> (<xsl:value-of select="$versionNumber"/>) Changes</title>
          </xsl:when>
          <xsl:otherwise>
            <title><xsl:value-of select="$project-name"/> (<xsl:value-of select="//release[1]/@version"/>) Changes</title>
          </xsl:otherwise>
        </xsl:choose><link>
        <xsl:value-of select="$changes-url"/></link>
        <xsl:choose>
          <xsl:when test="$versionNumber = 'current'">
            <description>
              <xsl:value-of select="$project-name"/>
              (
              <xsl:value-of select="//release[1]/@version"/>
              ) Changes
            </description>
          </xsl:when>
          <xsl:when test="$versionNumber">
            <description>
              <xsl:value-of select="$project-name"/>
              (
              <xsl:value-of select="$versionNumber"/>
              ) Changes
            </description>
          </xsl:when>
          <xsl:otherwise>
            <description>
              <xsl:value-of select="$project-name"/>
              (
              <xsl:value-of select="//release[1]/@version"/>
              ) Changes
            </description>
          </xsl:otherwise>
        </xsl:choose>
        <language>en-us</language>
        <xsl:choose>
          <xsl:when test="$versionNumber">
            <xsl:choose>
              <xsl:when test="$versionNumber='current'">
                <xsl:apply-templates select="//release[1]"/>
              </xsl:when>
              <xsl:when test="$versionNumber">
                <xsl:apply-templates select="//release[@version=$versionNumber]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="//release[1]"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="changes/release[1]/action"/>
      </channel>
    </rss>
  </xsl:template>
  <xsl:template match="action">
    <item>
      <title><xsl:value-of select="$project-name"/><xsl:text> </xsl:text><xsl:value-of select="@context" />
<xsl:text> </xsl:text>
        <xsl:value-of select="@type" />
        <xsl:if test="@fixes-bug">
          (bug <xsl:value-of select="@fixes-bug" />)
        </xsl:if></title><link>
      <xsl:value-of select="$changes-url"/></link>
      <description>
        <xsl:value-of select="@context" />
<xsl:text> </xsl:text>
        <xsl:value-of select="@type" />
        by
        <xsl:value-of select="@dev" />
        <xsl:if test="@fixes-bug">
          (fixes bug <xsl:value-of select="@fixes-bug" />)
        </xsl:if>
        :
        <xsl:value-of select="." />
        <xsl:if test="@due-to"> Thanks to <xsl:value-of select="@due-to" />.</xsl:if>
      </description>
    </item>
  </xsl:template>
  <xsl:template match="skinconfig"/>
  <xsl:template match="notes"/>
  <xsl:template match="todo"/>
</xsl:stylesheet>
