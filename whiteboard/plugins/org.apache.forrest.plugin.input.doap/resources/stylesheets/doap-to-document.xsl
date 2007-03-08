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
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
                xmlns:doap="http://usefulinc.com/ns/doap#"
                xmlns:asfext="http://projects.apache.org/ns/asfext#"
                >

  <xsl:template match="/">
    <xsl:apply-templates select="doap:Project|rdf:RDF|atom:feed" />
  </xsl:template>

  <xsl:template match="rdf:RDF">
    <xsl:apply-templates select="doap:Project" />
  </xsl:template>

  <xsl:template match="atom:feed">
    <xsl:apply-templates select="atom:entry/atom:content/doap:Project" />
  </xsl:template>

  <xsl:template match="doap:Project">
      <xsl:call-template name="project" />
  </xsl:template>

  <xsl:template name="project">
    <document>
      <xsl:call-template name="header" />
      <xsl:call-template name="body" />
    </document>
  </xsl:template>

  <xsl:template name="header">
    <header>
      <link rel="stylesheet" type="text/css" href="../html/projects.css" />
      <title>Information about <xsl:value-of select="doap:name"/></title>
    </header>
  </xsl:template>

  <xsl:template name="body">
      <body>
        <xsl:call-template name="project-header" />
        <xsl:call-template name="project-summary" />
        
        <section>
          <title>Description</title>
          <p><xsl:value-of select="doap:description"/></p>
        </section>
        
        <xsl:call-template name="project-releases" />
        
     </body>
  </xsl:template>

  <xsl:template match="@rdf:resource">
    <a>
      <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
      <xsl:value-of select="."/>
    </a>
  </xsl:template>
    
  <xsl:template match="doap:programming-language">
    <a>
      <xsl:attribute name="href"><xsl:value-of select="."/>_lang.html</xsl:attribute>
      <xsl:value-of select="."/>
    </a>
    <xsl:if test="not(position() = last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="doap:category">
    <xsl:value-of select="@rdf:resource"/>
    <xsl:if test="not(position() = last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template name="project-header">
      <div class="description">
          <p>
            <xsl:value-of select="doap:shortdesc"/>
          </p>
          <xsl:if test="doap:homepage">
            <p>
              For more information visit 
              <xsl:apply-templates select="doap:homepage/@*" />
            </p>
          </xsl:if>
      </div>
  </xsl:template>

  <xsl:template name="project-summary">
    <section>
      <title>Summary</title>

        <div class="content">
          <table>
               <tr>
                 <td class="left">Programming Languages</td>
                 <td class="right">
                   <xsl:apply-templates select="doap:programming-language" />
                 </td>
               </tr>
               <tr>
                 <td class="left">Categories</td>
                 <td class="right">
                   <xsl:apply-templates select="doap:category" />
                 </td>
               </tr>  
               <tr>
                 <td class="left">Mailing Lists</td>
                 <td class="right">
                   <xsl:apply-templates select="doap:mailing-list/@*" />
                 </td>
               </tr>
               <tr>
                 <td class="left">Bug/Issue Tracker</td>
                 <td class="right">
                   <xsl:apply-templates select="doap:bug-database/@*" />
                 </td>
               </tr>
               <tr>
                 <td class="left">Wiki</td>
                 <td class="right">
                   <xsl:apply-templates select="doap:wiki/@*" />
                 </td>
               </tr>
               <tr>
                 <td class="left">License</td>
                 <td class="right">
                   <xsl:choose>
                     <xsl:when test="doap:license/@rdf:resource = 'http://usefulinc.com/doap/licenses/asl20'">
                       <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License Version 2.0</a>
                     </xsl:when>
                     <xsl:otherwise>
                       <xsl:apply-templates select="doap:license/@*" />
                     </xsl:otherwise>
                   </xsl:choose>
                 </td>
               </tr>    
               <tr>
                 <td class="left">Project Website</td>
                 <td class="right">
                       <xsl:apply-templates select="doap:homepage/@*" />
                 </td>
               </tr>
             </table>
           </div>
     </section>
  </xsl:template>

  <xsl:template name="project-scm">

    <div class="content">
      <xsl:choose>
        <xsl:when test="doap:repository">
          <xsl:for-each select="doap:repository/doap:SVNRepository">
            <table>
              <tr>
                <td class="left">Browse</td>
                <td class="right">
                  <xsl:apply-templates select="doap:browse/@*" />
                </td>
              </tr>
              <tr>
                <td class="left">Checkout</td>
                <td class="right">
                  <pre>svn co <xsl:apply-templates select="doap:location/@rdf:resource" /></pre>
                </td>
              </tr>              
            </table>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <p>No source control information provided.</p>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>

  <xsl:template match="doap:release/doap:Version">
    <tr>
        <td class="title">
            <xsl:value-of select="doap:name" />
        </td>
        <td class="right"><xsl:value-of  select="doap:revision" /></td>
        <td class="right"><xsl:value-of  select="doap:created" /></td>
    </tr>
  </xsl:template>
  
  <xsl:template name="project-releases">
    <section>
      <title>Source and Releases</title>

      <div class="content">
        <xsl:choose>
            <xsl:when test="doap:doanload-page">
                <p>Releases can be downloaded from
                  <xsl:apply-templates select="doap:download-page/@*" />
                  .
                </p>
            </xsl:when>
            <xsl:otherwise>
              <p>No release download page available.</p>
            </xsl:otherwise>
        </xsl:choose>
    </div>
    
    <xsl:choose>
    <xsl:when test="doap:release">
      <p>Most recent releases:</p>

      <div class="content">  
        <table>
         <tr><td>Release</td><td>Version</td><td>Date</td></tr>         
         <xsl:apply-templates select="doap:release/doap:Version" />   
        </table>
     </div>
    </xsl:when>
    <xsl:otherwise>
      <p>No known current releases.</p>
    </xsl:otherwise>
    </xsl:choose>
   
   <p>Access to the source code:</p>
   
   <xsl:call-template name="project-scm" />
  </section>
  </xsl:template>
</xsl:stylesheet>
