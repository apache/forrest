<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  license agreements.  See the NOTICE file distributed with
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

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <xsl:param name="includeImplementationNotes">true</xsl:param>
    <xsl:param name="includeStepsSummary">true</xsl:param>

 <xsl:template match="/">
  <xsl:apply-templates select="//useCases"/>
 </xsl:template>

 <xsl:template match="useCases">
  <document>
   <header>
    <title>
    <xsl:choose>
     <xsl:when test="title">
       <xsl:value-of select="title"/>
     </xsl:when>
     <xsl:otherwise>
       <xsl:text>Use Cases</xsl:text>
     </xsl:otherwise>
    </xsl:choose>
    </title>
   </header>
   <body>
    <xsl:apply-templates select="useCase"/>
   </body>
  </document>
 </xsl:template>

 <xsl:template match="useCase">
  <section>
    <xsl:apply-templates/>
  </section>
 </xsl:template>
 
 <xsl:template match="steps"> 
 
   <xsl:if test="$includeStepsSummary='true'">  
     <section>
       <title>Summary</title>
       <ol class="steps">
         <xsl:apply-templates mode="stepIndex"/>
       </ol>
     </section>
   </xsl:if>
   <section>
     <title>Details</title>
     <table>
       <tr>
         <th>Step</th>
         <th>Description</th>
         <th>Result</th>
         <th>Status</th>
       </tr>
       <xsl:apply-templates mode="userDocs"/>
     </table>
   </section>
   
   <xsl:if test="$includeImplementationNotes='true'">   
     <section>
       <title>Implementation Notes</title>
       <table>
         <tr>
           <th>Step</th>
           <th>Notes</th>
         </tr>
         <xsl:apply-templates mode="devDocs"/>
       </table>
     </section>
   </xsl:if>
 </xsl:template>
 
 <xsl:template match="step" mode="stepIndex">
   <li>
     <strong><xsl:value-of select="title"/></strong>     
     <xsl:if test="@required='false'">
       <xsl:text> (Optional)</xsl:text>
     </xsl:if>
   </li>
 </xsl:template>
 
 <xsl:template match="step" mode="userDocs">
   <tr>
     <td>
       <xsl:number format="1.1.1.1.1.1.1" count="step" level="single"/><xsl:text>. </xsl:text><xsl:value-of select="title"/>
       <xsl:if test="@required='false'">
         <br/><xsl:text>(Optional)</xsl:text>
       </xsl:if>
     </td>
     <td><xsl:apply-templates select="description"/></td> 
     <td><xsl:apply-templates select="result"/></td>
     <td>
       <xsl:choose>
         <xsl:when test="not(fixme)">Implemented</xsl:when>
         <xsl:when test="fixme[@priority='Blocker']"><warning>Not Implemented</warning></xsl:when>
         <xsl:otherwise>
           Implemented with fixmes:-<br/>
         </xsl:otherwise>
       </xsl:choose>
       <xsl:if test="fixme[@priority='Blocker']">
         Blockers: <xsl:value-of select="count(fixme[@priority='Blocker'])"/> <br/>
       </xsl:if>
       <xsl:if test="fixme[@priority='High']">
         High: <xsl:value-of select="count(fixme[@priority='High'])"/> <br/>
       </xsl:if>
       <xsl:if test="fixme[@priority='Low']">
         Low: <xsl:value-of select="count(fixme[@priority='Low'])"/> <br/>
       </xsl:if>
       <xsl:if test="fixme[@priority='Enhancements']">
         Enhancements: <xsl:value-of select="count(fixme[@priority='Enhancement'])"/> <br/>
       </xsl:if>
     </td>
   </tr>
 </xsl:template>
 
 <xsl:template match="step" mode="devDocs">
   <tr>
     <td><xsl:number format="1.1.1.1.1.1.1" count="step" level="single"/><xsl:text>. </xsl:text><xsl:value-of select="title"/></td>
     <td>
       <xsl:apply-templates select="implementation/description"/><br/>
       <xsl:apply-templates select="fixme[@priority='Blocker']"/>
       <xsl:apply-templates select="fixme[@priority='High']"/>
       <xsl:apply-templates select="fixme[@priority='Low']"/>
       <xsl:apply-templates select="fixme[@priority='Enhancement']"/>
     </td> 
   </tr>
 </xsl:template>
 
 <xsl:template match="description">
     <xsl:apply-templates/>
 </xsl:template>
 
 <xsl:template match="result">
     <xsl:apply-templates/>
 </xsl:template>  
 
 <xsl:template match="implementation">
     <xsl:apply-templates select="description"/>
 </xsl:template>
 
 <xsl:template match="fixme">
   <fixme>
     <xsl:attribute name="author"><xsl:value-of select="@priority"/></xsl:attribute>
     <xsl:value-of select="."/>
   </fixme>
 </xsl:template>

  <xsl:template match="@*|*|text()|processing-instruction()|comment()">
    <xsl:copy>
      <xsl:apply-templates select="@*|*|text()|processing-instruction()|comment()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
