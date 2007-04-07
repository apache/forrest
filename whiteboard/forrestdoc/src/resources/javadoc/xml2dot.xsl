<?xml version="1.0"?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method = "text"   
                omit-xml-declaration="yes"/>
<!--
	c46 [label="CharScanner"];
	c46 -> c45 [dir=back,arrowtail=empty];
	c42 -> c45 [dir=back,arrowtail=empty,style=dashed];	//org.apache.alexandria.javasrc.xref.JavaLexer implements antlr.CharScanner
	// org.apache.alexandria.jsdoc.GenerateHTMLDoc
	c47 [label="{GenerateHTMLDoc\n||}"];
	// org.apache.alexandria.jsdoc.GenerateHTMLIndex
	c48 [label="{GenerateHTMLIndex\n||}"];
	// org.apache.alexandria.jsdoc.JSDocTask
	c49 [label="{JSDocTask\n||}"];
	//org.apache.alexandria.jsdoc.JSDocTask extends org.apache.tools.ant.Task
	// org.apache.tools.ant.Task
	c50 [label="{Task\n||}", fontname="Helvetica-Oblique"];
	c50 -> c49 [dir=back,arrowtail=empty];
-->
  <xsl:template match="javadoc">#!/usr/local/bin/dot
#
# Class diagram 
#
		
		digraph G {
			ranksep=1.4;
			edge [fontname="Helvetica",fontsize=9,labelfontname="Helvetica",labelfontsize=9];
			node [fontname="Helvetica",fontsize=9,shape=record];
			cluster [fontname="Helvetica",fontsize=9,shape=record];
					
			<xsl:apply-templates/>
    <xsl:for-each select="package/class">
      <xsl:if test="extends_class and extends_class/classref/@name!='java.lang.Object'">
        <xsl:call-template name="fullname">
          <xsl:with-param name="name" select="extends_class/classref/@name"/>
        </xsl:call-template>
                     -> 	   
		              <xsl:call-template name="fullname">
          <xsl:with-param name="name" select="../@name"/>
          <xsl:with-param name="parentname" select="@name"/>
        </xsl:call-template> [dir=back,arrowtail=empty];
				</xsl:if>
      <xsl:if test="implements">
        <xsl:for-each select="implements/interfaceref">
          <xsl:call-template name="fullname">
            <xsl:with-param name="name" select="../../../@name"/>
            <xsl:with-param name="parentname" select="../../@name"/>
          </xsl:call-template>
		            -> 
		            <xsl:call-template name="fullname">
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
                    [dir=back,arrowtail=empty,style=dashed];
		         </xsl:for-each>
      </xsl:if>
    </xsl:for-each>
    <xsl:for-each select="package/interface">
      <xsl:if test="extends_class and extends_class/classref/@name!='java.lang.Object'">
        <xsl:value-of select="extends_class/classref/@name"/> -> <xsl:value-of select="../@name"/>.<xsl:value-of select="@name"/> [dir=back,arrowtail=empty];
				</xsl:if>
      <xsl:if test="implements">
        <xsl:for-each select="implements/interfaceref">
          <xsl:value-of select="../../../@name"/>.<xsl:value-of select="../../@name"/> -> <xsl:value-of select="@name"/> [dir=back,arrowtail=empty,style=dashed];
		         </xsl:for-each>
      </xsl:if>
    </xsl:for-each>
           
           		
		}
	</xsl:template>
  <xsl:template match="package">
		subgraph cluster<xsl:call-template name="fullname">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template> {
		node [style=filled];
		
						
			<xsl:apply-templates/>
<!-- rank same, min, max, source or sink
             rankdir TB LR (left to right) or TB (top to bottom)
             ranksep .75 separation between ranks, in inches.
             ratio approximate aspect ratio desired, fill or auto
             remincross if true and there are multiple clusters, re-run crossing minimization
           -->
<!-- rank=same; 
             rankdir=TB;
             ranksep=1;
             ratio=fill; 
             remincross=true;
           -->
        
        label = "<xsl:value-of select="@name"/>";
		color="#000000";
		fillcolor="#dddddd";	
			
		}
	</xsl:template>
  <xsl:template match="class">
    <xsl:call-template name="fullname">
      <xsl:with-param name="name" select="../@name"/>
      <xsl:with-param name="parentname" select="@name"/>
    </xsl:call-template> [
         <xsl:choose>
      <xsl:when test="@extensiblity='abstract'">
             color="#848684", fillcolor="#ced7ce", fontname="Helvetica-Italic"           	
           </xsl:when>
      <xsl:otherwise>
             color="#9c0031", fillcolor="#ffffce", 
           </xsl:otherwise>
    </xsl:choose>
<!-- need to replace newline chars with \n -->
<!--comment="<xsl:value-of select="doc" />" ,-->
            URL="<xsl:call-template name="filepath">
      <xsl:with-param name="name" select="../@name"/>
      <xsl:with-param name="parentname" select="@name"/>
    </xsl:call-template>", 
            style=filled, 
            label="{<xsl:value-of select="@name"/>\n|<!--
            -->
    <xsl:if test="show">
      <xsl:for-each select = "field">
        <xsl:choose>
          <xsl:when test="@access='public'">+</xsl:when>
          <xsl:when test="@access='private'">-</xsl:when>
          <xsl:when test="@access='protected'">#</xsl:when>
          <xsl:otherwise>/</xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/primitive/@type" />
          <xsl:with-param name="marker" select="'.'" />
        </xsl:call-template>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/classref/@name" />
          <xsl:with-param name="marker" select="'.'" />
        </xsl:call-template>\l<!--                              
            -->
      </xsl:for-each>
    </xsl:if>|<xsl:if test="show">
<!--
                  constructor  
            -->
      <xsl:for-each select = "method">
        <xsl:choose>
          <xsl:when test="@access='public'">+</xsl:when>
          <xsl:when test="@access='private'">-</xsl:when>
          <xsl:when test="@access='protected'">#</xsl:when>
          <xsl:otherwise>/</xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/primitive/@type" />
          <xsl:with-param name="marker" select="'.'" />
        </xsl:call-template>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/classref/@name" />
          <xsl:with-param name="marker" select="'.'" />
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name" />( <!--
              -->
        <xsl:for-each select = "parameter">
          <xsl:value-of select="primitive/@type" />
          <xsl:call-template name="substring-after-last">
            <xsl:with-param name="input" select="classref/@name" />
            <xsl:with-param name="marker" select="'.'" />
          </xsl:call-template>,<!--
              -->
        </xsl:for-each>)\l<!--                              
            -->
      </xsl:for-each>
