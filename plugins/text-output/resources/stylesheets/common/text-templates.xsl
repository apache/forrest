<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- A single carriage return -->
  <xsl:template name="cr">
    <xsl:text>
</xsl:text>
  </xsl:template>

  <!-- A blank line -->
  <xsl:template name="newLine">
    <xsl:call-template name="cr"/>
    <xsl:call-template name="cr"/>
  </xsl:template>

  <!-- Justify passed in text based on a passed in width and
       type of alignment desired.

       Currently only supports (and defaults to) center alignment
  -->
  <xsl:template name="justify-text">
    <xsl:param name="text"/>
    <xsl:param name="width" select="'76'"/>
    <xsl:param name="align" select="'center'"/>

    <xsl:variable name="leader">
      <xsl:choose>
        <xsl:when test="$align='center'">
          <xsl:call-template name="lineOf">
            <xsl:with-param name="chars" select="' '"/>
            <xsl:with-param name="size"
              select="floor(($width - string-length($text)) div 2)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>*** </xsl:text>
          <xsl:value-of select="$align"/>
          <xsl:text> alignment is not implemented</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="concat($leader, $text)"/>
  </xsl:template>

  <xsl:template name="lineOf">
    <xsl:param name="chars" select="' '"/>
    <xsl:param name="size" select="80"/>

    <xsl:choose>
      <xsl:when test="$size &lt; 1"/>
      <xsl:when test="string-length($chars) &lt; $size">
        <xsl:call-template name="lineOf">
          <xsl:with-param name="chars" select="concat($chars,$chars)"/>
          <xsl:with-param name="size" select="$size"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="substring($chars, 1, $size)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="emit">
    <xsl:param name="text"/>
    <xsl:param name="indent"/>
    <xsl:param name="width" select="'76'"/>

    <xsl:choose>
      <xsl:when test="string-length($text) > 0">
        <xsl:variable name="trailing-space">
          <xsl:if test="substring($text, string-length($text))=' '">
            <xsl:text> </xsl:text>
          </xsl:if>
        </xsl:variable>
      
        <xsl:variable name="tmp">
          <xsl:call-template name="lineOf">
            <xsl:with-param name="size" select="$indent"/>
          </xsl:call-template>
          <xsl:value-of
                select="concat(normalize-space($text),$trailing-space)"/>
        </xsl:variable>

        <xsl:variable name="remaining">
          <xsl:choose>
            <xsl:when test="string-length($tmp) > $width">
              <xsl:value-of select="substring($tmp, $width+1)"/>
            </xsl:when>
            <xsl:otherwise/>
          </xsl:choose>
        </xsl:variable>

        <xsl:value-of select="substring($tmp, 1, $width)"/>
        <xsl:call-template name="cr"/>

        <xsl:call-template name="emit">
          <xsl:with-param name="text" select="$remaining"/>
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="width" select="$width"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>
