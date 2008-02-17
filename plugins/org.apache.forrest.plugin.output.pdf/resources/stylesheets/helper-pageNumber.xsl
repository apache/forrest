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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
  <!-- Display the document numerotation -->
  <xsl:template name="insertPageNumber">
    <xsl:param name="text-align" select="'start'"/>
    <xsl:variable name="prefixe"
      select="substring-before($page-numbering-format,'1')"/>
    <xsl:variable name="sep"
      select="substring-before(substring-after($page-numbering-format,'1'),'1')"/>
    <xsl:variable name="postfixe">
      <xsl:choose>
        <xsl:when
          test="contains(substring-after($page-numbering-format,'1'),'1')">
          <xsl:value-of
            select="substring-after(substring-after($page-numbering-format,'1'),'1')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring-after($page-numbering-format,'1')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    <!-- if 'page-numbering-format' contains 1 digits, the page number is displayed in the footer -->
    <xsl:if test="contains($page-numbering-format,'1')">
      <fo:block font-size="70%" text-align="{$text-align}">
        <!-- if the separator is not found, the total page number is skipped -->
        <xsl:value-of select="$prefixe"/>
        <fo:page-number/>
        <xsl:if test="$sep != ''">
          <xsl:value-of select="$sep"/>
          <fo:page-number-citation ref-id="term"/>
        </xsl:if>
        <xsl:value-of select="$postfixe"/>
      </fo:block>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
