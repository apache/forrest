<?xml version="1.0" encoding="iso-8859-1"?>
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

<xsl:stylesheet version="1.0" xmlns:style="http://openoffice.org/2000/style" xmlns:text="http://openoffice.org/2000/text" xmlns:office="http://openoffice.org/2000/office" xmlns:table="http://openoffice.org/2000/table" xmlns:draw="http://openoffice.org/2000/drawing" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://openoffice.org/2000/meta" xmlns:number="http://openoffice.org/2000/datastyle" xmlns:svg="http://www.w3.org/2000/svg" xmlns:chart="http://openoffice.org/2000/chart" xmlns:dr3d="http://openoffice.org/2000/dr3d" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="http://openoffice.org/2000/form" xmlns:script="http://openoffice.org/2000/script" xmlns:config="http://openoffice.org/2001/config" office:class="text" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="office meta  table number dc fo xlink chart math script xsl draw svg dr3d form config text style">
  <xsl:param name="filename"/>
  <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
  <xsl:output method="xml" version="1.0" encoding="UTF-8" doctype-public="-//APACHE//DTD Documentation V1.3//EN" doctype-system="http://apache.org/forrest/dtd/document-v13.dtd"/>
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
      </header>
      <xsl:apply-templates select="/*/office:body"/>
    </document>
  </xsl:template>
  <!--+
      | create the body 
      +-->
  <xsl:template match="office:body">
    <body>  
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
    <xsl:choose>
      <xsl:when test="$currentLevel &gt; $prevLevel+1">
        <section>
          <title>hugo</title>
          <xsl:call-template name="createSection">
            <xsl:with-param name="currentLevel" select="$currentLevel"/>
            <xsl:with-param name="prevLevel" select="$prevLevel +1"/>
          </xsl:call-template>
        </section>
      </xsl:when>
      <xsl:otherwise>
        <section>
          <title>
            <xsl:apply-templates/>
          </title>
          <xsl:apply-templates select="key('rootChildren', generate-id())"/>
          <xsl:apply-templates select="key('chieldElements', generate-id())"/>
        </section>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <!--+
      | paragraph
      +-->
  <xsl:template match="text:p">
    <xsl:element name="p">
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  <xsl:template match="text:p" mode="in-element">
    <xsl:apply-templates/>
  </xsl:template>  
  <!--+
      | strong emphasised text
      +-->
  <xsl:template match="text:span[@text:style-name='Strong Emphasis']">
    <strong>
      <xsl:apply-templates/>
    </strong>
  </xsl:template>  
  <!--+
      | emphasised text
      +-->
  <xsl:template match="text:span[@text:style-name='Emphasis']">
    <em>
      <xsl:apply-templates/>
    </em>
  </xsl:template>    
  <!--+
      | Code
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Code']">
    <code>
      <xsl:value-of select="."/>
    </code>
  </xsl:template>
  <!--+
      | sup
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Above']">
    <sup>
      <xsl:value-of select="."/>
    </sup>
  </xsl:template>
  <!--+
      | sub
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Below']">
    <sub>
      <xsl:value-of select="."/>
    </sub>
  </xsl:template>    
  <!--+
      | line breaks 
      +-->
  <xsl:template match="text:line-break">
    <br/>
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
      <xsl:apply-templates select="text:p" mode="in-element"/>
    </li>
    <xsl:apply-templates select="text:unordered-list"/>
    <xsl:apply-templates select="text:ordered-list"/>    
  </xsl:template>
  <!--+
      | Images
      +-->
  <xsl:template match="draw:image[@xlink:show]">
    <img src="{@xlink:href}" alt="{@draw:name}"/>
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
      | Images
      +-->
  
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
      | Source
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Source']">
    <source>
<xsl:text>
</xsl:text>
      <xsl:apply-templates/>
<xsl:text>
</xsl:text>
   
    </source>
  </xsl:template>      
  <xsl:template match="text:p[@text:style-name='Forrest: Source']/text:line-break">
    <br/>
  </xsl:template> 
  <!--+
      | Warning
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Warning']">
    <warning>
      <xsl:apply-templates/>
    </warning>
  </xsl:template>
  <!--+
      | Fixme
      | - the author attribute is ignored but this has to change with
      |   XHTML2 anyway ... (RP)
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Fixme']">
    <fixme>
      <xsl:apply-templates/>
    </fixme>
  </xsl:template>
  <!--+
      | Note
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Note']">
    <note>
      <xsl:apply-templates/>
    </note>
  </xsl:template>    
</xsl:stylesheet>
