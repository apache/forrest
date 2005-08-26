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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:map="http://apache.org/cocoon/sitemap/1.0" 
  xmlns:xalan="http://xml.apache.org/xalan">

  <xsl:template name="print-transformers">
    <xsl:variable name="transformers.all">
      <xsl:for-each select="/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$aggregate/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$faq/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$forrest/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$i18n/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$issues/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$linkmap/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$menu/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$profiler/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$raw/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$resources/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$revisions/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$search/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$tabs/map:sitemap/map:components/map:transformers//map:transformer">
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="transformers.all.node" select="xalan:nodeset($transformers.all)"/>
    <xsl:variable name="transformers.all.sorted">
      <xsl:for-each select="$transformers.all.node/*">
        <xsl:sort select="./@name"/>
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="transformers.all.new" select="xalan:nodeset($transformers.all.sorted)"/>
    <p>
      <strong>Transformers</strong>
    </p>
    <ul>
      <xsl:for-each select="$transformers.all.new/*">
        <xsl:sort select="@name"/>
        <xsl:variable name="pos" select="position()"/>
        <xsl:variable name="current-transformer" select="."/>
        <xsl:if test="$pos=1 or not($current-transformer/@name=$transformers.all.new/*[$pos - 1]/@name) ">
          <li>
            <xsl:value-of select="./@name"/> - 
              
                <xsl:call-template name="xmap-tran-component-list">
              <xsl:with-param name="component" select="./@name"/>
            </xsl:call-template>
          </li>
        </xsl:if>
      </xsl:for-each>
    </ul>
  </xsl:template>
  <xsl:template name="xmap-tran-component-list">
    <xsl:param name="component" select="''"/>
          {
          
          <xsl:if test="$aggregate/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            aggregate.xmap, 
          </xsl:if>
    <xsl:if test="$faq/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            faq.xmap, 
          </xsl:if>
    <xsl:if test="$forrest/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            forrest.xmap, 
          </xsl:if>
    <xsl:if test="$i18n/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            i18n.xmap, 
          </xsl:if>
    <xsl:if test="$issues/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            issues.xmap, 
          </xsl:if>
    <xsl:if test="$linkmap/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            linkmap.xmap, 
          </xsl:if>
    <xsl:if test="$menu/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            menu.xmap, 
          </xsl:if>
    <xsl:if test="$profiler/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            profiler.xmap, 
          </xsl:if>
    <xsl:if test="$raw/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            raw.xmap, 
          </xsl:if>
    <xsl:if test="$resources/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            resources.xmap, 
          </xsl:if>
    <xsl:if test="$revisions/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            revisions.xmap, 
          </xsl:if>
    <xsl:if test="$search/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            search.xmap, 
          </xsl:if>
    <xsl:if test="$sitemap/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            sitemap.xmap, 
          </xsl:if>
    <xsl:if test="$tabs/map:sitemap/map:components/map:transformers//map:transformer/@name=$component">
            tabs.xmap, 
          </xsl:if>
          
          }
        </xsl:template>
</xsl:stylesheet>
