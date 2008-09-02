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

  <xsl:include href="lm://transform.xml.pathutils"/>

  <xsl:template match="anchor">
    <fo:block>
      <xsl:copy-of select="@id"/>
    </fo:block>
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="section">
    <xsl:param name="level">0</xsl:param>
    <xsl:variable name="size">
      <xsl:value-of select="12-number($level)"/>
    </xsl:variable>
    <fo:block
            font-family="{$sectionTitleFontFamily}"
            font-size="{$size}pt"
            font-weight="bold"
            space-before="12pt"
            space-after="8pt"
            margin="0"
            keep-with-next.within-column="always">
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="normalize-space(@id)!=''">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:if test="$heading-type = 'boxed'">
        <xsl:attribute name="background-color">
          <xsl:value-of select="$heading-color"/>
        </xsl:attribute>
        <xsl:attribute name="padding-left">3pt</xsl:attribute>
        <xsl:attribute name="padding-top">4pt</xsl:attribute>
      </xsl:if>
      <xsl:if test="$heading-type = 'underlined'">
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-width">
          <xsl:value-of select="2+(2-number($level))*2"/>pt</xsl:attribute>
        <xsl:attribute name="border-bottom-color">
          <xsl:value-of select="$heading-color"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if
        test="$numbersections = 'true' and number($level) &lt; $numbering-max-depth+1">
        <xsl:variable name="section-nr">
          <xsl:number count="section" format="1.1.1.1.1.1.1" level="multiple"/>
        </xsl:variable>
        <xsl:if test="not(starts-with(title, $section-nr))">
          <xsl:value-of select="$section-nr"/>
          <xsl:text> </xsl:text>
        </xsl:if>
      </xsl:if>
      <!-- For sections 4  or more nestings deep, indent instead of number -->
      <xsl:if test="number($level) &gt; $numbering-max-depth+1">
        <xsl:attribute name="start-indent">
          <xsl:value-of select="4+number($level)"/>
          <xsl:text>pt</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <xsl:value-of select="title"/>
    </fo:block>
    <fo:block background-color="{$background-color}">
      <xsl:apply-templates>
        <xsl:with-param name="level" select="number($level)+1"/>
      </xsl:apply-templates>
    </fo:block>
  </xsl:template>
  <xsl:template match="title">
    <!-- do nothing as titles are handled in their parent templates -->
  </xsl:template>
  <xsl:template match="subtitle">
    <xsl:param name="level">0</xsl:param>
    <xsl:variable name="size" select="16-(number($level)*1.5)"/>
    <fo:block font-weight="bold" font-size="{$size}pt">
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  <xsl:template match="p">
    <xsl:choose>
      <xsl:when test="ancestor::li and not(preceding-sibling::*)">
        <fo:block space-after="4pt">
          <xsl:copy-of select="@id"/>
          <xsl:call-template name="insertPageBreaks"/>
          <xsl:apply-templates/>
        </fo:block>
      </xsl:when>
      <xsl:otherwise>
        <fo:block space-before="4pt" space-after="4pt">
          <xsl:copy-of select="@id"/>
          <xsl:call-template name="insertPageBreaks"/>
          <xsl:apply-templates/>
        </fo:block>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="source">
    <xsl:variable name="color"
      select="$config/colors/color[@name='code']/@value"/>
    <fo:block
            font-family="{$sourceFontFamily}"
            font-size="8pt"
            padding="6pt"
            margin="0"
            background-color="{$color}"
            white-space-collapse="false"
            linefeed-treatment="preserve"
            white-space-treatment="preserve"
            wrap-option="wrap"
            text-align="start">
      <xsl:copy-of select="@id"/>
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  <xsl:template match="ol|ul">
    <fo:list-block provisional-distance-between-starts="18pt"
      provisional-label-separation="3pt" text-align="start">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>
  <xsl:template match="ol/li">
    <fo:list-item>
      <xsl:copy-of select="@id"/>
      <xsl:if test="not(following-sibling::li[1])">
        <xsl:attribute name="space-after">6pt</xsl:attribute>
      </xsl:if>
      <fo:list-item-label end-indent="label-end()" font-size="9">
        <fo:block>
          <xsl:number format="1."/>
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>
  <!-- Emulate browser handling of these invalid combinations that our DTD
  unfortunately allows -->
  <xsl:template match="ul/ul | ul/ol | ol/ul | ol/ol | ul/dl | ol/dl">
    <fo:list-item>
      <xsl:copy-of select="@id"/>
      <fo:list-item-label end-indent="label-end()">
        <fo:block/>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:list-block>
          <xsl:apply-templates/>
        </fo:list-block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>
  <xsl:template match="ul/li">
    <fo:list-item>
      <xsl:copy-of select="@id"/>
      <xsl:if test="not(following-sibling::li[1])">
        <xsl:attribute name="space-after">6pt</xsl:attribute>
      </xsl:if>
      <fo:list-item-label end-indent="label-end()">
        <fo:block>&#x2022;
        </fo:block>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>
  <xsl:template match="dl">
    <fo:list-block provisional-distance-between-starts="18pt"
      provisional-label-separation="3pt" text-align="start">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:list-block>
  </xsl:template>
  <xsl:template match="dt">
    <fo:list-item>
      <xsl:copy-of select="@id"/>
      <fo:list-item-label end-indent="label-end()">
        <fo:block/>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block font-weight="bold">
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>
  <xsl:template match="dd">
    <fo:list-item>
      <xsl:copy-of select="@id"/>
      <fo:list-item-label end-indent="label-end()">
        <fo:block/>
      </fo:list-item-label>
      <fo:list-item-body start-indent="body-start()">
        <fo:block>
          <xsl:apply-templates/>
        </fo:block>
      </fo:list-item-body>
    </fo:list-item>
  </xsl:template>
  <xsl:template match="strong">
    <fo:inline font-weight="bold">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="em">
    <fo:inline font-style="italic">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="sub">
    <fo:inline vertical-align="sub">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="sup">
    <fo:inline vertical-align="super">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="code">
    <fo:inline font-family="{$codeFontFamily}">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  <xsl:template match="warning">
    <xsl:variable name="color"
      select="$config/colors/color[@name='warning']/@value"/>
    <fo:block
            margin-left="0.25in"
            margin-right="0.25in"
            padding-left="3pt"
            padding-top="2pt"
            padding-bottom="1pt"
            font-size="9pt"
            font-family="{$warningTitleFontFamily}"
            space-before="10pt"
            border-before-style="solid"
            border-start-style="solid"
            border-end-style="solid"
            border-color="{$color}"
            background-color="{$color}"
            color="#ffffff"
            keep-with-previous.within-column="always"
            keep-with-next.within-column="always">
      <xsl:copy-of select="@id"/>
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:choose>
        <xsl:when test="@label">
          <xsl:value-of select="@label"/>
        </xsl:when>
        <xsl:otherwise>Warning: </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="title"/>
    </fo:block>
    <fo:block margin-left="0.25in" margin-right="0.25in"
      font-size="10pt" border-after-style="solid" border-start-style="solid"
      border-end-style="solid" border-color="{$color}" background-color="#fff0f0"
      padding-start="3pt" padding-end="3pt" padding-before="3pt"
      padding-after="3pt" space-after="10pt">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  <xsl:template match="note">
    <xsl:variable name="color"
      select="$config/colors/color[@name='note']/@value"/>
    <fo:block
            margin-left="0.25in"
            margin-right="0.25in"
            padding-left="3pt"
            padding-top="2pt"
            padding-bottom="1pt"
            font-size="9pt"
            font-family="{$noteTitleFontFamily}"
            space-before="10pt"
            border-before-style="solid"
            border-start-style="solid"
            border-end-style="solid"
            border-color="{$color}"
            background-color="{$color}"
            color="#ffffff"
            keep-with-previous.within-column="always"
            keep-with-next.within-column="always">
      <xsl:copy-of select="@id"/>
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:choose>
        <xsl:when test="@label">
          <xsl:value-of select="@label"/>
        </xsl:when>
        <!-- insert i18n stuff here -->
        <xsl:otherwise>Note: </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="title"/>
    </fo:block>
    <fo:block margin-left="0.25in" margin-right="0.25in"
      font-size="10pt" space-after="10pt" border-after-style="solid"
      border-start-style="solid" border-end-style="solid" border-color="{$color}"
      background-color="#F0F0FF" padding-start="3pt" padding-end="3pt"
      padding-before="3pt" padding-after="3pt">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  <xsl:template match="fixme">
    <xsl:variable name="color"
      select="$config/colors/color[@name='fixme']/@value"/>
    <fo:block
            margin-left="0.25in"
            margin-right="0.25in"
            padding-left="3pt"
            padding-top="2pt"
            padding-bottom="1pt"
            font-size="9pt"
            font-family="{$fixmeTitleFontFamily}"
            space-before="10pt"
            border-before-style="solid"
            border-start-style="solid"
            border-end-style="solid"
            border-color="{$color}"
            background-color="{$color}"
            color="#FFFFFF"
            keep-with-previous.within-column="always"
            keep-with-next.within-column="always">
      <xsl:copy-of select="@id"/>
      <xsl:call-template name="insertPageBreaks"/>
      <!-- insert i18n stuff here --> FIXME (
      <xsl:value-of select="@author"/>):
      <xsl:value-of select="title"/> </fo:block>
    <fo:block margin-left="0.25in" margin-right="0.25in"
      font-size="10pt" space-after="10pt" border-after-style="solid"
      border-start-style="solid" border-end-style="solid" border-color="{$color}"
      background-color="#FFF0F0" padding-start="3pt" padding-end="3pt"
      padding-before="3pt" padding-after="3pt">
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  <xsl:template match="link|fork|jump">
    <xsl:variable name="color"
      select="$config/colors/color[@name = 'body']/@link"/>
    <xsl:choose>
      <xsl:when test="not(boolean(@href))">
        <!-- html2document.xsl creates links with name but with no href -> filter those -->
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="starts-with(@href, '#')">
        <fo:basic-link color="{$color}" text-decoration="underline"
          internal-destination="{substring(@href,2)}">
          <xsl:copy-of select="@id"/>
          <xsl:apply-templates/>
        </fo:basic-link>
      </xsl:when>
      <xsl:otherwise>
        <!-- Make relative URLs absolute -->
        <xsl:variable name="href">
          <xsl:choose>
            <!-- already absolute -->
            <xsl:when test="contains(@href,'://')">
              <xsl:value-of select="@href"/>
            </xsl:when>
            <!-- add prefix if one is set -->
            <xsl:when test="$url-prefix != ''">
              <xsl:value-of select="concat($url-prefix,@href)"/>
            </xsl:when>
            <!-- keep as is -->
            <xsl:otherwise>
              <xsl:value-of select="@href"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <fo:basic-link color="{$color}" text-decoration="underline"
          external-destination="{$href}">
          <xsl:copy-of select="@id"/>
          <xsl:apply-templates/>
        </fo:basic-link>
        <xsl:if test="$show-external-urls = 'true' and @href != string(.)"> (
          <xsl:value-of select="$href"/>) </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="imgpath">
    <xsl:choose>
      <!-- already absolute -->
      <xsl:when
        test="contains(string(@src),':')">
        <xsl:value-of select="@src"/>
      </xsl:when>
      <!-- absolute to servlet context -->
      <xsl:when test="starts-with(string(@src),'/')">
        <xsl:value-of
          select="concat('cocoon:/',@src)"/>
      </xsl:when>
      <!-- relative to document -->
      <xsl:otherwise>
        <xsl:call-template name="normalize">
          <xsl:with-param name="path">
            <xsl:value-of select="concat('cocoon://',$path,@src)"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="img-explicit-size">
    <xsl:if test="@height">
      <xsl:attribute name="height">
        <xsl:value-of select="@height"/>
      </xsl:attribute>
      <xsl:attribute name="content-height">scale-to-fit</xsl:attribute>
    </xsl:if>
    <xsl:if test="@width">
      <xsl:attribute name="width">
        <xsl:value-of select="@width"/>
      </xsl:attribute>
      <xsl:attribute name="content-width">scale-to-fit</xsl:attribute>
    </xsl:if>
  </xsl:template>
  <xsl:template match="icon">
    <!-- Make relative paths absolute -->
    <xsl:variable name="imgpath">
      <xsl:call-template name="imgpath"/>
    </xsl:variable>
    <fo:external-graphic src="{$imgpath}">
      <xsl:call-template name="img-explicit-size"/>
    </fo:external-graphic>
    <!-- alt text -->
    <xsl:if test="$config/pdf/show-image-alt-text='true'">
      <xsl:if test="normalize-space(@alt)!=''">
        <xsl:text> (</xsl:text>
        <xsl:value-of select="@alt"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  <xsl:template match="figure|img">
    <fo:block text-align="center">
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:copy-of select="@id"/>
      <!-- Make relative paths absolute -->
      <xsl:variable name="imgpath">
        <xsl:call-template name="imgpath"/>
      </xsl:variable>
      <fo:external-graphic src="{$imgpath}">
        <xsl:call-template name="img-explicit-size"/>
        <!-- Work around for fop094 not supporting 
          scale-down-to-fit, to be replaced as soon as fop095 is released
        -->
        <xsl:if test="not((@width|@height))">
          <xsl:attribute name="width">100%</xsl:attribute>
          <xsl:attribute name="content-width">scale-to-fit</xsl:attribute>
          <xsl:attribute name="content-height">100%</xsl:attribute>
        </xsl:if>
      </fo:external-graphic>
      <!-- alt text -->
      <xsl:if test="$config/pdf/show-image-alt-text='true'">
        <xsl:if test="normalize-space(@alt)!=''">
          <fo:block>
            <xsl:value-of select="@alt"/>
          </fo:block>
        </xsl:if>
      </xsl:if>
    </fo:block>
  </xsl:template>
  
  <xsl:template match="table">
    <!-- FIXME: Apache FOP must have column widths specified at present,
    this section can be removed when this limitation is removed from Fop.
    Unfortunately, this means that each column is a fixed width,
    but at least the table displays! -->
    <xsl:variable name="max-number-columns-td">
      <xsl:for-each select="tr">
        <xsl:sort select="count(td|th)" data-type="number" order="descending"/>
        <xsl:if test="position() = 1">
          <xsl:value-of select="count(td|th)"/>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="max-number-columns-colspan">
      <xsl:for-each select="tr">
        <xsl:sort select="count(td|th)" data-type="number" order="descending"/>
        <xsl:if test="position() = 1">
          <xsl:value-of select="sum(td/@colspan|th/@colspan)"/>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="max-number-columns">
      <xsl:choose>
        <xsl:when test="$max-number-columns-colspan&gt;$max-number-columns-td">
          <xsl:value-of select="$max-number-columns-colspan"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$max-number-columns-td"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <fo:table table-layout="fixed" width="100%">
      <xsl:copy-of select="@id"/>
      <fo:table-column>
        <xsl:attribute name="column-width">
          proportional-column-width(1)
        </xsl:attribute>
        <xsl:attribute name="number-columns-repeated">
          <xsl:value-of select="number($max-number-columns)"/>
        </xsl:attribute>
      </fo:table-column>
      <!-- End of hack for Fop support (if removing this hack, remember
      you need the <fo:table> element) -->
      <xsl:if test="tr[count(th) &gt; 0 and count(td) = 0]">
        <fo:table-header font-size="10pt">
          <xsl:apply-templates select="tr[count(th) &gt; 0 and count(td) = 0]"/>
        </fo:table-header>
      </xsl:if>
      <fo:table-body font-size="10pt">
        <xsl:apply-templates select="tr[count(td) &gt; 0]"/>
      </fo:table-body>
    </fo:table>
    <!-- FIXME: Apache Fop does not support the caption element yet.
    This hack will display the table caption accordingly. -->
    <xsl:if test="caption">
      <fo:block font-size="10pt" text-align="left" font-weight="normal"
        margin-top="5pt" keep-with-next.within-column="always">
        <!-- insert i18n stuff here --> Table
        <xsl:text>
        </xsl:text>
        <xsl:number count="table" level="multiple"/>
        <xsl:text>: </xsl:text>
        <xsl:value-of select="caption"/> </fo:block>
    </xsl:if>
  </xsl:template>
  <xsl:template match="tr">
    <fo:table-row>
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </fo:table-row>
  </xsl:template>
  <xsl:template match="th">
    <xsl:variable name="border-color"
      select="$config/colors/color[@name = 'table']/@value"/>
    <xsl:variable name="background-color" select="$border-color"/>
    <fo:table-cell padding-before="4pt" padding-after="4pt" padding-start="4pt"
      padding-end="4pt" color="#FFFFFF" background-color="{$background-color}"
      border="1pt solid {$border-color}">
      <xsl:copy-of select="@id"/>
      <xsl:if test="@colspan!=''">
        <xsl:attribute name="number-columns-spanned">
          <xsl:value-of select="@colspan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@rowspan!=''">
        <xsl:attribute name="number-rows-spanned">
          <xsl:value-of select="@rowspan"/>
        </xsl:attribute>
      </xsl:if>
      <fo:block text-align="center">
        <xsl:apply-templates/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>
  <xsl:template match="td">
    <xsl:variable name="border-color"
      select="$config/colors/color[@name = 'table']/@value"/>
    <xsl:variable name="background-color"
      select="$config/colors/color[@name = 'table-cell']/@value"/>
    <fo:table-cell padding-before="4pt" padding-after="4pt" padding-start="4pt"
      padding-end="4pt" background-color="{$background-color}"
      border="1pt solid {$border-color}">
      <xsl:copy-of select="@id"/>
      <xsl:if test="@colspan!=''">
        <xsl:attribute name="number-columns-spanned">
          <xsl:value-of select="@colspan"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@rowspan!=''">
        <xsl:attribute name="number-rows-spanned">
          <xsl:value-of select="@rowspan"/>
        </xsl:attribute>
      </xsl:if>
      <fo:block>
        <xsl:apply-templates/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>
  <xsl:template match="br">
    <fo:block>
      <xsl:copy-of select="@id"/>
    </fo:block>
  </xsl:template>
  <xsl:template match="legal">
    <fo:inline font-size="8pt">
      <xsl:apply-templates/>
    </fo:inline>
  </xsl:template>
  
  <!-- ====================================================================== -->
  <!-- Local Extensions section -->
  
  <!-- ====================================================================== -->
  <xsl:template match="citation">
    <fo:inline>
      <xsl:copy-of select="@id"/> [
      <xsl:value-of select="@def"/>] </fo:inline>
  </xsl:template>
  <xsl:template match="p[@class='quote']">
    <fo:block padding="3pt" margin="0" space-before="4pt" space-after="4pt"
      background-color="#f0f0f0" font-style="italic">
      <xsl:copy-of select="@id"/>
      <xsl:call-template name="insertPageBreaks"/>
      <xsl:apply-templates/>
    </fo:block>
  </xsl:template>
  
  <!-- ====================================================================== -->
  <!-- Temporary section - subject to change on short notice  -->
  
  <!-- ====================================================================== -->
  <xsl:template match="//style">
    <!-- HACK: The OpenOffice.org input plugin currently produces
    intermediate documents that contain a style element, invalid per
    the Forrest Document DTD. This style element must be ignored
    here. To find out why this is done this way, read the comments
    attached to issue FOR-433. -->
  </xsl:template>
</xsl:stylesheet>
