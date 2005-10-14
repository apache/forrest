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
  xmlns="http://apache.org/forrest/locationmap/1.0"
  xmlns:lm="http://apache.org/forrest/locationmap/1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  

   <xsl:output method="xml" indent="yes" />
   
   <xsl:param name="plugin-name"/>
   
   
   <xsl:template match="lm:locationmap">
    <locationmap>
      <xsl:apply-templates/>
    </locationmap>
   </xsl:template>
   


   <xsl:template match="lm:locator">
     <locator>
       <xsl:apply-templates/>
       <xsl:element name="select">
           <xsl:element name="mount">
             <xsl:attribute name="src">{forrest:plugins}/<xsl:value-of select="$plugin-name"/>/src/documentation/content/locationmap.xml</xsl:attribute> 
           </xsl:element>
       </xsl:element>
     </locator>
    </xsl:template>
    

  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>
  
</xsl:stylesheet>

