<?xml version="1.0"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
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
        <xsl:apply-templates select="resume:header/resume:address"/>
        <xsl:apply-templates select="resume:header/resume:contact"/>
        <xsl:apply-templates select="*[name(.) != 'header']"/>
      </body>
    </document>
  </xsl:template>
  
  <xsl:template match="resume:address">
    <div id="resume-address">
      <section>
        <title>Address</title>
        <p>
          <xsl:value-of select="resume:street"/><br/>
          <xsl:value-of select="resume:suburb"/><br/>
          <xsl:value-of select="resume:city"/><br/>
          <xsl:value-of select="resume:postalCode"/><br/>
          <xsl:value-of select="resume:country"/><br/>
        </p>
      </section>
    </div>
  </xsl:template>
  
  <xsl:template match="resume:contact">
    <div id="resume-contact">
      <section>
        <title>Contact Details</title>
        <p>
          <xsl:apply-templates select="resume:phone"/>
          <xsl:apply-templates select="resume:email"/>
          <xsl:apply-templates select="resume:instantMessage"/>
        </p>
      </section>
    </div>
  </xsl:template>
  
  <xsl:template match="resume:phone">
    Phone <xsl:value-of select="@location"/>: <xsl:value-of select="."/><br/>
  </xsl:template>
  
  <xsl:template match="resume:email">
    EMail: 
    <a>
      <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
      <xsl:value-of select="."/>
    </a><br/>
  </xsl:template>
  
  <xsl:template match="resume:instantMessage">
    Instant Message (<xsl:value-of select="@service"/>): <xsl:value-of select="."/><br/>
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
  
  <xsl:template match="resume:objective">
    <section>
      <title>Objective</title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:availability">
    <section>
      <title>Availability</title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:history">
    <section>
      <title>Employment History</title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:academics">
    <section>
      <title>Academic History</title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:interests">
    <section>
      <title>Interests</title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:interest">
    <section>
      <title><xsl:value-of select="resume:title"/></title>
      <xsl:apply-templates/>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:degrees">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="resume:degree">
    <section>
      <title><xsl:value-of select="resume:major"/> (<xsl:value-of select="resume:level"/>)</title>
      <p>
        <xsl:value-of select="resume:institution"/> 
        <xsl:if test="resume:date"> (<xsl:value-of select="resume:date"/>)</xsl:if>
        <xsl:if test="resume:annotation"> <xsl:value-of select="resume:annotation"/></xsl:if>
      </p>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:job">
    <section>
      <title><xsl:value-of select="resume:jobtitle"/></title>
      <table class="job">
        <tr>
          <th width="10%">Employer:</th>
          <td><xsl:value-of select="resume:employer"/></td>
        </tr>
        <tr>
          <th>Period:</th>
          <td><xsl:apply-templates select="resume:period"/></td>
        </tr>
        <tr>
          <th colspan='2'>Job Description</th>
        </tr>
        <tr>
          <td colspan='2'><xsl:apply-templates select="resume:description"/></td>
        </tr>
        <xsl:apply-templates select="resume:achievements"/>
        <xsl:apply-templates select="resume:projects"/>
      </table>
    </section>
  </xsl:template>
  
  <xsl:template match="resume:achievements">
    <tr>
      <th colspan='2'>Achievements</th>
    </tr>
    <xsl:apply-templates select="resume:achievement"/>
  </xsl:template>
  
  <xsl:template match="resume:achievement">
    <tr>
      <td colspan='2'>
        <xsl:apply-templates select="text()"/>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template match="resume:projects">
    <tr>
      <th colspan='2'>Significant Projects</th>
    </tr>
    <xsl:apply-templates select="resume:project"/>
  </xsl:template>
  
  <xsl:template match="resume:project">
    <tr>
      <th colspan="2"><xsl:value-of select="@title"/></th>
    </tr>
    <tr>
      <td colspan="2">
      <p><xsl:value-of select="text()"/></p>
      <xsl:if test="resume:url">
        <p>
          <a>
            <xsl:attribute name="href"><xsl:value-of select="resume:url"/></xsl:attribute>
            More information...
          </a>
        </p>
      </xsl:if>
      </td>
    </tr>
  </xsl:template>
  
  <xsl:template match="resume:period">
    <xsl:value-of select="resume:from"/>
    -
    <xsl:choose>
      <xsl:when test="resume:to/resume:present">
        present
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="resume:to"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="resume:skillareas">
    <section>
      <title>Skills</title>
      <xsl:apply-templates/>
    </section>
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
      <table class="skillset">
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
  
  <xsl:template match="resume:para">
    <p><xsl:apply-templates/></p>
  </xsl:template>
  
</xsl:stylesheet>
