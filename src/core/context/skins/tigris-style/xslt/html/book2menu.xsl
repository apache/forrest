<?xml version="1.0"?>
<!--
book2menu.xsl generates the HTML menu.  See the imported book2menu.xsl for
details.

$Id: book2menu.xsl,v 1.1 2003/12/22 09:56:11 nicolaken Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/book2menu.xsl"/>
         
  <xsl:template match="book">
    <div class="menu">
        <div class="body">
          <xsl:apply-templates select="menu"/>
        </div>
    </div>
    
    <div class="strut">&#160;</div>
  </xsl:template>

  <xsl:template match="menu">
    <div>
      <a href="{@href}"><xsl:value-of select="@label"/></a>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template match="menu-item[@type='hidden']"/>

  <xsl:template match="menu-item">
    <div>
      <a href="{@href}"><xsl:value-of select="@label"/></a>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template name="selected">
    <div class="selfref">
      <xsl:value-of select="@label"/>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <xsl:template name="print-external">
      <xsl:apply-imports/>
  </xsl:template>
  
</xsl:stylesheet>
