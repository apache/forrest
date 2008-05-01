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
  <xsl:param name="path"/>
  <xsl:param name="versionNumber"/>
  <xsl:param name="projectInfo.changes.sort"/>
  <xsl:param name="projectInfo.changes.includeCommitterList"/>
  <xsl:param name="projectInfo.changes.includeContributorList"/>
  <xsl:include href="lm://transform.xml.dotdots"/>
<!-- Calculate path to site root, eg '../../' -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="bugzilla" select="'http://issues.apache.org/bugzilla/buglist.cgi?bug_id='"/>
  <xsl:param name="bugtracking-url" select="$bugzilla"/>
  <xsl:key name="contexts" match="changes/release/action" use="concat(../@version, '_', @context)"/>
  <xsl:key name="types" match="changes/release/action" use="@type"/>
  <xsl:key name="committers" match="developers/person" use="@id"/>
  <xsl:key name="distinct-committer" match="changes/release/action" use="concat(../@version, '_', @dev)"/>
  <xsl:key name="distinct-contributor" match="changes/release/action" use="concat(../@version, '_', @due-to)"/>
  <!-- versionNumber: detect the value "current" or use the number that was supplied -->
  <xsl:variable name="realVersionNumber">
    <xsl:choose>
      <xsl:when test="$versionNumber='current' or $versionNumber=''">
        <xsl:value-of select="//release[1]/@version"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$versionNumber"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:template match="/">
    <xsl:apply-templates select="//changes"/>
  </xsl:template>
  <xsl:template match="changes">
    <document>
      <header>
        <title><xsl:choose>
            <xsl:when test="@title!=''">
              <xsl:value-of select="@title"/>
            </xsl:when>
            <xsl:otherwise>
              <i18n:text i18n:key="title">History of Changes</i18n:text>
<xsl:text> </xsl:text>
              <xsl:value-of select="$versionNumber"/>
            </xsl:otherwise>
          </xsl:choose></title>
      </header>
      <body>
        <p>
          <link>
          <xsl:choose>
            <xsl:when test="$versionNumber">
              <xsl:attribute name="href">changes_<xsl:value-of select="$versionNumber"/>.rss</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="href">changes.rss</xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
          <img src="{$root}images/rss.png" alt="RSS"/>
          </link>
        </p>
        <section id="introduction">
          <title><i18n:text i18n:key="introduction">Introduction and explanation of symbols</i18n:text></title>
          <p>
            <i18n:text i18n:key="intro_s1_begin">Changes are sorted</i18n:text>
<xsl:text> </xsl:text>
            <xsl:if test="$projectInfo.changes.sort!='none'">
              <i18n:translate>
                <i18n:text i18n:key="intro_s1_bythen">by "{0}" and then</i18n:text>
                <i18n:param>
                  <i18n:text>
                    <xsl:value-of select="$projectInfo.changes.sort"/>
                  </i18n:text>
                </i18n:param>
              </i18n:translate>
<xsl:text> </xsl:text>
            </xsl:if>
            <i18n:text i18n:key="intro_s1_end">chronologically with the most recent at the top.</i18n:text>
<xsl:text> </xsl:text>
            <i18n:text i18n:key="intro_s2_begin">These symbols denote the various action types:</i18n:text>
            <xsl:for-each select="//release/action[generate-id()=generate-id(key('types', @type))]">
              <xsl:sort select="@type"/>
              <icon src="{$root}images/{@type}.jpg" alt="{@type}"/>
<xsl:text>=</xsl:text>
              <i18n:text>
                <xsl:value-of select="@type"/>
              </i18n:text>
              <xsl:if test="not(position()=last())">
<xsl:text>, </xsl:text>
              </xsl:if>
            </xsl:for-each>
          </p>
        </section>
        <xsl:choose>
          <xsl:when test="$versionNumber">
            <xsl:choose>
              <xsl:when test="$versionNumber='current'">
                <xsl:apply-templates select="//release[1]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="//release[@version=$versionNumber]"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$projectInfo.changes.includeCommitterList = 'true' and //developers">
          <section id="all-committers">
            <title><i18n:text i18n:key="committers">All Committers</i18n:text></title>
            <p>
              <i18n:text i18n:key="committers_s1">This is a list of all people who have ever participated
           as committers on this project.</i18n:text>
            </p>
            <ul>
              <xsl:for-each select="//developers/person">
                <li><xsl:value-of select="@name"/> (<xsl:value-of select="@id"/>)</li>
              </xsl:for-each>
            </ul>
          </section>
        </xsl:if>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="release">
    <section id="version_{@version}">
      <title><i18n:text i18n:key="version">Version</i18n:text>
