<?xml version="1.0"?>

<!--
This stylesheet filters out links to URLs that we don't want Forrest to crawl,
like Javadocs.  By default, URLs beginning with 'api/' or containing 'apidocs'
are filtered out.  Edit the @test attribute below to customize for your site.

To test link filtering, request a page URL with '?cocoon-view=links' appended,
for example after a 'forrest run', request
http://localhost:8888/index.html?cocoon-view=links That will return a list of
paths in the page that Forrest will try to recursively render.
-->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

  <xsl:param name="ctxbasedir" select="'undefined'"/>

  <xsl:template match="@src|@href|@background">

    <xsl:choose>
      <xsl:when test="contains(., 'apidocs')">
        <xsl:message>Ignoring javadoc link: <xsl:value-of select="."/></xsl:message>
      </xsl:when>
      <xsl:when test="contains(., 'api/')">
        <xsl:message>Ignoring javadoc link: <xsl:value-of select="."/></xsl:message>
      </xsl:when>
      <xsl:when test="not(contains(., ':')) and substring(., string-length(.)) = '/'">
        <xsl:message>Ignoring directory link: <xsl:value-of select="."/></xsl:message>
        <!-- Don't try to render links to local directories (that end with '/'). -->
      </xsl:when>
      <xsl:when test="starts-with(., $ctxbasedir)">
        <xsl:message>Ignoring absolute PDF link: <xsl:value-of select="."/></xsl:message>
        <!-- Ignore absolute image paths used in PDFs from being rendered. This
        will go away when the FOP-Cocoon link improves -->
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="."/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|node()">
   <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
