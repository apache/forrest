<?xml version="1.0"?>
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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:p="http://outerx.org/daisy/1.0#publisher"
    xmlns:ns="http://outerx.org/daisy/1.0"
    xmlns:nav="http://outerx.org/daisy/1.0#navigationspec"
    version="1.0">
    
  
  <!-- The path to the current document, used to calculate the path
       to the site root when working out paths in links -->
  <xsl:param name="documentPath"/>
  
  <!-- The pathPrefix is added to the start of all resolved Daisy links 
       It must include a trailing slash if it is non-empty -->
  <xsl:param name="pathPrefix">/</xsl:param>
    
  <xsl:template match="daisyDocument">
    <xsl:variable name="rootElementName"><xsl:value-of select="name(*)"/></xsl:variable>
    <html>
      <head>
        <xsl:choose>
          <xsl:when test="//ns:document">
            <title><xsl:value-of select="//ns:document/@name"/></title>
            <xsl:apply-templates select="//p:preparedDocument/ns:document/ns:fields/ns:field"/>
          </xsl:when>
          <xsl:when test="$rootElementName = 'html'">
            <title><xsl:value-of select="html/head/title"/></title>
          </xsl:when>
          <xsl:otherwise>
            <title>Daisy Error</title>
          </xsl:otherwise>
        </xsl:choose>
      </head>
      <body>
        <xsl:choose>
          <xsl:when test="$rootElementName = 'p:publisherResponse'">
            <xsl:apply-templates select="p:publisherResponse/p:document/p:preparedDocuments/p:preparedDocument[@id='1']"/>
          </xsl:when>
          <xsl:when test="$rootElementName = 'html'">
            <xsl:apply-templates select="//body"/>
          </xsl:when>
          <xsl:otherwise>
            <h1>Daisy Error</h1>
            <p>Unable to transform the daisy document with root element of <xsl:value-of select="$rootElementName"/>.</p>
          </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="ns:field">
    <xsl:for-each select="ns:string">
      <meta>
        <xsl:attribute name="name"><xsl:value-of select="../@name"/></xsl:attribute>
        <xsl:attribute name="content"><xsl:value-of select="."/></xsl:attribute>
      </meta>
    </xsl:for-each>
  </xsl:template>
  
  <xsl:template match="p:preparedDocument">
    <xsl:comment>Prepared Document: ID = <xsl:value-of select="@id"/></xsl:comment>
    <xsl:apply-templates select="ns:document/ns:parts/ns:part"/>
  </xsl:template>
  
  <xsl:template match="ns:part">
    <xsl:comment>Part: Name <xsl:value-of select="@name"/></xsl:comment>
    <xsl:if test="@name!='SimpleDocumentContent'">
      <h1><xsl:value-of select="@name"/></h1>
    </xsl:if>
    <xsl:apply-templates select="html/body"/>
  </xsl:template>
  
  <xsl:template match="ns:searchResult">
    <table>
        <xsl:apply-templates/>
    </table>
  </xsl:template>
  
  <xsl:template match="ns:titles">
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>
  
  <xsl:template match="ns:title">
    <td>
      <xsl:apply-templates/>
    </td>
  </xsl:template>
  
  <xsl:template match="ns:rows">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="ns:row">
    <tr>
      <xsl:apply-templates/>
    </tr>
  </xsl:template>
  
  <xsl:template match="ns:value">
    <td>
      <xsl:choose>
        <xsl:when test="position() = 1">
          <a>
            <xsl:attribute name="href"><xsl:value-of select="../@documentId"/>.daisy.html</xsl:attribute>
            <xsl:apply-templates/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </xsl:template>
  
  <xsl:template match="body">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="p:daisyPreparedInclude">
    <xsl:variable name="id" select="@id"/>
    <h1><xsl:value-of select="//p:preparedDocument[@id=$id]/ns:document/@name"/></h1>
    <xsl:apply-templates select="//p:preparedDocument[@id=$id]"/>
  </xsl:template>
  
  <xsl:template match="p[@class='note']">
    <div class="frame note">
      <div class="label">Note</div>
      <div class="content"><xsl:apply-templates/></div>
    </div>
  </xsl:template>
  
  <xsl:template match="p[@class='fixme']">
    <div class="frame fixme">
      <div class="label">Fixme</div>
      <div class="content"><xsl:apply-templates/></div>
    </div>
  </xsl:template>
  
  <xsl:template match="p[@class='warning']">
    <div class="frame warning">
      <div class="label">Warning</div>
      <div class="content"><xsl:apply-templates/></div>
    </div>
  </xsl:template>
  
  <xsl:template match="a">
    <xsl:variable name="pathToRoot">
      <xsl:call-template name="dotdots">
        <xsl:with-param name="path"><xsl:value-of select="$documentPath"/></xsl:with-param>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:choose>
      <xsl:when test="starts-with(@href, 'daisy:')">
        <xsl:variable name="docId"><xsl:value-of select="substring-after(@href, 'daisy:')"/></xsl:variable>
        <xsl:variable name="path">
          <xsl:value-of select="$pathPrefix"/>
          <xsl:for-each select="//daisyDocument/descendant::doc[@id=$docId][1]/ancestor::group|//daisyDocument/descendant::doc[@id=$docId][1]/ancestor::doc[@nodeId]"><xsl:value-of select="@href"/></xsl:for-each>
        </xsl:variable>
        <xsl:variable name="url"><xsl:value-of select="$pathToRoot"/><xsl:value-of select="$path"/><xsl:value-of select="//doc[@id=$docId]/@href"/></xsl:variable>
        <a>
          <xsl:choose>
            <xsl:when test="contains($url, '#')">
              <xsl:variable name="pageURL"><xsl:value-of select="substring-before($url, '#')"/></xsl:variable>
              <xsl:variable name="anchor"><xsl:value-of select="substring-after(@href, '#')"/></xsl:variable>
              <xsl:attribute name="href">FIXME: URLS with anchors not working at present - <xsl:value-of select="$pageURL"/>.html#<xsl:value-of select="$anchor"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href"><xsl:value-of select="$url"/></xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:attribute name="description"><xsl:value-of select="@daisyDocumentName"/></xsl:attribute>
          <xsl:apply-templates/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <a>
          <xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
          <xsl:attribute name="description"><xsl:value-of select="@daisyDocumentName"/></xsl:attribute>
          <xsl:apply-templates/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="img">
    <xsl:choose>
      <xsl:when test="starts-with(@src, 'daisy:')">
        <img>
          <xsl:apply-templates select="@*"/>
          <xsl:attribute name="src"><xsl:value-of select="substring-after(@src, 'daisy:')"/>.daisy.img</xsl:attribute>
          <xsl:apply-templates/>
        </img>
      </xsl:when>
      <xsl:otherwise>
        <img>
          <xsl:apply-templates select="@*"/>
          <xsl:attribute name="src"><xsl:value-of select="@src"/></xsl:attribute>
          <xsl:apply-templates/>
        </img>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="@ns:*|ns:*"/>
  <xsl:template match="@p:*|p:*"/>
  
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- FIXME: this should come from include of dotdots.xsl in forest core -->
  <xsl:template name="dotdots">
    <xsl:param name="path"/>
    <xsl:variable name="dirs" select="normalize-space(translate(concat($path, 'x'), ' /\', '_  '))"/>
    <!-- The above does the following:
       o Adds a trailing character to the path. This prevents us having to deal
         with the special case of ending with '/'
       o Translates all directory separators to ' ', and normalize spaces,
		 cunningly eliminating duplicate '//'s. We also translate any real
		 spaces into _ to preserve them.
    -->
    <xsl:variable name="remainder" select="substring-after($dirs, ' ')"/>
    <xsl:if test="$remainder">
      <xsl:text>../</xsl:text>
      <xsl:call-template name="dotdots">
        <xsl:with-param name="path" select="translate($remainder, ' ', '/')"/>
		<!-- Translate back to /'s because that's what the template expects. -->
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
