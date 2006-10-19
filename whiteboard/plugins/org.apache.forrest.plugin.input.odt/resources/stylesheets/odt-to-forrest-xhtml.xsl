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
  xmlns:dc="http://purl.org/dc/elements/1.1/">
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

  <xsl:template match="/">
    <xsl:apply-templates select="/odt/content/*"/>
  </xsl:template>
  <xsl:template match="office:document-content">
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <title>
          <xsl:choose>
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
          </xsl:choose>
        </title>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
        <xsl:apply-templates select="office:automatic-styles"/>
      </head>
      <body>
        <xsl:apply-templates select="office:body/office:text"/>
      </body>
    </html>
  </xsl:template>

<!-- FIXME : this is not XHTML -->
<!-- Case of Notes -->
<xsl:template match="text:p[@text:style-name='Forrest_3a__20_Note']">
	<note>
		<xsl:apply-templates/>
		<xsl:if test="count(node())=0"><br /></xsl:if>
	</note>
</xsl:template>

<!-- FIXME : this is not XHTML -->
<!-- Case of Warnings -->
<xsl:template match="text:p[@text:style-name='Forrest_3a__20_Warning']">
	<warning>
		<xsl:apply-templates/>
		<xsl:if test="count(node())=0"><br /></xsl:if>
	</warning>
</xsl:template>

<!-- Case of Fixme  - still a problem to retrieve the author...-->
<!-- FIXME : this is not XHTML -->
<xsl:template match="text:p[@text:style-name='Forrest_3a__20_Fixme']">
	<fixme>
		<xsl:apply-templates/>
		<xsl:if test="count(node())=0"><br /></xsl:if>
	</fixme>
</xsl:template>

<!-- Case of Sources -->
<!-- FIXME : this is not XHTML -->
<xsl:template match="text:p[@text:style-name='Forrest_3a__20_Source']">
	<source>
		<xsl:apply-templates/>
		<xsl:if test="count(node())=0"><br /></xsl:if>
	</source>
</xsl:template>

<!-- Otherwise -->

<!-- FIXME : An idea should be to add a class attribute to <p> in order to be able to keep the original
             layout. Maybe depending on a properties keepLayout=True. -->
<xsl:template match="text:p">
	<p>
		<xsl:apply-templates/>
	</p>
</xsl:template>

<!-- Instead of using styles like in the Lenya file, we try here to recognise the style to use XHTML specific
     tags such as <em>, <strong>, <sup> and <sub>... -->
<xsl:template match="text:span">
	<xsl:variable name="styleName" select="@text:style-name"/>
	<xsl:apply-templates select="//office:document-content/office:automatic-styles/style:style[@style:name=$styleName]/style:text-properties/@*[last()]">
		<xsl:with-param name="text" select="./text()"/>
	</xsl:apply-templates>
</xsl:template>

<xsl:template match="style:text-properties/@*">
	<xsl:param name="text"/>
	<xsl:param name="indStyle" select="count(../@*)"/>
	<xsl:choose>
		<!-- Case of the code style - generally rendered with Courier -->
		<xsl:when test="name()='style:font-name' and starts-with(.,'Courier')">
			<xsl:call-template name="layout-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
				<xsl:with-param name="tag" select="'code'"/>
			</xsl:call-template>
		</xsl:when>
		<!-- Case of the emphasys style - generally rendered with Italic -->
		<xsl:when test="name()='fo:font-style' and .='italic'">
			<xsl:call-template name="layout-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
				<xsl:with-param name="tag" select="'em'"/>
			</xsl:call-template>
		</xsl:when>
		<!-- Case of the strong style -->
		<xsl:when test="name()='fo:font-weight' and .='bold'">
			<xsl:call-template name="layout-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
				<xsl:with-param name="tag" select="'strong'"/>
			</xsl:call-template>
		</xsl:when>
		<!-- Case of the exponent style -->
		<xsl:when test="name()='style:text-position' and starts-with(.,'super')">
			<xsl:call-template name="layout-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
				<xsl:with-param name="tag" select="'sup'"/>
			</xsl:call-template>
		</xsl:when>
		<!-- Case of the subscript style -->
		<xsl:when test="name()='style:text-position' and starts-with(.,'sub')">
			<xsl:call-template name="layout-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
				<xsl:with-param name="tag" select="'sub'"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="text-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
			</xsl:call-template>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="layout-span">
	<xsl:param name="text"/>
	<xsl:param name="indStyle"/>
	<xsl:param name="tag" select="NONE"/>
	<!-- Add a layout tag for a span -->
	<xsl:if test="not($tag='NONE')">
		<xsl:element name="{$tag}">
			<xsl:call-template name="text-span">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="$indStyle"/>
			</xsl:call-template>
		</xsl:element>
	</xsl:if>
</xsl:template>

<xsl:template name="text-span">
	<xsl:param name="text"/>
	<xsl:param name="indStyle" select="last()"/>
	<!-- Add the text of a span or continue to browse the styles -->
	<xsl:choose>
		<xsl:when test="$indStyle=1">
			<xsl:apply-templates select="$text"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="../@*[number($indStyle)-1]">
				<xsl:with-param name="text" select="$text"/>
				<xsl:with-param name="indStyle" select="number($indStyle)-1"/>
			</xsl:apply-templates>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

  <!--+
      | Images
      +-->
<xsl:template match="draw:image[@xlink:show]">
	<xsl:variable name="href"><xsl:value-of select="@xlink:href"/></xsl:variable>
	<xsl:choose>
		<xsl:when test="starts-with($href, 'http:')">
			<img src="{$href}" alt="{@draw:name}"/>
		</xsl:when>
		<xsl:otherwise>
			<img src="/{$dirname}openDocumentEmbeddedImages/zip-{$filename}.odt/file-{$href}" alt="{../@draw:name}"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

  <!--+
      | Links
      +-->
<!-- A little more detailled than in the Lenya file, we add a title and a target attribute if it is supplied... -->
<xsl:template match="text:a">
	<a href="{@xlink:href}">
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
		<xsl:apply-templates/>
	</a>
</xsl:template>

</xsl:stylesheet>