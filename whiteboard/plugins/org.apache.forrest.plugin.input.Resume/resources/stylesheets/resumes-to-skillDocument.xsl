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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:tag="http://forrest.apache.org/TagExpansion/0.1"
    xmlns:resume="http://xmlresume.sourceforge.net/resume/0.0"
    version="1.0">
  <xsl:import href="lm://Resume.transform.resume.document"/>
  <xsl:key name="skillSets" match="resume:skillset" use="resume:title"/>
  <xsl:key name="skills" match="resume:skill" use="."/>
  <xsl:template match="/">
    <xsl:apply-templates select="resumes"/>
  </xsl:template>
  <xsl:template match="resumes">
    <document>
      <header>
        <title>All Candidates By Skill</title>
      </header>
      <body>
        <xsl:apply-templates select="file"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="file">
    <xsl:for-each select="//resume:skillset[generate-id(.) = generate-id(key('skillSets', resume:title)[1])]">
      <xsl:sort select="."/>
      <xsl:variable name="skillSet">
        <xsl:value-of select="."/>
      </xsl:variable>
      <section>
        <title><xsl:value-of select="resume:title"/></title>
        <xsl:for-each select="resume:skill[generate-id(.) = generate-id(key('skills', .)[1])]">
          <xsl:sort select="."/>
          <xsl:variable name="skill">
            <xsl:value-of select="."/>
          </xsl:variable>
          <section>
            <title><xsl:value-of select="."/></title>
            <table>
              <tr>
                <th>Candidate</th>
                <th width="{$table.levelWidth}">Level</th>
              </tr>
              <xsl:apply-templates select="//resume:resume[resume:skillarea/resume:skillset/resume:skill = $skill]">
                <xsl:with-param name="skill">
                  <xsl:value-of select="$skill"/>
                </xsl:with-param>
              </xsl:apply-templates>
            </table>
          </section>
        </xsl:for-each>
      </section>
    </xsl:for-each>
  </xsl:template>
  <xsl:template match="resume:resume">
    <xsl:param name="skill"/>
    <tr>
      <td>
        <xsl:apply-templates select="resume:header"/>
      </td>
      <td width="{$table.levelWidth}">
        <xsl:value-of select="resume:skillarea/resume:skillset/resume:skill[. = $skill]/@level"/>
      </td>
    </tr>
  </xsl:template>
  <xsl:template match="resume:header">
    <xsl:apply-templates select="resume:name"/>
  </xsl:template>
</xsl:stylesheet>
