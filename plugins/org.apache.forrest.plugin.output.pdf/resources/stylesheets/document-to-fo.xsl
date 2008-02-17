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
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    version="1.0">
  <xsl:variable
        name="config"
        select="//skinconfig" />
<!-- left, justify, right -->
  <xsl:variable
        name="text-align"
        select="string($config/pdf/page/@text-align)" />
<!-- prefix which turns relative URLs into absolute ones, empty by default -->
  <xsl:variable
        name="url-prefix"
        select="string($config/pdf/url-prefix)" />
<!-- print URL of external links -->
  <xsl:variable
        name="show-external-urls"
        select="$config/pdf/show-external-urls" />
<!-- disable the table of content (enabled by default) -->
  <xsl:variable
        name="disable-toc"
        select="string($config/pdf/disable-toc)" />
<!-- Get the section depth to use when generating the minitoc (default is 2) -->
  <xsl:variable
        name="toc-max-depth"
        select="number($config/toc/@max-depth)" />
<!-- The page size to be used -->
  <xsl:variable
        name="pagesize"
        select="string($config/pdf/page/@size)" />
<!-- The page orientation ("portrait" or "landscape") -->
  <xsl:variable
        name="pageorientation"
        select="string($config/pdf/page/@orientation)" />
<!-- Double-sided printing toggle -->
  <xsl:variable
        name="doublesided"
        select="string($config/pdf/margins/@double-sided)" />
<!-- The top page margin -->
  <xsl:variable
        name="topmargin"
        select="string($config/pdf/margins/top)" />
<!-- The bottom page margin -->
  <xsl:variable
        name="bottommargin"
        select="string($config/pdf/margins/bottom)" />
