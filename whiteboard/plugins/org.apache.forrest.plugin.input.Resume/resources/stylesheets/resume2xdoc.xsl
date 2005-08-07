<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
  as applicable.

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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:resume="http://xmlresume.sourceforge.net/resume/0.0"
    version="1.0">

  <xsl:param name="table.levelWidth">20%</xsl:param>
  
  <xsl:template match="/">
    <xsl:apply-templates select="resume:resume"/>
  </xsl:template>
  
  <xsl:template match="resume:resume">
    <document>
        <xsl:apply-templates select="resume:header"/>
      <body>
        <xsl:apply-templates select="resume:skillarea"/>
      </body>
    </document>
  </xsl:template>
  
  <xsl:template match="resume:header">
    <header>
      <title>          
          <xsl:apply-templates select="resume:name"/>
      </title>
    </header>
  </xsl:template>
  
  <xsl:template match="resume:name">
     <xsl:value-of select="resume:surname"/>, <xsl:value-of select="resume:firstname"/>
  </xsl:template>
  
  <xsl:template match="resume:skillarea">
    <section>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:title">
    <title><xsl:value-of select="."/></title>
  </xsl:template>
  
  <xsl:template match="resume:skillset">
    <section>
      <xsl:apply-templates select="resume:title"/>
      <table>
        <tr>
          <th>Skill</th>
          <th width="{$table.levelWidth}">Level</th>
        </tr>
        <xsl:apply-templates select="resume:skill"/>
      </table>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:skill">
    <tr>
      <td><xsl:value-of select="."/></td>
      <td width="{$table.levelWidth}"><xsl:value-of select="@level"/></td>
    </tr>
  </xsl:template>
  
</xsl:stylesheet>
