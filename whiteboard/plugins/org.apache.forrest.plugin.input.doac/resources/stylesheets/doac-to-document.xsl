<?xml version="1.0"?>
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
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/"
    xmlns:doac="http://ramonantonio.net/doac/0.1/"
    exclude-result-prefixes="rdf dc foaf doac"
  >
  <xsl:template match="/">
    <xsl:apply-templates select="rdf:RDF/foaf:Person"/>
  </xsl:template>
  
  <xsl:template match="foaf:Person">
    <document>
      <xsl:call-template name="header" />
      <xsl:call-template name="body" />
     </document>
  </xsl:template>
  
  <xsl:template name="header">
    <header>
      <title>Description-Of-A-Career (DOAC) Information of <xsl:value-of select="foaf:name"/></title>
    </header>
  </xsl:template>
  
  <xsl:template name="body">
    <body>
       <xsl:call-template name="personalInfo" />
       <xsl:call-template name="experience" />
       <xsl:call-template name="education" />
       <xsl:call-template name="skill" />
       <!-- <xsl:call-template name="reference" />
       <xsl:call-template name="publication" /> -->
    </body>
  </xsl:template>

  <xsl:template name="personalInfo">
    <section>
      <title>Personal Information</title>
         <p><img src="{foaf:depiction/@rdf:resource}" height="120" border="0" /></p>
    	 <p><strong>Phone:</strong> <xsl:value-of select="foaf:phone/@rdf:resource"/></p>
         <p><strong>Email:</strong> <xsl:value-of select="substring-after(foaf:mbox/@rdf:resource, 'mailto:')"/></p>
    </section>
  </xsl:template>
    
  <xsl:template name="experience">
    <section>
        <title>Experience</title>
        <xsl:for-each select="doac:experience">
           <section>
              <title><xsl:value-of select="doac:title"/></title>
              <xsl:if test="doac:start-date">
                 <p>From <xsl:value-of select="doac:start-date"/> to <xsl:value-of select="doac:end-date"/></p>
              </xsl:if>
              <xsl:if test="doac:organization">
                 <p><strong>Organization :</strong><xsl:value-of select="doac:organization"/></p>           
       	      </xsl:if>
       	   </section>
       	 </xsl:for-each>
     </section>
  </xsl:template>

  <xsl:template name="education">
      <section>
        <title>Education</title>
           <xsl:for-each select="doac:education">
           	  <section>
                 <title><xsl:value-of select="doac:degree/doac:title"/></title>
                 <p>From <xsl:value-of select="doac:degree/doac:start-date"/> to <xsl:value-of select="doac:degree/doac:end-date"/></p>
                 <p><strong>Organization :</strong><xsl:value-of select="doac:degree/doac:organization"/></p>
              </section>
           </xsl:for-each>
       </section>
  </xsl:template>

   <xsl:template name="skill">
      <section>
        <title>Language Skills</title>
           <xsl:for-each select="doac:skill">
              <section>
                 <xsl:variable name="unwanted" select="'http://ramonantonio.net/doac/0.1/'" />
                 <xsl:variable name="langSkill" select="doac:LanguageSkill/doac:language/@rdf:resource" />
                 <xsl:variable name="reads" select="doac:LanguageSkill/doac:reads/@rdf:resource" />
                 <xsl:variable name="writes" select="doac:LanguageSkill/doac:writes/@rdf:resource" />
                 <xsl:variable name="speaks" select="doac:LanguageSkill/doac:speaks/@rdf:resource" />
                 <title><xsl:value-of select="substring-after($langSkill, $unwanted)"/></title>
                 <p><strong>Reads :</strong><xsl:value-of select="substring-after($reads, $unwanted)"/></p>
                 <p><strong>Writes :</strong><xsl:value-of select="substring-after($writes, $unwanted)"/></p>
                 <p><strong>Speaks :</strong><xsl:value-of select="substring-after($speaks, $unwanted)"/></p>
              </section>
           </xsl:for-each>
       </section>
  </xsl:template>

</xsl:stylesheet>
