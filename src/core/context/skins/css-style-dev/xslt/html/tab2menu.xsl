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
This stylesheet generates 'tabs' at the top left of the screen.
See the imported tab2menu.xsl for details.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/tab2menu.xsl"/>

  <xsl:template match="tabs">
  	<ul id="tabs">
  		<xsl:call-template name="base-tabs"/>
  	</ul>
  </xsl:template>

  <xsl:template name="pre-separator">
  </xsl:template>

  <xsl:template name="post-separator">
  </xsl:template>

  <xsl:template name="separator">
  </xsl:template>

  <xsl:template name="selected">
	<li class="current"><xsl:call-template name="base-selected"/></li>
  </xsl:template>

  <xsl:template name="not-selected">
	<li><xsl:call-template name="base-not-selected"/></li>
  </xsl:template>

</xsl:stylesheet>
