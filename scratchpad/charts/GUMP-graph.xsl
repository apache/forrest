<?xml version="1.0"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xlink="http://www.w3.org/1999/xlink">

<xsl:variable name="graph_width">500</xsl:variable>
<xsl:variable name="graph_height">150</xsl:variable>
<xsl:variable name="graph_margin">50</xsl:variable>

<xsl:variable name="num_points" select="count(/graph/data/datum)"/>

<xsl:variable name="x_scale" select="$graph_width div ($num_points - 1)"/>
<xsl:variable name="y_scale" select="$graph_height div 100"/>

<xsl:template match="graph">
  <svg
    width="{$graph_margin + $graph_width + $graph_margin}"
    height="{$graph_margin + $graph_height + $graph_margin}">

    <title>Forrest GUMP Graphing</title>

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
        fill: rgb(0,192,0);
        stroke: #000088;
        stroke-width: 1px; }
      #graph-dependancy {
        fill: rgb(255,255,90);
        stroke: #000088;
        stroke-width: 1px; }
      #graph-failed {
        fill: rgb(255,0,0);
        stroke: #000088;
        stroke-width: 1px; }
      #horizontalRuler {
        fill: #000088; }
    </style>

    <defs>
      <line id="week" class="tick" x1="0" y1="0" x2="0" y2="3"/>
      <line id="month" class="tick" x1="0" y1="0" x2="0" y2="5"/>
    </defs>

    <xsl:apply-templates select="data"/>

  </svg>
</xsl:template>

<xsl:template match="data">
  <g transform="translate({$graph_margin} {$graph_margin})">

    <rect id="border" x="0" y="0" width="{$graph_width}" height="{$graph_height}"/>

    <g id="graph" transform="matrix(1 0 0 -1 0 {$graph_height})">
      <!-- Those projects which failed due to a dependancy problem -->
      <xsl:variable name="dependancy">
        <xsl:value-of select="$graph_width"/><xsl:text>,0 0,0</xsl:text>
        <xsl:for-each select="datum">
          <xsl:text> </xsl:text>
          <xsl:value-of select="(position() - 1) * $x_scale"/>
          <xsl:text>,</xsl:text>
          <xsl:value-of select="(@dependancy + @failed) * ($graph_height div (@passed + @dependancy + @failed))"/>
        </xsl:for-each>
      </xsl:variable>
      <polygon id="graph-dependancy" points="{$dependancy}"/>

      <!-- Those projects which failed to compile -->
      <xsl:variable name="failed">
        <xsl:value-of select="$graph_width"/><xsl:text>,0 0,0</xsl:text>
        <xsl:for-each select="datum">
          <xsl:text> </xsl:text>
          <xsl:value-of select="(position() - 1) * $x_scale"/>
          <xsl:text>,</xsl:text>
          <xsl:value-of select="@failed * ($graph_height div (@passed + @dependancy + @failed))"/>
        </xsl:for-each>
      </xsl:variable>
      <polygon id="graph-failed" points="{$failed}"/>
    </g>

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

</xsl:stylesheet>