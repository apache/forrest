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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:map="http://apache.org/cocoon/sitemap/1.0" 
  xmlns:xalan="http://xml.apache.org/xalan">
  
  <xsl:template name="print-selectors">
    <xsl:variable name="selectors.all">
      <xsl:for-each select="/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$aggregate/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$faq/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$forrest/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$i18n/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$issues/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$linkmap/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$menu/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$profiler/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$raw/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$resources/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$revisions/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$search/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
      <xsl:for-each select="$tabs/map:sitemap/map:components/map:selectors//map:selector">
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="selectors.all.node" select="xalan:nodeset($selectors.all)"/>
    <xsl:variable name="selectors.all.sorted">
      <xsl:for-each select="$selectors.all.node/*">
        <xsl:sort select="./@name"/>
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="selectors.all.new" select="xalan:nodeset($selectors.all.sorted)"/>
    <p>
      <strong>Selectors</strong>
    </p>
    <ul>
      <xsl:for-each select="$selectors.all.new/*">
        <xsl:sort select="@name"/>
        <xsl:variable name="pos" select="position()"/>
        <xsl:variable name="current-selector" select="."/>
        <xsl:if test="$pos=1 or not($current-selector/@name=$selectors.all.new/*[$pos - 1]/@name) ">
          <li>
            <xsl:value-of select="./@name"/> - 
              
                <xsl:call-template name="xmap-sel-component-list">
              <xsl:with-param name="component" select="./@name"/>
            </xsl:call-template>
          </li>
        </xsl:if>
      </xsl:for-each>
    </ul>
  </xsl:template>
  <xsl:template name="xmap-sel-component-list">
    <xsl:param name="component" select="''"/>
          {
          
          <xsl:if test="$aggregate/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            aggregate.xmap, 
          </xsl:if>
    <xsl:if test="$faq/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            faq.xmap, 
          </xsl:if>
    <xsl:if test="$forrest/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            forrest.xmap, 
          </xsl:if>
    <xsl:if test="$i18n/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            i18n.xmap, 
          </xsl:if>
    <xsl:if test="$issues/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            issues.xmap, 
          </xsl:if>
    <xsl:if test="$linkmap/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            linkmap.xmap, 
          </xsl:if>
    <xsl:if test="$menu/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            menu.xmap, 
          </xsl:if>
    <xsl:if test="$profiler/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            profiler.xmap, 
          </xsl:if>
    <xsl:if test="$raw/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            raw.xmap, 
          </xsl:if>
    <xsl:if test="$resources/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            resources.xmap, 
          </xsl:if>
    <xsl:if test="$revisions/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            revisions.xmap, 
          </xsl:if>
    <xsl:if test="$search/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            search.xmap, 
          </xsl:if>
    <xsl:if test="$sitemap/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            sitemap.xmap, 
          </xsl:if>
    <xsl:if test="$tabs/map:sitemap/map:components/map:selectors//map:selector/@name=$component">
            tabs.xmap, 
          </xsl:if>
          
          }
        </xsl:template>
</xsl:stylesheet>
