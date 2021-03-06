<?xml version="1.0" encoding="UTF-8"?>
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
<xsl:stylesheet version = "1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:atom="http://www.w3.org/2005/Atom"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
  xmlns:doap="http://usefulinc.com/ns/doap#"
  xmlns:foaf="http://xmlns.com/foaf/0.1/">
  <xsl:template match="descriptors">
    <rdf:RDF>
      <xsl:apply-templates select="//doap:maintainer"/>
      <xsl:apply-templates select="//doap:developer"/>
      <xsl:apply-templates select="//doap:documenter"/>
      <xsl:apply-templates select="//doap:translator"/>
      <xsl:apply-templates select="//doap:tester"/>
      <xsl:apply-templates select="//doap:helper"/>      
    </rdf:RDF>
  </xsl:template>
  
  <xsl:template match="doap:maintainer">
    <xsl:copy-of select="*"/>
  </xsl:template>
</xsl:stylesheet>
