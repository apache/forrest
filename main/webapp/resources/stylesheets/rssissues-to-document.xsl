<?xml version='1.0'?>
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- Handle JIRA system maintenance note when provided in HTML format -->
  <xsl:template match="html">
    <document>
      <header>
        <title>Open issues for the next release</title>
      </header>
      <body>
        <note>
          <xsl:copy-of select="body/*" />
        </note>
      </body>
    </document>
  </xsl:template>

  <xsl:template match="rss">
    <document>
      <header>
        <title>Open issues for the next release</title>
      </header>
      <body>
        <note>
          These are the top 25 open issues for the next release that are
          scheduled in our
          <link href="http://forrest.apache.org/issues.html">issue tracking
          system</link> (see
          <link href="http://issues.apache.org/jira/secure/IssueNavigator.jspa?pid=12310000&amp;resolutionIds=-1&amp;tempMax=1000&amp;reset=true">all</link>
          open issues). The listing below is regenerated on each Forrest run.
        </note>
        <xsl:apply-templates select="channel/item"/>
      </body>
    </document>
  </xsl:template>
  <xsl:template match="item">
    <section>
      <title><xsl:value-of select="title" disable-output-escaping="yes"/></title>
      <p>
        <link href="{link}">
        <xsl:value-of select="link"/>
        </link>
      </p>
<!--<p><xsl:value-of select="description" disable-output-escaping="yes" /></p>-->
      <p>
        <xsl:value-of select="description" />
      </p>
    </section>
  </xsl:template>
</xsl:stylesheet>
