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
  xmlns:forrest="http://apache.org/forrest/templates/1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--This template will match the different combinations for forrest:hooks-->
  <xsl:template match="forrest:hook[@name and (@type='div' or not(@type))]">
    <div id="{@name}">
      <xsl:apply-templates/>
      <xsl:if test="@nbsp='true'">&#160;</xsl:if>
    </div>
  </xsl:template>
  <xsl:template match="forrest:hook[@class and (@type='div' or not(@type))]">
    <div class="{@class}">
      <xsl:apply-templates/>
      <xsl:if test="@nbsp='true'">&#160;</xsl:if>
    </div>
  </xsl:template>
  <xsl:template match="forrest:hook[@class and @type='span']">
    <span class="{@class}"> 
      <xsl:apply-templates/>
      <xsl:if test="@nbsp='true'">&#160;</xsl:if>
    </span>
  </xsl:template>
  <xsl:template match="forrest:hook[@name and @type='span']">
    <span id="{@name}"> 
      <xsl:apply-templates/>
      <xsl:if test="@nbsp='true'">&#160;</xsl:if>
    </span>
  </xsl:template>
</xsl:stylesheet>
