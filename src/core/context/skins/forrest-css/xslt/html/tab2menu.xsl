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
This stylesheet generates 'tabs' at the top left of the Forrest skin.  Tabs are
visual indicators that a certain subsection of the URI space is being browsed.
For example, if we had tabs with paths:

Tab1:  ''
Tab2:  'community'
Tab3:  'community/howto'
Tab4:  'community/howto/xmlform/index.html'

Then if the current path was 'community/howto/foo', Tab3 would be highlighted.
The rule is: the tab with the longest path that forms a prefix of the current
path is enabled.

The output of this stylesheet is HTML of the form:
    <div class="tab">
      ...
    </div>

which is then merged by site2xhtml.xsl

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/tab2menu.xsl"/>

  <xsl:template match="tabs">
  	<div id="tabs">
    	<h2 id="categories">
          <xsl:call-template name="base-tabs"/>
        </h2>
	</div>       
  </xsl:template>

  <xsl:template name="pre-separator">
  </xsl:template>

  <xsl:template name="post-separator">
  </xsl:template>

  <xsl:template name="separator">
    <span class="textonly">-</span>
  </xsl:template>

  <xsl:template name="selected">
    <span class="category"><xsl:value-of select="@label"/></span>
  </xsl:template>

  <xsl:template name="not-selected">
    <a class="category">
      <xsl:attribute name="href">
        <xsl:call-template name="calculate-tab-href">
          <xsl:with-param name="tab" select="."/>
          <xsl:with-param name="path" select="$path"/>
        </xsl:call-template>
      </xsl:attribute>
      <xsl:value-of select="@label"/>
    </a>           
  </xsl:template>

</xsl:stylesheet>
