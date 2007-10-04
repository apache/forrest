<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns="http://www.w3.org/1999/xhtml" 
  xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" 
  xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" 
  xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0" 
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:svg="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0">
  <xsl:import href="lm://transform.odt.xhtml"/>
  <xsl:param name="path" select="'odt path name'"/>
  <xsl:include href="lm://transform.xml.dotdots"/>
  <xsl:include href="lm://transform.xml.pathutils"/>
<!-- Calculate dirname -->
  <xsl:variable name="dirname">
    <xsl:call-template name="dirname">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
<!-- Calculate filename -->
  <xsl:variable name="filename">
    <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
<!-- Path to site root, eg '../../' -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:template match="/">
    <xsl:apply-templates select="/odt/content/*"/>
  </xsl:template>
  <xsl:template match="office:document-content">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title><xsl:choose>
<!-- display title element from ODT File:Properties -->
            <xsl:when 
              test="//odt/meta/office:document-meta/office:meta/dc:title">
              <xsl:value-of 
                select="//odt/meta/office:document-meta/office:meta/dc:title"/>
            </xsl:when>
<!-- if no title element, display 1st h1 -->
            <xsl:when 
              test="not(//odt/meta/office:document-meta/office:meta/dc:title) and
        office:body/office:text/text:h[1] != ''">
              <xsl:value-of select="office:body/office:text/text:h[1]"/>
            </xsl:when>
            <xsl:otherwise>
<!-- if no title element or h1 elements, display $filename -->
              <xsl:value-of select="$filename"/>
            </xsl:otherwise>
          </xsl:choose></title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <xsl:apply-templates select="office:automatic-styles"/>
      </head>
      <body>
        <xsl:apply-templates select="office:body/office:text"/>
      </body>
    </html>
  </xsl:template>
<!-- FIXME : An idea should be to add a class attribute to <p> in order to be able to keep the original
             layout. Maybe depending on a properties keepLayout=True. -->
  <xsl:template match="text:p">
    <xsl:variable name="styleName" select="@text:style-name"/>
    <xsl:variable name="tag">
       <xsl:call-template name="selectTag">
         <xsl:with-param name="currentStyle" select="$styleName"/>
       </xsl:call-template>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="not($tag='p')">
        <xsl:variable name="predecessorIsTheSame">
          <xsl:call-template name="isEqualToStyle">
            <xsl:with-param name="currentStyle" select="$styleName"/>
            <xsl:with-param name="styleToFind" select="preceding-sibling::*[1]/@text:style-name"/>
          </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="not($predecessorIsTheSame='true')">
            <xsl:element name="{$tag}">
              <xsl:apply-templates select="text:span[starts-with(@text:style-name,'Forrest_3a__20_')]"/>
              <xsl:apply-templates/>
              <xsl:apply-templates select="following-sibling::*[1]" mode="followingline">
                <xsl:with-param name="styleToFind" select="$styleName"/>
              </xsl:apply-templates>
            </xsl:element>
          </xsl:when>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:element name="{$tag}">
          <xsl:variable name='layout'>
            <xsl:apply-templates select="//odt/content/office:document-content/office:automatic-styles/style:style[@style:name=$styleName]|//odt/styles/office:document-styles/office:styles/style:style[@style:name=$styleName]"/>
          </xsl:variable>
          <xsl:call-template name="displayLayout">
            <xsl:with-param name="layout" select="$layout"/>
          </xsl:call-template>
          <xsl:if test="count(node())=0">
            <br />
          </xsl:if>
        </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="text:p" mode="followingline">
    <xsl:param name="styleToFind"/>
    <xsl:variable name="isSameStyle">
      <xsl:call-template name="isEqualToStyle">
        <xsl:with-param name="currentStyle" select="@text:style-name"/>
        <xsl:with-param name="styleToFind" select="$styleToFind"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:if test="$isSameStyle='true'">
      <xsl:apply-templates/>
      <xsl:apply-templates select="following-sibling::*[1]" mode="followingline">
        <xsl:with-param name="styleToFind" select="$styleToFind"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>

  <xsl:template match="*" mode="followingline"/>

  <!-- Calculates the tag to insert. -->
  <!-- the specific tags starts with 'Forrest_3a__20_' -->
  <!-- else the <p> tag is returned -->
  <xsl:template name="selectTag">
    <xsl:param name="currentStyle"/>
      <xsl:choose>
        <xsl:when test="starts-with($currentStyle,'Forrest_3a__20_')">
          <xsl:value-of select="translate(normalize-space(substring-after($currentStyle,'Forrest_3a__20_')),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="parentStyleName">
            <xsl:value-of select="//odt/content/office:document-content/office:automatic-styles/style:style[@style:name=$currentStyle]/@style:parent-style-name"/>
            <xsl:value-of select="//odt/styles/office:document-styles/office:styles/style:style[@style:name=$currentStyle]/@style:parent-style-name"/>
          </xsl:variable>
          <xsl:choose>
            <xsl:when test="not($parentStyleName='')">
              <xsl:call-template name="selectTag">
                <xsl:with-param name="currentStyle" select="$parentStyleName"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:if test="$currentStyle">p</xsl:if>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <!-- Try to find the styleToFind in the inheritance tree of currentStyle -->
  <!-- returns 'true' if found -->
  <xsl:template name="isEqualToStyle">
    <xsl:param name="currentStyle"/>
    <xsl:param name="styleToFind"/>
    <xsl:choose>
      <xsl:when test='$currentStyle=$styleToFind or not($currentStyle) or not($styleToFind)'>true</xsl:when>
      <xsl:otherwise>
        <xsl:variable name="parentStyleName">
          <xsl:value-of select="//odt/content/office:document-content/office:automatic-styles/style:style[@style:name=$currentStyle]/@style:parent-style-name"/>
          <xsl:value-of select="//odt/styles/office:document-styles/office:styles/style:style[@style:name=$currentStyle]/@style:parent-style-name"/>
        </xsl:variable>
        <xsl:if test='not($parentStyleName="")'>
          <xsl:call-template name="isEqualToStyle">
            <xsl:with-param name="currentStyle" select="$parentStyleName"/>
            <xsl:with-param name="styleToFind" select="$styleToFind"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


