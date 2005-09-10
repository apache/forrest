<?xml version="1.0"?>
<!--
  Copyright 2002-2005 The Apache Software Foundation or its licensors,
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
                xmlns:dir="http://apache.org/cocoon/directory/2.0">

  <xsl:param name="category"></xsl:param>                
  
  <xsl:template match="dir:directory">
    <document> 
      <header> 
        <title>Category: <xsl:value-of select="$category"/></title> 
      </header> 
      <body> 
        <section id="overview">
          <title>
            My thoughts on <xsl:value-of select="$category"/>
          </title> 
   
          <xsl:apply-templates select="//dir:file"/>
         
        </section>

      </body>
    </document>
  </xsl:template>
  
  <xsl:template match="dir:file">
   <xsl:if test="./dir:xpath/title">
    
    <xsl:variable name="dir-path">
    <!-- 
      pitiful i can't figure out how to do this without getting spaces polluting it:( 
      which is why it's all crammed together on one line.  embarrassing.    
    -->
      <xsl:for-each select="ancestor::dir:directory"><xsl:if test="position() > 0"><xsl:value-of select="./@name"/>/</xsl:if></xsl:for-each>
    </xsl:variable>
    <p>
      <strong>
        <xsl:element name="a">
          <xsl:attribute name="href">/<xsl:value-of select="$dir-path"/><xsl:value-of select="substring-before(./@name,'.xml')"/>.html</xsl:attribute>
          <xsl:value-of select="./dir:xpath/title"/>
        </xsl:element>
      </strong>
      (<em>Published <xsl:value-of select="./@date"/></em>)
    </p>
    </xsl:if>
  </xsl:template>   
  
  
</xsl:stylesheet>