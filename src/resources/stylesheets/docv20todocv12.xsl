<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!-- document-v20x.dtd to document-v12.dtd transformation -->

  <!-- normally, I would include something like this:-->
  <xsl:output 
     doctype-public="-//APACHE//DTD Documentation V1.2//EN"
     doctype-system="document-v1.2.dtd"
     indent="no" />
     
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="a">
    <link>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
      <xsl:apply-templates/>
    </link>
  </xsl:template>

  <!--
  <xsl:template match="s1/@title | s2/@title | s3/@title | s4/@title">
    <title><xsl:value-of select="."/></title>
  </xsl:template>
  -->


  <!-- the obligatory copy-everything -->
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
