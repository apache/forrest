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



<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- The URL of the document which the indexing information stems
  from -->
  <xsl:param name="document-url"/>
  <xsl:param name="project"/>
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="document">
    <doc>
      <field name="id">
        <xsl:value-of select="$project"/>:<xsl:value-of select="$document-url"/>
      </field>
      <xsl:apply-templates select="header/*"/>
      <field name="content">
        <xsl:apply-templates select="body"/>
      </field>
    </doc>
  </xsl:template>
  <!-- Copies document header, title, and version-->
  <xsl:template
    match="header/title |
                       header/subtitle |
                       header/abstract |
                       header/version">
    <field name="{name(.)}">
      <xsl:apply-templates/>
    </field>
  </xsl:template>
  <xsl:template match="header/authors/person">
    <field name="author">
      <xsl:value-of select="@name"/>
    </field>
  </xsl:template>
</xsl:stylesheet>
