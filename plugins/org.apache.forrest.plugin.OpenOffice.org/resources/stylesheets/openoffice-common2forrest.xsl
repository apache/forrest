<?xml version="1.0" encoding="iso-8859-1"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
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

<xsl:stylesheet version="1.0" xmlns:style="http://openoffice.org/2000/style" xmlns:text="http://openoffice.org/2000/text" xmlns:office="http://openoffice.org/2000/office" xmlns:table="http://openoffice.org/2000/table" xmlns:draw="http://openoffice.org/2000/drawing" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://openoffice.org/2000/meta" xmlns:number="http://openoffice.org/2000/datastyle" xmlns:svg="http://www.w3.org/2000/svg" xmlns:chart="http://openoffice.org/2000/chart" xmlns:dr3d="http://openoffice.org/2000/dr3d" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="http://openoffice.org/2000/form" xmlns:script="http://openoffice.org/2000/script" xmlns:config="http://openoffice.org/2001/config" office:class="text" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="office meta  table number dc fo xlink chart math script xsl draw svg dr3d form config text style">
  <xsl:param name="filename"/>
  <xsl:param name="extension"/>
  
  <xsl:variable name="OOtemplate" select="/office:document/office:meta/meta:template/@xlink:title"/>
   
  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
  <xsl:output method="xml" version="1.0" encoding="UTF-8" 
    doctype-public="-//APACHE//DTD Documentation V1.2//EN" 
    doctype-system="http://apache.org/forrest/dtd/document-v12.dtd"/>
  
    <!--+
      | keys to generate the structure
      +-->
  <xsl:key name="rootChildren" match="draw:text-box | text:footnote-body | text:section | text:p |table:table | text:span | text:ordered-list | office:annotation | text:unordered-list | text:footnote | text:a | text:list-item | draw:plugin" use="generate-id((..|preceding-sibling::text:h[@text:level='1']|preceding-sibling::text:h[@text:level='2']|preceding-sibling::text:h[@text:level='3']|preceding-sibling::text:h[@text:level='4']|preceding-sibling::text:h[@text:level='5'])[last()])"/>
  <xsl:key name="chieldElements" match="text:h[@text:level &gt; '1' and @text:level &lt;= '5']" use="generate-id(preceding-sibling::text:h[@text:level &lt; current()/@text:level][1])"/>
  
  <!--+
      | start transforming 
      +-->
  <xsl:template match="/">
    <document>
      <header>
        <title>
          <xsl:choose>
            <xsl:when test="/office:document/office:meta/dc:title = '' or not(/office:document/office:meta/dc:title)">
              <xsl:value-of select="$filename"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="/office:document/office:meta/dc:title"/>
            </xsl:otherwise>
          </xsl:choose>
        </title>
        <!-- Abstract is required field in HowTos, so always create it and fill with description field  -->
        <abstract>
          <xsl:value-of select="/office:document/office:meta/dc:description"/>
        </abstract>
      
        <!-- last modified date -->
        <last-modified-content-date date="{substring-before(/office:document/office:meta/dc:date, 'T')}"/>

        <xsl:call-template name="style"/>
      </header>
      <xsl:apply-templates select="//office:body"/>
    </document>
  </xsl:template>
  
  <!--+
      | create the body 
      +-->
  <xsl:template match="office:body">
    <body>
      <xsl:if test="$OOtemplate = 'OpenOffice.org Writer HowTo Template'">
        <!-- Special processing for top of HowTos -->
        <header>
          <title>
              <xsl:choose>
                <xsl:when test="/office:document/office:meta/dc:title = '' or not(/office:document/office:meta/dc:title)">
                  <xsl:value-of select="$filename"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="/office:document/office:meta/dc:title"/>
                </xsl:otherwise>
              </xsl:choose>
            </title>
            <section id="Overview">
              <title>Overview</title>
            <p>
              <xsl:value-of select="/office:document/office:meta/dc:description"/>
            </p>
          </section>
          </header>
          
        </xsl:if> 
         <xsl:apply-templates select="key('rootChildren', generate-id())"/>
        <xsl:apply-templates select="text:h[@text:level='1']"/>  
      
    </body>
  </xsl:template>
  
  <!--+
      | headings
      +-->
  <xsl:template match="text:h[@text:level='1']">
    <xsl:call-template name="createSection">
      <xsl:with-param name="currentLevel" select="@text:level"/>
      <xsl:with-param name="prevLevel" select="1"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="text:h[@text:level='2'] | text:h[@text:level='3']| text:h[@text:level='4'] | text:h[@text:level='5']">
    <xsl:variable name="level" select="@text:level"/>
    <xsl:call-template name="createSection">
      <xsl:with-param name="currentLevel" select="$level"/>
      <xsl:with-param name="prevLevel" select="preceding-sibling::text:h[@text:level &lt; $level][1]/@text:level "/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template name="createSection">
    <xsl:param name="currentLevel"/>
    <xsl:param name="prevLevel"/>
          
      
    <section class="{.}">  
      <title>
        <xsl:apply-templates/>
      </title>
      <!-- Report structuring errors with a fixme -->
      <xsl:if test="$currentLevel &gt; $prevLevel+1">
        <fixme author="openoffice-common2forrest template">
          The previous heading is more than one level below the heading before it. To remove this fixme, correct the structuring of your document.
        </fixme>
      </xsl:if>
      <xsl:apply-templates select="key('rootChildren', generate-id())"/>
      <xsl:apply-templates select="key('chieldElements', generate-id())"/>
    </section>
  
  </xsl:template>
  
  <!--+
      | paragraph
      +-->
  <xsl:template match="text:p">
    <xsl:element name="p">
      <xsl:apply-templates select="@text:style-name"/>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  
  <!--+
      | lists
      +-->
  <xsl:template match="text:unordered-list">
    <ul>
      <xsl:apply-templates select="text:list-item"/>
    </ul>
  </xsl:template>

  <xsl:template match="text:ordered-list">
    <ol>
      <xsl:apply-templates select="text:list-item"/>
    </ol>
  </xsl:template>
  
  <xsl:template match="text:list-item">
    <li>
      <xsl:apply-templates select="text:p"/>
      <xsl:apply-templates select="text:unordered-list"/>
      <xsl:apply-templates select="text:ordered-list"/>    
    </li>
  </xsl:template>
      
  
    <!--+
      | Tables
      | Note (RP): need some improvement, maybe forrest-doc isn't good enough
      | for many talbe requirements ...
      +-->
  <xsl:template match="table:table">
    <table>
      <caption><xsl:value-of select="@table:name"/></caption>
      <xsl:apply-templates select="table:*"/>
    </table>
  </xsl:template>
  <xsl:template match="table:table-header-rows">
    <xsl:apply-templates select="table:table-row"/>
  </xsl:template>  
  <xsl:template match="table:table-row">
    <tr>
      <xsl:apply-templates select="table:table-cell"/>
    </tr>
  </xsl:template>
  <xsl:template match="table:table-header-rows/table:table-row/table:table-cell">
    <th>
      <xsl:apply-templates select="text:p" mode="in-element"/>
    </th>
  </xsl:template>  
  <xsl:template match="table:table-cell">
    <td>
      <xsl:apply-templates select="text:p" mode="in-element"/>
    </td>
  </xsl:template>
  
  <!--+
      | Links
      +-->
  <xsl:template match="text:a[not(@office:target-frame-name)]">
    <link href="{@xlink:href}" title="{@office:name}">
      <xsl:value-of select="."/>
    </link>
  </xsl:template>
  <xsl:template match="text:a[@office:target-frame-name='_new']">
    <jump href="{@xlink:href}" title="{@office:name}">
      <xsl:value-of select="."/>
    </jump>
  </xsl:template>  
  <xsl:template match="text:a">
    <fork href="{@xlink:href}" title="{@office:name}">
      <xsl:value-of select="."/>
    </fork>
  </xsl:template>  
  
  <!--+
      | Images
      +-->
  <xsl:template match="draw:image[@xlink:show]">
    <xsl:variable name="href"><xsl:value-of select="@xlink:href"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="starts-with($href, '#')">
        <img src="openOfficeEmbeddedImage/zip-{$filename}.{$extension}/file-{substring($href, 2)}"
          alt="{@draw:name}"/>
      </xsl:when>
      <xsl:otherwise>
        <img src="{$href}" alt="{@draw:name}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--+
      | line breaks 
      +-->
  <xsl:template match="text:line-break">
    <br/>
  </xsl:template>
  
  <!-- ==================================================================== -->
  <!-- Span                                                                 -->
  <!-- ==================================================================== -->
  <xsl:template match="text:span">
    <span>
      <xsl:apply-templates select="@text:style-name"/>
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <!-- ==================================================================== -->
  <!-- Styles                                                               -->
  <!-- ==================================================================== -->
  
  <xsl:template match="@text:style-name">
    <xsl:attribute name="class">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>
  
  <xsl:template name="style">
    <style>
      <xsl:apply-templates select="//office:styles/style:style"/>
      <xsl:apply-templates select="//office:automatic-styles/style:style"/>
    </style>
  </xsl:template>
 
  <xsl:template match="style:style">
    .<xsl:value-of select="@style:name"/> {
      <xsl:call-template name="fontStyle"/>
      <xsl:call-template name="textDecorationStyle"/>
      <xsl:call-template name="colourStyle"/>
      <xsl:call-template name="marginStyle"/>
    }
  </xsl:template>
  
  <!-- Margin Style -->
  
  <xsl:template name="marginStyle">
    <xsl:apply-templates select="style:properties/@fo:margin-top"/>
    <xsl:apply-templates select="style:properties/@fo:margin-bottom"/>
    <xsl:apply-templates select="style:properties/@fo:margin-left"/>
    <xsl:apply-templates select="style:properties/@fo:margin-right"/>
  </xsl:template>
  
  <xsl:template match="@fo:margin-top">
    margin-top: <xsl:value-of select="."/>;
  </xsl:template>
  
  <xsl:template match="@fo:margin-bottom">
    margin-bottom: <xsl:value-of select="."/>;
  </xsl:template>
  
  <xsl:template match="@fo:margin-left">
    margin-left: <xsl:value-of select="."/>;
  </xsl:template>
  
  <xsl:template match="@fo:margin-right">
    margin-right: <xsl:value-of select="."/>;
  </xsl:template>
  
  <!-- Font Style -->
  
  <xsl:template name="fontStyle">
    <xsl:apply-templates select="style:properties/@fo:font-weight"/>
    <xsl:apply-templates select="style:properties/@fo:font-style"/>
    <xsl:apply-templates select="style:properties/@fo:font-size"/>
    <xsl:apply-templates select="style:properties/@style:font-name"/>
  </xsl:template>
  
  <xsl:template match="@fo:font-size">
    font-size: <xsl:value-of select="."/>;
  </xsl:template>
  
  <xsl:template match="@fo:font-weight">
    font-weight: <xsl:value-of select="."/>;
  </xsl:template>
  
  <xsl:template match="@fo:font-style">
    font-style: <xsl:value-of select="."/>;
  </xsl:template>
  
  <xsl:template match="@style:font-name">
    font-family: <xsl:value-of select="."/>;
  </xsl:template>
  
  <!-- Text Decoration -->
  
  <xsl:template name="textDecorationStyle">
    <xsl:apply-templates select="style:properties/@style:text-underline"/>
  </xsl:template>
  
  <xsl:template match="@style:text-underline">
    text-decoration: underline;
  </xsl:template>
  
  <!-- Colour -->
  
  <xsl:template name="colourStyle">
    <xsl:apply-templates select="style:properties/@fo:color"/>
  </xsl:template>
  
  <xsl:template match="@fo:color">
    color: <xsl:value-of select="."/>;
  </xsl:template>
  
</xsl:stylesheet>
