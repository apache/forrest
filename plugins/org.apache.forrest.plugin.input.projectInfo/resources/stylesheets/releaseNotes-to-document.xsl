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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:i18n="http://apache.org/cocoon/i18n/2.1"
    version="1.0">

  <xsl:param name="versionNumber"/>
  <xsl:include href="changes-to-document.xsl"/>

  <!-- Key to retrieve important Actions (@important='high') of the selected version sorted by context... -->
  <xsl:key name="ActionByVersionByContextByImportance" match="changes/release/action" use="concat(../@version, '_', @context, '_', @importance)"/>

  <!-- versionNumber: detect the value "current" or use the number that was supplied -->
  <xsl:variable name="realVersionNumber">
    <xsl:choose>
      <xsl:when test="$versionNumber='current'">
        <xsl:value-of select="//release[1]/@version"/>
      </xsl:when>
      <xsl:when test="$versionNumber=''">
        <xsl:value-of select="//release[1]/@version"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$versionNumber"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

 <xsl:template match="changes">
  <document>
   <header>
    <title>
    <xsl:choose>
     <xsl:when test="@title!=''">
       <xsl:value-of select="@title"/>
     </xsl:when>
     <xsl:otherwise>
       <i18n:translate>
         <i18n:text i18n:key="titleReleaseNotesFor">Release Notes for {0} {1}</i18n:text>
         <i18n:param><xsl:value-of select="../../skinconfig/project-name"/></i18n:param>
         <i18n:param><i18n:text><xsl:value-of select="$versionNumber"/></i18n:text></i18n:param>
       </i18n:translate>
     </xsl:otherwise>
    </xsl:choose>
   </title>
   </header>
   <body>
     <xsl:if test="contains($realVersionNumber, 'dev')">
       <warning>
         <i18n:translate>
           <i18n:text i18n:key="warningDevRelease">Version {0} is a development release,
       these notes are therefore not complete, they are intended to be an indicator
       of the major features that are so far included in this version.</i18n:text>
         <i18n:param><i18n:text><xsl:value-of select="$realVersionNumber"/></i18n:text></i18n:param>
         </i18n:translate>
       </warning>
     </xsl:if>

     <xsl:apply-templates select="release[@version=$realVersionNumber]"/>
   </body>
  </document>
 </xsl:template>

 <xsl:template match="release">
  <xsl:apply-templates select="notes"/>
  <section id="version_{@version}">
   <title><i18n:text i18n:key="majorChanges">Major Changes in Version</i18n:text><xsl:text> </xsl:text><xsl:value-of select="@version"/></title>
   <note><i18n:text i18n:key="noteMajorChanges">This is not a complete list of changes, just some of the more important ones.
     A full list of changes in this release</i18n:text>
   <xsl:text> </xsl:text>
   <a href="changes_{$versionNumber}.html"><i18n:text i18n:key="isavailable">is available</i18n:text></a>.</note>
   <xsl:for-each select="action[generate-id()=generate-id(key('ActionByVersionByContextByImportance',concat(../@version, '_', @context, '_high')))]">
    <xsl:sort select="@context"/>
    <section>
    <xsl:variable name="context" select="@context"/>
    <title>
    <xsl:choose>
      <xsl:when test="//contexts/context[@id=$context]">
       <xsl:value-of select="//contexts/context[@id=$context]/@title"/>
      </xsl:when>
      <xsl:otherwise>
       <xsl:value-of select="@context"/>
      </xsl:otherwise>
    </xsl:choose>
    </title>
     <ul>
      <xsl:choose>
        <xsl:when test="contains($projectInfo.changes.sort, 'type')">
          <xsl:apply-templates select="key('ActionByVersionByContextByImportance',concat(../@version, '_', @context, '_high') )">
              <xsl:sort select="@type"/>
          </xsl:apply-templates>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="key('ActionByVersionByContextByImportance',concat(../@version, '_', @context, '_high') )"/>
        </xsl:otherwise>
      </xsl:choose>
     </ul>
    </section>
   </xsl:for-each>
  </section>
 </xsl:template>

</xsl:stylesheet>
