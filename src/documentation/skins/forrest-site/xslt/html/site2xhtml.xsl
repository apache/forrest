<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="site">
    <html>
      <head>
        <title>
          <xsl:value-of select="td[@class='content']/h2"/>
        </title>
        <link rel="stylesheet" href="skin/page.css" type="text/css"/>
      </head>

      <body>

        <div class="path">
          <a href="@link1.href@">@link1@</a> : <a href="@link2.href@">@link2@</a> : <a href="@link3.href@">@link3@</a>
        </div>

        <table class="top" cellspacing="0" cellpadding="0" summary="top bar">
          <tr>
            <td>
              <a href="@group-logo.href@"><img src="@group-logo.src@" border="0"/></a>
            </td>
            <td valign="middle" align="center" width="100%">
              <a href="@project-logo.href@"><img src="@project-logo.src@" border="0"/></a>
            </td>
            <td valign="top" height="69" nowrap="nowrap">
              <div class="search">

               <form method="get" action="http://www.google.com/search" onsubmit="q.value = query.value + ' site:xml.apache.org'">
                  <input type="hidden" name="q"/>
                  <input type="text" id="query" size="15"/>&#160;<input type="button" value="Search" name="Search"/><br/>
                  <!-- <input type="radio" name="web" value="web">web site&#160;</input>&#160;<input type="radio" name="mail" value="mail">mail lists</input> -->
                </form>
              </div>
            </td>
          </tr>
        </table>

        <div class="tabs">
          <span class="tab">
            <a href="http://xml.apache.org/forrest/">Home</a>
          </span>
          <!--
          <span class="tab">
            <a href="#">not</a>
          </span>
          <span class="tab">
            <a href="#">implemented</a>
          </span>
          <span class="tab">
            <a href="#">yet</a>
          </span>
          -->
        </div>


       <div class="topline">&#160;</div>

        <table cellspacing="0" cellpadding="0" summary="content pane">
          <tr>
            <td width="10px" class="navbar">&#160;</td>
            <td rowspan="2" valign="top" nowrap="nowrap" width="160px">
              <xsl:apply-templates select="div[@class='menu']"/>
            </td>
            <!-- <td valign="top" class="navbar" align="left">no chunking yet</td> -->
            <td valign="top" class="navbar" align="left">&#160;</td>
            <td width="*" valign="top" class="navbar" align="right">
              <!-- <a href="#">&lt;&lt; prev</a> <strong>[x]</strong> <a href="#">next &gt;&gt;</a> -->
              &#160;
            </td>
          </tr>
          <tr>
            <td>&#160;</td>
            <xsl:apply-templates select="td[@class='content']"/>
         </tr>
          <tr>
            <td>&#160;</td>

           <td>&#160;</td>
          </tr>
        </table>
        <div class="copyright">
          Copyright &#x00A9;@year@ @vendor@. All Rights Reserved.
        </div>
        
        <a href="http://xml.apache.org/cocoon/"><img align="right" src="skin/images/built-with-cocoon.gif" alt="Cocoon Logo" border="0"/></a> 
        <a href="http://www.krysalis.org/centipede/"><img align="right" src="skin/images/centipede-logo-small.gif" alt="Krysalis Centipede Logo" border="0"/></a>         
      
      </body>
    </html>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>