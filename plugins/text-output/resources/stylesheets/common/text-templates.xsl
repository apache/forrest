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

    <!-- FIXME: Handle case when ($width - $indent) less than 1 -->
    <xsl:variable name="work-width" select="$width - $indent"/>

    <!-- We'll lose whether the text had a trailing space when we
    normalize-space() it.  Needed if we're combining multiple outputs
    of emit() into a single variable and then re-emitting ... -->
    <xsl:variable name="trailing-space">
      <xsl:if test="substring($text, string-length($text))=' '">
        <xsl:text> </xsl:text>
      </xsl:if>
    </xsl:variable>

    <!-- Normalized text.  We'll use this for calculations on
    splitting, lengths, etc. -->
    <xsl:variable name="ntext" select="normalize-space($text)"/>

    <xsl:choose>
      <xsl:when test="string-length($ntext) > $work-width">

        <!-- Grab string of the maximum width we can have. -->
        <xsl:variable name="text-maxwidth">
          <xsl:value-of select="substring($ntext, 1, $work-width)"/>
        </xsl:variable>

        <!-- Grab the substring of text-maxwidth that breaks on the last
        space in text-maxwidth -->
        <xsl:variable name="text-wrap">
          <xsl:call-template name="text-to-last-space">
            <xsl:with-param name="text" select="$text-maxwidth"/>
          </xsl:call-template>
        </xsl:variable>

        <!-- Grab the remaining text which will then be emit()'d again -->
        <xsl:variable name="text-remaining">
          <xsl:value-of select="substring($ntext, string-length($text-wrap)+1)"/>
        </xsl:variable>

        <xsl:call-template name="lineOf">
          <xsl:with-param name="size" select="$indent"/>
        </xsl:call-template>
        <xsl:value-of select="$text-wrap"/>
        <xsl:call-template name="cr"/>

        <xsl:call-template name="emit">
          <xsl:with-param name="text" select="$text-remaining"/>
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="width" select="$width"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="lineOf">
          <xsl:with-param name="size" select="$indent"/>
        </xsl:call-template>
        <xsl:value-of select="concat($ntext,$trailing-space)"/>
        <xsl:call-template name="cr"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Grab the text up until the last space in the passed text -->
  <xsl:template name="text-to-last-space">
    <xsl:param name="text"/>

    <xsl:variable name="leading" select="substring-before($text, ' ')"/>
    <xsl:variable name="remaining" select="substring-after($text,' ')"/>

    <xsl:value-of select="concat($leading,' ')"/>

    <xsl:choose>
      <xsl:when test="contains($remaining,' ')">
        <xsl:call-template name="text-to-last-space">
          <xsl:with-param name="text" select="$remaining"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
