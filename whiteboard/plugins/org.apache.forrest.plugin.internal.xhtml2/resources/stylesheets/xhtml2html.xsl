<?xml version="1.0" encoding="utf-8"?>
<!--
  - Copyright (c) 2004, Jonathan McGee
  - All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions
  - are met:
  -
  - * Redistributions of source code must retain the above copyright
  -   notice, this list of conditions and the following disclaimer. 
  - * Redistributions in binary form must reproduce the above copyright
  -   notice, this list of conditions and the following disclaimer in
  -   the documentation and/or other materials provided with the
  -   distribution. 
  - * Neither the name of the author nor the names of his contributors
  -   may be used to endorse or promote products derived from this
  -   software without specific prior written permission. 
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  - "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  - LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
  - FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
  - COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  - INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  - (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  - SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
  - HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
  - STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  - ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
  - ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  -->
<xsl:stylesheet version="1.0" exclude-result-prefixes="xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xhtml="http://www.w3.org/2002/06/xhtml2">

  <!-- If imported into another stylesheet, allow the first level heading
    -  depth to be configured -->
  <xsl:param name="xhtml-heading" select="1"/>

  <!-- Use XHTML 1.0 as the output schema -->
  <xsl:output method="html" version="1.0" encoding="utf-8"
    omit-xml-declaration="yes" media-type="text/html"
    doctype-public="-//W3C//DTD HTML 4.01//EN"
    doctype-system="http://www.w3.org/TR/html4/strict.dtd"/>

  <!-- Copy elements by default -->
  <xsl:template match="comment()">
   <xsl:comment><xsl:value-of select="."/></xsl:comment>
  </xsl:template>

  <!-- Do not process elements and attributes by default -->
  <xsl:template match="xhtml:*"/>
  <xsl:template match="xhtml:*/@*" priority="0"/>
  <xsl:template match="xhtml:*/@*" mode="insdel" priority="0"/>
  <xsl:template match="xhtml:*/@*" mode="image" priority="0"/>
  <xsl:template match="xhtml:*/@*" mode="quote" priority="0"/>
  <xsl:template match="xhtml:*/@*" mode="link" priority="0"/>

  <!--
    - XHTML Attribute Collections
    -->

  <!-- Core Attribute Collection -->
  <xsl:template match="@class">
   <xsl:param name="class"/>
   <xsl:attribute name="class">
    <xsl:if test="$class">
     <xsl:value-of select="$class"/>
     <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:value-of select="."/>
   </xsl:attribute>
  </xsl:template>
  <xsl:template match="@id|@title">
   <xsl:attribute name="{local-name()}">
    <xsl:value-of select="."/>
   </xsl:attribute>
  </xsl:template>

  <!-- I18N Attribute Collection -->
  <xsl:template match="@xml:lang">
   <xsl:attribute name="lang"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!-- Bi-directional Text Collection -->
  <xsl:template match="@dir">
   <!-- TODO: Need to handle lro and rlo.  Not in HTML4 -->
   <xsl:attribute name="dir"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!-- Edit Collection -->
  <xsl:template match="@edit" mode="insdel"/>
  <xsl:template match="@datetime" mode="insdel">
   <xsl:attribute name="datetime"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!-- Hypertext Attribute Collection -->
  <xsl:template match="@href|@rel|@rev|@accesskey|@target" mode="link">
   <xsl:attribute name="{local-name()}">
    <xsl:value-of select="."/>
   </xsl:attribute>
  </xsl:template>
  <xsl:template match="@navindex" mode="link">
   <xsl:attribute name="tabindex"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>
  <!-- Make sure only one tag gets the cite attribute -->
  <xsl:template match="@cite" mode="insdel">
   <xsl:attribute name="cite"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>
  <xsl:template match="@cite" mode="quote">
   <xsl:if test="count(@edit)=0">
    <xsl:attribute name="cite"><xsl:value-of select="."/></xsl:attribute>
   </xsl:if>
  </xsl:template>

  <!-- Embedding Attribute Collection -->
  <xsl:template match="@src" mode="image">
   <xsl:attribute name="src"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>
  <xsl:template match="xhtml:link/@type" mode="link">
   <xsl:attribute name="type"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!-- Image Map Attribute Collection -->
  <xsl:template match="@usemap|@ismap" mode="image">
   <xsl:attribute name="{local-name()}">
    <xsl:value-of select="."/>
   </xsl:attribute>
  </xsl:template>

  <!-- Style Attribute Collection -->
  <xsl:template match="@style">
   <xsl:attribute name="style"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!--
    - Block rendering templates
    -->
  <!-- If the edit attribute has been used, add a ins or del as appropriate -->
  <xsl:template name="block">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- No edit attribute, just call next level -->
    <xsl:when test="count(@edit)=0">
     <xsl:call-template name="block-element">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Edit attribute set to "deleted", create a del element -->
    <xsl:when test="@edit='deleted'">
     <del>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="block-element">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </del>
    </xsl:when>
    <!-- Edit attribute set to "inserted", "changed", "moved", create an ins
      -  element -->
    <xsl:otherwise>
     <ins>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="block-element">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </ins>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- Create the element before any automatic inline children -->
  <xsl:template name="block-element">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- The hr element is empty -->
    <xsl:when test="$element='hr'">
     <hr>
      <xsl:apply-templates select="@*">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
     </hr>
    </xsl:when>
    <!-- Create element -->
    <xsl:otherwise>
     <xsl:element name="{$element}">
      <xsl:if test="$class">
       <xsl:attribute name="class">
        <xsl:value-of select="$class"/>
       </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@*">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="block-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </xsl:element>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the cite attribute appears without edit, create a quote element -->
  <xsl:template name="block-quote">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no cite attribute or edit was defined, continue to next level -->
    <xsl:when test="(count(@cite)=0) or (count(@edit)!=0)
      or ($element='blockquote')">
     <xsl:call-template name="block-link">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Create the quote element -->
    <xsl:otherwise>
     <q>
      <xsl:apply-templates select="@*" mode="quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="block-link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </q>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the href attribute appears, make a hyperlink -->
  <xsl:template name="block-link">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no href attribute appears, continue to next level -->
    <xsl:when test="count(@href)=0">
     <xsl:call-template name="block-image">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- href has been used, so create an anchor -->
    <xsl:otherwise>
     <a>
      <xsl:apply-templates select="@*" mode="link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="block-image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </a>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the src attribute appears, make an image -->
  <xsl:template name="block-image">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no src attribute appears, continue to next level -->
    <xsl:when test="count(@src)=0">
     <xsl:apply-templates/>
    </xsl:when>
    <!-- src has been used, so replace with an image -->
    <xsl:otherwise>
     <img alt="{.}">
      <xsl:apply-templates select="@*" mode="image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
     </img>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!--
    - Inline rendering templates
    -->
  <!-- If the edit attribute has been used, add a ins or del as appropriate -->
  <xsl:template name="inline">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- No edit attribute, just call next level -->
    <xsl:when test="count(@edit)=0">
     <xsl:call-template name="inline-quote">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Edit attribute set to "deleted", create a del element -->
    <xsl:when test="@edit='deleted'">
     <del>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="inline-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </del>
    </xsl:when>
    <!-- Edit attribute set to "inserted", "changed", "moved", create an ins
      -  element -->
    <xsl:otherwise>
     <ins>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="inline-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </ins>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the cite attribute appears without edit, create a quote element -->
  <xsl:template name="inline-quote">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no cite attribute or edit was defined, continue to next level -->
    <xsl:when test="(count(@cite)=0) or (count(@edit)!=0)">
     <xsl:call-template name="inline-link">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Create the quote element -->
    <xsl:otherwise>
     <q>
      <xsl:apply-templates select="@*" mode="quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="inline-link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </q>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the href attribute appears, make a hyperlink -->
  <xsl:template name="inline-link">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no href attribute appears, continue to next level -->
    <xsl:when test="count(@href)=0">
     <xsl:call-template name="inline-image">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- href has been used, so create an anchor -->
    <xsl:otherwise>
     <a>
      <xsl:apply-templates select="@*" mode="link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="inline-image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </a>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the src attribute appears, make an image -->
  <xsl:template name="inline-image">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no src attribute appears, continue to next level -->
    <xsl:when test="count(@src)=0">
     <xsl:call-template name="inline-element">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- src has been used, so replace with an image -->
    <xsl:otherwise>
     <img alt="{.}">
      <xsl:apply-templates select="@*" mode="image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
     </img>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- Finally, create the element (assuming not quote) -->
  <xsl:template name="inline-element">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If element is 'q' or 'a', it has already been made -->
    <xsl:when test="($element='q') or ($element='a')">
     <xsl:apply-templates select="@*">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:apply-templates>
     <xsl:apply-templates/>
    </xsl:when>
    <!-- If element is 'br', make a span and put br at end -->
    <xsl:when test="$element='br'">
     <span class="line">
      <xsl:apply-templates select="@*">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:apply-templates/><br/>
     </span>
    </xsl:when>
    <!-- Create element -->
    <xsl:otherwise>
     <xsl:element name="{$element}">
      <xsl:apply-templates select="@*">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:apply-templates/>
     </xsl:element>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!--
    - List rendering templates
    -->
  <!-- If the edit attribute has been used, add a ins or del as appropriate -->
  <xsl:template name="list">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- No edit attribute, just call next level -->
    <xsl:when test="count(@edit)=0">
     <xsl:call-template name="list-quote">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Edit attribute set to "deleted", create a del element -->
    <xsl:when test="@edit='deleted'">
     <del>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="list-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </del>
    </xsl:when>
    <!-- Edit attribute set to "inserted", "changed", "moved", create an ins
      -  element -->
    <xsl:otherwise>
     <ins>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="list-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </ins>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the cite attribute appears without edit, create a quote element -->
  <xsl:template name="list-quote">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no cite attribute or edit was defined, continue to next level -->
    <xsl:when test="(count(@cite)=0) or (count(@edit)!=0)">
     <xsl:call-template name="list-element">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Create the quote element -->
    <xsl:otherwise>
     <blockquote>
      <xsl:apply-templates select="@*" mode="quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="list-element">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </blockquote>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- Create the element before any automatic inline children -->
  <xsl:template name="list-element">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If going to be replaced with an image, create a para instead -->
    <xsl:when test="count(@src)">
     <p>
      <xsl:apply-templates select="@*">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="list-link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </p>
    </xsl:when>
    <!-- Create element and continue to children -->
    <xsl:otherwise>
     <xsl:element name="{$element}">
      <xsl:if test="$class">
       <xsl:attribute name="class">
        <xsl:value-of select="$class"/>
       </xsl:attribute>
      </xsl:if>
      <xsl:if test="count(xhtml:summary)">
       <xsl:attribute name="summary">
        <xsl:value-of select="xhtml:summary"/>
       </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="@*">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:apply-templates/>
     </xsl:element>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the href and src are provided, make a linked image -->
  <xsl:template name="list-link">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no href attribute appears, continue to next level -->
    <xsl:when test="count(@href)=0">
     <xsl:call-template name="list-image">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- href has been used, so create an anchor -->
    <xsl:otherwise>
     <a>
      <xsl:apply-templates select="@*" mode="link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="list-image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </a>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the src attribute appears, make an image -->
  <xsl:template name="list-image">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <img alt="{.}">
    <xsl:apply-templates select="@*" mode="image">
     <xsl:with-param name="element" select="$element"/>
     <xsl:with-param name="class" select="$class"/>
    </xsl:apply-templates>
   </img>
  </xsl:template>

  <!--
    - List Item rendering templates
    -->
  <!-- Create the element before any automatic inline children -->
  <xsl:template name="listitem">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:element name="{$element}">
    <xsl:if test="$class">
     <xsl:attribute name="class">
      <xsl:value-of select="$class"/>
     </xsl:attribute>
    </xsl:if>
    <xsl:apply-templates select="@*">
     <xsl:with-param name="element" select="$element"/>
     <xsl:with-param name="class" select="$class"/>
    </xsl:apply-templates>
    <xsl:call-template name="listitem-insdel">
     <xsl:with-param name="element" select="$element"/>
     <xsl:with-param name="class" select="$class"/>
    </xsl:call-template>
   </xsl:element>
  </xsl:template>

  <!-- If the edit attribute has been used, add a ins or del as appropriate -->
  <xsl:template name="listitem-insdel">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- No edit attribute, just call next level -->
    <xsl:when test="count(@edit)=0">
     <xsl:call-template name="listitem-link">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Edit attribute set to "deleted", create a del element -->
    <xsl:when test="@edit='deleted'">
     <del>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="listitem-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </del>
    </xsl:when>
    <!-- Edit attribute set to "inserted", "changed", "moved", create an ins
      -  element -->
    <xsl:otherwise>
     <ins>
      <xsl:apply-templates select="@*" mode="insdel">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="listitem-quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </ins>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the cite attribute appears without edit, create a quote element -->
  <xsl:template name="listitem-quote">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no cite attribute or edit was defined, continue to next level -->
    <xsl:when test="(count(@cite)=0) or (count(@edit)!=0)">
     <xsl:call-template name="listitem-link">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- Create the quote element -->
    <xsl:otherwise>
     <q>
      <xsl:apply-templates select="@*" mode="quote">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="listitem-link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </q>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the href attribute appears, make a hyperlink -->
  <xsl:template name="listitem-link">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no href attribute appears, continue to next level -->
    <xsl:when test="count(@href)=0">
     <xsl:call-template name="listitem-image">
      <xsl:with-param name="element" select="$element"/>
      <xsl:with-param name="class" select="$class"/>
     </xsl:call-template>
    </xsl:when>
    <!-- href has been used, so create an anchor -->
    <xsl:otherwise>
     <a>
      <xsl:apply-templates select="@*" mode="link">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
      <xsl:call-template name="listitem-image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:call-template>
     </a>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!-- If the src attribute appears, make an image -->
  <xsl:template name="listitem-image">
   <xsl:param name="element"/>
   <xsl:param name="class"/>
   <xsl:choose>
    <!-- If no src attribute appears, continue to next level -->
    <xsl:when test="count(@src)=0">
     <xsl:apply-templates/>
    </xsl:when>
    <!-- src has been used, so replace with an image -->
    <xsl:otherwise>
     <img alt="{.}">
      <xsl:apply-templates select="@*" mode="image">
       <xsl:with-param name="element" select="$element"/>
       <xsl:with-param name="class" select="$class"/>
      </xsl:apply-templates>
     </img>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>

  <!--
    - XHTML Structure Module
    -->
  <!-- Add our Subversion/CVS Id to the root element -->
  <xsl:template match="xhtml:html">
   <xsl:comment> $Id: xhtml2html.xslt 127 2004-11-14 03:06:21Z etherealwake $ </xsl:comment>
   <html>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
   </html>
  </xsl:template>
  <!-- Specify namespace for metadata (if not using schema.X links) -->
  <xsl:template match="xhtml:html/@profile">
   <xsl:attribute name="profile"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>
  <!-- Add a generator tag to the head section -->
  <xsl:template match="xhtml:head">
   <head>
    <xsl:apply-templates select="@*"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <xsl:apply-templates/>
    <xsl:if test="count(xhtml:meta[@name='generator'])=0">
     <meta name="generator" content="Ethereal Wake XHTML2 to XHTML1 XSLT"/>
    </xsl:if>
   </head>
  </xsl:template>
  <!-- <title> and <body> are pretty much copied as is -->
  <xsl:template match="xhtml:title|xhtml:body">
   <xsl:element name="{local-name()}">
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
   </xsl:element>
  </xsl:template>
    
  <!--
    - XHTML Block Text Module
    -->
  <!-- Handle the elements practically unchanged since XHTML 1.0 -->
  <xsl:template match="xhtml:address|xhtml:blockquote|xhtml:div|xhtml:hr
    |xhtml:pre|xhtml:hr">
   <xsl:call-template name="block">
    <xsl:with-param name="element" select="local-name(.)"/>
   </xsl:call-template>
  </xsl:template>
  <!-- In XHTML 2.0, paragraphs are a bit more sophisticated than in 1.X -->
  <xsl:template match="xhtml:p">
   <xsl:choose>
    <!-- If we have XHTML 1.0 block children, make us a div -->
    <xsl:when test="count(xhtml:ul|xhtml:ol|xhtml:nl|xhtml:dl|xhtml:blockcode
      |xhtml:blockquote|xhtml:pre|xhtml:table)&gt;0">
     <xsl:call-template name="block">
      <xsl:with-param name="element" select="'div'"/>
      <xsl:with-param name="class" select="'p'"/>
     </xsl:call-template>
    </xsl:when>
    <!-- If no block children, be a traditional paragraph -->
    <xsl:otherwise>
     <xsl:call-template name="block">
      <xsl:with-param name="element" select="'p'"/>
     </xsl:call-template>
    </xsl:otherwise>
   </xsl:choose>
  </xsl:template>
  <!-- We will treat sections as simple <div>s of class section -->
  <xsl:template match="xhtml:section">
   <xsl:call-template name="block">
    <xsl:with-param name="element" select="'div'"/>
    <xsl:with-param name="class" select="'section'"/>
   </xsl:call-template>
  </xsl:template>
  <!-- <h> gets mapped to a h# based on how many parent sections -->
  <xsl:template match="xhtml:h">
   <!-- Count the number of sections -->
   <xsl:variable name="levelbase">
    <xsl:value-of select="count(ancestor::xhtml:section)+$xhtml-heading"/>
   </xsl:variable>
   <!-- Limit the heading element to h6 -->
   <xsl:variable name="level">
    <xsl:choose>
     <xsl:when test="$levelbase &gt; 6">6</xsl:when>
     <xsl:otherwise><xsl:value-of select="$levelbase"/></xsl:otherwise>
    </xsl:choose>
   </xsl:variable>
   <!-- Create the appropriate element -->
   <xsl:call-template name="block">
    <xsl:with-param name="element" select="concat('h',$level)"/>
   </xsl:call-template>
  </xsl:template>

  <!--
    - XHTML Inline Text Module
    -->
  <!-- Handle the elements practically unchanged since XHTML 1.0 -->
  <xsl:template match="xhtml:abbr|xhtml:cite|xhtml:code|xhtml:dfn|xhtml:em
    |xhtml:kbd|xhtml:samp|xhtml:span|xhtml:strong|xhtml:sub|xhtml:sup
    |xhtml:var">
   <xsl:call-template name="inline">
    <xsl:with-param name="element" select="local-name()"/>
   </xsl:call-template>
  </xsl:template>
  <!-- quote maps to the 'q' element from XHTML 1.0 -->
  <xsl:template match="xhtml:quote">
   <xsl:call-template name="inline">
    <xsl:with-param name="element" select="'q'"/>
   </xsl:call-template>
  </xsl:template>
  <!-- <l> is a more complex version of HTML's <br> -->
  <xsl:template match="xhtml:l">
   <xsl:call-template name="inline">
    <xsl:with-param name="element" select="'br'"/>
   </xsl:call-template>
  </xsl:template>

  <!--
    - XHTML Hypertext Module
    -->
  <xsl:template match="xhtml:a">
   <xsl:call-template name="inline">
    <xsl:with-param name="element" select="'a'"/>
   </xsl:call-template>
  </xsl:template>
  <xsl:template match="xhtml:a/@id" mode="link">
   <xsl:attribute name="id"><xsl:value-of select="."/></xsl:attribute>
   <xsl:attribute name="name"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!--
    - XHTML List Module
    -->
  <xsl:template match="xhtml:ul|xhtml:ol|xhtml:dl">
   <xsl:call-template name="list">
    <xsl:with-param name="element" select="local-name(.)"/>
   </xsl:call-template>
  </xsl:template>
  <xsl:template match="xhtml:li|xhtml:dt|xhtml:dd">
   <xsl:call-template name="listitem">
    <xsl:with-param name="element" select="local-name(.)"/>
   </xsl:call-template>
  </xsl:template>

  <!--
    - XHTML Linking Module
    -->
  <xsl:template match="xhtml:link">
   <link><xsl:apply-templates select="@*" mode="link"/></link>
  </xsl:template>
  <xsl:template match="xhtml:link/@media" mode="link">
   <xsl:attribute name="media"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>
  <xsl:template match="xhtml:link/@xml:lang" mode="link">
   <xsl:attribute name="hreflang"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!--
    - XHTML Metainformation Module
    -->
  <xsl:template match="xhtml:meta">
   <meta>
    <xsl:apply-templates select="@*"/>
    <xsl:attribute name="content"><xsl:value-of select="."/></xsl:attribute>
   </meta>
  </xsl:template>
  <xsl:template match="xhtml:meta/@name">
   <xsl:attribute name="name"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!--
    - XHTML Object Module
    -->
  <!--
    - Ruby Module
    -->
  <!--
    - XHTML Scripting Module
    -->
  <!--
    - XHTML Style Sheet Module
    -->
  <xsl:template match="xhtml:style">
   <style>
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
   </style>
  </xsl:template>
  <xsl:template match="xhtml:style/@media">
   <xsl:attribute name="media"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!--
    - XHTML Tables Module
    -->
  <!-- Some table elements are pretty much left unchanged -->
  <xsl:template match="xhtml:caption|xhtml:col|xhtml:colgroup|xhtml:thead
    |xhtml:tfoot|xhtml:tbody|xhtml:tr">
   <xsl:element name="{local-name()}">
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates/>
   </xsl:element>
  </xsl:template>
  <xsl:template match="xhtml:col/@span|xhtml:colspan/@span">
   <xsl:attribute name="span"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>
  <!-- td and th are pretty much as they were -->
  <xsl:template match="xhtml:td|xhtml:th">
   <xsl:call-template name="listitem">
    <xsl:with-param name="element" select="local-name()"/>
   </xsl:call-template>
  </xsl:template>
  <xsl:template match="xhtml:td/@abbr|xhtml:td/@axis|xhtml:td/@colspan
    |xhtml:td/@headers|xhtml:td/@rowspan|xhtml:td/@scope|xhtml:th/@abbr
    |xhtml:th/@axis|xhtml:th/@colspan|xhtml:th/@headers|xhtml:th/@rowspan
    |xhtml:th/@scope">
   <xsl:attribute name="{local-name()}">
    <xsl:value-of select="."/>
   </xsl:attribute>
  </xsl:template>
  <!-- The table element acts a lot like a list -->
  <xsl:template match="xhtml:table">
   <xsl:call-template name="list">
    <xsl:with-param name="element" select="'table'"/>
   </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
