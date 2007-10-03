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
  Expands replaces rdf:resource attributes by CInclude elements to allow later
  processing stages to retrieve those resources and embed them into the RDF document. 
 -->
<xsl:stylesheet version = "1.0"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
                xmlns:doap="http://usefulinc.com/ns/doap#"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:asfext="http://projects.apache.org/ns/asfext#"
                xmlns:cinclude="http://apache.org/cocoon/include/1.0"
                >                
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>
  
  <!-- SourceKibitzer uses rdf:resource to indicate contributors -->
  <xsl:template match="doap:maintainer/@rdf:resource|doap:developer/@rdf:resource|doap:documentor/@rdf:resource|doap:translator/@rdf:resource|doap:tester/@rdf:resource|doap:helperr/@rdf:resource">
      <cinclude:cached-include>
        <xsl:attribute name="src"><xsl:value-of select="."/></xsl:attribute>
      </cinclude:cached-include>
  </xsl:template>
  
  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>