<?xml version="1.0"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink">

<xsl:variable name="graph_width">500</xsl:variable>
<xsl:variable name="graph_height">150</xsl:variable>
<xsl:variable name="graph_margin">50</xsl:variable>

<xsl:variable name="max_point">
  <xsl:for-each select="/graph/data/datum">
    <xsl:sort order="descending" data-type="number" select="@value"/>
    <xsl:if test="position() = 1"><xsl:value-of select="@value"/></xsl:if>
  </xsl:for-each>
</xsl:variable>

<xsl:variable name="min_point">
  <xsl:for-each select="/graph/data/datum">
    <xsl:sort order="ascending" data-type="number" select="@value"/>
    <xsl:if test="position() = 1"><xsl:value-of select="@value"/></xsl:if>
  </xsl:for-each>
</xsl:variable>

<xsl:variable name="num_points" select="count(/graph/data/datum)"/>

<xsl:variable name="x_scale" select="$graph_width div ($num_points - 1)"/>
<xsl:variable name="y_scale" select="$graph_height div $max_point * 0.95"/>

<xsl:template match="graph">
  <svg
    width="{$graph_margin + $graph_width + $graph_margin}"
    height="{$graph_margin + $graph_height + $graph_margin}">

    <title>Forrest Graphing</title>

    <style type="text/css">
      text {
        font-family: 'Verdana';
        font-size: 10px;
        stroke: none;
        fill: #000088; }
      .tick {
        fill:none;
        stroke: #000088;
        stroke-width: 1px; }
      #border {
        fill: none;
        stroke: #000088;
        stroke-width: 1px; }
      #graph {
        fill: rgb(192,192,255);
        stroke: #000088;
        stroke-width: 1px; }
      #events {
        fill: black;
        stroke: black;
        stroke-width: 1px; }
      #events text {
        text-anchor: middle; }
      #horizontalRuler {
        fill: #000088; }
    </style>

    <defs>
      <line id="week" class="tick" x1="0" y1="0" x2="0" y2="3"/>
      <line id="month" class="tick" x1="0" y1="0" x2="0" y2="5"/>
      <path id="down-triangle" d="M0,0 l-3,-4.5 l6,0 z"/>		
      <path id="up-triangle" d="M0,0 l-3,4.5 l6,0 z"/>		
      <path id="left-triangle" d="M0,0 l4.5,-3 l0,6 z"/>		
      <path id="right-triangle" d="M0,0 l-4.5,-3 l0,6 z"/>		
    </defs>

    <xsl:apply-templates select="data"/>
    <xsl:apply-templates select="events"/>

  </svg>
</xsl:template>

<xsl:template match="data">
  <g transform="translate({$graph_margin} {$graph_margin})">
    <g id="graph" transform="matrix(1 0 0 -1 0 {$graph_height})">
      <xsl:variable name="points">
        <xsl:value-of select="$graph_width"/><xsl:text>,0 0,0</xsl:text>
        <xsl:for-each select="datum">
          <xsl:text> </xsl:text>
          <xsl:value-of select="(position() - 1) * $x_scale"/>
          <xsl:text>,</xsl:text>
          <xsl:value-of select="@value * $y_scale"/>
        </xsl:for-each>
      </xsl:variable>
      <polygon points="{$points}"/>
      <!-- max -->
      <g transform="translate(0 {$max_point * $y_scale})">
        <use xlink:href="#right-triangle"/>
        <text text-anchor="end" x="-7" y="4" transform="matrix(1 0 0 -1 0 0)"><xsl:value-of select="$max_point"/></text>
      </g>
      <!-- min -->
      <g transform="translate(0 {$min_point * $y_scale})">
        <use xlink:href="#right-triangle"/>
        <text text-anchor="end" x="-7" y="4" transform="matrix(1 0 0 -1 0 0)"><xsl:value-of select="$min_point"/></text>
      </g>
      <!-- average -->
      <g transform="translate(0 {sum(datum/@value) div $num_points * $y_scale})">
        <use xlink:href="#right-triangle"/>
        <text text-anchor="end" x="-7" y="4" transform="matrix(1 0 0 -1 0 0)"><xsl:value-of select="ceiling(sum(datum/@value) div $num_points)"/></text>
      </g>
      <!-- current -->
      <g transform="translate({$graph_width} {datum[position() = last()]/@value * $y_scale})">
        <use xlink:href="#left-triangle"/>
        <text x="7" y="4" transform="matrix(1 0 0 -1 0 0)"><xsl:value-of select="datum[position() = last()]/@value"/></text>
      </g>
    </g>

    <rect id="border" x="0" y="0" width="{$graph_width}" height="{$graph_height}"/>

    <g id="horizontalRuler">
      <g transform="matrix(1 0 0 -1 0 {$graph_height - 2})">
        <xsl:for-each select="datum">
          <use x="{(position() -1) * $x_scale}" xlink:href="#week"/>
        </xsl:for-each>
      </g>
      <!-- Question: how do we 'translate' weeks into a month? -->
    </g>
  </g>
</xsl:template>

<xsl:template match="events">
  <g id="events" transform="translate({$graph_margin})">
    <!-- Question: if the text (or date) for two (or more!) events overlap, how
                   do we compensate (ie shift up/down)? -->
    <xsl:for-each select="event">
      <xsl:if test="/graph/data/datum[@week = current()/@week]">
        <g transform="translate({(count(/graph/data/datum[@week = current()/@week]/preceding-sibling::*)) * $x_scale})">
          <use x="0" y="{$graph_margin}" xlink:href="#down-triangle"/>
          <use x="0" y="{$graph_margin + $graph_height}" xlink:href="#up-triangle"/>
          <line x1="0" y1="{$graph_margin}" x2="0" y2="{$graph_margin + $graph_height}"/>
          <text x="0" y="{$graph_margin - 10}"><xsl:value-of select="@comment"/></text>
          <text x="0" y="{$graph_margin + $graph_height + 15}"><xsl:value-of select="@date"/></text>
        </g>
      </xsl:if>
    </xsl:for-each>
  </g>
</xsl:template>

</xsl:stylesheet>