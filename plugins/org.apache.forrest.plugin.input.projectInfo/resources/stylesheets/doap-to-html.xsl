<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version = "1.0" 
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
                xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
                xmlns:doap="http://usefulinc.com/ns/doap#">
  
  <xsl:template match="rdf:RDF">
    <xsl:apply-templates select="doap:Project"/>
  </xsl:template>
  
  <xsl:template match="atom:feed">
    <xsl:apply-templates select="atom:entry/atom:content/doap:Project"/>
  </xsl:template>

  <xsl:template match="doap:Project">
    <html>
      <head>
        <link rel="stylesheet" href="projects.css"/>
        <title>Information about Apache <xsl:value-of select="doap:name"/></title>
      </head>
      <xsl:call-template name="body"/>
    </html>
  </xsl:template>
  
  <xsl:template name="body">    
      <body>
        <div class="project-details">
          <xsl:call-template name="project-header"/>
          <xsl:call-template name="project-description"/>
          <xsl:call-template name="project-summary"/>
          <xsl:call-template name="project-repositories"/>
          <xsl:call-template name="project-issues"/>
          <xsl:call-template name="project-maillists"/>
          <xsl:call-template name="project-releases"/>
        </div>
      </body>
  </xsl:template>
  
  <xsl:template name="project-header">
    <div class="header">
      <h1>
        <xsl:text>Apache </xsl:text> <xsl:value-of  select="doap:name" />
      </h1>
    </div>
   </xsl:template>
   
   <xsl:template name="project-description">
      <div class="description">
        <p>
          <xsl:value-of select="doap:description"/>
        </p>
      </div>
   </xsl:template>
   
   <xsl:template name="project-summary">
      <h2>
        <img src="http://www.apache.org/images/redarrow.gif" />
        <a><xsl:attribute name="name">summary</xsl:attribute></a>
        Summary
      </h2>
      <div class="content">
        <table width="100%">
          <tr>
            <th class="left">Project Website</th>
            <td class="right">
              <xsl:apply-templates  select="doap:homepage/@*"/>
            </td>
          </tr>
          <tr>
            <th class="left">Programming Languages</th>
            <td class="right">
              <xsl:apply-templates select="doap:programming-language"/>
            </td>
          </tr>
          <tr>
            <th class="left">Download Page</th>
            <td class="right">
              <xsl:apply-templates  select="doap:download-page/@*"/>
            </td>
          </tr>
       </table>
     </div>
   </xsl:template>
   
   <xsl:template name="project-repositories">
     <xsl:if test="doap:repository">
        <h2>
          <img src="http://www.apache.org/images/redarrow.gif" />
          <a><xsl:attribute name="name">repos</xsl:attribute></a>
          Source Repositories
        </h2>
        <div class="content">
          <xsl:choose>
            <xsl:when test="doap:repository/doap:SVNRepository">
              <table>
                <tr>
                  <th>Browse URL</th>
                  <td><xsl:apply-templates select="doap:repository/doap:SVNRepository/doap:browse/@*"/></td>
                </tr>
                <tr>
                  <th>Location</th>
                  <td><xsl:apply-templates select="doap:repository/doap:SVNRepository/doap:location/@*"/></td>
                </tr>
              </table>
            </xsl:when>
            <xsl:otherwise>
              <fixme author="Project Info Dev Team">
                Unable to parse repository information.
                Element name is '<xsl:value-of select="name(doap:repository/*)"/>'</fixme>
            </xsl:otherwise>
          </xsl:choose>
        </div>
      </xsl:if>
   </xsl:template>

   <xsl:template name="project-issues">
      <h2>
        <img src="http://www.apache.org/images/redarrow.gif" />
        <a><xsl:attribute name="name">issues</xsl:attribute></a>
        Issue Tracking
      </h2>
      <div class="content">
        <p>Please report any issues, such as unexpected behaviour, bugs and 
        requests for enhancements to the projects issue tracker</p>
        <xsl:apply-templates select="doap:bug-database/@rdf:resource"/>
      </div>
    </xsl:template>
    
   <xsl:template name="project-maillists">
      <h2>
        <img src="http://www.apache.org/images/redarrow.gif" />
        <a><xsl:attribute name="name">maillists</xsl:attribute></a>
        Mailing List information
      </h2>
      <div class="content">
        <xsl:apply-templates select="doap:mailing-list/@*"/>
      </div>
    </xsl:template>
    
    <xsl:template name="project-releases">
      <xsl:if test="doap:release">
      <h2>
        <img src="http://www.apache.org/images/redarrow.gif" />
        <a><xsl:attribute name="name">releases</xsl:attribute></a>
        Release Information
      </h2>
      <div class="content">              
        <xsl:for-each select="doap:release/doap:Version">
          <table width="100%">
            <tr>
              <th class="left">Version</th>
              <td class="right"><xsl:value-of  select="doap:revision" /></td>
            </tr>
            <tr>
              <th class="left">Date</th>
              <td class="right"><xsl:value-of  select="doap:created" /></td>
            </tr>
          </table>
        </xsl:for-each>
      </div>
      </xsl:if>
    </xsl:template>
    
    <xsl:template match="@rdf:resource">
      <a>
        <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
        <xsl:value-of select="."/>
      </a>
    </xsl:template>
    
    <xsl:template match="doap:programming-language">
      <xsl:value-of select="."/>
      <xsl:if test="not(position() = last())">
        <xsl:text>. </xsl:text>
      </xsl:if>
    </xsl:template>
</xsl:stylesheet>

