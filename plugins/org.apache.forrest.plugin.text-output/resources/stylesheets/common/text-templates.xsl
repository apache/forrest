<?xml version="1.0"?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- A single carriage return -->
  <xsl:template name="cr">
    <xsl:text>&#xa;</xsl:text>
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

  <!-- Takes a block of text, normalizes all the spaces, carriage returns
    etc.  It then word wraps to a specified width after indenting each
    line -->

  <xsl:template name="wrap-text">
    <xsl:param name="text"/>
    <xsl:param name="indent"/>
    <xsl:param name="width" select="$document-width"/>
    <xsl:param name="fixed" select="false"/>

    <!-- FIXME: Handle case when ($width - $indent) less than 1 -->
    <xsl:variable name="work-width" select="$width - $indent"/>

    <!-- We'll lose whether the text had a trailing space when we
    normalize-space() it.  Needed to preserve the space between
    a text() node and in-line markup.  For example

      <p>This text is the text node and <strong>this is in-line
      markup</strong>.  We want to preserve the space between
      the first text node and the "strong" markup.</p>

    A string that consists purely of white space will have the trailing-space
    variable set to ''.
    -->

    <xsl:variable name="trailing-space">
      <xsl:choose>
        <xsl:when test="normalize-space($text)!=''">
          <xsl:if test="substring($text, string-length($text))=' '">
            <xsl:text> </xsl:text>
          </xsl:if>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>

    <!-- Normalized text.  We'll use this for calculations on
    splitting, lengths, etc.  If we're emitting based on fixed
    width text, do not normalize-space() the incoming text -->
    <xsl:variable name="ntext">
      <xsl:choose>
        <xsl:when test="$fixed">
          <xsl:value-of select="$text"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="normalize-space($text)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

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

        <xsl:call-template name="wrap-text">
          <xsl:with-param name="text" select="concat($text-remaining,$trailing-space)"/>
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="width" select="$width"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="lineOf">
          <xsl:with-param name="size" select="$indent"/>
        </xsl:call-template>
        <xsl:value-of select="concat($ntext,$trailing-space)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Prefixes each line of a block of text with necessary indent,
    markers (bullets, item numbers) and additional hanging indent.

    It does absolutely no word-wrapping as it is assumed that this
    has already been done. -->

  <xsl:template name="emit-with-indent">
    <xsl:param name="text"/>
    <xsl:param name="indent"/>
    <xsl:param name="marker" select="''"/>
    <xsl:param name="first-line" select="1"/>

    <xsl:variable name="marker-length" select="string-length($marker)"/>
    <xsl:variable name="tmp" select="substring-before($text,'&#xa;')"/>

    <xsl:choose>
      <xsl:when test="contains($text,'&#xa;')">

        <!-- Determine the text to emit.  If it's the first line, include
             include the indentation marker.  Otherwise, include 
             an equivalent amount of blank space to replace the
             marker used in the first line.  This will create a
             hanging indent -->

        <xsl:variable name="text-to-emit">
          <xsl:call-template name="lineOf">
            <xsl:with-param name="size" select="$indent"/>
            <xsl:with-param name="chars" select="' '"/>
          </xsl:call-template>
          <xsl:choose>
            <xsl:when test="$first-line">
              <xsl:value-of select="$marker"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="lineOf">
                <xsl:with-param name="size" select="$marker-length"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:value-of select="$tmp"/>
        </xsl:variable>

        <xsl:value-of select="concat($text-to-emit,'&#xa;')"/>
        
        <xsl:variable name="text-remaining"
                    select="substring($text, string-length($tmp)+2)"/>

        <xsl:call-template name="emit-with-indent">
          <xsl:with-param name="text" select="$text-remaining"/>
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="marker" select="$marker"/>
          <xsl:with-param name="first-line" select="0"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="text-to-emit">
          <xsl:call-template name="lineOf">
            <xsl:with-param name="size" select="$indent"/>
          </xsl:call-template>
          <xsl:choose>
            <xsl:when test="$first-line">
              <xsl:value-of select="$marker"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="lineOf">
                <xsl:with-param name="size" select="$marker-length"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:value-of select="$text"/>
        </xsl:variable>

        <xsl:value-of select="concat($text-to-emit,'&#xa;')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Grab the text up until the last space in the passed text -->
  <xsl:template name="text-to-last-space">
    <xsl:param name="text"/>

    <xsl:variable name="leading">
      <xsl:choose>
        <xsl:when test="contains($text,' ')"> 
          <xsl:value-of select="substring-before($text, ' ')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$text"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="remaining">
      <xsl:choose>
        <xsl:when test="contains($text,' ')"> 
          <xsl:value-of select="substring-after($text, ' ')"/>
        </xsl:when>
        <xsl:otherwise/>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$leading"/>
    <!-- Emit the space if we had one to begin with -->
    <xsl:if test="contains($text,' ')">
      <xsl:text> </xsl:text>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="contains($remaining,' ')">
        <xsl:call-template name="text-to-last-space">
          <xsl:with-param name="text" select="$remaining"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>

  <!-- ==================================================================== -->
  <!--  Table Handling

        The column widths are calculated automatically based on a maximum
        width, "maxwidth" (default 76),  that the table can be .  This maxiumum
        width is used only for the calculation of the column widths.  These
        column widths cannot be less than the "mincolwidth" (default 10).

        If the calculated column widths are less than the "mincolwidth", the
        table is grown horizontally based on the "mincolwidth" and the max
        number of columns.

        An idententation paramenter, "indent" (default 4) is used since pod2man
        does a default indentation of 4 for verbatim text.  This value is used
        in the calculation of the column widths.
  -->
  <!-- ==================================================================== -->

  <xsl:template match="table">
    <xsl:param name="level">1</xsl:param>
    <xsl:param name="mincolwidth">10</xsl:param>
    <xsl:param name="width" select="$document-width"/>
    <xsl:variable name="cols">
      <xsl:value-of select="count(tr[1]/th|tr[1]/td) - count(tr[1]/th[@colspan]|tr[1]/td[@colspan]) + sum(tr[1]/th/@colspan) + sum(tr[1]/td/@colspan)"/>

    </xsl:variable>

    <xsl:variable name="indent" select="$level * $indent-per-level"/>
    <xsl:variable name="maxwidth" select="$width - $indent"/>

    <xsl:variable name="calc-colwidth">
      <xsl:value-of select="floor(($maxwidth - $indent - $cols) div $cols) - 1"/>
    </xsl:variable>

    <xsl:variable name="colwidth">
      <xsl:choose>
        <xsl:when test="$mincolwidth > $calc-colwidth">
          <xsl:value-of select="$mincolwidth"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$calc-colwidth"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="caption-text">
      <xsl:apply-templates select="caption" mode="in-list">
        <xsl:with-param name="level" select="$level"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit-with-indent">
      <xsl:with-param name="text" select="$caption-text"/>
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:call-template>

    <xsl:variable name="the-table">
      <xsl:apply-templates select="tr" mode="in-list">
        <xsl:with-param name="level" select="$level"/>
        <xsl:with-param name="colwidth" select="$colwidth"/>
        <xsl:with-param name="cols" select="$cols"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit-with-indent">
      <xsl:with-param name="text" select="$the-table"/>
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:call-template>

    <xsl:call-template name="newLine"/>

  </xsl:template>

  <xsl:template match="table" mode="in-list">
    <xsl:param name="level">0</xsl:param>
    <xsl:param name="mincolwidth">10</xsl:param>
    <xsl:param name="width" select="$document-width"/>
    <xsl:variable name="cols">
      <xsl:value-of select="count(tr[1]/th|tr[1]/td)"/>
    </xsl:variable>

    <xsl:variable name="indent" select="$level * $indent-per-level"/>
    <xsl:variable name="maxwidth" select="$width - $indent"/>

    <xsl:variable name="calc-colwidth">
      <xsl:value-of select="floor(($maxwidth - $indent - $cols) div $cols) - 1"/>
    </xsl:variable>

    <xsl:variable name="colwidth">
      <xsl:choose>
        <xsl:when test="$mincolwidth > $calc-colwidth">
          <xsl:value-of select="$mincolwidth"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$calc-colwidth"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="caption-text">
      <xsl:apply-templates select="caption" mode="in-list">
        <xsl:with-param name="level" select="$level"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit-with-indent">
      <xsl:with-param name="text" select="$caption-text"/>
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:call-template>

    <xsl:variable name="the-table">
      <xsl:apply-templates select="tr" mode="in-list">
        <xsl:with-param name="level" select="$level"/>
        <xsl:with-param name="colwidth" select="$colwidth"/>
        <xsl:with-param name="cols" select="$cols"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit-with-indent">
      <xsl:with-param name="text" select="$the-table"/>
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:call-template>

    <xsl:call-template name="newLine"/>

  </xsl:template>

  <xsl:template match="caption" mode="in-list">
    <xsl:param name="level"/>
    <xsl:apply-templates/>
  </xsl:template>
    
  <xsl:template match="tr" mode="in-list">
    <xsl:param name="level" select="'1'"/>
    <xsl:param name="colwidth"/>
    <xsl:param name="cols"/>

    <xsl:variable name="dashes">
      <xsl:call-template name="lineOf">
        <xsl:with-param name="size" select="'80'"/>
        <xsl:with-param name="chars" select="'-'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="cell-border">
      <xsl:value-of select="substring( $dashes, 1, $colwidth )"/>
    </xsl:variable>
    
    <xsl:variable name="row-border">
      <xsl:call-template name="make-row-border">
        <xsl:with-param name="col">1</xsl:with-param>
        <xsl:with-param name="colwidth" select="$colwidth"/>
        <xsl:with-param name="maxcols" select="$cols"/>
        <xsl:with-param name="cell-border" select="$cell-border"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:value-of select="$row-border"/>
    <xsl:call-template name="cr"/>

    <!-- Can we handle a dynamic number of columns?  Not sure how to
         do that, so just limit the number we can handle to some reasonable
         maximum.  Currently, that maximum is 10 -->

    <!-- For the current row, grab the content for each cell in this row.
         The content will be fit by the "handle-cell-content" template.  -->

    <xsl:variable name="col-1">
      <xsl:if test="(th|td)[1]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[1]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-2">
      <xsl:if test="(th|td)[2]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[2]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-3">
      <xsl:if test="(th|td)[3]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[3]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-4">
      <xsl:if test="(th|td)[4]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[4]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-5">
      <xsl:if test="(th|td)[5]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[5]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-6">
      <xsl:if test="(th|td)[6]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[6]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-7">
      <xsl:if test="(th|td)[7]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[7]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-8">
      <xsl:if test="(th|td)[8]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[8]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-9">
      <xsl:if test="(th|td)[9]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[9]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="col-10">
      <xsl:if test="(th|td)[10]">
        <xsl:variable name="tmp">
          <xsl:apply-templates select="(th|td)[10]" mode="in-list">
            <xsl:with-param name="width" select="$colwidth - 2"/>
          </xsl:apply-templates>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:variable>

    <!--
      Okay, so we've got the contents of the cells for a single row.
      Let's pass this off to emit-table-row where we can extract
      $colwidth from each column and emit until we're out of content
    -->

    <xsl:call-template name="emit-table-row">
      <xsl:with-param name="maxcols" select="$cols"/>
      <xsl:with-param name="colwidth" select="$colwidth"/>
      <xsl:with-param name="col-1" select="$col-1"/>
      <xsl:with-param name="col-2" select="$col-2"/>
      <xsl:with-param name="col-3" select="$col-3"/>
      <xsl:with-param name="col-4" select="$col-4"/>
      <xsl:with-param name="col-5" select="$col-5"/>
      <xsl:with-param name="col-6" select="$col-6"/>
      <xsl:with-param name="col-7" select="$col-7"/>
      <xsl:with-param name="col-8" select="$col-8"/>
      <xsl:with-param name="col-9" select="$col-9"/>
      <xsl:with-param name="col-10" select="$col-10"/>
    </xsl:call-template>

    <xsl:if test="position()=last()">
      <xsl:value-of select="$row-border"/>
      <xsl:call-template name="cr"/>
    </xsl:if>

  </xsl:template>

  <xsl:template match="td|th" mode="in-list">
    <xsl:param name="width"/>

    <xsl:apply-templates mode="in-list">
      <xsl:with-param name="width" select="$width"/>
      <xsl:with-param name="level" select="'0'"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="make-row-border">
    <xsl:param name="col"/>
    <xsl:param name="colwidth"/>
    <xsl:param name="maxcols"/>
    <xsl:param name="cell-border"/>
    <xsl:param name="border">+</xsl:param>

    <xsl:choose>
      <xsl:when test="$col > $maxcols">
        <xsl:value-of select="$border"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="make-row-border">
          <xsl:with-param name="col" select="$col + 1"/>
          <xsl:with-param name="maxcols" select="$maxcols"/>
          <xsl:with-param name="cell-border" select="$cell-border"/>
          <xsl:with-param name="border"
              select="concat($border, $cell-border, '+')"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- emit-table-row

       Recursive template to keep extracting content from each of the
       columns, emit a single line in each cell, until all the content
       is used up
  -->

  <xsl:template name="emit-table-row">
    <xsl:param name="maxcols"/>
    <xsl:param name="colwidth"/>
    <xsl:param name="col-1"/>
    <xsl:param name="col-2"/>
    <xsl:param name="col-3"/>
    <xsl:param name="col-4"/>
    <xsl:param name="col-5"/>
    <xsl:param name="col-6"/>
    <xsl:param name="col-7"/>
    <xsl:param name="col-8"/>
    <xsl:param name="col-9"/>
    <xsl:param name="col-10"/>

    <xsl:text>| </xsl:text>
    <xsl:call-template name="make-cell">
      <xsl:with-param name="content" select="$col-1"/>
      <xsl:with-param name="colwidth" select="$colwidth"/>
    </xsl:call-template>

    <xsl:if test="$maxcols > 1">

      <xsl:text> | </xsl:text>
      <xsl:call-template name="make-cell">
        <xsl:with-param name="content" select="$col-2"/>
        <xsl:with-param name="colwidth" select="$colwidth"/>
      </xsl:call-template>

      <xsl:if test="$maxcols > 2">

        <xsl:text> | </xsl:text>
        <xsl:call-template name="make-cell">
          <xsl:with-param name="content" select="$col-3"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>

        <xsl:if test="$maxcols > 3">

          <xsl:text> | </xsl:text>
          <xsl:call-template name="make-cell">
            <xsl:with-param name="content" select="$col-4"/>
            <xsl:with-param name="colwidth" select="$colwidth"/>
          </xsl:call-template>

          <xsl:if test="$maxcols > 4">

            <xsl:text> | </xsl:text>
            <xsl:call-template name="make-cell">
              <xsl:with-param name="content" select="$col-5"/>
              <xsl:with-param name="colwidth" select="$colwidth"/>
            </xsl:call-template>

            <xsl:if test="$maxcols > 5">

              <xsl:text> | </xsl:text>
              <xsl:call-template name="make-cell">
                <xsl:with-param name="content" select="$col-6"/>
                <xsl:with-param name="colwidth" select="$colwidth"/>
              </xsl:call-template>

              <xsl:if test="$maxcols > 6">

                <xsl:text> | </xsl:text>
                <xsl:call-template name="make-cell">
                  <xsl:with-param name="content" select="$col-7"/>
                  <xsl:with-param name="colwidth" select="$colwidth"/>
                </xsl:call-template>

                <xsl:if test="$maxcols > 7">

                  <xsl:text> | </xsl:text>
                  <xsl:call-template name="make-cell">
                    <xsl:with-param name="content" select="$col-8"/>
                    <xsl:with-param name="colwidth" select="$colwidth"/>
                  </xsl:call-template>

                  <xsl:if test="$maxcols > 8">

                    <xsl:text> | </xsl:text>
                    <xsl:call-template name="make-cell">
                      <xsl:with-param name="content" select="$col-9"/>
                      <xsl:with-param name="colwidth" select="$colwidth"/>
                    </xsl:call-template>

                    <xsl:if test="$maxcols > 9">

                      <xsl:text> | </xsl:text>
                      <xsl:call-template name="make-cell">
                        <xsl:with-param name="content" select="$col-10"/>
                        <xsl:with-param name="colwidth" select="$colwidth"/>
                      </xsl:call-template>

                    </xsl:if>
                  </xsl:if>
                </xsl:if>
              </xsl:if>
            </xsl:if>
          </xsl:if>
        </xsl:if>
      </xsl:if>
    </xsl:if>

    <xsl:text> |</xsl:text>
    <xsl:call-template name="cr"/>

    <!-- Determine whether there is any content left in any of the
         cells on this row.  If so, we need to call emit-table-row again -->

    <xsl:variable name="content-remains">
      <xsl:if test="string-length(substring($col-1, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-2, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-3, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-4, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-5, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-6, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-7, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-8, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-9, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
      <xsl:if test="string-length(substring($col-10, $colwidth - 2)) > 1">
        <xsl:text>1</xsl:text>
      </xsl:if>
    </xsl:variable>

    <xsl:if test="string-length($content-remains) > 0">
      <xsl:call-template name="emit-table-row">
        <xsl:with-param name="maxcols" select="$maxcols"/>
        <xsl:with-param name="colwidth" select="$colwidth"/>
        <xsl:with-param name="col-1"
            select="substring( $col-1, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-2" 
            select="substring( $col-2, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-3"
            select="substring( $col-3, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-4"
            select="substring( $col-4, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-5"
            select="substring( $col-5, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-6"
            select="substring( $col-6, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-7" 
            select="substring( $col-7, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-8"
            select="substring( $col-8, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-9"
            select="substring( $col-9, $colwidth - 2 + 1 )"/>
        <xsl:with-param name="col-10"
            select="substring( $col-10, $colwidth - 2 + 1 )"/>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <xsl:template name="make-cell">
    <xsl:param name="content"/>
    <xsl:param name="colwidth"/>

    <xsl:variable name="padding">
      <xsl:call-template name="lineOf">
        <xsl:with-param name="size" select="'80'"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="cell">
      <xsl:choose>
        <xsl:when test="$colwidth - 2 > string-length($content)">

          <!-- The subtraction of 2 is for the border padding.  It is reflected
               in the call to make-cell in the "content" parameter -->

          <xsl:value-of select="concat($content,
               substring($padding,1,$colwidth - 2 - string-length($content)))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="substring($content, 1, $colwidth - 2)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$cell"/>
  </xsl:template>

  <!--  handle-cell-content

        Replaces any new-line characters found in the content with 
        an appropriate number of spaces to fill out the cell to the
        end of the cell and another number of spaces to fill out a 
        "blank" line.
  -->

  <xsl:template name="handle-cell-content">
    <xsl:param name="text"/>
    <xsl:param name="colwidth"/>

    <xsl:variable name="spaces">
      <xsl:call-template name="lineOf">
        <xsl:with-param name="size" select="'80'"/>
        <xsl:with-param name="chars" select="' '"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="contains( $text, '&#xa;' )">
        <xsl:variable name="text-before">
          <xsl:value-of select="substring-before( $text, '&#xa;' )"/>
        </xsl:variable>
        <xsl:variable name="tmp">
          <xsl:value-of select="$text-before"/>

          <!-- Pad only if the length of the text we've emitted is not
               a multiple of the unpadded column width ($colwidth - 2). -->
          <xsl:if test="(string-length($text-before) mod ($colwidth - 2))!=0">
            <xsl:value-of select="substring( $spaces, 1,
                ($colwidth - 2) - (string-length($text-before) mod ($colwidth -2)))"/>
          </xsl:if>
          <xsl:value-of select="substring-after($text, '&#xa;')"/>
        </xsl:variable>
        <xsl:call-template name="handle-cell-content">
          <xsl:with-param name="text" select="$tmp"/>
          <xsl:with-param name="colwidth" select="$colwidth"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>
