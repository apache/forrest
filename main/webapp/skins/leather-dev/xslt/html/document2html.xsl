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
<!--
This stylesheet contains templates for converting documentv11 to HTML.  See the
imported document2html.xsl for details.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/document2html.xsl"/>

  <xsl:template match="document">
    <div id="content-main">

      <div id="skinconf-pdflink"/>

      <xsl:if test="normalize-space(header/title)!=''">
        <h1><xsl:value-of select="header/title"/></h1>
      </xsl:if>

      <xsl:if test="normalize-space(header/subtitle)!=''">
        <h3><xsl:value-of select="header/subtitle"/></h3>
      </xsl:if>
<!--
      <xsl:apply-templates select="header/type"/>
      <xsl:apply-templates select="header/notice"/>
      <xsl:apply-templates select="header/abstract"/>
      <xsl:apply-templates select="body"/>

      <div class="attribution">
        <xsl:apply-templates select="header/authors"/>
        <xsl:if test="header/authors and header/version">
          <xsl:text>; </xsl:text>
        </xsl:if>
        <xsl:apply-templates select="header/version"/>
      </div>
    -->  
	<xsl:if test="header/abstract">
        <div class="abstract">
          <xsl:value-of select="header/abstract"/>
        </div>
      </xsl:if>

      <xsl:apply-templates select="body"/>

      <xsl:if test="header/authors">
        <p align="right">
          <font size="-2">
            <xsl:for-each select="header/authors/person">
              <xsl:choose>
                <xsl:when test="position()=1">by&#160;</xsl:when>
                <xsl:otherwise>,&#160;</xsl:otherwise>
              </xsl:choose>
              <xsl:value-of select="@name"/>
            </xsl:for-each>
          </font>
        </p>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template match="body">
    <div id="skinconf-toc-page"/>
    <xsl:apply-templates/>
  </xsl:template>
  
  
  <xsl:template match="@id">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="section">
    <a name="{generate-id()}"/>
    <xsl:apply-templates select="@id"/>

    <xsl:variable name = "level" select = "count(ancestor::section)+1" />

    <xsl:choose>
      <xsl:when test="$level=1">
        <div class="skinconf-heading-{$level}">
          <h1><xsl:value-of select="title"/></h1>
        </div>
        <div class="section">
			<xsl:apply-templates select="*[not(self::title)]"/>
		</div>  
      </xsl:when>
      <xsl:when test="$level=2">
        <div class="skinconf-heading-{$level}">
          <h2><xsl:value-of select="title"/></h2>
        </div>
        <xsl:apply-templates select="*[not(self::title)]"/>
      </xsl:when>
      <!-- If a faq, answer sections will be level 3 (1=Q/A, 2=part) -->
      <xsl:when test="$level=3 and $notoc='true'">
        <h4 class="faq"><xsl:value-of select="title"/></h4>
        <div align="right"><a href="#{@id}-menu">^</a></div>
        <div style="margin-left: 15px">
          <xsl:apply-templates select="*[not(self::title)]"/>
        </div>
      </xsl:when>
      <xsl:when test="$level=3">
        <h4><xsl:value-of select="title"/></h4>
        <xsl:apply-templates select="*[not(self::title)]"/>

      </xsl:when>

      <xsl:otherwise>
        <h5><xsl:value-of select="title"/></h5>
        <xsl:apply-templates select="*[not(self::title)]"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>  
  
  
  <!--xsl:template match="section"-->
    <!-- count the number of section in the ancestor-or-self axis to compute
         the title element name later on -
    <xsl:variable name="sectiondepth" select="count(ancestor-or-self::section)"/>
    <a name="{generate-id()}"/>
    <xsl:apply-templates select="@id"/-->
    <!-- generate a title element, level 1 -> h2, level 2 -> h3 and so on... -
    <xsl:element name="{concat('h',$sectiondepth + 1)}">
      <xsl:value-of select="title"/>
      <xsl:if test="$notoc='true' and $sectiondepth = 3">
        <span style="float: right"><a href="#{@id}-menu">^</a></span>
      </xsl:if>
    </xsl:element>

    <xsl:apply-templates select="*[not(self::title)]"/>
  </xsl:template-->
  
  <!-- Generates the "printer friendly version" link -->
  <!--xsl:template name="printlink">
    <xsl:if test="$disable-print-link = 'false'"> 
    <div class="printlink">
	  <a href="javascript:void(window.print())">
        <img class="skin" src="{$skin-img-dir}/printer.gif" alt="Print this Page"/>
	  </a>
	</div>
    </xsl:if>
  </xsl:template-->

  <!-- Generates the PDF link -->
  <!--xsl:template name="pdflink">
    <xsl:if test="$dynamic-page='false'">
      <xsl:if test="not($config/disable-pdf-link) or $disable-pdf-link = 'false'"> 
      <div class="printlink">
        <a href="{$filename-noext}.pdf">
          <img class="skin" src="{$skin-img-dir}/pdfdoc.gif" alt="PDF"/>
        </a>
  	  </div>
      </xsl:if>
    </xsl:if>
  </xsl:template-->
  

  <!-- Generates the XML link -->
  <!--xsl:template name="xmllink">
    <xsl:if test="$dynamic-page='false'">
      <xsl:if test="$disable-xml-link = 'false'">
      <div class="printlink">
        <a href="{$filename-noext}.xml">
          <img class="skin" src="{$skin-img-dir}/xmldoc.gif" alt="xml"/>
        </a>
      </div>
      </xsl:if>
    </xsl:if>
  </xsl:template-->

  <xsl:template match="figure">
    <xsl:apply-templates select="@id"/>
    <div style="text-align: center;">
      <img src="{@src}" alt="{@alt}" class="figure">
        <xsl:if test="@height">
          <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@width">
          <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
        </xsl:if>
      </img>
    </div>
  </xsl:template>

</xsl:stylesheet>
