<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:param name="path"/>
  <xsl:param name="dir_index" select="'index.html'"/>
  <xsl:include href="dotdots.xsl"/>
  <xsl:include href="pathutils.xsl"/>

  <!-- NOTE: Xalan has a bug (race condition?) where sometimes $root is only half-evaluated -->
  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>


  <!--
    The longest path of any tab, whose path is a subset of the current URL.  Ie,
    the path of the 'current' tab.
  -->
  <xsl:variable name="longest-dir">
    <xsl:for-each select="/tabs/tab[starts-with($path, @dir|@href)]">
      <xsl:sort select="string-length(@dir|@href)"
                data-type="number" order="descending"/>
      <xsl:if test="position()=1">
        <xsl:value-of select="@dir|@href"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:variable>

  <xsl:template name="separator">|</xsl:template>

  <xsl:template name="selected">
    <span class="selectedTab"><xsl:value-of select="@label"/></span>
  </xsl:template>

  <xsl:template name="not-selected">
    <span class="unselectedTab">
    <a>
      <xsl:attribute name="href">
        <xsl:if test="starts-with(@href, 'http')">
          <!-- Absolute URL -->
          <xsl:value-of select="@href"/>
        </xsl:if>
        <xsl:if test="not(starts-with(@href, 'http'))">
          <!-- Root-relative path -->
          <xsl:variable name="backpath">
            <xsl:value-of select="$root"/>
            <xsl:text>/</xsl:text>
            <xsl:value-of select="@dir|@href"/>
              <!-- If we obviously have a directory, add /index.html -->
            <xsl:if test="@dir or substring(@href, string-length(@href), string-length(@href)) = '/'">
              <xsl:text>/</xsl:text>
              <xsl:value-of select="$dir_index"/>
            </xsl:if>
          </xsl:variable>
          <xsl:value-of
                select="translate(normalize-space(translate($backpath, ' /', '/ ')), ' /', '/ ')"/>

          <!-- Link to backpath, normalizing slashes -->
        </xsl:if>
      </xsl:attribute>
      <xsl:value-of select="@label"/>
    </a>
    </span>
  </xsl:template>

  <xsl:template match="tabs">
    <div class="tab">
      <xsl:for-each select="tab">
        <xsl:if test="position()!=1"><xsl:call-template name="separator"/></xsl:if>
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="."/>
        <xsl:text> </xsl:text>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template match="tab">
    <xsl:choose>
      <xsl:when test="@dir = $longest-dir or @href = $longest-dir">
        <xsl:call-template name="selected"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="not-selected"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
