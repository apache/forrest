<?xml version="1.0" encoding="iso-8859-1"?>

<xsl:stylesheet version="1.0" 
  xmlns:style="http://openoffice.org/2000/style" 
  xmlns:text="http://openoffice.org/2000/text" 
  xmlns:office="http://openoffice.org/2000/office" 
  xmlns:table="http://openoffice.org/2000/table" 
  xmlns:draw="http://openoffice.org/2000/drawing" 
  xmlns:presentation="http://openoffice.org/2000/presentation" 
  xmlns:fo="http://www.w3.org/1999/XSL/Format" 
  xmlns:xlink="http://www.w3.org/1999/xlink" 
  xmlns:dc="http://purl.org/dc/elements/1.1/" 
  xmlns:meta="http://openoffice.org/2000/meta" 
  xmlns:number="http://openoffice.org/2000/datastyle" 
  xmlns:svg="http://www.w3.org/2000/svg" 
  xmlns:chart="http://openoffice.org/2000/chart" 
  xmlns:dr3d="http://openoffice.org/2000/dr3d" 
  xmlns:math="http://www.w3.org/1998/Math/MathML" 
  xmlns:form="http://openoffice.org/2000/form" 
  xmlns:script="http://openoffice.org/2000/script" 
  xmlns:config="http://openoffice.org/2001/config" 
  office:class="text" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  exclude-result-prefixes="office meta  table number dc fo xlink chart math script xsl draw svg dr3d form config text style">

  <xsl:import href="openoffice-common2forrest.xsl"/>

  <!--+
      | create the body 
      +-->
  <xsl:template match="office:body">
    <body>  
      <xsl:apply-templates select="key('rootChildren', generate-id())"/>
      <xsl:apply-templates select="draw:page"/>
    </body>
  </xsl:template>
  
  <!--+
      | slide
      +-->
  <xsl:template match="draw:page">
    <xsl:variable name="layout-name"><xsl:value-of select="@presentation:presentation-page-layout-name"/></xsl:variable>
      <section>
          <section class="slide">
            <title><xsl:value-of select="draw:text-box/text:p"/></title>
            <xsl:apply-templates select="draw:image" mode="slide">
              <xsl:with-param name="layout-name"><xsl:value-of select="$layout-name"/></xsl:with-param>
            </xsl:apply-templates>
            <xsl:apply-templates select="draw:text-box" mode="slide"/>
          </section>
          <xsl:apply-templates select="presentation:notes/draw:text-box" mode="notes"/>
      </section>
  </xsl:template>
  
  <!-- ==================================================================== -->
  <!-- Text Boxes                                                           -->
  <!-- ==================================================================== -->
  <xsl:template match="draw:text-box" mode="slide">
    <!-- ignore first element if it is a paragraph as this
         is the title of the slide -->
    <xsl:apply-templates select="*[not(self::text:p)]"/>
  </xsl:template>
  
  <xsl:template match="draw:text-box" mode="notes">
    <xsl:apply-templates select="*"/>
  </xsl:template>
  
  <xsl:template name="fontStyle">
    <xsl:apply-templates select="style:properties/@fo:font-weight"/>
    <xsl:apply-templates select="style:properties/@fo:font-style"/>
    <xsl:apply-templates select="style:properties/@style:font-name"/>
  </xsl:template>
  
  
  <xsl:template match="draw:image[@xlink:show]" mode="slide">
    <xsl:param name="layout-name"/>
    <xsl:variable name="href"><xsl:value-of select="@xlink:href"/></xsl:variable>
    <xsl:choose>
      <xsl:when test="starts-with($href, '#')">
        <img src="openOfficeEmbeddedImage/zip-{$filename}.{$extension}/file-{substring($href, 2)}"
          alt="{@draw:name}"/>
      </xsl:when>
      <xsl:otherwise>
        <img src="{$href}" alt="{@draw:name}"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
