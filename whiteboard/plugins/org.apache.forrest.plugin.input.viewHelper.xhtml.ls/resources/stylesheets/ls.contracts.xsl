<?xml version="1.0" encoding="UTF-8"?>
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
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:session="http://apache.org/cocoon/session/1.0"
    xmlns:forrest="http://apache.org/forrest/templates/1.0"
    >

<xsl:param name="contractDir" select="'default/path'"/>

  <!--
      Create row for each document.  Information about the document is
      extracted from the document itself using the document()
      function.
  -->
  <xsl:template match="/">
      <forrest:contracts xmlns:forrest="http://apache.org/forrest/templates/1.0">
        <xsl:apply-templates select="//dir:file"/>                    
      </forrest:contracts>
  </xsl:template>

<xsl:template match="dir:file">
  <xsl:variable name="realpath">
    <xsl:value-of select="$contractDir"/>
    <xsl:text>/resources/templates/</xsl:text>
    <xsl:value-of select="@name"/>
  </xsl:variable>
  <xsl:variable name="contract-title">
    <xsl:value-of select="document($realpath)//forrest:contract/@name"/>
  </xsl:variable>
  <xsl:variable name="contract-type">
    <xsl:value-of select="document($realpath)/forrest:contract/@type"/>
  </xsl:variable>
  <xsl:variable name="contract-description">
    <xsl:value-of select="document($realpath)/forrest:contract/description"/>
  </xsl:variable>
  <xsl:variable name="contract-usage" select="document($realpath)/forrest:contract/usage"/>
  <forrest:contract name="{$contract-title}" file-name="{@name}">
      <description>
        <xsl:value-of select="$contract-description"/>
      </description>
      <usage><xsl:value-of select="$contract-usage"/></usage>
      <realpath>  
        <xsl:value-of select="$realpath"/>
      </realpath>
    </forrest:contract>

</xsl:template>

</xsl:stylesheet>