<!-- FIXME : this is not XHTML -->
  <xsl:template match="text:span[starts-with(@text:style-name,'Forrest_3a__20_')]">
    <xsl:variable name="attr" select="translate(normalize-space(substring-after(@text:style-name,'Forrest_3a__20_')),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
    <xsl:attribute name="{$attr}">
      <xsl:value-of select="."/>
    </xsl:attribute>
  </xsl:template>

<!-- Instead of using styles like in the Lenya file, we try here to recognise the style to use XHTML specific
     tags such as <em>, <strong>, <sup> and <sub>... -->
  <xsl:template match="text:span">
    <xsl:variable name="styleName" select="@text:style-name"/>
<!-- the style can be either in content or in style, we try both, one of them does not exist and so is empty... -->
    <xsl:variable name='layout'>
      <xsl:apply-templates select="//odt/content/office:document-content/office:automatic-styles/style:style[@style:name=$styleName]|//odt/styles/office:document-styles/office:styles/style:style[@style:name=$styleName]"/>
    </xsl:variable>
    <xsl:call-template name="displayLayout">
      <xsl:with-param name="layout" select="$layout"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name='displayLayout'>
    <xsl:param name='layout' select="NONE"/>
    <xsl:variable name="tag">
      <xsl:choose>
        <xsl:when test="contains($layout,',')">
          <xsl:value-of select="normalize-space(substring-before($layout,','))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="normalize-space($layout)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="not($tag='NONE') and not($tag='')">
        <xsl:element name="{$tag}">
          <xsl:call-template name="displayLayout">
            <xsl:with-param name="layout" select="substring-after($layout,',')"/>
          </xsl:call-template>
        </xsl:element>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="style:style">
    <xsl:param name="text"/>
    <xsl:variable name="styleName" select="@style:name"/>
    <xsl:choose>
      <xsl:when test='./style:text-properties/@*[last()]'>
        <xsl:apply-templates select="./style:text-properties/@*[last()]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="parentStyleName" select="@style:parent-style-name"/>
        <xsl:apply-templates select="//odt/content/office:document-content/office:automatic-styles/style:style[@style:name=$parentStyleName]|//odt/styles/office:document-styles/office:styles/style:style[@style:name=$parentStyleName]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="style:text-properties/@*">
    <xsl:param name="text"/>
    <xsl:param name="indStyle" select="count(../@*)"/>
    <xsl:choose>
