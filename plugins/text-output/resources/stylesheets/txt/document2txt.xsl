<?xml version="1.0"?>
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
<!--
This stylesheet contains the majority of templates for converting documentv13
to Plain Text.  

No navigation is provided and no rendering of graphics is attempted.

-->
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../common/text-templates.xsl"/>
                
  <xsl:param name="dynamic-page" select="'false'"/>  
  <xsl:param name="notoc"/>
  <xsl:param name="path"/>

  <xsl:variable name="indent-per-level">2</xsl:variable>
  <xsl:variable name="document-width">76</xsl:variable>

  <xsl:template match="/">
    <xsl:apply-templates select="//document"/>
  </xsl:template>

  <xsl:template match="document">
    <xsl:apply-templates select="header"/>

    <xsl:apply-templates select="body" mode="toc"/>

    <xsl:apply-templates select="body"/>
  </xsl:template>

  <!-- Handle the document header bits -->
  <xsl:template match="header">
    <xsl:call-template name="justify-text">
      <xsl:with-param name="text" select="title"/>
      <xsl:with-param name="width" select="$document-width"/>
      <xsl:with-param name="align" select="'center'"/>
    </xsl:call-template>
    <xsl:call-template name="cr"/>

    <xsl:if test="normalize-space(subtitle)!=''">
      <xsl:call-template name="justify-text">
        <xsl:with-param name="text" select="subtitle"/>
        <xsl:with-param name="width" select="$document-width"/>
        <xsl:with-param name="align" select="'center'"/>
      </xsl:call-template>
      <xsl:call-template name="cr"/>
    </xsl:if>
    <xsl:call-template name="cr"/>

    <xsl:apply-templates select="type"/>
    <xsl:apply-templates select="notice"/>
    <xsl:call-template name="cr"/>
    <xsl:apply-templates select="abstract"/>
    <xsl:call-template name="cr"/>

    <xsl:apply-templates select="authors"/>
    <xsl:if test="authors and version">
      <xsl:text>; </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="version"/>
    
  </xsl:template>

  <xsl:template match="header/authors">
    <xsl:for-each select="person">
      <xsl:choose>
        <xsl:when test="position()=1">by&#160;</xsl:when>
        <xsl:otherwise>,&#160;</xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="@name"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="notice">
    <xsl:param name="level" select="'2'"/>

    <xsl:call-template name="justify-text">
      <xsl:with-param name="text" select="'NOTICE'"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>
    <xsl:call-template name="cr"/>

    <xsl:variable name="para">
      <xsl:apply-templates>
        <xsl:with-param name="level" select="$level"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit">
      <xsl:with-param name="text" select="$para"/>
      <xsl:with-param name="indent" select="$level * $indent-per-level"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="abstract">
    <xsl:param name="level" select="'2'"/>

    <xsl:call-template name="justify-text">
      <xsl:with-param name="text" select="'ABSTRACT'"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>
    <xsl:call-template name="cr"/>

    <xsl:variable name="para">
      <xsl:apply-templates>
        <xsl:with-param name="level" select="$level"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit">
      <xsl:with-param name="text" select="$para"/>
      <xsl:with-param name="indent" select="$level * $indent-per-level"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>
  </xsl:template>

  <!-- Handle the body bits -->
  <xsl:template match="body">
    <xsl:apply-templates>
      <xsl:with-param name="level" select="'0'"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="p[@xml:space='preserve']|source">
    <xsl:param name="level" select="'1'"/>
    <xsl:value-of select="."/>
    <xsl:call-template name="cr"/>
  </xsl:template>
  
  <xsl:template match="p">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="cr"/>

    <xsl:variable name="para">
      <xsl:apply-templates>
        <xsl:with-param name="level" select="$level"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit">
      <xsl:with-param name="text" select="$para"/>
      <xsl:with-param name="indent" select="$level * $indent-per-level"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>

  </xsl:template>
  
  <xsl:template match="ol|ul">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="cr"/>
    <xsl:apply-templates select="li">
      <xsl:with-param name="level" select="$level"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="ol/li">
    <xsl:param name="level" select="'1'"/>

    <xsl:variable name="item">
      <xsl:value-of select="position()"/><xsl:text>. </xsl:text>
      <xsl:apply-templates>
        <xsl:with-param name="level" select="'0'"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit">
      <xsl:with-param name="text" select="$item"/>
      <xsl:with-param name="indent" select="$level * $indent-per-level"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="ul/li">
    <xsl:param name="level" select="'1'"/>

    <xsl:variable name="item">
      <xsl:text>* </xsl:text>
      <xsl:apply-templates>
        <xsl:with-param name="level" select="'0'"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit">
      <xsl:with-param name="text" select="$item"/>
      <xsl:with-param name="indent" select="$level * $indent-per-level"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>

  </xsl:template>
  
  <xsl:template match="dt">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="newLine"/>

    <xsl:call-template name="lineOf">
      <xsl:with-param name="size"
                  select="$level * $indent-per-level"/>
    </xsl:call-template>
    <xsl:text>: </xsl:text>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="$level"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="dd">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="newLine"/>

    <xsl:call-template name="lineOf">
      <xsl:with-param name="size"
                  select="$level * $indent-per-level"/>
    </xsl:call-template>
    <xsl:text>  </xsl:text>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="$level"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template match="section">
    <xsl:param name="level" select="'0'"/>

    <xsl:variable name="ttl">
      <xsl:value-of select="normalize-space(title)"/>
    </xsl:variable>
    
    <xsl:call-template name="cr"/>
    <xsl:call-template name="lineOf">
      <xsl:with-param name="size" select="$level * $indent-per-level"/>
    </xsl:call-template>
    <xsl:value-of select="$ttl"/>
    <xsl:call-template name="cr"/>

    <!-- generate a title element, level 1 -> h3, level 2 -> h4 and so on... -->
    <xsl:call-template name="lineOf">
      <xsl:with-param name="size" select="$level * $indent-per-level"/>
    </xsl:call-template>
    <xsl:choose>
      <xsl:when test="$level=0">
        <xsl:call-template name="lineOf">
          <xsl:with-param name="chars" select="'='"/>
          <xsl:with-param name="size" select="string-length($ttl)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$level=1">
        <xsl:call-template name="lineOf">
          <xsl:with-param name="chars" select="'-'"/>
          <xsl:with-param name="size" select="string-length($ttl)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$level=2">
        <xsl:call-template name="lineOf">
          <xsl:with-param name="chars" select="'.-'"/>
          <xsl:with-param name="size" select="string-length($ttl)"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$level=3">
        <xsl:call-template name="lineOf">
          <xsl:with-param name="chars" select="'.'"/>
          <xsl:with-param name="size" select="string-length($ttl)"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>    
    <xsl:call-template name="cr"/>

    <!-- FIXME display $indent spaces -->
    <xsl:apply-templates select="*[not(self::title)]">
      <xsl:with-param name="level" select="$level + 1"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="note | warning | fixme">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="cr"/>

    <xsl:call-template name="lineOf">
      <xsl:with-param name="size" select="$level * $indent-per-level"/>
    </xsl:call-template>

    <xsl:variable name="ttl">
      <xsl:text>** </xsl:text>
      <xsl:choose>
        <xsl:when test="@label">
          <xsl:value-of select="@label"/>
        </xsl:when>
        <xsl:when test="local-name() = 'note'">Note</xsl:when>
        <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
        <xsl:otherwise>Fixme (<xsl:value-of select="@author"/>)</xsl:otherwise>
      </xsl:choose>
      <xsl:text> **</xsl:text>
    </xsl:variable>

    <xsl:call-template name="justify-text">
      <xsl:with-param name="text" select="$ttl"/>
      <xsl:with-param name="width"
            select="$document-width - ($level * $indent-per-level)"/>
    </xsl:call-template>
    <xsl:call-template name="cr"/>

    <xsl:variable name="para">
      <xsl:apply-templates>
        <xsl:with-param name="level" select="$level"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:call-template name="emit">
      <xsl:with-param name="text" select="$para"/>
      <xsl:with-param name="indent" select="$level * $indent-per-level"/>
      <xsl:with-param name="width" select="$document-width"/>
    </xsl:call-template>

    <xsl:call-template name="lineOf">
      <xsl:with-param name="size" select="$level * $indent-per-level"/>
    </xsl:call-template>

    <xsl:call-template name="lineOf">
      <xsl:with-param name="size"
            select="$document-width - ($level * $indent-per-level)"/>
      <xsl:with-param name="chars" select="'-'"/>
    </xsl:call-template>
    <xsl:call-template name="cr"/>

  </xsl:template>

  <xsl:template match="link|jump|fork|a">
    <xsl:param name="level" select="'1'"/>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="$level + 1"/>
    </xsl:apply-templates>

    <xsl:text>[Link to: </xsl:text>
    <xsl:value-of select="@href"/>
    <xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template match="icon">
    <xsl:param name="level" select="'1'"/>
    <xsl:text> </xsl:text>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="$level + 1"/>
    </xsl:apply-templates>

    <xsl:value-of select="concat('[Icon: ', @alt, '] ')"/>
  </xsl:template>

  <xsl:template match="img">
    <xsl:param name="level" select="'1'"/>
    <xsl:text> </xsl:text>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="$level + 1"/>
    </xsl:apply-templates>

    <xsl:text>[Image: </xsl:text>
    <xsl:value-of select="@alt"/>
    <xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template match="figure">
    <xsl:param name="level" select="'1'"/>
    <xsl:text> </xsl:text>
    <xsl:apply-templates>
      <xsl:with-param name="level" select="$level + 1"/>
    </xsl:apply-templates>

    <xsl:text>[Figure: </xsl:text>
    <xsl:value-of select="@alt"/>
    <xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template match="table">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
  </xsl:template>
    
  <xsl:template match="tr">
    <xsl:param name="level" select="'1'"/>
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
  </xsl:template>
      
  <xsl:template match="td">
    <xsl:param name="level" select="'1'"/>
    <xsl:apply-templates/>, 
  </xsl:template>
  
  <xsl:template match="acronym/@title">
    <xsl:param name="level" select="'1'"/>
    <xsl:attribute name="title">
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="version">
    <xsl:param name="level" select="'1'"/>
    <span class="version">
      <xsl:apply-templates select="@major"/>
      <xsl:apply-templates select="@minor"/>
      <xsl:apply-templates select="@fix"/>
      <xsl:apply-templates select="@tag"/>
      <xsl:choose>
        <xsl:when test="starts-with(., '$Revision: ')">
          version <xsl:value-of select="substring(., 12, string-length(.) -11-2)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>
  
  <xsl:template match="@major">
     v<xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="@minor">
     <xsl:value-of select="concat('.',.)"/>
  </xsl:template>
  
  <xsl:template match="@fix">
     <xsl:value-of select="concat('.',.)"/>
  </xsl:template>
  
  <xsl:template match="@tag">
     <xsl:value-of select="concat('-',.)"/>
  </xsl:template>

  <xsl:template match="type">
    <xsl:param name="level" select="'1'"/>
    <xsl:text>Type: </xsl:text><xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <xsl:template name="email">
    <xsl:param name="level" select="'1'"/>
    <a>
      <xsl:attribute name="href">
        <xsl:value-of select="concat('mailto:',@email)"/>
      </xsl:attribute>
       <xsl:value-of select="@name"/>
    </a>
  </xsl:template>
                                                                                
  <!--  Templates for "toc" mode.  This will generate a complete 
        Table of Contents for the document.  This will then be used
        by the site2xhtml to generate a Menu ToC and a Page ToC -->

  <xsl:template match="body" mode="toc">
    <xsl:text>Table Of Contents&#xa;=================</xsl:text>
    <xsl:call-template name="cr"/>
    <xsl:apply-templates select="section" mode="toc">
      <xsl:with-param name="level" select="1"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="section" mode="toc">
    <xsl:param name="level" select="'1'"/>

    <xsl:call-template name="lineOf">
      <xsl:with-param name="chars" select="' '"/>
      <xsl:with-param name="size" select="($level - 1) * 2"/>
    </xsl:call-template>
    <xsl:value-of select="title"/>
    <xsl:call-template name="cr"/>

    <xsl:apply-templates mode="toc">
      <xsl:with-param name="level" select="$level+1"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="text()" mode="toc">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  
  <xsl:template match="node()|@*" mode="toc"/>
    
  
  <!-- End of "toc" mode templates -->

</xsl:stylesheet>