<!--            
           -->
    </xsl:if>}"];
	</xsl:template>
  <xsl:template match="interface">
    <xsl:call-template name="fullname">
      <xsl:with-param name="name" select="../@name"/>
      <xsl:with-param name="parentname" select="@name"/>
    </xsl:call-template> [ color="#9c0031", fillcolor="#deffff", label="{«interface»\n<xsl:value-of select="@name"/>\n}"];
	</xsl:template>
  <xsl:template match="doc"></xsl:template>
  <xsl:template match="extends"></xsl:template>
  <xsl:template match="field"></xsl:template>
  <xsl:template match="constructor"></xsl:template>
  <xsl:template match="method"></xsl:template>
  <xsl:template name="fullname">
    <xsl:param name="name"/>
    <xsl:param name="parentname"/>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$name"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'_'"/>
    </xsl:call-template>
    <xsl:if test = "$parentname!=''">_</xsl:if>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$parentname"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'_'"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="filepath">
    <xsl:param name="name"/>
    <xsl:param name="parentname"/>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$name"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'/'"/>
    </xsl:call-template>
    <xsl:if test = "$parentname!=''">/</xsl:if>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$parentname"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'/'"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template name="replace-string">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="with"/>
    <xsl:choose>
      <xsl:when test="contains($text,$replace)">
        <xsl:value-of select="substring-before($text,$replace)"/>
        <xsl:value-of select="$with"/>
        <xsl:call-template name="replace-string">
          <xsl:with-param name="text"
select="substring-after($text,$replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="with" select="$with"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="substring-after-last">
    <xsl:param name="input" />
    <xsl:param name="marker" />
    <xsl:choose>
      <xsl:when test="contains($input,$marker)">
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" 
          select="substring-after($input,$marker)" />
          <xsl:with-param name="marker" select="$marker" />
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$input" />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
