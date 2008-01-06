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
  xmlns:dc="http://purl.org/dc/elements/1.1/"
  xmlns:foaf="http://xmlns.com/foaf/0.1/">

 <xsl:template name="replaceString">
   <xsl:param name="string" />
   <xsl:param name="old" />
   <xsl:param name="new" />
     <xsl:choose>
       <xsl:when test="contains($string, $old)">
           <xsl:value-of select="substring-before($string, $old)"/><xsl:value-of
 select="$new" /><xsl:call-template name="replaceString">
                   <xsl:with-param name="string"><xsl:value-of select="substring-after($string, $old)" /></xsl:with-param>
                   <xsl:with-param name="new"><xsl:value-of select="$new" /></xsl:with-param>
                   <xsl:with-param name="old"><xsl:value-of select="$old" /></xsl:with-param>
                 </xsl:call-template>
       </xsl:when>
       <xsl:otherwise>
       <xsl:value-of select="$string" />
       </xsl:otherwise>
     </xsl:choose>
 </xsl:template>


 <xsl:template name="json-escape">
     <xsl:param name="text"/>
          <xsl:call-template name="replaceString">
                   <xsl:with-param name="string">
                       <xsl:call-template name="replaceString">
                          <xsl:with-param name="string"><xsl:value-of select="$text" /></xsl:with-param>
                          <xsl:with-param name="new">\\</xsl:with-param>
                          <xsl:with-param name="old">\</xsl:with-param>
                        </xsl:call-template></xsl:with-param>
                  <xsl:with-param name="new">\"</xsl:with-param>
                   <xsl:with-param name="old">"</xsl:with-param>
           </xsl:call-template>
</xsl:template>


  <xsl:template match="/">{ "items": [<xsl:apply-templates select="descriptors"/>]} 
  </xsl:template>
  
  <xsl:template match="descriptors"><xsl:apply-templates select="descriptor"/></xsl:template>
  
  <xsl:template match="descriptor">
    {<xsl:apply-templates select="doap:Project|rdf:RDF|atom:feed"/>
    }<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>

  <xsl:template match="atom:feed"><xsl:apply-templates select="atom:entry/atom:content/doap:Project"/></xsl:template>
  
  <xsl:template match="rdf:RDF"><xsl:apply-templates select="doap:Project"/></xsl:template>
  
  <xsl:template match="doap:Project">
    "label":"<xsl:call-template name="json-escape"><xsl:with-param name="text" select="doap:name"/></xsl:call-template>",
    <xsl:call-template name="categories"/>
    <xsl:call-template name="contributors"/>
    <xsl:call-template name="programming-language"/>
    <xsl:apply-templates select="doap:name|doap:shortdesc|doap:homepage"/>
  </xsl:template>
  
  <xsl:template match="doap:homepage">
    "<xsl:call-template name="json-escape"><xsl:with-param name="text" select="local-name(.)"/></xsl:call-template>":"<xsl:apply-templates select="@rdf:resource"/>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
  
  <xsl:template name="contributors">
    <xsl:if test="doap:documentor|doap:maintainer|doap:developer|doap:helper">
      "person" : [<xsl:apply-templates select="doap:maintainer|doap:documentor|doap:developer|doap:helper"/>],
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:documentor|doap:maintainer|doap:developer|doap:helper">
    <xsl:apply-templates select="foaf:Person"/><xsl:if test="following-sibling::doap:maintainer|following-sibling::doap:documentor|following-sibling::doap:developer|following-sibling::doap:helper">,</xsl:if>
  </xsl:template>
  
  <xsl:template match="foaf:Person">
    "<xsl:call-template name="json-escape"><xsl:with-param name="text" select="foaf:name"/></xsl:call-template>"<xsl:if test="not(position()=last())">,</xsl:if>
  </xsl:template>
  
  <xsl:template name="categories">
    <xsl:if test="doap:category">
      "category" : [<xsl:apply-templates select="doap:category"/>],
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:category">
    <xsl:variable name="category" select="@rdf:resource"/>
    "<xsl:choose>
          <xsl:when test="//descriptors/categories/categories/doap:category[@rdf:resource = $category]/@dc:title">
            <xsl:value-of select="//descriptors/categories/categories/doap:category[@rdf:resource = $category]/@dc:title"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="@dc:title">
                <xsl:value-of select="@dc:title"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="@rdf:resource"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:otherwise>
        </xsl:choose>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
  
  <xsl:template name="programming-language">
    <xsl:if test="doap:programming-language">
      "programming-language" : [<xsl:apply-templates select="doap:programming-language"/>],
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:programming-language">
    "<xsl:call-template name="json-escape"><xsl:with-param name="text" select="."/></xsl:call-template>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:*">
    "<xsl:call-template name="json-escape"><xsl:with-param name="text" select="local-name(.)"/></xsl:call-template>":"<xsl:call-template name="json-escape"><xsl:with-param name="text" select="normalize-space(.)"/></xsl:call-template>"<xsl:if test="not(position()=last())">, </xsl:if>
  </xsl:template>
    
</xsl:stylesheet>
