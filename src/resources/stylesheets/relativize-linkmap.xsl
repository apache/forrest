<?xml version="1.0"?>
<!--
Stylesheet which makes site.xml links relative to the site root.

If the current path ($path) is HTML, links with have ..'s added, to make the
URIs relative to some root.  Eg, given an 'absolutized' file (from
absolutize-linkmap.xsl):

<site href="">
  <community href="community/">
    <faq href="community/faq.html">
      <how_can_I_help href="community/faq.html#help"/>
    </faq>
  </community>
</site>

if $path was 'community/index.html', then '../' would be added to each href:

<site href="../">
  <community href="../community/">
    <faq href="../community/faq.html">
      <how_can_I_help href="../community/faq.html#help"/>
    </faq>
  </community>
</site>

If the current path is PDF, then an absolute URL to a site root ($site-root) is
prepended.  In our example above, if $site-root were http://www.mysite.com/,
the result would be:

<site href="http://www.mysite.com/">
  <community href="http://www.mysite.com/community/">
    <faq href="http://www.mysite.com/community/faq.html">
      <how_can_I_help href="http://www.mysite.com/community/faq.html#help"/>
    </faq>
  </community>
</site>


Jeff Turner <jefft@apache.org>
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="path"/>
  <xsl:param name="site-root"/>

  <xsl:include href="dotdots.xsl"/>

  <!-- Path to site root, eg '../../' -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:template match="@href">

    <xsl:attribute name="href">
      <xsl:choose>
        <xsl:when test="starts-with(., 'http:') or starts-with(., 'https:')">
          <xsl:value-of select="."/>
        </xsl:when>
        <xsl:when test="contains(., '.png') or
          contains(., '.jpeg') or
          contains(., '.jpg') or
          contains(., '.gif') or
          contains(., '.tif')">
          <!-- Image links are always relative -->
          <xsl:value-of select="$root"/><xsl:value-of select="."/>
        </xsl:when>


        <!-- PDFs can handle inline images, but everything else must become an
        external link -->
        <xsl:when test="contains($path, '.pdf')">

          <!-- Links to outside a PDF are all absolute -->
          <xsl:value-of select="concat($site-root, .)"/>

        </xsl:when>
        <xsl:otherwise>

          <!-- Links outside a HTML are relative -->
          <xsl:value-of select="$root"/><xsl:value-of select="."/>

        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>

  <xsl:include href="copyover.xsl"/>

</xsl:stylesheet>
