<?xml version="1.0"?>

<!--
This stylesheet filters all references to the javadocs, the samples and any
protocols (file:, java:) that Cocoon shouldn't handle.  This stylesheet is
applied while rendering the 'links' View.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
  <xsl:include href="linkutils.xsl"/>

  <!-- Directory in which to write unprocessed-<protocol>s.txt files. Any
  protocol'ed link that is deliberately stripped before being passed to Cocoon
  should be recorded in a file. -->
  <xsl:param name="linkdir"/>

  <xsl:template match="@src|@href|@background">
    <xsl:choose>
      <xsl:when test="not(contains(.,'apidocs')) and not(starts-with(.,
        'samples/')) and not(starts-with(., 'file:'))">
        <xsl:copy>
          <xsl:apply-templates select="."/>
        </xsl:copy>
      </xsl:when>
      <xsl:when test="starts-with(., 'file:')">
        <xsl:call-template name="record-link">
          <xsl:with-param name="href" select="." /> 
          <xsl:with-param name="linkdir" select="$linkdir" /> 
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- This is a hack which makes the javascript images work -->
  <xsl:template match="img[@onLoad and starts-with(@src, 'graphics')]">
      <img src="{@src}"/>
      <img>
        <xsl:attribute name="src">
          <xsl:value-of select="substring-before(@src, '.')"/>_over.<xsl:value-of select="substring-after(@src, '.')"/>
        </xsl:attribute>
      </img>
  </xsl:template>

  <xsl:template match="img[@onLoad and starts-with(@src, 'images') and contains(@src, '-lo.gif')]">
      <img src="{@src}"/>
      <img>
        <xsl:attribute name="src"><xsl:value-of select="substring-before(@src, '-lo.gif')"/>-hi.gif</xsl:attribute>
      </img>
  </xsl:template>

  <xsl:template match="@*|node()">
   <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
   </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
