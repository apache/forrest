<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation or its licensors,
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
                xmlns:req="http://apache.org/cocoon/request/2.0">
                
  <xsl:param name="queryString"/>
  
  <xsl:template match="/page">
    <html>
      <head>
        <title>Save Result</title>
      </head>
      <body>
        <xsl:apply-templates select="sourceResult"/>
        
        <a>
          <xsl:variable name="URI"><xsl:value-of select="substring-after($queryString, 'filename=')"/></xsl:variable>
          <xsl:attribute name="href">/<xsl:value-of select="$URI"/></xsl:attribute>
          Back to Source Page
        </a>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="sourceResult">
    <xsl:choose>
      <xsl:when test="execution='success'">
        <h1>File Written Succesfully</h1>
      </xsl:when>
      <xsl:otherwise>
        <h1>Problem Writing File</h1>
      </xsl:otherwise>
    </xsl:choose>
    
    <dl>
      <dt>Message</dt>
      <dd><xsl:value-of select="message"/></dd>
      
      <dt>Behaviour</dt>
      <dd><xsl:value-of select="behaviour"/></dd>
      
      <dt>Action</dt>
      <dd><xsl:value-of select="action"/></dd>
      
      <dt>Source</dt>
      <dd><xsl:value-of select="source"/></dd>
    </dl>
  </xsl:template>
  
  <xsl:template match="node()|@*" priority="-1">
        <xsl:copy>
              <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
