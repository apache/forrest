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
<xsl:stylesheet version="1.0"  
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:foaf="http://xmlns.com/foaf/0.1/"
>

<xsl:template match="/">
<document>
   <xsl:call-template name="header" />
   <xsl:call-template name="body" />
</document>
</xsl:template>

<xsl:template name="header">
   <header>
   <title>Index and Explore FOAF files</title>
   </header>
</xsl:template>

<xsl:template name="body">
   <body>
   <section>
      <title>FOAF Index</title>
      <p>To list and explore FOAF data here, edit the <code>samples/foafIndex.xml</code> and
      add the URIs of the FOAF documents.</p>
      <p>Usage:</p>
         <source>
            <![CDATA[
   <people>
      <person id="johnDoe" location="http://example.org/john/foaf.rdf" />
      <person id="janeDoe" location="http://example.org/jane/foaf.rdf" />
   </people>]]>
        </source>
        
      <p>The following list is generated from the entries listed in <code>samples/foafIndex.xml</code>.</p>
      <p><ul>
      <xsl:for-each select="people/person">
         <xsl:variable name='target'>
         <xsl:value-of select="@location"/>
         </xsl:variable>
  	     <li><a>
            <xsl:attribute name="href">person-<xsl:value-of select="substring-after ($target, 'http://')"/>.html</xsl:attribute>
             <xsl:value-of select="@id"/>
         </a></li>   
      </xsl:for-each>
      </ul></p>
   </section>	
   </body>
</xsl:template>

</xsl:stylesheet>
