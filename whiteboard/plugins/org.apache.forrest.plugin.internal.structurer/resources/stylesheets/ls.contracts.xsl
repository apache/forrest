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
    xmlns:dir="http://apache.org/cocoon/directory/2.0"
    xmlns:forrest="http://apache.org/forrest/templates/1.0"
    >


  <xsl:template match="/dir:directory">
   <forrest:themes xmlns:forrest="http://apache.org/forrest/templates/1.0">
    <xsl:apply-templates />
   </forrest:themes>
  </xsl:template>

<xsl:template match="dir:directory">
      <forrest:theme name="{@name}">
        <xsl:apply-templates />
      </forrest:theme>
</xsl:template>

<xsl:template match="dir:file[./*/*/@name]">
  <forrest:contract name="{./*/*/@name}" file-name="{@name}">
    <xsl:copy-of select="./*/*/description"/>
    <xsl:copy-of select="./*/*/usage"/>
    <xsl:apply-templates select=".//forrest:template"/>
  </forrest:contract>
</xsl:template>

<xsl:template match="forrest:template">
  <forrest:template>
    <xsl:copy-of select="@*"/>
  </forrest:template>
</xsl:template>

</xsl:stylesheet>

