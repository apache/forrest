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

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:forrest="http://apache.org/forrest/templates/1.0"  
  xmlns:xi="http://www.w3.org/2001/XInclude"
  >

<xsl:attribute-set name="att">
  <xsl:attribute name="xmlns:xsl">http://www.w3.org/1999/XSL/Transform</xsl:attribute>
  <xsl:attribute name="xmlns:forrest">http://apache.org/forrest/templates/1.0</xsl:attribute>
</xsl:attribute-set>

  <xsl:template match="/">
    <xsl:element name="xsl:stylesheet" use-attribute-sets="att">
      <xsl:for-each select="forrest:filter/descendant::xsl:stylesheet">
        <xsl:copy-of select="*"/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>
<!--
  <xsl:template match="forrest:hook">
    <forrest:hook id="{@name}">
      <xsl:apply-templates select="forrest:contract"/>
    </forrest:hook>
  </xsl:template>
  
  <xsl:template match="forrest:contract">
    <xsl:variable name="css-ft"><xsl:value-of select="@name"/></xsl:variable>
    <xsl:variable name="includePath">fbits-xsl/<xsl:value-of select="//forrest:contracts/forrest:contract[@css=$css-ft]/@file-name"/></xsl:variable>
    <xi:include href="{$includePath}"/>
  </xsl:template>
  
  <xsl:template match="forrest:contracts/forrest:contract"/>
  -->  
</xsl:stylesheet>
