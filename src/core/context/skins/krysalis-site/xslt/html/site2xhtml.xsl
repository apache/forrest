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

$Id: site2xhtml.xsl,v 1.13 2004/01/28 21:23:20 brondsem Exp $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/site2xhtml.xsl"/>
  
  <xsl:template match="site">
    <html>
      <head>
        <title><xsl:value-of select="div[@class='content']/table/tr/td/h1"/></title>
        <link rel="stylesheet" href="{$root}skin/page.css" type="text/css"/>
        <link rel="stylesheet" href="{$root}skin/forrest.css" type="text/css"/>
        <xsl:if test="$config/favicon-url">
          <link rel="shortcut icon">
            <xsl:attribute name="href">
              <xsl:value-of select="concat($root,$config/favicon-url)"/>
            </xsl:attribute>
          </link>
        </xsl:if>
        <script type="text/javascript" language="javascript" src="{$root}skin/fontsize.js"></script>
        <script type="text/javascript" language="javascript" src="{$root}skin/menu.js"></script>
      </head>
      <body onload="init()" >
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
        ||tab|tab|tab|                                         |
        +======================================================+
        ||subtab|subtab|subtab|                  publish date  |
        +======================================================+        
    -->
    <table class="header" cellspacing="0" cellpadding="0" border="0" width="100%">
      <tr>
        <!-- ( ================= Group Logo ================== ) -->
        <td >
            <div class="headerlogo">
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="$config/group-name"/>
              <xsl:with-param name="url" select="$config/group-url"/>
              <xsl:with-param name="logo" select="$config/group-logo"/>
              <xsl:with-param name="root" select="$root"/>
            </xsl:call-template>
            </div>
        </td>
        <!-- ( ================= Project Logo ================== ) -->
        <td align="center" >
         <div class="headerlogo">
          <xsl:call-template name="renderlogo">
            <xsl:with-param name="name" select="$config/project-name"/>
            <xsl:with-param name="url" select="$config/project-url"/>
            <xsl:with-param name="logo" select="$config/project-logo"/>
            <xsl:with-param name="root" select="$root"/>
          </xsl:call-template>
          </div>
        </td>
        <!-- ( =================  Search ================== ) -->
        <td class="search" align="right" rowspan="2" valign="top">
          <xsl:if test="$config/search">
	    <xsl:choose>
              <xsl:when test="$config/search/@provider = 'lucene'">
                <!-- Lucene search -->
                <form method="get" action="{$root}{$lucene-search}">
		  <table class="dialog" cellspacing="0" cellpadding="0" border="0">
		    <tr>
		      <td colspan="3" class="border" height="10"></td>
		    </tr>
		    <tr>
		      <td colspan="3" height="8"></td>
		    </tr>
		    <tr>
		      <td></td>
		      <td nowrap="nowrap">
			<input type="text" id="query" name="queryString" size="15"/>
			&#160;
			<input type="submit" value="Search" name="Search"/>
			<br />
			the <xsl:value-of select="$config/search/@name"/> site
		      </td>
		      <td></td>
		    </tr>
		    
		    <tr>
		      <td colspan="3" height="7"></td>
		    </tr>
		    
		    <tr>
		      <td class="search border bottom-left"></td>
		      <td class="search border bottomborder"></td>
		      <td class="search border bottom-right"></td>
		    </tr>
		  </table>
		</form>
	      </xsl:when>
	      <xsl:otherwise>
		<!-- Google search -->
		<form method="get" action="http://www.google.com/search" target="_blank">
		  <table class="dialog" cellspacing="0" cellpadding="0" border="0">
		    <tr>
		      <td colspan="3" class="border" height="10"></td>
		    </tr>
		    <tr>
		      <td colspan="3" height="8"></td>
		    </tr>
		    <tr>
		      <td></td>
		      <td nowrap="nowrap">
			<input type="hidden" name="as_sitesearch" value="{$config/search/@domain}"/>
			<input type="text" id="query" name="as_q" size="15"/>
			&#160;
			<input type="submit" value="Search" name="Search"/>
			<br />
			the <xsl:value-of select="$config/search/@name"/> site
			<!-- setting search options off for the moment -->
			<!--
			<input type="radio" name="web" value="web"/>web site&#160;&#160;<input type="radio" name="mail" value="mail"/>mail lists
			-->
		      </td>
		      <td></td>
		    </tr>
		    
		    <tr>
		      <td colspan="3" height="7"></td>
		    </tr>
		    
		    <tr>
		      <td class="search border bottom-left"></td>
		      <td class="search border bottomborder"></td>
		      <td class="search border bottom-right"></td>
		    </tr>
		  </table>
		</form>
	      </xsl:otherwise>
	    </xsl:choose>
          </xsl:if>
        </td>

        <td align="right" width="10" height="10">
          <span class="textheader"><xsl:value-of select="$config/project-name"/></span>
        </td>
      </tr>
      <!-- ( ================= Tabs ================== ) -->
      <tr>
        <td colspan="4" class="tabstrip">
          <xsl:apply-templates select="table[@class='tab']"/>
        </td>
      </tr>
      <tr>
        <td colspan="2" class="level2tabstrip">
          <xsl:apply-templates select="table[@class='level2tab']"/>
        </td>
        <td colspan="2" class="datenote level2tabstrip">
           <script language="JavaScript" type="text/javascript"><![CDATA[<!--
              document.write("Published: " + document.lastModified);
              //  -->]]></script>
        </td>
      </tr>
    </table>
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
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
      <tr>
        <!-- ( =================  Menu  ================== ) -->
        <td valign="top">
          <!-- If we have any menu items, draw a menu -->
          <xsl:if test="div[@class='menu']/ul/li">
            <xsl:call-template name="menu"/>
          </xsl:if>
        </td>
        <!-- ( =================  Main Area  ================== ) -->
        <td valign="top" width="100%">
           <xsl:call-template name="mainarea"/>
        </td>
      </tr>
    </table>
  </xsl:template>

  <xsl:template name="menu">
    <table cellpadding="0" cellspacing="0" class="menuarea">
      <tr>
        <!-- ( ================= start left top NavBar ================== ) -->
        <td valign = "top" width="6px">
          <table class="leftpagemargin" cellspacing="0" cellpadding="0" border="0">
            <tr><td class="subborder trail">&#160;</td></tr>
          </table>
        </td>
        <!-- ( ================= end left top NavBar ================== ) -->

        <td class="dialog">
          <!-- ( ================= start Menu items ================== ) -->
          <div class="menu">
            <xsl:for-each select = "div[@class='menu']/ul/li">
              <xsl:call-template name = "innermenuli" >
                  <xsl:with-param name="id" select="concat('1.', position())"/>
              </xsl:call-template>
            </xsl:for-each>
          </div>

          <!-- ( ================= end Menu items ================== ) -->
        </td>
      </tr>

      <tr>
        <td></td>
        <td>
          <table cellspacing="0" cellpadding="0" border="0" width="100%">
            <tr>
              <td class="border bottom-left"></td>
              <td class="border bottomborder"></td>
              <td class="border bottom-right" ></td>
            </tr>
          </table>
        </td>      
      </tr>

      <tr>
        <td height="10" colspan="2"></td>
      </tr>
      
      <xsl:if test="not($config/disable-search) or
            $config/disable-search='alt' and $config/searchsite-domain and
            $config/searchsite-name">
      <tr>
        <td ></td>
        <td class="search">
            <form method="get" action="http://www.google.com/search" target="_blank">
             <table class="dialog" cellspacing="0" cellpadding="0" border="0" width="100%">
                <tr>
                  <td class="border top-left"></td>
                  <td class="border"></td>
                  <td class="border top-right"></td>
                </tr>    
                <tr>
                  <td class="border" ></td>
                  <td colspan="2" class="border" height="10"><b>Search</b></td>
                </tr>
                <tr>
                  <td colspan="3" height="8"></td>
                </tr>
                <tr>
                  <td></td>
                  <td>
                    <input type="hidden" name="sitesearch" value="{$config/searchsite-domain}"/>
                    the <xsl:value-of select="$config/searchsite-name"/> site
                    <br />
                    <input type="text" id="query" name="q" size="13"/><input type="submit" value="Go" name="Search"/>
                  </td>
                  <td></td>
                </tr>

                <tr>
                  <td colspan="3" height="7"></td>
                </tr>

                <tr>
                  <td class="border bottom-left"></td>
                  <td class="border bottomborder"></td>
                  <td class="border bottom-right"></td>
                </tr>
              </table>
              </form>
              </td>
          </tr>
          </xsl:if>
 	  
          <xsl:if test="$filename = 'index.html' and $config/credits">
 	     <tr>
               <td></td>
 	       <td class="search">
             <table class="dialog" cellspacing="0" cellpadding="0" border="0" width="100%"> 
                <tr>
                  <td class="border top-left"></td>
                  <td class="border"></td>
                  <td class="border top-right"></td>
                </tr>     
                  <td class="border" ></td>
                  <td colspan="2" class="border" height="10"><b>Credits</b></td>
                <tr>
                  <td colspan="3" height="8"></td>
                </tr>
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
		            <td></td>
		          </tr> 
		        </xsl:for-each>
                <tr>
                  <td colspan="3" height="7"></td>
                </tr>

                <tr>
                  <td class="border bottom-left"></td>
                  <td class="border bottomborder"></td>
                  <td class="border bottom-right"></td>
                </tr>       
            </table> 
          </td>
        </tr> 
      </xsl:if>      

    </table>
  </xsl:template>


  <xsl:template name="innermenuli">
    <xsl:param name="id"/>
    <xsl:variable name="tagid">
      <xsl:choose>
        <xsl:when test="descendant-or-self::node()/li/span/@class='sel'"><xsl:value-of select="concat('menu_selected_',$id)"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="concat('menu_',concat(font,$id))"/></xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="whichGroup">
      <xsl:choose>
    	<xsl:when test="descendant-or-self::node()/li/span/@class='sel'">selectedmenuitemgroup</xsl:when>
       	<xsl:otherwise>menuitemgroup</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    
    
    <div class="menutitle" id="{$tagid}Title" onclick="SwitchMenu('{$tagid}')"><xsl:value-of select="span"/></div>
      <div class="{$whichGroup}" id="{$tagid}">
        <xsl:for-each select= "ul/li">

          <xsl:choose>
            <xsl:when test="a">
              <div class="menuitem"><a href="{a/@href}"><xsl:value-of select="a" /></a></div>
            </xsl:when>
            <xsl:when test="span/@class='sel'">
              <div class="menupage">
                <div class="menupagetitle"><xsl:value-of select="span" /></div>
                <xsl:if test="//toc/tocc"> 
                  <div class="menupageitemgroup">
                      <xsl:for-each select = "//toc/tocc">
                        <div class="menupageitem">
                          <xsl:choose>
                            <xsl:when test="string-length(toca)>15">
                              <a href="{toca/@href}" title="{toca}"><xsl:value-of select="substring(toca,0,20)" />...</a>
                            </xsl:when>
                            <xsl:otherwise>
                              <a href="{toca/@href}"><xsl:value-of select="toca" /></a>
                            </xsl:otherwise>
                          </xsl:choose>

                          <xsl:if test="toc2/tocc">
                          <!-- nicolaken: this enables double-nested page links-->
                            <ul>
                              <xsl:for-each select = "toc2/tocc">

                                <xsl:choose>
                                  <xsl:when test="string-length(toca)>15">
                                    <li><a href="{toca/@href}" title="{toca}"><xsl:value-of select="substring(toca,0,20)" />...</a></li>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <li><a href="{toca/@href}"><xsl:value-of select="toca" /></a></li>
                                  </xsl:otherwise>
                                </xsl:choose>

                              </xsl:for-each>
                            </ul> 
                          <!-- nicolaken: ...till here -->
                          </xsl:if>
                        </div>
                      </xsl:for-each>
                  </div>
                </xsl:if>
              </div>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name = "innermenuli">
                 <xsl:with-param name="id" select="concat($id, '.', position())"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>

        </xsl:for-each>
      </div>
  </xsl:template>

  <xsl:template match="toc|toc2|tocc|toca">
  </xsl:template>
  
  <xsl:template name="mainarea">
	  <table cellspacing="0" cellpadding="0" border="0" width="100%">
	    <!-- ( ================= middle NavBar ================== ) -->
	    <tr>
	      <!-- ============ Breadcrumbs =========== -->
          <td class="subborder trail">
	         &#160;<xsl:call-template name="breadcrumbs"/>&#160;
	      </td>
	      <!-- ============ Page font settings =========== -->
	      <td class="subborder trail" align="right" nowrap="true">
	        Font size: 
	          &#160;<input type="button" onclick="ndeSetTextSize('reset'); return false;" title="Reset text" class="resetfont" value="Reset"/>      
	          &#160;<input type="button" onclick="ndeSetTextSize('decr'); return false;" title="Shrink text" class="smallerfont" value="-a"/>
	          &#160;<input type="button" onclick="ndeSetTextSize('incr'); return false;" title="Enlarge text" class="biggerfont" value="+a"/>
          </td>
	    </tr>

	    <!-- ( ================= Content================== ) -->
	    <tr >
	      <td align="left" colspan="2">
	        <xsl:apply-templates select="div[@class='content']"/>
	      </td>
	    </tr>
	  </table>
  </xsl:template>
  
  <xsl:template name="bottomstrip">
    <!-- ( ================= start Footer ================== ) -->
    <table>
      <tr>
       <td><!-- using breaks so it scales with font size -->
         <br/><br/>
       </td>
      </tr>
    </table>
    <table class="footer">
      <tr>
        <xsl:if test="$config/host-logo and not($config/host-logo = '')">
          <div class="host">
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="$config/host-name"/>
              <xsl:with-param name="url" select="$config/host-url"/>
              <xsl:with-param name="logo" select="$config/host-logo"/>
              <xsl:with-param name="root" select="$root"/>
            </xsl:call-template>
          </div>
        </xsl:if>
        <td width="90%" align="center" colspan="2">
          <span class="footnote">
            <xsl:choose>
              <xsl:when test="$config/copyright-link">
                <a>
                  <xsl:attribute name="href">
                    <xsl:value-of select="$config/copyright-link"/>
                  </xsl:attribute>
                Copyright &#169; <xsl:value-of select="$config/year"/>&#160;
                <xsl:value-of select="$config/vendor"/>
                </a>
              </xsl:when>
              <xsl:otherwise>
                Copyright &#169; <xsl:value-of select="$config/year"/>&#160;
                <xsl:value-of select="$config/vendor"/>
              </xsl:otherwise>
            </xsl:choose>
            All rights reserved.
            <br/><script language="JavaScript" type="text/javascript"><![CDATA[<!--
              document.write(" - "+"Last Published: " + document.lastModified);
              //  -->]]></script></span>
        </td>
        <td class="logos" align="right" nowrap="nowrap">

          <xsl:call-template name="compliancy-logos"/>
          <xsl:call-template name="bottom-credit-icons"/>              

        </td>
      </tr>
    </table>
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