<!-- The inner page margin (always the left margin if
  double-sided printing is off, alternating between left and right if
  it's on) -->
  <xsl:variable
        name="innermargin"
        select="string($config/pdf/margins/inner)" />
<!-- The outer page margin (always the right margin if
  double-sided printing is off, alternating between right and left if
  it's on)-->
  <xsl:variable
        name="outermargin"
        select="string($config/pdf/margins/outer)" />
  <xsl:param
        name="numbersections"
        select="'true'" />
<!-- page breaks after TOC and each page if an aggregate document -->
  <xsl:variable
        name="page-break-top-sections"
        select="'true'" />
<!-- page numbering format -->
  <xsl:variable
        name="page-numbering-format"
        select="string($config/pdf/page-numbering-format)" />
  <xsl:variable
        name="background-color"
        select="$config/colors/color[@name='body']/@value" />
  <xsl:variable
        name="heading-color"
        select="$config/colors/color[@name='subheading']/@value" />
  <xsl:variable
        name="heading-type"
        select="$config/headings/@type" />
<!-- Section depth at which we stop numbering and just indent -->
  <xsl:param
        name="numbering-max-depth"
        select="'3'" />
  <xsl:param
        name="imagesdir"
        select="." />
  <xsl:param
        name="xmlbasedir" />
  <xsl:include
        href="pdfoutline.xsl" />
  <xsl:include
        href="footerinfo.xsl" />
  <xsl:include
        href="helper-pageBreaks.xsl" />
  <xsl:include
        href="helper-pageNumber.xsl" />
  <xsl:include
        href="helper-commonElements.xsl" />
<!-- Determine page height for various page sizes (US Letter portrait
  is the default) -->
<!-- FIXME: JJP:would this be better of a file? -->
  <xsl:variable
        name="pageheight">
    <xsl:choose>
      <xsl:when
                test="$pageorientation = 'landscape'">
        <xsl:choose>
          <xsl:when
                        test="$pagesize = 'a0'">841mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a1'">594mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a2'">420mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a3'">297mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a4'">210mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a5'">148mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'executive'">7.25in</xsl:when>
          <xsl:when
                        test="$pagesize = 'folio'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'ledger'">11in</xsl:when>
          <xsl:when
                        test="$pagesize = 'legal'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'letter'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'quarto'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'tabloid'">11in</xsl:when>
          <xsl:otherwise>8.5in</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when
                        test="$pagesize = 'a0'">1189mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a1'">841mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a2'">594mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a3'">420mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a4'">297mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a5'">210mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'executive'">10.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'folio'">13in</xsl:when>
          <xsl:when
                        test="$pagesize = 'ledger'">17in</xsl:when>
          <xsl:when
                        test="$pagesize = 'legal'">14in</xsl:when>
          <xsl:when
                        test="$pagesize = 'quarto'">10.83in</xsl:when>
          <xsl:when
                        test="$pagesize = 'tabloid'">17in</xsl:when>
          <xsl:otherwise>11in</xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
<!-- Determine page width for various page sizes (US Letter portrait
  is the default) -->
  <xsl:variable
        name="pagewidth">
    <xsl:choose>
      <xsl:when
                test="$pageorientation = 'landscape'">
        <xsl:choose>
          <xsl:when
                        test="$pagesize = 'a0'">1189mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a1'">841mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a2'">594mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a3'">420mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a4'">297mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a5'">210mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'executive'">10.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'folio'">13in</xsl:when>
          <xsl:when
                        test="$pagesize = 'ledger'">17in</xsl:when>
          <xsl:when
                        test="$pagesize = 'legal'">14in</xsl:when>
          <xsl:when
                        test="$pagesize = 'quarto'">10.83in</xsl:when>
          <xsl:when
                        test="$pagesize = 'tabloid'">17in</xsl:when>
          <xsl:otherwise>11in</xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:choose>
          <xsl:when
                        test="$pagesize = 'a0'">841mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a1'">594mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a2'">420mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a3'">297mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a4'">210mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'a5'">148mm</xsl:when>
          <xsl:when
                        test="$pagesize = 'executive'">7.25in</xsl:when>
          <xsl:when
                        test="$pagesize = 'folio'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'ledger'">11in</xsl:when>
          <xsl:when
                        test="$pagesize = 'legal'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'letter'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'quarto'">8.5in</xsl:when>
          <xsl:when
                        test="$pagesize = 'tabloid'">11in</xsl:when>
          <xsl:otherwise>8.5in</xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:template
        match="/">
    <fo:root
            xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master
                    master-name="first-page"
                    page-height="{$pageheight}"
                    page-width="{$pagewidth}"
                    margin-top="{$topmargin}"
                    margin-bottom="{$bottommargin}"
                    margin-left="{$innermargin}"
                    margin-right="{$outermargin}">
          <fo:region-body
                        margin-top="0.5in"
                        margin-bottom=".5in" />
          <fo:region-after
                        region-name="first-footer"
                        extent=".5in"
                        display-align="before" />
        </fo:simple-page-master>
        <fo:simple-page-master
                    master-name="even-page"
                    page-height="{$pageheight}"
                    page-width="{$pagewidth}"
                    margin-top="{$topmargin}"
                    margin-bottom="{$bottommargin}">
          <xsl:choose>
            <xsl:when
                            test="$doublesided = 'true'">
              <xsl:attribute
                                name="margin-left">
                <xsl:value-of
                                    select="$outermargin" />
              </xsl:attribute>
              <xsl:attribute
                                name="margin-right">
                <xsl:value-of
                                    select="$innermargin" />
              </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute
                                name="margin-left">
                <xsl:value-of
                                    select="$innermargin" />
              </xsl:attribute>
              <xsl:attribute
                                name="margin-right">
                <xsl:value-of
                                    select="$outermargin" />
              </xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
          <fo:region-body
                        margin-top="0.5in"
                        margin-bottom=".5in" />
          <fo:region-before
                        region-name="even-header"
                        extent="0.5in" />
<!-- commented out from region-body
                           because it is not standard conforming
                        border-bottom="0.5pt solid"  -->
          <fo:region-after
                        region-name="even-footer"
                        extent=".5in"
                        display-align="before" />
        </fo:simple-page-master>
        <fo:simple-page-master
                    master-name="odd-page"
                    page-height="{$pageheight}"
                    page-width="{$pagewidth}"
                    margin-top="{$topmargin}"
                    margin-bottom="{$bottommargin}"
                    margin-left="{$innermargin}"
                    margin-right="{$outermargin}">
          <fo:region-body
                        margin-top="0.5in"
                        margin-bottom=".5in" />
          <fo:region-before
                        region-name="odd-header"
                        extent="0.5in" />
<!-- commented out from region-body
                        because it is not standard conforming
                        border-bottom="0.5pt solid"  -->
          <fo:region-after
                        region-name="odd-footer"
                        extent=".5in"
                        display-align="before" />
        </fo:simple-page-master>
        <fo:page-sequence-master
                    master-name="book">
          <fo:repeatable-page-master-alternatives>
            <fo:conditional-page-master-reference
                            page-position="first"
                            master-reference="first-page" />
            <fo:conditional-page-master-reference
                            odd-or-even="odd"
                            master-reference="odd-page" />
            <fo:conditional-page-master-reference
                            odd-or-even="even"
                            master-reference="even-page" />
          </fo:repeatable-page-master-alternatives>
        </fo:page-sequence-master>
      </fo:layout-master-set>
      <xsl:apply-templates
                select="/site/document"
                mode="outline" />
      <fo:page-sequence
                master-reference="book">
        <xsl:apply-templates
                    select="/site/document" />
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <xsl:template
        match="document">
    <fo:title>
      <xsl:value-of
                select="header/title" />
    </fo:title>
    <fo:static-content
            flow-name="first-footer">
      <fo:block
                border-top="0.25pt solid"
                padding-before="6pt"
                text-align="center">
        <xsl:apply-templates
                    select="footer" />
      </fo:block>
<!-- don't list page number on first page if its content is just the TOC -->
      <xsl:if
                test="$disable-toc = 'true' or not($toc-max-depth > 0 and $page-break-top-sections)">
        <xsl:call-template
                    name="insertPageNumber">
          <xsl:with-param
                        name="text-align">start</xsl:with-param>
        </xsl:call-template>
      </xsl:if>
      <xsl:call-template
                name="info" />
    </fo:static-content>
    <fo:static-content
            flow-name="even-header">
      <fo:block
                font-size="70%"
                text-align="end"
                font-style="italic">
        <xsl:value-of
                    select="header/title" />
      </fo:block>
    </fo:static-content>
    <fo:static-content
            flow-name="even-footer">
      <fo:block
                border-top="0.25pt solid"
                padding-before="6pt"
                text-align="center">
        <xsl:apply-templates
                    select="footer" />
      </fo:block>
      <xsl:call-template
                name="insertPageNumber">
        <xsl:with-param
                    name="text-align">end</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template
                name="info" />
    </fo:static-content>
    <fo:static-content
            flow-name="odd-header">
      <fo:block
                font-size="70%"
                text-align="start"
                font-style="italic">
        <xsl:value-of
                    select="header/title" />
      </fo:block>
    </fo:static-content>
    <fo:static-content
            flow-name="odd-footer">
      <fo:block
                border-top="0.25pt solid"
                padding-before="6pt"
                text-align="center">
        <xsl:apply-templates
                    select="footer" />
      </fo:block>
      <xsl:call-template
                name="insertPageNumber">
        <xsl:with-param
                    name="text-align">start</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template
                name="info" />
    </fo:static-content>
    <fo:flow
            flow-name="xsl-region-body">
      <fo:block
                padding-before="24pt"
                padding-after="24pt"
                font-size="24pt"
                font-weight="bold"
                id="{generate-id()}">
        <xsl:value-of
                    select="header/title" />
      </fo:block>
      <fo:block
                text-align="{$text-align}"
                padding-before="18pt"
                padding-after="18pt">
        <xsl:apply-templates />
      </fo:block>
<!-- Total number of pages calculation... -->
      <fo:block
                id="term" />
    </fo:flow>
  </xsl:template>
  <xsl:template
        match="abstract">
    <fo:block
            font-size="12pt"
            text-align="center"
            space-before="20pt"
            space-after="25pt"
            width="7.5in"
            font-family="serif"
            font-style="italic">
      <xsl:call-template
                name="insertPageBreaks" />
      <xsl:apply-templates />
    </fo:block>
  </xsl:template>
  <xsl:template
        match="notice">
    <fo:block
            font-size="10pt"
            text-align="left"
            space-before="20pt"
            width="7.5in"
            font-family="serif"
            border-top="0.25pt solid"
            border-bottom="0.25pt solid"
            padding-before="6pt"
            padding-after="6pt">
      <xsl:copy-of
                select="@id" />
      <xsl:call-template
                name="insertPageBreaks" />
<!-- insert i18n stuff here --> NOTICE: <xsl:apply-templates />
    </fo:block>
  </xsl:template>
  <xsl:template
        match="anchor">
    <fo:block>
      <xsl:copy-of
                select="@id" />
    </fo:block>
    <xsl:apply-templates />
  </xsl:template>
  <xsl:template
        match="authors">
    <fo:block
            space-before="20pt"
            font-weight="bold"
            font-size="9pt">
      <xsl:call-template
                name="insertPageBreaks" />
<!-- insert i18n stuff here --> by <xsl:for-each
                select="person">
        <xsl:value-of
                    select="@name" />
        <xsl:if
                    test="not(position() = last())">, </xsl:if>
      </xsl:for-each>
    </fo:block>
  </xsl:template>
  
</xsl:stylesheet>
