<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
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
    version="1.0">

 <xsl:import href="copyover.xsl"/>
 

 <!-- FIXME (JJP):  bugzilla is hardwired -->
 <xsl:variable name="bugzilla" select="'http://issues.apache.org/bugzilla/buglist.cgi?bug_id='"/>

 <xsl:param name="bugtracking-url" select="$bugzilla"/>

 <xsl:template match="/">
  <xsl:apply-templates select="//changes"/>
 </xsl:template>
 
 <xsl:template match="changes">
  <document>
   <header>
    <title>History of Changes</title>
   </header>
   <body>
    <p><link href="changes.rss"><img src="images/rss.png" alt="RSS"/></link></p>    
    <xsl:apply-templates/>
   </body>
  </document>
 </xsl:template>

 <xsl:template match="release">
  <section id="version_{@version}">
   <title>Version <xsl:value-of select="@version"/> (<xsl:value-of select="@date"/>)</title>
   <ul>
     <!-- To sort types by add,remove,update,fix. Look nicer -->
     <xsl:apply-templates select="action[@type='add']"/>
     <xsl:apply-templates select="action[@type='remove']"/>
     <xsl:apply-templates select="action[@type='update']"/>
     <xsl:apply-templates select="action[@type='fix']"/>
     <xsl:apply-templates select="action[@type='hack']"/>
     <xsl:apply-templates select="action[@type!='add' and @type!='remove' and @type!='update' and @type!='fix' and @type!='hack']"/>
   </ul>
  </section>
 </xsl:template>

 <xsl:template match="action">
  <li>
   <icon src="images/{@type}.jpg" alt="{@type}"/>
   <xsl:apply-templates/>
   <xsl:text>(</xsl:text><xsl:value-of select="@dev"/><xsl:text>)</xsl:text>

   <xsl:if test="@due-to and @due-to!=''">
    <xsl:text> Thanks to </xsl:text>
    <xsl:choose>
     <xsl:when test="@due-to-email and @due-to-email!=''">
      <link href="mailto:{@due-to-email}">
       <xsl:value-of select="@due-to"/>
      </link>
     </xsl:when>
     <xsl:otherwise>
      <xsl:value-of select="@due-to"/>
     </xsl:otherwise>
    </xsl:choose>
    <xsl:text>.</xsl:text>
   </xsl:if>

   <xsl:if test="@fixes-bug">
     <xsl:text> Fixes </xsl:text>
     <xsl:call-template name="print-bugs">
       <xsl:with-param name="buglist" select="translate(normalize-space(@fixes-bug),' ','')"/>
     </xsl:call-template>
     <!--
     <xsl:choose>
       <xsl:when test="contains(@fixes-bug, ',')">
         <!-<link href="{$bugtracking-url}{translate(normalize-space(@fixes-bug),' ','')}">->
           <link href="{$bugtracking-url}">
             <xsl:text>bugs </xsl:text><xsl:value-of select="normalize-space(@fixes-bug)"/>
           </link>
         </xsl:when>
         <xsl:otherwise>
           <link href="{$bugtracking-url}{@fixes-bug}">
             <xsl:text>bug </xsl:text><xsl:value-of select="@fixes-bug"/>
           </link>
         </xsl:otherwise>
       </xsl:choose>
       -->
       <xsl:text>.</xsl:text>
     </xsl:if>
   </li>
 </xsl:template>

 <!-- Print each bug id in a comma-separated list -->
 <xsl:template name="print-bugs">
   <xsl:param name="buglist"/>
   <xsl:choose>
     <xsl:when test="contains($buglist, ',')">
       <xsl:variable name="current" select="substring-before($buglist, ',')"/>
       <link href="{concat($bugtracking-url, $current)}">
         <xsl:value-of select="$current"/>
       </link>
       <xsl:text>, </xsl:text>
       <xsl:call-template name="print-bugs">
         <xsl:with-param name="buglist" select="substring-after($buglist, ',')"/>
       </xsl:call-template>
     </xsl:when>
     <xsl:otherwise>
       <link href="{concat($bugtracking-url, $buglist)}"><xsl:value-of select="$buglist"/></link>
     </xsl:otherwise>
   </xsl:choose>
 </xsl:template>

</xsl:stylesheet>
