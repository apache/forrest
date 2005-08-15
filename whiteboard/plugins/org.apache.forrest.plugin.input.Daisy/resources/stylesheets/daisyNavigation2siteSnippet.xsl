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
    xmlns:d="http://outerx.org/daisy/1.0#navigationspec"
    version="1.0">
      
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="d:doc">
    <doc>
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
      <xsl:attribute name="href"><xsl:value-of select="@id"/>.daisy.html</xsl:attribute>
    </doc>
  </xsl:template>
    
  <xsl:template match="d:group">
    <group>
      <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
      <xsl:attribute name="label"><xsl:value-of select="@label"/></xsl:attribute>
      <xsl:apply-templates/>
    </group>
  </xsl:template>
</xsl:stylesheet>
