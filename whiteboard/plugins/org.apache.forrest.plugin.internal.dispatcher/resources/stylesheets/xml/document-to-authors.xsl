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
<!--
This stylesheet contains templates for converting documentv11 to HTML.  See the
imported document2html.xsl for details.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--  Templates for "toc" mode.  This will generate a complete
        Table of Contents for the document.  This will then be used
        by the site2xhtml to generate a Menu ToC and a Page ToC -->
  <xsl:template match="document">
    <div id="content-authors">
      <xsl:apply-templates select="header/authors"/>
    </div>
  </xsl:template>
  <xsl:template match="header/authors">
    <xsl:if test="person">
        <xsl:for-each select="person">
          <div class="author">
            <div class="name">
              <xsl:value-of select="@name"/>
            </div>
            <div class="email">
              <xsl:value-of select="@email"/>
            </div>
          </div>
        </xsl:for-each>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>