<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  license agreements.  See the NOTICE file distributed with
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
<!--
This stylesheet contains the majority of templates for converting documentv11
to HTML.  It renders XML as HTML in this form:

  <div class="content">
   ...
  </div>

..which site2xhtml.xsl then combines with HTML from the index (book2menu.xsl)
and tabs (tab2menu.xsl) to generate the final HTML.

Section handling
  - <a name/> anchors are added if the id attribute is specified

-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns="http://www.w3.org/2002/06/xhtml2">


  <xsl:template match="/document">
    <html xmlns="http://www.w3.org/2002/06/xhtml2" xml:lang="en"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.w3.org/2002/06/xhtml2/ http://www.w3.org/MarkUp/SCHEMA/xhtml2.xsd"
    >
      <head>
        <title><xsl:value-of select="header/title"/></title>
        <xsl:apply-templates select="header/meta"/>
        <xsl:apply-templates select="header/version"/>
        <xsl:apply-templates select="header/authors"/>
        <xsl:apply-templates select="header/type | header/subtitle"/>
      </head>
      <body>
        <xsl:apply-templates select="header/notice | header/abstract"/>
        <xsl:apply-templates select="body/*"/>   
        <xsl:apply-templates select="footer"/>
      </body>
    </html>
  </xsl:template>

  <!-- Generate a <a name="..."> tag for an @id -->
  <xsl:template match="@id">
    <xsl:if test="normalize-space(.)!=''">
      <a name="{.}"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="section">
    <section>
      <xsl:attribute name="xml:id"><xsl:value-of select="@id"/></xsl:attribute>
      <h><xsl:value-of select="./title"/></h>
      <xsl:apply-templates/>
    </section>
  </xsl:template>

  <xsl:template match="section/title">
    
  </xsl:template>
  
  <xsl:template match="abstract | legal">
    <xsl:element name="p">
      <xsl:attribute name="property"><xsl:value-of select="local-name()"/></xsl:attribute>
        <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="note | warning | fixme | notice">
    <xsl:element name="p">
      <xsl:attribute name="class"><xsl:value-of select="local-name()"/></xsl:attribute>
        <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="meta">
    <xsl:element name="meta">
      <xsl:attribute name="property"><xsl:value-of select="./@name"/></xsl:attribute>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="type | subtitle">
    <xsl:element name="meta">
      <xsl:attribute name="property"><xsl:value-of select="local-name()"/></xsl:attribute>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>
  
  
  <xsl:template match="version">
    <xsl:element name="meta">
      <xsl:attribute name="property">version</xsl:attribute>
      <xsl:if test="./@major">
        <meta>
          <xsl:attribute name="property">major</xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:if test="./@minor">
        <meta>
          <xsl:attribute name="property">minor</xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:if test="./@fix">
        <meta>
          <xsl:attribute name="property">fix</xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:if test="./@tag">
        <meta>
          <xsl:attribute name="property">tag</xsl:attribute>
        </meta>
      </xsl:if>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="authors">
    <xsl:element name="meta">
      <xsl:attribute name="property">authors</xsl:attribute>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="person">
    <xsl:element name="meta">
      <xsl:attribute name="property">person</xsl:attribute>
      <meta><xsl:attribute name="property">email</xsl:attribute></meta>
      <meta><xsl:attribute name="property">name</xsl:attribute></meta>
    </xsl:element>
  </xsl:template>  
  
  <xsl:template match="footer">
    <section>
      <xsl:attribute name="property">footer</xsl:attribute>
       <xsl:apply-templates/>
    </section>
  </xsl:template>
  
    
  <xsl:template match="br">
    <l/>
  </xsl:template>

  <xsl:template match="p[@xml:space='preserve']">
    <xsl:apply-templates select="@id"/>
    <div class="pre">
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="source">
    <blockcode>
      <xsl:apply-templates/>
    </blockcode>
  </xsl:template>

  <xsl:template match="anchor">
    <a id="{@id}">
      <xsl:copy-of select="@id"/>
    </a>
  </xsl:template>
  
  <xsl:template match="link | fork | jump">
    <xsl:element name="a">
      <xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
      <xsl:if test="./@role">
        <xsl:attribute name="role"><xsl:value-of select="@role"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="./@title">
        <xsl:attribute name="title"><xsl:value-of select="@title"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="local-name() = 'fork'">
        <xsl:attribute name="target">_blank</xsl:attribute>
      </xsl:if>
      <xsl:if test="local-name() = 'jump'">
        <xsl:attribute name="target">_top</xsl:attribute>
      </xsl:if>
      <xsl:value-of select="."/>  
    </xsl:element>
  </xsl:template>

  <xsl:template match="icon | figure  | img">
  <!-- FIXME:  Where to match these to?  span doesn't allow 'style' attrib.-->
    <p>
      <xsl:attribute name="class"><xsl:value-of select="local-name()"/></xsl:attribute>
      <xsl:copy-of select="@src | @alt | @id"/>
      
      <xsl:variable name="height-val">
        <xsl:if test="./@height">height:<xsl:value-of select="./@height"/></xsl:if>
      </xsl:variable>
      <xsl:variable name="width-val">
        <xsl:if test="./@width">width:<xsl:value-of select="./@width"/></xsl:if>
      </xsl:variable>
      
      <xsl:if test="./@height or ./@width">
        <xsl:attribute name="style"><xsl:value-of select="concat($height-val,';',$width-val)"/></xsl:attribute>
      </xsl:if>
    </p>
  </xsl:template>

  
  <xsl:template match="table">
    <xsl:apply-templates select="@id"/>
    <table class="ForrestTable">
      <!--FIXME: Should these be included as internal style snippets?-->
      <!--@cellspacing | @cellpadding |-->
      <xsl:copy-of select="@border | @class | @bgcolor |@id"/>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="acronym">
    <xsl:element name="abbr">
      <xsl:attribute name="title">
        <xsl:value-of select="normalize-space(.)"/>
      </xsl:attribute>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>


  <xsl:template match="node()|@*" mode="toc"/>

  <!-- End of "toc" mode templates -->

  <xsl:template match="node()|@*" priority="-1">
    <!-- id processing will create its own a-element so processing has to 
         happen outside the copied element 
    -->
    <xsl:apply-templates select="@id"/>
    <xsl:copy>
      <xsl:apply-templates select="@*[name(.) != 'id']"/>
      <xsl:copy-of select="@id"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>

