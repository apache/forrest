<?xml version="1.0"?>
<!--
This stylesheet is a variant of relativize-linkmap.xsl that handles the case
where a set of nodes have been aggregated into the content for the current page
($path).  Any node may be inside or outside the aggregation set.

For nodes inside the aggregation set, links are prepended with '#' (to form an
internal anchor).
For nodes outside the aggregation set, links are made relative to the context
root (either absolute or relative).

For instance, if we have:

<about label="About">
  <index label="Index" href="index.html"/>
  <who label="Who we are" href="who.html"/>
  <dreams label="Dream list" href="dreams.html" tab="mytab"/>
  <faq label="FAQs" href="faq.html"             tab="mytab"/>
  <changes label="Changes" href="changes.html"/>
  <todo label="Todo" href="todo.html"           tab="mytab"/>
</about>

Nodes 'dreams', 'faq' and 'todo' are inside the 'mytab' aggregation.  If $path
was 'faq.html', then the generated linkmap would be:

<about label="About">
  <index label="Index" href="index.html"/>
  <who label="Who we are" href="who.html"/>
  <dreams label="Dream list" href="#dreams.html" tab="mytab"/>
  <faq label="FAQs" href="#faq.html"             tab="mytab"/>
  <changes label="Changes" href="changes.html"/>
  <todo label="Todo" href="#todo.html"           tab="mytab"/>
</about>

Where links like '#dreams.html' are assumed to be anchors in the aggregated
document.

Jeff Turner <jefft@apache.org>
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="path"/>
  <xsl:param name="site-root" select="'http://localhost:8787/forrest/'"/>

  <xsl:variable name="tab">
    <xsl:value-of select="string(//*[starts-with(@href, $path)]/@tab)"/>
  </xsl:variable>

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

        <xsl:when test="$tab='' or ../@tab=$tab">
          <xsl:value-of select="concat('#', .)"/>
        </xsl:when>

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

  <xsl:include href="../copyover.xsl"/>

</xsl:stylesheet>
