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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

  <xsl:param name="docname">docname parameter from sitemap</xsl:param>

  <xsl:template match="/">
    <xsl:apply-templates select="//document"/>
  </xsl:template>

  <xsl:template match="document">
    <xsl:text>
=head1 NAME

</xsl:text>
    <xsl:value-of select="$docname"/><xsl:text> - </xsl:text>
    <xsl:if test="//document/header/title">
      <xsl:value-of select="//document/header/title"/>
    </xsl:if>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates select="body"/>
  </xsl:template>

  <xsl:template match="body">
    <xsl:text>
=head1 DESCRIPTION

</xsl:text>
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="source">
    <xsl:call-template name="emit-verbatim">
      <xsl:with-param name="text" select="."/>
    </xsl:call-template>

    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template name="emit-verbatim">
    <xsl:param name="text"/>

    <xsl:text> </xsl:text>
    <xsl:choose>
      <xsl:when test="contains( $text, '&#xa;')">
        <xsl:value-of select="substring-before( $text, '&#xa;' )"/>
        <xsl:call-template name="cr"/>
        <xsl:call-template name="emit-verbatim">
          <xsl:with-param name="text" select="substring-after($text, '&#xa;')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
        <xsl:call-template name="cr"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="max-len">
    <xsl:param name="maxlen">0</xsl:param>
    <xsl:param name="text"/>

    <xsl:variable name="pre-newline">
      <xsl:choose>
        <xsl:when test="contains( $text, '&#xa;' )">
          <xsl:value-of select="substring-before( $text, '&#xa;' )"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$text"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="post-newline">
      <xsl:value-of select="substring-after( $text, '&#xa;' )"/>
    </xsl:variable>

    <xsl:variable name="len">
      <xsl:choose>
        <xsl:when test="string-length( $pre-newline ) > $maxlen">
          <xsl:value-of select="string-length( $pre-newline )"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$maxlen"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$post-newline=''">
        <xsl:value-of select="$len"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="max-len">
          <xsl:with-param name="maxlen" select="$len"/>
          <xsl:with-param name="text" select="$post-newline"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="abstract">
    <xsl:text>

=head1 ABSTRACT

</xsl:text>
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="section">
    <!-- The sections become the main body of the POD, starting under 
         a =head1 DESCRIPTION.  Therefore, their begining =head number
         is 2.
    -->
    <xsl:param name="level">2</xsl:param>
    <xsl:text>

=head</xsl:text>
    <xsl:value-of select="$level"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="title"/>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="number($level)+1"/>
    </xsl:apply-templates>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="section/title"/>

  <xsl:template match="subtitle"/>

  <xsl:template match="authors"/>

  <xsl:template match="p[@class='quote']">
    <xsl:text>"</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>"</xsl:text>
    <xsl:call-template name="line-blank"/>
  </xsl:template>
  
  <xsl:template match="p">
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="p" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="note|warning|fixme">
    <!-- Set up the proper label -->
    <xsl:variable name="label">
      <xsl:choose>
        <xsl:when test="@label!=''">
          <xsl:value-of select="@label"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="local-name()='note'">
              <xsl:text>Note:</xsl:text>
            </xsl:when>
            <xsl:when test="local-name()='warning'">
              <xsl:text>Warning:</xsl:text>
            </xsl:when>
            <xsl:when test="local-name()='fixme'">
              <xsl:text>Fixme</xsl:text>
              <xsl:if test="@author">
                <xsl:text> (</xsl:text>
                <xsl:value-of select="@author"/>
                <xsl:text>)</xsl:text>
              </xsl:if>
              <xsl:text>:</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="local-name()"/>
              <xsl:text>:</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="dashes">
      <xsl:call-template name="line-dashed"/>
    </xsl:variable>

    <xsl:variable name="separator">
      <xsl:value-of select="concat( ' ', substring( $dashes, 1, 60 ) )"/>
    </xsl:variable>

    <xsl:text>B&lt;</xsl:text>
    <xsl:value-of select="$label"/>
    <xsl:text>&gt;</xsl:text>

    <xsl:call-template name="line-blank"/>
    <xsl:value-of select="$separator"/>
    <xsl:call-template name="line-blank"/>
    <xsl:text>
=over 4</xsl:text>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
    <xsl:text>
=back</xsl:text>
    <xsl:call-template name="line-blank"/>
    <xsl:value-of select="$separator"/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="note" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="ol|ul">
    <xsl:call-template name="line-blank"/>
    <xsl:text>
=over 4
</xsl:text>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates select="li"/>
    <xsl:text>
