<?xml version="1.0"?>
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
<xsl:stylesheet version="1.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
    <xsl:apply-templates select="//document"/>
  </xsl:template>
  <xsl:template match="document">
    <document>
      <xsl:apply-templates select="header"/>
      <xsl:apply-templates select="body"/>
    </document>
  </xsl:template>
  <xsl:template match="header">
    <properties>
      <xsl:apply-templates/>
    </properties>
  </xsl:template>
<!-- The anakia2markdown.xsl seems to require everything enclosed in a raw section. -->
  <xsl:template match="body">
    <body>
      <xsl:copy-of select="@*"/>
      <section>
        <xsl:apply-templates/>
      </section>
    </body>
  </xsl:template>
  <xsl:template match="tbody">
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="body//link"><a>
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates/></a>
  </xsl:template>
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
