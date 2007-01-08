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
<!--
Generates a lucene:index for the whole site with CInclude elements where lucene:documents should be pulled in.
Input is expected to be in standard book.xml format. @hrefs should be normalized, although unnormalized hrefs can be
handled by uncommenting the relevant section.
-->
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:cinclude="http://apache.org/cocoon/include/1.0"
  xmlns:h="http://apache.org/cocoon/request/2.0">

  <!-- The extension of the lucene index fragments. -->
  <xsl:param name="host"/>


  <!-- Creates the lucene:index root element from the Forrest
  book. -->
  <xsl:template match="h:request ">
    <xsl:variable name="query">
      <xsl:apply-templates select="h:requestParameters"/>
    </xsl:variable>
    <cinclude:include>
      <xsl:attribute name="src">
        <xsl:value-of select="$host"/>?<xsl:value-of select="normalize-space($query)"/>
      </xsl:attribute>
    </cinclude:include>
  </xsl:template>

  <!-- Recursively processes h:parameter elements. -->
  <xsl:template match="h:requestParameters">
    <xsl:for-each select="h:parameter">
      <xsl:value-of select="@name"/>=<xsl:value-of select="normalize-space(h:value/text())"/>
      <xsl:if test="position()!=last()">&amp;</xsl:if>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