<!-- Case of the code style - generally rendered with Courier -->
      <xsl:when test="name()='style:font-name' and starts-with(.,'Courier')">
        <xsl:call-template name="addLayout2List">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="indStyle" select="$indStyle"/>
          <xsl:with-param name="tag" select="'code'"/>
        </xsl:call-template>
      </xsl:when>
<!-- Case of the emphasys style - generally rendered with Italic -->
      <xsl:when test="name()='fo:font-style' and .='italic'">
        <xsl:call-template name="addLayout2List">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="indStyle" select="$indStyle"/>
          <xsl:with-param name="tag" select="'em'"/>
        </xsl:call-template>
      </xsl:when>
<!-- Case of the strong style -->
      <xsl:when test="name()='fo:font-weight' and .='bold'">
        <xsl:call-template name="addLayout2List">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="indStyle" select="$indStyle"/>
          <xsl:with-param name="tag" select="'strong'"/>
        </xsl:call-template>
      </xsl:when>
<!-- Case of the exponent style -->
      <xsl:when test="name()='style:text-position' and starts-with(.,'super')">
        <xsl:call-template name="addLayout2List">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="indStyle" select="$indStyle"/>
          <xsl:with-param name="tag" select="'sup'"/>
        </xsl:call-template>
      </xsl:when>
<!-- Case of the subscript style -->
      <xsl:when test="name()='style:text-position' and starts-with(.,'sub')">
        <xsl:call-template name="addLayout2List">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="indStyle" select="$indStyle"/>
          <xsl:with-param name="tag" select="'sub'"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="addLayout2List">
          <xsl:with-param name="text" select="$text"/>
          <xsl:with-param name="indStyle" select="$indStyle"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="addLayout2List">
    <xsl:param name="text"/>
    <xsl:param name="indStyle"/>
    <xsl:param name="tag" select="NONE"/>
<!-- Add a layout tag -->
<!-- If the tag has been supplied, we add it to the layout list... -->
    <xsl:if test="not($tag='NONE') and not(normalize-space($tag)='')">
      <xsl:value-of select='$tag'/>,
    </xsl:if>
<!-- if the style index is not the first (1), we continue - note we start from the end... -->
    <xsl:if test="not($indStyle=1)">
      <xsl:apply-templates select="../@*[number($indStyle)-1]">
        <xsl:with-param name="text" select="$text"/>
        <xsl:with-param name="indStyle" select="number($indStyle)-1"/>
      </xsl:apply-templates>
    </xsl:if>
  </xsl:template>
<!--+
      | Images
      +-->
  <xsl:template match="draw:image[@xlink:show]">
    <xsl:variable name="href">
      <xsl:value-of select="@xlink:href"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="starts-with($href, 'http:')">
        <img src="{$href}" alt="{../@draw:name}" heigth="{../@svg:heigth}" width="{../@svg:width}"/>
      </xsl:when>
      <xsl:otherwise>
        <img src="./{$root}{$dirname}openDocumentEmbeddedImages/zip-{$filename}.odt/file-{$href}" alt="{../@draw:name}" heigth="{../@svg:heigth}" width="{../@svg:width}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!--+
      | Links
      +-->
<!-- A little more detailled than in the Lenya file, we add a title and a target attribute if it is supplied... -->
  <xsl:template match="text:a">
<!-- There is a strange behaviour with the links, a .. is used when pointing to a sub-folder instead of '.' -->
<!-- we replace it by '.' in the next variable... -->
    <xsl:variable name="href">
      <xsl:choose>
        <xsl:when test="starts-with(@xlink:href,'..')">
          <xsl:value-of select="substring-after(@xlink:href,'.')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@xlink:href"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable><a href="{$href}">
    <xsl:if test="@office:target-frame-name">
      <xsl:attribute name="target">
        <xsl:value-of select="@office:target-frame-name"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="@office:name">
      <xsl:attribute name="title">
        <xsl:value-of select="@office:name"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates/></a>
  </xsl:template>
</xsl:stylesheet>
