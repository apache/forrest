<?xml version="1.0" encoding="utf-8"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output indent="yes" method="xml"/>
  <xsl:template match="projects">
    <html>
      <body bgcolor="#999999">
        <h1>Apache Project Logos</h1>
        <p>
          The SVN module that contains these logos and the code to make them is
          in the forrest repository under 'tools/logos'. If you need a new logo,
          or want to change some params, look at the others for examples, change
          the projects.xml file accordingly, then run 'ant'.
          <br/>
          <a href="#moreinfo">[more info]</a>
        </p>
        <p>
          Note: Please be aware of the ASF
          <a href="http://www.apache.org/foundation/marks/">Apache Trademark Policy</a>.
        </p>
        <h2>Projects</h2>
        <ul>
          <xsl:for-each select="project">
            <li><a href="#{@id}">
              <xsl:value-of select="@name" /> - <xsl:value-of select="@id" /></a></li>
          </xsl:for-each>
        </ul>
        <xsl:apply-templates select="project"/>
        <h2 id="moreinfo">HOWTO</h2>
        <p>
          Basically these are the infos:
          <ul>
            <li><b>id:</b> directory</li>
            <li><b>url:</b> i n c u b a t o r</li>
            <li><b>logo:</b> apache</li>
            <li><b>color:</b> black</li>
            <li><b>bgcolor:</b> white</li>
            <li><b>scale:</b> normal</li>
          </ul>
          where:
          <ul>
            <li><b>id:</b> [unixname]</li>
            <li><b>url:</b> [secondleveldomain]</li>
            <li><b>logo:</b> [apache | apache-jakarta | apache-resource | apache-httpd]</li>
            <li><b>color:</b> [fontcolor]</li>
            <li><b>bgcolor:</b> [backgroundcolor | $forrest | $maven]</li>
            <li><b>scale:</b> [small | normal | big]</li>
          </ul>
          The above example is like this in projects.xml:
          <pre>
  &lt;project
    id="directory"
    logo="apache"
    name="Directory"
    url="i n c u b a t o r"
    bgcolor="white"
    color="black"
    scale="normal" /&gt;
          </pre>
          In particular the Incubator Project logos are an example of some of
          the possible combinations.
        </p>
        <p>
          If you need a different logo type, you can add the xsl template to the
          'templates' dir and reference it by filename (minus the
          extension).
        </p>
        <p>
          In alternative, ask someone that knows how to do it
          and send the SVG version of a logo template.
        </p>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="project">
    <h2 id="{@id}">
      <xsl:value-of select="@name" />
    </h2>
    <img>
      <xsl:attribute name="src">apache-<xsl:value-of select="@id" />.png</xsl:attribute>
    </img>
    <br/><a>
    <xsl:attribute name="href">apache-<xsl:value-of select="@id" />.svg</xsl:attribute>apache-<xsl:value-of select="@id" />.svg</a>
    <ul>
      <li><b>id: </b>
        <xsl:value-of select="@id" /></li>
      <li><b>url: </b>
        <xsl:value-of select="@url" /></li>
      <li><b>logo: </b>
        <xsl:value-of select="@logo" /></li>
      <li><b>color: </b>
        <xsl:value-of select="@color" /></li>
      <li><b>bgcolor: </b>
        <xsl:value-of select="@bgcolor" /></li>
      <li><b>scale: </b>
        <xsl:value-of select="@scale" /></li>
    </ul>
    <hr/>
  </xsl:template>
</xsl:stylesheet>
