<?xml version="1.0"?>
<!--
  Copyright 2002-2004 The Apache Software Foundation

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

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsltu="http://xstlunit.org/0">

    <xsl:template match="/">
     <xsl:choose>
   	   <xsl:when test="name(child::node())='testsuite'">
         <xsl:apply-templates/>
	   </xsl:when>
  
	   <xsl:otherwise>
	     <document>
	      <header><title>Error in conversion</title></header>
	      <body>
	       <warning>This file is not in testsuite format, please convert manually.</warning>
	      </body>
	     </document>
	   </xsl:otherwise>
     </xsl:choose>
    </xsl:template>
           
    <xsl:template match="testsuite">
        <document>
        <header>Test Suite</header>
        <body>
            <table>
            <th>Test units</th>
            <xsl:apply-templates />
            </table>
        </body>
        </document>
    </xsl:template>
 
    <xsl:template match="node()">
    <!-- FIXME: this should be xsltu:assert -->
        <tr><td><xsl:value-of select="@id"/></td><td><xsl:value-of select="@outcome"/></td></tr>
    </xsl:template>

    
</xsl:stylesheet>
