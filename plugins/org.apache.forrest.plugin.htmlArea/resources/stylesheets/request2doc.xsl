<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation or its licensors,
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

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:req="http://apache.org/cocoon/request/2.0">
  
  <xsl:template match="/req:request">
    <page>
      <xsl:apply-templates select="req:requestParameters" mode="meta"/>
      <content>
        <xsl:call-template name="body"/>
      </content>
    </page>
  </xsl:template>
  
  <xsl:template name="body">
    <body>
      <xsl:apply-templates select="req:requestParameters" mode="body"/>
    </body>
  </xsl:template>
  
  <xsl:template match="req:requestParameters" mode="meta">
      <meta name="filename">
        <xsl:attribute name="content"><xsl:apply-templates select="req:parameter[@name='filename']"/></xsl:attribute>
      </meta>
      <xsl:apply-templates select="req:parameter[@name='title']"/>
  </xsl:template>
  
  <xsl:template match="req:requestParameters" mode="body">
      <xsl:apply-templates select="req:parameter[@name='editor']"/>
  </xsl:template>
  
  <xsl:template match="req:parameter[@name='title']">
    <title>
      <xsl:value-of select="req:value"/>
    </title>
  </xsl:template>
  
  <xsl:template match="req:parameter[@name='filename']">
    <filename>
      <xsl:value-of select="req:value"/>
    </filename>
  </xsl:template>
  
  <xsl:template match="req:parameter[@name='editor']">
      <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

</xsl:stylesheet>
