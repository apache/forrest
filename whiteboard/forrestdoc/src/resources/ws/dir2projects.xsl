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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method = "html" encoding="Windows-1252" />
  <xsl:template match="/">
    <html>
      <head><link rel="stylesheet" type="text/css" href="styles.css"/>
      </head>
      <body>
        <h3 class="packageListItem">Projects</h3>
        <p class="packageListItem">
          <A HREF="projects-summary.html" TARGET="projects-summaryFrame">Summary</A>
        </p>
        <br/>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="pathelement">
    <xsl:variable name="dir" select="substring(.,2)" />
    <xsl:variable name="url" select="concat(substring(.,2),'/index.html')" />
    <p class="packageListItem">
      <a href="{$url}" TARGET="projects-summaryFrame">
      <xsl:value-of select="$dir" />
      </a>
    </p>
  </xsl:template>
</xsl:stylesheet>
