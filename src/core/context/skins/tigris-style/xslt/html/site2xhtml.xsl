<?xml version="1.0"?>
<!--
site2xhtml.xsl is the final stage in HTML page production.  It merges HTML from
document2html.xsl, tab2menu.xsl and book2menu.xsl, and adds the site header,
footer, searchbar, css etc.  As input, it takes XML of the form:

<site>
  <? class="menu">
    ...
  </?>
  <? class="tab">
    ...
  </?>
  <? class="content">
    ...
  </?>
</site>

$Id: site2xhtml.xsl,v 1.1 2003/12/22 09:56:11 nicolaken Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/site2xhtml.xsl"/>
  
  <xsl:template match="site">
    <html>
      <head>
        <style type="text/css">
          /* <![CDATA[ */ 
          @import "]]><xsl:value-of select="$root"/><![CDATA[skin/tigris.css";  
          @import "]]><xsl:value-of select="$root"/><![CDATA[skin/inst.css"; 
          /*  ]]> */
        </style>
        <link rel="stylesheet" type="text/css" href="{$root}skin/print.css" media="print" />
        <script src="{$root}skin/tigris.js" type="text/javascript"></script>
        <script type="text/javascript" language="javascript" src="{$root}skin/fontsize.js"></script>
        <script type="text/javascript" language="javascript" src="{$root}skin/menu.js"></script>
        <title><xsl:value-of select="div[@class='content']/table/tr/td/h1"/></title>
        <meta http-equiv="Content-type" content="text/html" />
        <meta http-equiv="Content-style-type" content="text/css" />
      </head>
      <body onload="init();focus()" marginwidth="0" marginheight="0" class="composite">
        <script type="text/javascript">ndeSetTextSize();</script>

        <!--
          +=========================+
          |       topstrip          |
          +=========================+
          |                         |
          |       centerstrip       |
          |                         |
          |                         |
          +=========================+
          |       bottomstrip       |
          +=========================+
        -->
        
        <xsl:call-template name = "topstrip" />

        <xsl:call-template name="centerstrip"/>

        <xsl:call-template name="bottomstrip"/>
        
      </body>
    </html>
  </xsl:template>

  <xsl:template name="topstrip">
    <!--   
        +======================================================+
        |+============+    +==============+    | search box |  |
        || group logo |    | project logo |    +============+  |
        |+============+    +==============+                    |
        +======================================================+
        ||tab|tab|tab|                           publish date  |
        +======================================================+
    -->
    
    <div id="banner">   
      <table border="0" cellspacing="0" cellpadding="8" width="100%">
       <tr>
       <!-- ( ================= Group Logo ================== ) -->
        <td align="left" >
          <xsl:if test="$config/group-url">
            <div><!--<div id="cn">-->
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="$config/group-name"/>
              <xsl:with-param name="url" select="$config/group-url"/>
              <xsl:with-param name="logo" select="$config/group-logo"/>
              <xsl:with-param name="root" select="$root"/>
            </xsl:call-template>
            </div>
            <span class="alt"><xsl:value-of select="$config/group-name"/></span>
          </xsl:if>
        </td>
               

        <!-- ( ================= Project Logo ================== ) -->
        <td align="center" >
         <xsl:if test="$config/project-url">
           <div><!--<div id="sc">-->
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="$config/project-name"/>
              <xsl:with-param name="url" select="$config/project-url"/>
              <xsl:with-param name="logo" select="$config/project-logo"/>
              <xsl:with-param name="root" select="$root"/>
            </xsl:call-template>
          </div>
          </xsl:if>        </td>
        
        <!-- ( =================  Search ================== ) -->        
        <td align="right" valign="top">
          <xsl:if test="not($config/disable-search) or
            $config/disable-search='false' and $config/searchsite-domain and
            $config/searchsite-name">
            <div id="login" align="right" class="right">
             <form method="get" action="http://www.google.com/search" target="_blank">
               <input type="hidden" name="sitesearch" value="{$config/searchsite-domain}"/>
               <select name="go">
                 <option value="foo">Search...</option>
     
                 <option value="site">The <xsl:value-of select="$config/searchsite-name"/> site</option>
                 <option value="web">The web</option>
               </select> for 
               <input type="text" id="query" name="q" size="15"/> 
               <input type="submit" value="Go" name="Search"/>
             </form>
          </div>
         </xsl:if>
        </td>       
       </tr>   
      </table>  
     </div>

      <!-- ( ================= Tabs ================== ) -->
      <xsl:apply-templates select="div[@class='tabs']"/>

  <div id="breadcrumbs">
    <xsl:call-template name="breadcrumbs"/>
  </div>

  </xsl:template>
  
  <xsl:template name="centerstrip" >
   <!--
     +=========+======================+
     |         |                      |
     |         |                      |
     |         |                      |
     |         |                      |
     |  menu   |   mainarea           |
     |         |                      |
     |         |                      |
     |         |                      |
     |         |                      |
     +=========+======================+
    -->
    <table border="0" cellspacing="0" cellpadding="4" width="100%" id="main">
      <tr valign="top">
        <!-- ( =================  Menu  ================== ) -->
        <td id="leftcol" width="20%">
          <!-- If we have any menu items, draw a menu -->
          <xsl:if test="div[@class='menu']">
            <xsl:call-template name="menu"/>
          </xsl:if>
          <div class="strut">&#160;</div>
        </td>
        <!-- ( =================  Main Area  ================== ) -->
        <!--<td valign="top" width="100%">-->
        <td>
           <xsl:call-template name="mainarea"/>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="menu">
    <div id="navcolumn">
    <!--
    projecttools
    admfun
    searchbox
    helptext
    -->
    <!-- ( ================= start Menu items ================== ) -->
    <div id="projecttools" class="toolgroup">
      <div class="label">
        <strong>Section</strong>
      </div>
      <xsl:apply-templates select="div[@class='menu']"/>
      
      <xsl:if test="//toc/tocc">
        <div class="label">
           <strong>Page</strong>
        </div>
        <div class="body">
          <xsl:for-each select = "//toc/tocc">
            <div>
              <xsl:choose>
                <xsl:when test="string-length(toca)>15">
                  <a href="{toca/@href}" title="{toca}"><xsl:value-of select="substring(toca,0,20)" />...</a>
                </xsl:when>
                <xsl:otherwise>
                  <a href="{toca/@href}"><xsl:value-of select="toca" /></a>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:if test="toc2/tocc"><!-- discard second level --></xsl:if>
            </div>
          </xsl:for-each>
        </div>     
      </xsl:if>
    </div>
    <!-- ( ================= end Menu items ================== ) -->
    
    <form action="" method="get">
      <div id="searchbox" class="toolgroup">
       <div class="label"><strong>Search</strong></div>
       <div class="body">
        <div>
        <select name="scope">

         <option value="projectAndSubs" selected="selected">This project</option>
         <option value="myprojects">My projects</option>
         <option value="domain" >All projects</option>
        </select>
       </div>
       <div>
        <input type="hidden" name="resultsPerPage" value="40" /> 
        <input type="text" name="query" size="10" /> 
        <input type="submit" name="Button" value="Go" />

       </div>
       <div><a href="#">Advanced search</a>    </div>
      </div>
     </div>
     </form>
 
    
 	<xsl:if test="$filename = 'index.html' and $config/credits">
      <div id="admfun" class="toolgroup">
        <div class="label">
         <strong>Credits</strong>
        </div>
        <div class="body">

        
    <table>
		        <xsl:for-each select="$config/credits/credit[not(@role='pdf')]">
		          <xsl:variable name="name" select="name"/>
		          <xsl:variable name="url" select="url"/>
		          <xsl:variable name="image" select="image"/>
		          <xsl:variable name="width" select="width"/>
		          <xsl:variable name="height" select="height"/>
		          <tr> 
		            <td></td>
		            <td colspan="4" height="5" class="logos">
		              <a href="{$url}">
		                <img alt="{$name} logo" border="0">
		                  <xsl:attribute name="src">
		                    <xsl:if test="not(starts-with($image, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
		                    <xsl:value-of select="$image"/>
		                  </xsl:attribute>
		                  <xsl:if test="$width"><xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute></xsl:if>
		                  <xsl:if test="$height"><xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute></xsl:if>
		                </img>
		              </a>
		            </td>
		          </tr> 
		        </xsl:for-each>
            </table>         
        </div>
      </div>
    </xsl:if>      
  </div>

  </xsl:template>
 
  <xsl:template name="mainarea">
      <xsl:apply-templates select="div[@class='content']"/>
  </xsl:template>
  
  <xsl:template name="bottomstrip">
    <!-- ( ================= start Footer ================== ) -->
  <div id="footer">
   <table border="0" cellspacing="0" cellpadding="4">
    <tr>
     <td><xsl:if test="$config/host-logo and not($config/host-logo = '')">
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="$config/host-name"/>
              <xsl:with-param name="url" select="$config/host-url"/>
              <xsl:with-param name="logo" select="$config/host-logo"/>
              <xsl:with-param name="root" select="$root"/>
            </xsl:call-template>
        </xsl:if></td>
     <td>
         Copyright &#169;
            <xsl:value-of select="$config/year"/>&#160;<xsl:value-of
              select="$config/vendor"/> All rights reserved.
            <br/><script language="JavaScript" type="text/javascript"><![CDATA[<!--
              document.write(" - "+"Last Published: " + document.lastModified);
              //  -->]]></script>
     </td>
    <td align="right" nowrap="nowrap">
          <xsl:call-template name="compliancy-logos"/>
          <xsl:call-template name="bottom-credit-icons"/>              
    </td>
    </tr>
   </table>
  </div>
   <!-- ( ================= end Footer ================== ) -->
  </xsl:template>
    
  <xsl:template name="bottom-credit-icons">
      <!-- old place where to put credits icons-->
      <!--
      <xsl:if test="$filename = 'index.html' and $config/credits">
        <xsl:for-each select="$config/credits/credit[not(@role='pdf')]">
          <xsl:variable name="name" select="name"/>
          <xsl:variable name="url" select="url"/>
          <xsl:variable name="image" select="image"/>
          <xsl:variable name="width" select="width"/>
          <xsl:variable name="height" select="height"/>
          <a href="{$url}">
            <img alt="{$name} logo" border="0">
              <xsl:attribute name="src">
                <xsl:if test="not(starts-with($image, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
                <xsl:value-of select="$image"/>
              </xsl:attribute>
              <xsl:if test="$width"><xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute></xsl:if>
              <xsl:if test="$height"><xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute></xsl:if>
            </img>
          </a>
        </xsl:for-each>
      </xsl:if>
      -->
  </xsl:template>
  
  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
