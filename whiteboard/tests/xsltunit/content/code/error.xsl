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

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:error="http://apache.org/cocoon/error/2.1"
>

<xsl:param name="element"/> <!-- Root element for test suites. -->
<xsl:param name="id"/> <!-- Name of the error suite. -->

<!-- Errors are translated into a testsuite, so they are visible in the report. -->
<xsl:template match="error:notify">
  <xsl:element name="{$element}">
    <xsl:attribute name="id"><xsl:value-of select="$id"/></xsl:attribute>
    <xsl:attribute name="error"><xsl:value-of select="error:title"/></xsl:attribute>
    <xsl:apply-templates select="@*|node()|text()"/>
  </xsl:element>
</xsl:template>

<!-- Throw away extra error information. -->
<xsl:template match="error:extra">
</xsl:template>

<!-- Keep all other elements, process content. -->
<xsl:template match="@*|*">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()|text()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="text()">
  <xsl:value-of select="normalize-space()"/>
</xsl:template>

</xsl:stylesheet>
