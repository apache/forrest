<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation or its licensors,
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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" 
               version="1.0" 
               omit-xml-declaration="no" 
               indent="yes"
               doctype-public="-//APACHE//DTD Documentation V1.2//EN"
               doctype-system="http://forrest.apache.org/dtd/document-v12.dtd" />
   
   <xsl:template match="/">
     <document>
       <header>
         <title>Site Linkmap</title>
       </header>
       <body>
        <section>
          <title>Table of Contents</title>
           <xsl:apply-templates select="*[not(self::site)]" />        
        </section>
       </body>
     </document>       
   </xsl:template>     

     <xsl:template match="*">
        <xsl:if test="@label">
          <li><a href="{@href}"><xsl:value-of select="@label" /></a>&#160;&#160;&#160;_________________________&#160;&#160;<em><xsl:value-of select="name(.)" /></em></li>
        </xsl:if>
        
        <xsl:if test="*">
         <ul> 
	     <xsl:apply-templates/>        
         </ul>
        </xsl:if>
        
     </xsl:template>

</xsl:stylesheet>

