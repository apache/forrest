<?xml version="1.0" encoding="UTF-8"?>
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
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:session="http://apache.org/cocoon/session/1.0"
    xmlns:forrest="http://apache.org/forrest/templates/1.0"
    >

<xsl:param name="contentDir" select="'default/path'"/>

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

<xsl:template match="dir:file[starts-with(@name,'c-')]">
  <xsl:variable name="fct-bit-file">
    <xsl:value-of select="$contentDir"/>
    <xsl:text>/fbits/</xsl:text>
    <xsl:value-of select="@name"/>
  </xsl:variable>
  <xsl:variable name="fct-bit-title">
    <xsl:value-of select="document($fct-bit-file)/forrest:contract/@name"/>
  </xsl:variable>
  <xsl:variable name="fct-bit-nc">
    <xsl:value-of select="document($fct-bit-file)/forrest:contract/@nc"/>
  </xsl:variable>
  <xsl:variable name="fct-bit-tlc">
    <xsl:value-of select="document($fct-bit-file)/forrest:contract/@tlc"/>
  </xsl:variable>
  <xsl:variable name="fct-bit-description">
    <xsl:value-of select="document($fct-bit-file)/forrest:contract/description"/>
  </xsl:variable>
  <xsl:if test="$fct-bit-tlc='content'">
    <forrest:contract name="{$fct-bit-title}" css="{$fct-bit-nc}" file-name="{@name}">
      <description>
        <xsl:value-of select="$fct-bit-description"/>
      </description>
      <realpath>  
        <xsl:value-of select="$fct-bit-file"/>
      </realpath>
    </forrest:contract>
  </xsl:if>
</xsl:template>

</xsl:stylesheet>

