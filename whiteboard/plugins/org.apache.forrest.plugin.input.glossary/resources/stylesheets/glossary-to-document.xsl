<?xml version="1.0"?>
<!--
  Copyright 2006 The Apache Software Foundation or its licensors,
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
    version="1.0">

    <xsl:import href="copyover.xsl"/>

  <xsl:template match="glossary">
   <document>
    <header>
     <title>Glossary</title>
    </header>
    <body>
      <xsl:apply-templates select="part"/>
    </body>
   </document>  
  </xsl:template>

  <xsl:template match="part">
    <section>
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <title><xsl:value-of select="title"/></title>
      <table>
        <tr>
          <th>Term</th>
          <th>Definition(s)</th>
          <th>Tutorial(s)</th>
        </tr>
        <xsl:apply-templates select="item"/>
      </table>
    </section>
  </xsl:template>

  <xsl:template match="item">
    <xsl:variable name="id">
      <xsl:call-template name="generate-id"/>
    </xsl:variable>
    <tr>
    <xsl:choose>
      <xsl:when test="acronym">
        <td id="{$id}"><xsl:apply-templates select="term"/> (<xsl:apply-templates select="acronym"/>)</td>
      </xsl:when>
      <xsl:otherwise>
        <td id="{$id}"><xsl:apply-templates select="term"/></td>
      </xsl:otherwise>
    </xsl:choose>
    <td>
    <xsl:apply-templates select="definition"/>
    <xsl:if test="see">
      <br/>See Also: <xsl:apply-templates select="see"/>
    </xsl:if>
    </td>
    <td>
      <xsl:apply-templates select="tutorial"/>
    </td>
    </tr>
  </xsl:template>
  
  <xsl:template match="term">
    <xsl:value-of select="."/>
    <xsl:if test="not(position() = last())">, </xsl:if>
  </xsl:template>

  <xsl:template match="acronym">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="definition">
    <p>
      <xsl:number/>. <xsl:apply-templates/>
      <xsl:if test="@cite">
        (Source: <link role="citation"><xsl:attribute name="href"><xsl:value-of select="@cite"/></xsl:attribute><xsl:value-of select="@cite"/></link>)
      </xsl:if>
    </p>
  </xsl:template>  
  
  <xsl:template match="see">
    <link role="glossary">
      <xsl:attribute name="href"><xsl:value-of select="id"/></xsl:attribute>
      <xsl:value-of select="text"/>      
    </link>
    <xsl:if test="not(position() = last())">, </xsl:if>
  </xsl:template>
  
  <xsl:template match="tutorial">
    <link><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute><xsl:value-of select="."/></link> 
    <xsl:if test="@level">
      (<xsl:value-of select="@level"/>)
    </xsl:if>
  </xsl:template>  
  
  <xsl:template name="generate-id">
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:value-of select="@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="concat(concat(local-name(.), '-'), generate-id(.))"/>
      </xsl:otherwise>
  </xsl:choose>
  </xsl:template>

  
</xsl:stylesheet>