<xsl:text> </xsl:text>
        <xsl:value-of select="@version"/> (<xsl:value-of select="@date"/>)</title>
      <xsl:apply-templates select="introduction"/>
      <xsl:for-each select="action[generate-id()=generate-id(key('contexts',concat(../@version, '_', @context)))]">
        <xsl:sort select="@context"/>
        <xsl:variable name="context" select="@context"/>
        <xsl:variable name="title">
          <xsl:choose>
            <xsl:when test="//contexts/context[@id=$context]">
              <xsl:value-of select="//contexts/context[@id=$context]/@title"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@context"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="$title != ''">
            <section>
              <title><xsl:value-of select="$title"/></title>
              <ul>
                <xsl:choose>
                  <xsl:when test="contains($projectInfo.changes.sort, 'type')">
                    <xsl:apply-templates select="key('contexts',concat(../@version, '_', @context) )">
                      <xsl:sort select="@type"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:apply-templates select="key('contexts',concat(../@version, '_', @context) )"/>
                  </xsl:otherwise>
                </xsl:choose>
              </ul>
            </section>
          </xsl:when>
          <xsl:otherwise>
            <ul>
              <xsl:choose>
                <xsl:when test="contains($projectInfo.changes.sort, 'type')">
                  <xsl:apply-templates select="key('contexts',concat(../@version, '_', @context) )">
                    <xsl:sort select="@type"/>
                  </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:apply-templates select="key('contexts',concat(../@version, '_', @context) )"/>
                </xsl:otherwise>
              </xsl:choose>
            </ul>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:if test="$projectInfo.changes.includeContributorList = 'true'">
        <section>
          <title><i18n:text i18n:key="release_committers">Contributors to this release</i18n:text></title>
          <p>
            <i18n:text i18n:key="release_committers_s1">We thank the following people for their contributions to this release.</i18n:text>
          </p>
          <p>
            <i18n:text i18n:key="release_committers_s2">This is a list of all people who participated as committers:</i18n:text>
            <br/>
            <xsl:for-each select="action[generate-id()=generate-id(key('distinct-committer', concat(../@version, '_', @dev)))]">
              <xsl:sort select="@dev"/>
              <xsl:value-of select="key('committers', @dev)/@name"/>
<xsl:text> (</xsl:text>
              <xsl:value-of select="@dev"/>
<xsl:text>)</xsl:text>
              <xsl:if test="not(position()=last())">
<xsl:text>, </xsl:text>
              </xsl:if>
              <xsl:if test="position()=last()">
<xsl:text>.</xsl:text>
              </xsl:if>
            </xsl:for-each>
          </p>
          <xsl:if test="action[@due-to]">
            <p>
              <i18n:text i18n:key="release_committers_s3">This is a list of other contributors:</i18n:text>
              <br/>
              <xsl:for-each select="action[generate-id()=generate-id(key('distinct-contributor', concat(../@version, '_', @due-to)))]">
                <xsl:sort select="@due-to"/>
                <xsl:value-of select="@due-to"/>
<!-- FIXME: There must be an extra node here from the entries
                  that do not have @due-to. Workaround is to skip position #1 -->
                <xsl:if test="not(position()=1) and not(position()=last())">
<xsl:text>, </xsl:text>
                </xsl:if>
                <xsl:if test="position()=last()">
<xsl:text>.</xsl:text>
                </xsl:if>
              </xsl:for-each>
            </p>
          </xsl:if>
        </section>
      </xsl:if>
    </section>
  </xsl:template>
  <xsl:template match="action">
    <li><icon src="{$root}images/{@type}.jpg" alt="{@type}"/>
      <xsl:apply-templates/>
<xsl:text> </xsl:text>
      <i18n:text i18n:key="committedby">Committed by</i18n:text>
<xsl:text> </xsl:text>
      <xsl:value-of select="@dev"/>
<xsl:text>.</xsl:text>
      <xsl:if test="@due-to and @due-to!=''">
<xsl:text> </xsl:text>
        <i18n:text i18n:key="thanksto">Thanks to</i18n:text>
<xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="@due-to-email and @due-to-email!=''"><link href="mailto:{@due-to-email}">
            <xsl:value-of select="@due-to"/></link>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@due-to"/>
          </xsl:otherwise>
        </xsl:choose>
<xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:if test="@fixes-bug">
<xsl:text> </xsl:text>
        <i18n:text i18n:key="seeissue">See Issue</i18n:text>
<xsl:text> </xsl:text>
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
      </xsl:if></li>
  </xsl:template>
<!-- Print each bug id in a comma-separated list -->
  <xsl:template name="print-bugs">
    <xsl:param name="buglist"/>
    <xsl:choose>
      <xsl:when test="contains($buglist, ',')">
        <xsl:variable name="current" select="substring-before($buglist, ',')"/>
        <xsl:call-template name="print-bug">
          <xsl:with-param name="bug-id" select="$current"/>
        </xsl:call-template>
<xsl:text>, </xsl:text>
        <xsl:call-template name="print-bugs">
          <xsl:with-param name="buglist" select="substring-after($buglist, ',')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="print-bug">
          <xsl:with-param name="bug-id" select="$buglist"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template name="print-bug">
    <xsl:param name="bug-id"/>
    <xsl:choose>
      <xsl:when test="string-length($bugtracking-url) &gt; 0">
        <link href="{concat($bugtracking-url, $bug-id)}"><xsl:value-of select="$bug-id"/></link>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$bug-id"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="introduction">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
