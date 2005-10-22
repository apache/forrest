<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version = "1.0" 
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
                xmlns:doap="http://usefulinc.com/ns/doap#"
                xmlns:asfext="http://projects.apache.org/ns/asfext#"
                >
                
  <xsl:param name="includePageHeader">true</xsl:param>

  <xsl:output method="html" 
              doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" 
              doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
              indent="yes" />
  
  <xsl:template match="rdf:RDF">
    <xsl:apply-templates select="doap:Project"/>
  </xsl:template>
  
  <xsl:template match="atom:feed">
    <xsl:apply-templates select="atom:entry/atom:content/doap:Project"/>
  </xsl:template>

  <xsl:template match="doap:Project">
      <xsl:call-template name="project" />
  </xsl:template>

  <xsl:template name="project">
    <html xmlns="http://www.w3.org/TR/xhtml1">
      <xsl:call-template name="header" />
      <xsl:call-template name="body" />
    </html>
  </xsl:template>

  <xsl:template name="header">
      <head>
      <link rel="stylesheet" type="text/css" href="../html/projects.css" />
        <title>Information about <xsl:value-of select="doap:name"/></title>
      </head>
  </xsl:template>
  
  <xsl:template name="body">    
      <body>
        <table width="100%" border="0" cellspacing="0">
          <xsl:if test="includePageHeader = 'true'">
            <xsl:call-template name="page-header" />
          </xsl:if>
          <xsl:call-template name="project-details" />
        </table>
      </body>
  </xsl:template>
  
  <xsl:template name="page-header">
    <tr>
      <td>
        <a href="http://www.apache.org/">
          <img src="http://www.apache.org/images/asf_logo_wide.gif" 
               alt="The Apache Software Foundation" border="0"/>
        </a>
        <hr noshade="noshade" size="1" />
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="section-header">
    <xsl:param name="title" />
    <h2>
      <img src="http://www.apache.org/images/redarrow.gif" />
      <xsl:value-of select="$title" />
    </h2>
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
    <a>
      <xsl:attribute name="href"><xsl:value-of select="./@rdf:resource" /></xsl:attribute>
      <xsl:value-of select="substring-after(@rdf:resource, 'category/')"/>
    </a>
    <xsl:if test="not(position() = last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="asfext:pmc">
    <!-- XXX: Fix this to get the real name of the PMC -->
    PMC
    <a>
      <xsl:attribute name="href"><xsl:value-of select="./@rdf:resource" /></xsl:attribute>
      <xsl:value-of select="substring-before(substring-after(@rdf:resource, 'http://'),'.apache.org')"/>
    </a>
    <xsl:if test="not(position() = last())">
      <xsl:text>, </xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template name="project-details">
    <tr>
      <td>
        <xsl:call-template name="project-header" />
        <xsl:call-template name="project-summary" />
        <xsl:call-template name="project-releases" />
        <xsl:if test="asfext:mailing-list">
          <xsl:call-template name="detailed-mailing-lists" />
        </xsl:if>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="project-header">
    <div class="header">
      <h1><xsl:value-of  select="doap:name" /></h1>
    </div>
      <div class="description">
        <p>
          <xsl:value-of select="doap:description"/>
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
    <xsl:call-template name="section-header">
      <xsl:with-param name="title" select="'Summary'" />
    </xsl:call-template>

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
         <td class="left">Project Management Committee</td>
         <td class="right">
           <xsl:apply-templates select="asfext:pmc" />
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
   </xsl:template>
   
  <xsl:template match="asfext:mailing-list/asfext:Mail-list">
  <tr><td class="title"><xsl:apply-templates select="doap:name"/></td>
    <td class="right"><xsl:value-of select="doap:description"/></td>
    <td class="right"><a><xsl:attribute name="href">
      <xsl:choose>
      <xsl:when test="asfext:subscribe">
        <xsl:value-of select="asfext:subscribe/@rdf:resource"/>
      </xsl:when>
      <xsl:otherwise>
        mailto:<xsl:value-of select="substring-before(doap:name,'@')"/>-subscribe@<xsl:value-of select="substring-after(doap:name,'@')"/>
      </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>Subscribe</a></td>
    <td class="right"><a><xsl:attribute name="href">
    <xsl:value-of select="asfext:archives/@rdf:resource"/>
    </xsl:attribute>Archives</a></td>
    </tr>
  </xsl:template>  

  <xsl:template name="detailed-mailing-lists">
    <xsl:call-template name="section-header">
      <xsl:with-param name="title" select="'Mailing List Details'" />
    </xsl:call-template>
    <table><tr><td>Name</td><td>Description</td><td>Subscribe</td>
    <td>Archives</td></tr>
    <xsl:apply-templates select="asfext:mailing-list/asfext:Mail-list" />
    </table>
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
    <xsl:call-template name="section-header">
      <xsl:with-param name="title" select="'Source and Releases'" />
    </xsl:call-template>
      
    <xsl:if test="doap:download-page">
      <div class="content">
      <p>Releases can be downloaded from
        <xsl:apply-templates select="doap:download-page/@*" />
        .
      </p>
      </div>
    </xsl:if>
    
    <p>Most recent releases:</p>
    
      <div class="content">              
      <table>
       <tr><td>Release</td><td>Version</td><td>Date</td></tr>         
       <xsl:apply-templates select="doap:release/doap:Version" />   
          </table>
      </div>
    
   <p>Access to the source code:</p>
   
   <xsl:call-template name="project-scm" />
    
    </xsl:template>
</xsl:stylesheet>
