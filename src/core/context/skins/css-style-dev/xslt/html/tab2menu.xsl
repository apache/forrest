<?xml version="1.0"?>
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
