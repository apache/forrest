<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:preserve-space elements="*" />
  <!-- faq-v20x.dtd to faq-v12.dtd transformation -->

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="faqs">
    <faqs>
      <xsl:attribute name="title">
        <xsl:value-of select="title"/>
      </xsl:attribute>
      <xsl:apply-templates/>
    </faqs>
  </xsl:template>

  <xsl:template match="faqs/title"/>

  <xsl:template match="faqsection">
    <part>
      <xsl:apply-templates select="node()|@*"/>
    </part>
  </xsl:template>

  <xsl:template match="a">
    <link>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="node()"/>
    </link>
  </xsl:template>


  <!-- the obligatory copy-everything -->
  <xsl:template match="node() | @*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
