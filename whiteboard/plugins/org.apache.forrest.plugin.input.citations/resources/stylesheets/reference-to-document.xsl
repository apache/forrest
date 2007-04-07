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
    version="1.0">

  <xsl:import href="lm://transform.xml.copyover.helper"/>

  <xsl:template match="references">
   <document>
    <header>
     <title>References</title>
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
          <th>Citation</th>
          <th>Details</th>
        </tr>
        <xsl:apply-templates select="item"/>
      </table>
    </section>
  </xsl:template>

  <xsl:template match="item">
    <xsl:variable name="id">
      <xsl:call-template name="generate-id"/>
    </xsl:variable>
    <tr id="{$id}">
      <td>[<xsl:value-of select="$id"/>]</td>
      <td><xsl:apply-templates/></td>
    </tr>
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
