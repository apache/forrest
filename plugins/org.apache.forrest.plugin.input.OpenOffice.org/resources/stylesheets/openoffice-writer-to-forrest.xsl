<?xml version="1.0" encoding="iso-8859-1"?>
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
<xsl:stylesheet version="1.0" xmlns:style="http://openoffice.org/2000/style" xmlns:text="http://openoffice.org/2000/text" xmlns:office="http://openoffice.org/2000/office" xmlns:table="http://openoffice.org/2000/table" xmlns:draw="http://openoffice.org/2000/drawing" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://openoffice.org/2000/meta" xmlns:number="http://openoffice.org/2000/datastyle" xmlns:svg="http://www.w3.org/2000/svg" xmlns:chart="http://openoffice.org/2000/chart" xmlns:dr3d="http://openoffice.org/2000/dr3d" xmlns:math="http://www.w3.org/1998/Math/MathML" xmlns:form="http://openoffice.org/2000/form" xmlns:script="http://openoffice.org/2000/script" xmlns:config="http://openoffice.org/2001/config" office:class="text" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" exclude-result-prefixes="office meta  table number dc fo xlink chart math script xsl draw svg dr3d form config text style">
  <xsl:import href="openoffice-common-to-forrest.xsl"/>
<!-- ================================================================== -->
<!-- The templates below match special styles created in a special 
       Forrest template. However, there is no longer any need to use this
       template as support for Open Office defined styles is being 
       embedded into the stylesheets, that is, all style information should
       be copied into the HTML page.                                      -->
<!-- ================================================================== -->
<!--+
      | strong emphasised text
      +-->
  <xsl:template match="text:span[@text:style-name='Strong Emphasis']"><strong>
    <xsl:apply-templates/></strong>
  </xsl:template>
<!--+
      | emphasised text
      +-->
  <xsl:template match="text:span[@text:style-name='Emphasis']"><em>
    <xsl:apply-templates/></em>
  </xsl:template>
<!--+
      | Code
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Code']"><code>
    <xsl:value-of select="."/></code>
  </xsl:template>
<!--+
      | sup
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Above']"><sup>
    <xsl:value-of select="."/></sup>
  </xsl:template>
<!--+
      | sub
      +-->
  <xsl:template match="text:span[@text:style-name='Forrest: Below']"><sub>
    <xsl:value-of select="."/></sub>
  </xsl:template>
<!--+
     | Source
     +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Source' and (preceding-sibling::node()[1]/@text:style-name='Forrest: Source')]">
<!-- Two subsequnt paragraphs should be considered a single source section -->
  </xsl:template>
  <xsl:template name="forrest-source" match="text:p[@text:style-name='Forrest: Source' and not (preceding-sibling::node()[1]/@text:style-name='Forrest: Source')]">
    <source>
<xsl:text>
      </xsl:text>
      <xsl:apply-templates />
      <xsl:for-each select="following-sibling::*[1][@text:style-name='Forrest: Source']">
        <xsl:call-template name="forrest-source-output" />
      </xsl:for-each>
<xsl:text>
      </xsl:text>
    </source>
  </xsl:template>
  <xsl:template match="text:p[@text:style-name='Forrest: Source']/text:line-break">
    <br/>
  </xsl:template>
<!-- When the indentation is formed by spaces -->
  <xsl:template match="text:p[@text:style-name='Forrest: Source']/text:s">
    <xsl:call-template name="forrest-source-indent">
      <xsl:with-param name="len">
        <xsl:value-of select="@text:c" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
<!-- When the indentation is formed by tab-stops -->
  <xsl:template match="text:p[@text:style-name='Forrest: Source']/text:tab-stop">
    <xsl:call-template name="forrest-source-indent">
      <xsl:with-param name="len">4</xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="forrest-source-indent">
    <xsl:param name="len"></xsl:param>
<!-- Limited to 100 indents, but it works!-->
    <xsl:variable name="padding">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</xsl:variable>
    <xsl:value-of select="substring($padding,1,$len)" />
  </xsl:template>
  <xsl:template name="forrest-source-output">
<xsl:text>
    </xsl:text>
    <xsl:apply-templates />
    <xsl:for-each select="following-sibling::*[1][@text:style-name='Forrest: Source']">
      <xsl:call-template name="forrest-source-output" />
    </xsl:for-each>
  </xsl:template>
<!--+
      | Fixme
      | - the author attribute is ignored but this has to change with
      |   XHTML2 anyway ... (RP)
      +-->
  <xsl:template match="text:p[@text:style-name='Forrest: Fixme']">
    <fixme author="">
      <xsl:apply-templates/>
    </fixme>
  </xsl:template>
<!--+
      | Fixme2
      | - translates the yellow stickers to fixmes
      |   
      +-->
  <xsl:template match="office:annotation">
    <fixme author="{@office:author}">
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
<!--+
      | special list format p2
      |
      | 0verwrites template in openoffice-common2forrest
      | to allow for bulleted list item 'forrest: instructional step' in oo to 
      | become a simple para with a class-attr in forrest (instruction)
      | In order to do so, this template omits the <ul>-Element
      | and bypasses the processing of the <li>-Element.
      |        
      +-->
  <xsl:template match="text:unordered-list[@text:style-name='L2']">
    <xsl:apply-templates select="text:list-item/text:p"/>
    <xsl:apply-templates select="text:list-item/text:unordered-list"/>
    <xsl:apply-templates select="text:list-item/text:ordered-list"/>
  </xsl:template>
<!--+
      | Instructional step
      +-->
  <xsl:template match="text:p[@text:style-name='P2']">
    <p class="instruction">
      <xsl:apply-templates/>
    </p>
  </xsl:template>
</xsl:stylesheet>
