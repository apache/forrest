<?xml version="1.0"?>
<!--
  Copyright 1999-2004 The Apache Software Foundation

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
                xmlns:req="http://apache.org/cocoon/request/2.0"
                xmlns:source="http://apache.org/cocoon/source/1.0">

  <xsl:param name="filepath"/>
  
  <xsl:template match="page">
    <page>
      <source:write create="true">
        <source:source><xsl:value-of select="$filepath"/><xsl:value-of select="meta[@name='filename']/@content"/></source:source>
        <source:path>html</source:path>
        <source:fragment>
          <xsl:call-template name="head"/>
          <xsl:call-template name="body"/>
        </source:fragment>
      </source:write>
    </page>
  </xsl:template>
  
  <xsl:template name="head">
    <head>
      <xsl:apply-templates select="/page/meta"/>
      <xsl:apply-templates select="/page/title"/>
    </head>
  </xsl:template>
  
  <xsl:template name="body">
    <body>
      <xsl:copy-of select="/page/content/html/body/*"/>
    </body>
  </xsl:template>
    
  <xsl:template match="node()|@*" priority="-1">
        <xsl:copy>
              <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
  </xsl:template>
</xsl:stylesheet>