=back
</xsl:text>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="ol|ul" mode="in-table">
    <xsl:apply-templates select="li" mode="in-table"/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="ol/li">
    <xsl:text>
=item </xsl:text>
    <xsl:value-of select="position()"/><xsl:text>.</xsl:text>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="ol/li" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="ul/li">
    <xsl:text>
=item *
</xsl:text>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="ul/li" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="dl">
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="dt">
    <xsl:text>B&lt;</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>&gt;</xsl:text>
  </xsl:template>

  <xsl:template match="dd">
    <xsl:call-template name="line-blank"/>
    <xsl:text>
=over 4</xsl:text>
    <xsl:call-template name="line-blank"/>
    <xsl:apply-templates/>
    <xsl:call-template name="line-blank"/>
    <xsl:text>
=back</xsl:text>
    <xsl:call-template name="line-blank"/>
  </xsl:template>

  <xsl:template match="strong">
    <xsl:variable name="tmp">
      <xsl:text>B&lt;</xsl:text><xsl:apply-templates/><xsl:text>&gt;</xsl:text>
    </xsl:variable>
    <xsl:value-of select="concat( normalize-space( $tmp ), ' ' )"/>
  </xsl:template>

  <xsl:template match="strong" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
  </xsl:template>

  <xsl:template match="em">
    <xsl:variable name="tmp">
      <xsl:text>I&lt;</xsl:text><xsl:apply-templates/><xsl:text>&gt;</xsl:text>
    </xsl:variable>
    <xsl:value-of select="concat( normalize-space( $tmp ), ' ' )"/>
  </xsl:template>

  <xsl:template match="em" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
  </xsl:template>

  <xsl:template match="code[@class='filename']">
    <xsl:variable name="tmp">
      <xsl:text>F&lt;</xsl:text><xsl:apply-templates/><xsl:text>&gt;</xsl:text>
    </xsl:variable>
    <xsl:value-of select="concat( normalize-space( $tmp ), ' ' )"/>
  </xsl:template>

  <xsl:template match="code">
    <xsl:variable name="tmp">
      <xsl:text>C&lt;</xsl:text><xsl:apply-templates/><xsl:text>&gt;</xsl:text>
    </xsl:variable>
    <xsl:value-of select="concat( normalize-space( $tmp ), ' ' )"/>
  </xsl:template>

  <xsl:template match="code" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
  </xsl:template>

  <xsl:template match="link|jump|fork">

    <!-- rewrite the rewritten link to make it palatable for POD -->
    <xsl:variable name="podlink-text">
      <xsl:value-of select="."/>
      <xsl:if test=".!=''">
        <xsl:text>|</xsl:text>
      </xsl:if>
    </xsl:variable>

    <xsl:variable name="podlink-tmp">
      <xsl:choose>
        <xsl:when test="contains( @href, '#' )">
          <xsl:value-of select="substring-before( @href, '#' )"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@href"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="podlink-link">
      <!-- Remove the '.html' suffix if we are a local URL -->
      <xsl:choose>
        <xsl:when test="starts-with( $podlink-tmp, 'http:' )">
          <xsl:value-of select="$podlink-tmp"/>
        </xsl:when>
        <xsl:when test="starts-with( $podlink-tmp, 'https:' )">
          <xsl:value-of select="$podlink-tmp"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:choose>
            <xsl:when test="contains( $podlink-tmp, '.html' )">
              <xsl:value-of select="substring-before( $podlink-tmp, '.html' )"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$podlink-tmp"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="podlink-anchor">
      <xsl:if test="substring-after( @href, '#' )!=''">
        <xsl:text>/"</xsl:text>
        <xsl:value-of select="substring-after( @href, '#' )"/>
        <xsl:text>"</xsl:text>
      </xsl:if>
    </xsl:variable>

    <xsl:text>L&lt;</xsl:text>
    <xsl:value-of select="concat( $podlink-text, $podlink-link, $podlink-anchor )"/>
    <xsl:text>&gt; </xsl:text>
  </xsl:template>

  <xsl:template match="link|jump|fork" mode="in-table">
    <xsl:choose>
      <xsl:when test="contains( @href, '.html' )">
        <xsl:value-of select="substring-before( @href, '.html' )"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@href"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="figure|img|icon">
    <xsl:variable name="as-text">
      <xsl:choose>
        <xsl:when test="@alt">
          <xsl:value-of select="concat( '[', local-name(), ': ', @alt, '] ' )"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="concat( '[', local-name(), ': ', @src, '] ' )"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$as-text"/>

  </xsl:template>

  <!-- ==================================================================== -->
  <!--  Table Handling

        The column widths are calculated automatically based on a maximum
        width, "maxwidth" (default 74),  that the table can be .  This maxiumum
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
    <xsl:param name="maxwidth">74</xsl:param>
    <xsl:param name="indent">4</xsl:param>
    <xsl:param name="mincolwidth">10</xsl:param>
    <xsl:variable name="cols">
      <xsl:value-of select="count(tr[1]/th|tr[1]/td)"/>
    </xsl:variable>

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

    <xsl:text> </xsl:text>
    <xsl:apply-templates select="caption" mode="in-table"/>
    <xsl:call-template name="cr"/>
    <xsl:apply-templates select="tr" mode="in-table">
      <xsl:with-param name="colwidth" select="$colwidth"/>
      <xsl:with-param name="cols" select="$cols"/>
    </xsl:apply-templates>

    <xsl:call-template name="line-blank"/>

  </xsl:template>

  <xsl:template match="table" mode="in-table">
    <!-- We can't handle nested tables correctly yet.  As a very
         simplistic interim measure, just emit the value of the
         entire table node. -->
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="caption" mode="in-table">
    <xsl:apply-templates/>
  </xsl:template>
    
  <xsl:template match="tr" mode="in-table">
    <xsl:param name="colwidth"/>
    <xsl:param name="cols"/>

    <xsl:variable name="dashes">
      <xsl:call-template name="line-dashed"/>
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
          <xsl:apply-templates select="(th|td)[1]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[2]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[3]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[4]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[5]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[6]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[7]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[8]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[9]" mode="in-table"/>
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
          <xsl:apply-templates select="(th|td)[10]" mode="in-table"/>
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

  <xsl:template match="td|th" mode="in-table">
    <xsl:apply-templates mode="in-table"/>
  </xsl:template>

  <!-- 120 dashes.  Used in building horizontal row borders -->
  <xsl:template name="line-dashed">
    <xsl:variable name="char-20">--------------------</xsl:variable>
    <xsl:value-of select="concat($char-20, $char-20, $char-20, $char-20, $char-20, $char-20)"/>
  </xsl:template>

  <!-- 120 spaces Used in building columns in tables.-->
  <xsl:template name="line-spaces">
    <xsl:variable name="char-20"><xsl:text>                    </xsl:text></xsl:variable>
    <xsl:value-of select="concat($char-20, $char-20, $char-20, $char-20, $char-20, $char-20)"/>
  </xsl:template>

  <xsl:template name="make-row-border">
    <xsl:param name="col"/>
    <xsl:param name="colwidth"/>
    <xsl:param name="maxcols"/>
    <xsl:param name="cell-border"/>
    <xsl:param name="border">+</xsl:param>

    <xsl:choose>
      <xsl:when test="$col > $maxcols">
        <!-- Space at beginning to tell POD to emit as is -->
        <xsl:value-of select="concat( ' ', $border )"/>
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

    <xsl:text> | </xsl:text>
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
      <xsl:call-template name="line-spaces"/>
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
      <xsl:call-template name="line-spaces"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="contains( $text, '&#xa;' )">
        <xsl:variable name="text-before">
          <xsl:value-of select="substring-before( $text, '&#xa;' )"/>
        </xsl:variable>
        <xsl:variable name="tmp">
          <xsl:value-of select="concat( $text-before,
                substring( $spaces, 1, ($colwidth - 2) - (string-length( $text-before ) mod ($colwidth - 2) )),
                substring-after( $text, '&#xa;' ))"/>
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
<!-- ====================================================================== -->
<!-- Local Extensions section -->
<!-- ====================================================================== -->

  <xsl:template match="version"/>

  <xsl:template match="authors"/>

  <xsl:template match="acronym">
    <xsl:value-of select="."/><xsl:text> [</xsl:text><xsl:value-of select="@title"/><xsl:text>] </xsl:text>

  </xsl:template>

  <xsl:template match="text()" mode="as-is">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:variable name="tmp">
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:variable>

    <xsl:if test="$tmp!=''">
      <xsl:value-of select="$tmp"/>
      <xsl:text> </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="text()" mode="in-table">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- Named templates -->

  <!-- Simple carriage return -->
  <xsl:template name="cr">
    <xsl:text>
</xsl:text>
  </xsl:template>

  <!-- Single blank line, no spaces, to separate POD elements -->
  <xsl:template name="line-blank">
    <xsl:call-template name="cr"/>
    <xsl:call-template name="cr"/>
  </xsl:template>


</xsl:stylesheet>
