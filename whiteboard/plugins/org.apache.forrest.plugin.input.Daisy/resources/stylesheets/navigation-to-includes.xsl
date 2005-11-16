<?xml version="1.0" encoding="UTF-8"?>
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

<!--+
    | Create a version of the navigation document that excludes all nodes that 
    | only contain imports and expand those imports.
    |
    | This is a workaround for the fact that Daisy Navigation documents cannot
    | group content in the menu without adding something to the directory.
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://outerx.org/daisy/1.0#navigationspec"
                xmlns="http://apache.org/forrest/locationmap/1.0"
                xmlns:xi="http://www.w3.org/2001/XInclude">
                
  <xsl:param name="publisherURL"/>
                
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="d:group[d:import]">
    <!-- ignore groups with only imports -->
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="d:import">
    <xi:include>
      <xsl:attribute name="href">cocoon://daisy.navigation.<xsl:value-of select="@docId"/></xsl:attribute>
    </xi:include>
  </xsl:template>

  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>
