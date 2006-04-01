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
  <xsl:import href="lm://transform.xml.dotdots"/>
  <xsl:import href="lm://transform.xml.pathutils"/>
  <xsl:param name="path" select="'test.html'"/>
  <!-- Path (..'s) to the root directory -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="filename-noext">
    <xsl:call-template name="filename-noext">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <!-- Source filename (eg 'foo.xml') of current page -->
  <xsl:variable name="filename">
    <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="skin-img-dir" 
    select="concat(string($root), 'themes/images')"/>
  <xsl:variable name="spacer" 
    select="concat($root, 'themer/images/spacer.gif')"/>
    <xsl:template match="/">
      <properties>
        <property name="skin-img-dir" value="{$skin-img-dir}"/>
        <property name="filename" value="{$filename}"/>
        <property name="filename-noext" value="{$filename-noext}"/>
        <property name="root" value="{$root}"/>
        <property name="path" value="{$path}"/>
      </properties>
    </xsl:template>
</xsl:stylesheet>