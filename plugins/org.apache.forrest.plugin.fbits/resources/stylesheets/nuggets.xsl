<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
  as applicable.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  xmlns:forrest="http://apache.org/forrest/templates/1.0"  
  xmlns:xi="http://www.w3.org/2001/XInclude"
  >
  <xsl:template name="html-meta">
    <meta name="Generator" content="Apache Forrest"/>
    <meta name="Forrest-version">
      <xsl:attribute name="content">
        <xsl:value-of select="//info/forrest-version"/>
      </xsl:attribute>
    </meta>
    <meta name="Forrest-skin-name">
      <xsl:attribute name="content">
        <xsl:value-of select="//info/project-skin"/>
      </xsl:attribute>
    </meta>
  </xsl:template>
  
  <xsl:template name="feedback">
    <div id="feedback">
      <xsl:value-of select="$config/feedback"/>
      <xsl:choose>
        <xsl:when test="$config/feedback/@href and not($config/feedback/@href='')">
          <a id="feedbackto">
            <xsl:attribute name="href">
              <xsl:value-of select="$config/feedback/@href"/>
              <xsl:value-of select="$path"/>
            </xsl:attribute>
            <xsl:value-of select="$config/feedback/@to"/>
          </a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$config/feedback/@to"/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>
  </xsl:stylesheet>