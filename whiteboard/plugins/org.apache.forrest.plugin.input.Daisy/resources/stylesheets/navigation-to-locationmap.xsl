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
    | Add an edit link into the page content.
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://outerx.org/daisy/1.0#navigationspec"
                xmlns="http://apache.org/forrest/locationmap/1.0"
                xmlns:xi="http://www.w3.org/2001/XInclude" >
                
  <xsl:param name="publisherURL"/>
  <xsl:param name="pathPrefix"/>
  <xsl:param name="navigationID"/>
                
  <xsl:template match="/">
    <locationmap>
  
      <components>
        <matchers default="lm">
          <matcher 
            name="lm" 
            src="org.apache.forrest.locationmap.WildcardLocationMapHintMatcher"/>
        </matchers>
      </components>
      
      <locator>
      
       <xi:include href="locationmap-daisy-include.xml#xpointer(//locationmapInclude/*)"/>
     
       <xsl:apply-templates/>
        
       <match pattern="*.daisy.source">
           <location>
             <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>document?documentId={1}&amp;includeNavigation=false&amp;locale=en_US&amp;version=live</xsl:attribute>
           </location>
       </match>
        
       <match pattern="**/*.daisy.source">
           <location>
             <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>document?documentId={2}&amp;includeNavigation=false&amp;locale=en_US&amp;version=live</xsl:attribute>
           </location>
       </match>
     
       <match pattern="**/*.daisy.img">
           <location>
             <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>blob?documentId={2}&amp;version=live&amp;partType=3</xsl:attribute>
           </location>
       </match>
     
       <match pattern="**.daisy.img">
           <location>
             <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>blob?documentId={1}&amp;version=live&amp;partType=3</xsl:attribute>
           </location>
       </match>
     
       <match pattern="daisy.site.*">
           <location>
             <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>blob?documentId={1}&amp;version=live&amp;partType=1</xsl:attribute>
           </location>
       </match>
     
       <match pattern="*.daisy.rawHTML">
           <location>
             <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>blob?documentId={1}&amp;version=live&amp;partType=4</xsl:attribute>
           </location>
       </match>
      </locator>
    </locationmap>
  </xsl:template>
    
  <xsl:template match="d:doc">    
    <xsl:variable name="path">
      <xsl:for-each select="ancestor::d:group|ancestor::d:doc">
        <xsl:choose>
          <xsl:when test="@nodeId"><xsl:value-of select="@nodeId"/>/</xsl:when>
          <xsl:otherwise>
            <xsl:if test="name()='d:group' and @id"><xsl:value-of select="@id"/>/</xsl:if>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
      <xsl:choose>
        <xsl:when test="@nodeId"><xsl:value-of select="@nodeId"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="@id"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
     <match>
       <xsl:attribute name="pattern"><xsl:value-of select="$pathPrefix"/><xsl:value-of select="$path"/>.daisy.source</xsl:attribute>
       <location>
         <xsl:attribute name="src"><xsl:value-of select="$publisherURL"/>document?documentId=<xsl:value-of select="@id"/>&amp;includeNavigation=false&amp;locale=en_US&amp;version=live</xsl:attribute>
       </location> 
     </match>
     <xsl:apply-templates/>
  </xsl:template>
  
</xsl:stylesheet>
