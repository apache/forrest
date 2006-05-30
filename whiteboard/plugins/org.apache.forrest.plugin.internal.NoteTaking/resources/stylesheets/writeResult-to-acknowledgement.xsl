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
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:dyn="http://exslt.org/dynamic">
  
  <!-- the page for which the note was added -->
  <xsl:param name="path"/>
  
  <xsl:template match="sourceResult">
    <xsl:choose>
      <xsl:when test="execution='success'">
        <xsl:call-template name="success"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="failure"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template name="success">
    <document>
      <header>
        <title>Note Written</title>
      </header>
      <body>
        <p>Your note has been succesfully written. You can now continue to 
        work.</p>
        
        <p>
          <link>
            <xsl:attribute name="href">/<xsl:value-of select="$path"/>.html</xsl:attribute>
            Return to originating page.
          </link>
        </p>
      </body>
    </document>
  </xsl:template>
  
  <xsl:template name="failure">
    <document>
      <header>
        <title>Failed Note Written</title>
      </header>
      <body>
        <warning>There was an error recording your note.</warning>
        
        <p>
          <link>
            <xsl:attribute name="href">/<xsl:value-of select="$path"/>.html</xsl:attribute>
            Return to originating page.
          </link>
        </p>
      </body>
    </document>
  </xsl:template>
</xsl:stylesheet>
