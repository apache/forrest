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

  <xsl:template match="@src|@href|@background">
    <xsl:if test="not(contains(.,'apidocs')) and not(starts-with(., 'api/'))">
      <xsl:copy>
        <xsl:apply-templates select="."/>
      </xsl:copy>
    </xsl:if>
  </xsl:template>

  <xsl:template match="@*|node()">
   <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
