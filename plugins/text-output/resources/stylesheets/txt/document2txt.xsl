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
                
  <xsl:param name="dynamic-page" select="'false'"/>  
  <xsl:param name="notoc"/>
  <xsl:param name="path"/>

  <xsl:template match="/">
    <xsl:apply-templates select="//document"/>
  </xsl:template>

  <xsl:template match="document">
  </xsl:template>

  <xsl:template name="newLine">
    <xsl:text>

</xsl:text>
  </xsl:template>
  
  <xsl:template match="document">
    <xsl:apply-templates select="header/title"/>
    <xsl:call-template name="newLine"/>
    Table of Contents
    =================
    <xsl:apply-templates mode="toc"/>
    <xsl:value-of select="normalize-space(header/title)"/>
    <xsl:call-template name="newLine"/>
    <!-- FIXME: underline and overline document title with '=' -->

    <xsl:if test="normalize-space(header/subtitle)!=''">
      <xsl:value-of select="normalize-space(header/subtitle)"/>
      <xsl:call-template name="newLine"/>
      <!-- FIXME: underline and overline document subtitle with '-' -->
    </xsl:if>

    <xsl:apply-templates select="header/type"/>
    <xsl:apply-templates select="header/notice"/>
    <xsl:apply-templates select="header/abstract"/>
    <xsl:apply-templates select="body"/>

    <xsl:apply-templates select="header/authors"/>
    <xsl:if test="header/authors and header/version">
      <xsl:text>; </xsl:text>
    </xsl:if>
    <xsl:apply-templates select="header/version"/>
      
    <!-- FIXME put in line separator of '-'chars -->
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="body">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="p[@xml:space='preserve']">
    <xsl:value-of select="."/>
  </xsl:template>
  
  <xsl:template match="p">
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="li">
    <xsl:call-template name="newLine"/>
    - <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="dt">
    <xsl:call-template name="newLine"/>
    _<xsl:apply-templates/>_
  </xsl:template>
  
  <xsl:template match="dd">
    <xsl:call-template name="newLine"/>
    <xsl:text>  </xsl:text><xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="section">
    <!-- count the number of section in the ancestor-or-self axis to compute
         the title element name later on -->
    <xsl:variable name="sectiondepth" select="count(ancestor-or-self::section)"/>
    
    <xsl:call-template name="newLine"/>
    <xsl:value-of select="normalize-space(title)"/>
    <!-- generate a title element, level 1 -> h3, level 2 -> h4 and so on... -->
    <xsl:choose>
      <xsl:when test="$sectiondepth=1">
        <!-- FIXME: underline level 1 title with '=' -->
        <xsl:call-template name="newLine"/>
      </xsl:when>
      <xsl:when test="$sectiondepth=2">
        <!-- FIXME: underline level 2 title with '-' -->
        <xsl:call-template name="newLine"/>
      </xsl:when>
      <xsl:when test="$sectiondepth=3">
        <!-- FIXME: underline level 3 title with '.' -->
        <xsl:call-template name="newLine"/>
      </xsl:when>
    </xsl:choose>    

    <!-- Indent FAQ entry text 5 characters -->
    <xsl:variable name="indent">
      <xsl:choose>
        <xsl:when test="$notoc='true' and $sectiondepth = 3">
          <xsl:text>5</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- FIXME display $indent spaces -->
    <xsl:apply-templates select="*[not(self::title)]"/>
  </xsl:template>

  <xsl:template match="note | warning | fixme">
    <xsl:call-template name="newLine"/>
    <!-- FIXME draw overline and underline to title section -->
    <xsl:choose>
      <xsl:when test="@label"><xsl:value-of select="@label"/></xsl:when>
      <xsl:when test="local-name() = 'note'">Note</xsl:when>
      <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
      <xsl:otherwise>Fixme (<xsl:value-of select="@author"/>)</xsl:otherwise>
    </xsl:choose>
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
    <!-- FIXME underline note/warning/fixme box -->
  </xsl:template>

  <xsl:template match="notice">
    <xsl:text>Notice: </xsl:text>
    <xsl:apply-templates/>
    <xsl:call-template name="newLine"/>
  </xsl:template>

  <xsl:template match="link">
    <xsl:text> </xsl:text><xsl:apply-templates/> [Link to <xsl:value-of select="@href"/>] </xsl:template>

  <xsl:template match="jump">
    <xsl:text> </xsl:text><xsl:apply-templates/> [Link to <xsl:value-of select="@href"/>] </xsl:template>

  <xsl:template match="fork">
    <xsl:text> </xsl:text><xsl:apply-templates/> [Link to <xsl:value-of select="@href"/>] </xsl:template>

  <xsl:template match="icon">
    <xsl:text> </xsl:text><xsl:apply-templates/> [Icon: <xsl:value-of select="@alt"/>] </xsl:template>

  <xsl:template match="img">
    <xsl:text> </xsl:text><xsl:apply-templates/> [Image: <xsl:value-of select="@alt"/>] </xsl:template>

  <xsl:template match="figure">
    <xsl:text> </xsl:text><xsl:apply-templates/> [Figure: <xsl:value-of select="@alt"/>] </xsl:template>
    
  <xsl:template match="table">
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
  </xsl:template>
    
  <xsl:template match="tr">
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
  </xsl:template>
      
  <xsl:template match="td">
    <xsl:apply-templates/>, 
  </xsl:template>
  
  <xsl:template match="acronym/@title">
    <xsl:attribute name="title">
      <xsl:value-of select="normalize-space(.)"/>
    </xsl:attribute>
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

  <xsl:template match="version">
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
    <xsl:text>Type: </xsl:text><xsl:value-of select="normalize-space(.)"/>
  </xsl:template>

  <xsl:template match="abstract">
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="email">
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

  <xsl:template match="document" mode="toc">
    <xsl:apply-templates mode="toc"/>
  </xsl:template>

  <xsl:template match="body" mode="toc">
    <xsl:apply-templates select="section" mode="toc">
      <xsl:with-param name="level" select="1"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="section" mode="toc">
    <xsl:param name="level"/>
    <!-- FIXME indent line by level * 2 spaces --> - <xsl:value-of select="title"/>
    <xsl:call-template name="newLine"/>
    <xsl:apply-templates mode="toc">
      <xsl:with-param name="level" select="$level+1"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="text()" mode="toc">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  
  <xsl:template match="node()|@*" mode="toc"/>
    
  
  <!-- End of "toc" mode templates -->
    
  <xsl:template match="text()">
    <xsl:value-of select="normalize-space(.)"/>
  </xsl:template>
  
</xsl:stylesheet>

