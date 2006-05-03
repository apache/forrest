<?xml version="1.0"?>
<!--
  Copyright 1999-2005 The Apache Software Foundation or its licensors,
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
                xmlns:dir="http://apache.org/cocoon/directory/2.0"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:DC="http://purl.oclc.org/dc/documents/rec-dces-199809.htm#">

  <xsl:param name="gallery-dir">gallery</xsl:param>
  <xsl:template match="/">
    <document> 
      <header> 
        <title>Photo Gallery Index</title> 
      </header> 
      <body> 
        <section id="overview">
          <title>
            Photo Albums 
          </title> 
          <p>List of all available albums in this gallery, showing the first
          photo from each. Select the photo to access that album.</p>
          <xsl:apply-templates select="/dir:directory//dir:directory"/>
        </section>
      </body>
    </document>
  </xsl:template>
  
  <xsl:template match="dir:directory">
    <section>
      <title>   
        <xsl:value-of select="./dir:file/dir:xpath/title"/>   
      </title>
      
       <a>
        <xsl:attribute name="href">./<xsl:value-of select="./@name"/>/index.html</xsl:attribute>
        <xsl:choose>
          <xsl:when test="count(./dir:file[contains(@name,'.JPG')]) > 0">
          <img>
           <xsl:attribute name="src"><xsl:value-of select="./@name"/>/<xsl:value-of select="substring-before(./dir:file[contains(@name,'.JPG')]/@name,'.')"/>.thumb.jpg</xsl:attribute>
          </img>   
        </xsl:when>
        <xsl:otherwise>
          <img>
            <xsl:attribute name="src"><xsl:value-of select="./@name"/>/<xsl:value-of select="substring-before(./dir:file[contains(@name,'.jpg')]/@name,'.')"/>.thumb.jpg</xsl:attribute>
          </img>
        </xsl:otherwise>
       </xsl:choose>
       
       </a>
     <p>
        <em><xsl:value-of select="./dir:file/dir:xpath/description"/></em>
      </p>
    </section>

  </xsl:template>   

  
  
</xsl:stylesheet>

