<?xml version="1.0"?>
<!--
This stylesheet contains templates for converting documentv11 to HTML.  See the
imported document2html.xsl for details.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/document2html.xsl"/>

  <xsl:template name="pdflink">
    <!-- No PDFs thankyou -->
  </xsl:template>

  <xsl:template match="section">
    <div class="section">
      <xsl:apply-imports/>
    </div>
  </xsl:template>

  <xsl:template match="section/section">
    <div class="subsection">
      <xsl:apply-imports/>
    </div>
  </xsl:template>

</xsl:stylesheet>
