<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!-- FIXME: still need to deal with all <header> elements
     subtitle?, version?, type?, authors?, notice*, abstract?
-->

<!-- document -->
  <xsl:template match="document">
<!-- header -->
    <div class="header">
      <xsl:if test="normalize-space(header/title)!=''">
        <h1>
          <xsl:value-of select="header/title"/>
        </h1>
      </xsl:if>
      <xsl:if test="normalize-space(header/subtitle)!=''">
        <h3>
          <xsl:value-of select="header/subtitle"/>
        </h3>
      </xsl:if>
    </div>
    <xsl:apply-templates/>
  </xsl:template>
<!-- body -->
  <xsl:template match="body">
    <div class="content">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
<!-- section -->
  <xsl:template match="section">
    <xsl:variable name="level" select="count(ancestor::section)+1"/>
    <xsl:if test="normalize-space(@id)!=''">
      <a name="{@id}"/>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="$level=1">
        <h2><xsl:value-of select="title"/></h2>
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$level=2">
        <h3><xsl:value-of select="title"/></h3>
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:when test="$level=3">
        <h4><xsl:value-of select="title"/></h4>
        <xsl:apply-templates/>
      </xsl:when>
      <xsl:otherwise>
        <h5><xsl:value-of select="title"/></h5>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
<!-- others -->
  <xsl:template match="note | warning | fixme">
    <div class="frame {local-name()}">
      <div class="label">
        <xsl:choose>
          <xsl:when test="local-name() = 'note'">Note</xsl:when>
          <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
          <xsl:otherwise>Fixme (
               <xsl:value-of select="@author"/>

               )</xsl:otherwise>
        </xsl:choose>
      </div>
      <div class="content">
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>
  <xsl:template match="link">
    <a href="{@href}">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  <xsl:template match="jump">
    <a href="{@href}" target="_top">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  <xsl:template match="fork">
    <a href="{@href}" target="_blank">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  <xsl:template match="source">
    <pre class="code">
      <xsl:apply-templates/>
    </pre>
  </xsl:template>
  <xsl:template match="anchor">
    <a name="{@id}"/>
  </xsl:template>
  <xsl:template match="icon">
    <img src="{@src}" alt="{@alt}">
      <xsl:if test="@height">
        <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@width">
        <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>
  <xsl:template match="figure">
    <div align="center">
      <img src="{@src}" alt="{@alt}" class="figure">
        <xsl:if test="@height">
          <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@width">
          <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
        </xsl:if>
      </img>
    </div>
  </xsl:template>
  <xsl:template match="table">
    <table class="table" cellpadding="4" cellspacing="1">
      <xsl:apply-templates/>
    </table>
  </xsl:template>
<!-- already used -->
  <xsl:template match="section/title">
  </xsl:template>
<!-- copy -->
  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
