<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <!-- Default skinconf.xml in the skins/ directory -->
  <xsl:param name="config-file" select="'../../../../skinconf.xml'"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>
  <xsl:param name="dir" select="'UNDEFINED'"/>
  <xsl:param name="resource" select="'UNDEFINED'"/>
  <xsl:include href="dotdots.xsl"/>

  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$dir"/>
    </xsl:call-template>
  </xsl:variable>
 
  <xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>
  <xsl:variable name="spacer" select="concat($root, 'skin/images/spacer.gif')"/>
 
  <xsl:template match="site">
    <html>
      <head>
        <title><xsl:value-of select="div[@class='content']/table/tr/td/h1"/></title>
        <link rel="stylesheet" href="{$root}skin/page.css" type="text/css"/>
      </head>
      <body bgcolor="#FFFFFF" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
      <!-- =========================== top line with navigation path ========================== -->
    <table cellspacing="0" cellpadding="0" border="0" width="100%" summary="navigation path">
      <tr>
        <td height="20" bgcolor="#CFDCED" valign="middle" nowrap="nowrap">
          <img src="{$spacer}" border="0" alt="" width="5" height="1" />
          <!-- breadcrumb trail (javascript-generated) -->
          <font face="Arial, Helvetica, Sans-serif" size="2">
              <script type="text/javascript" language="JavaScript" src="{$root}skin/breadcrumbs.js"></script>
          </font>
        </td>
      </tr>
      <tr>
        <td height="2" bgcolor="#4C6C8F"><img src="{$spacer}" border="0" alt="" width="2" height="2" /></td>
      </tr>
    </table>
    <!-- ================================= top bar with logo's and search box ===================================  -->
        <table cellspacing="0" cellpadding="0" border="0" width="100%" summary="header with logos">
      <tr>
        <td bgcolor="#294563">
        <xsl:if test="$config/group-url"><a href="{$config/group-url}"><img src="{$root}{$config/group-logo}" alt="{$config/group-name} logo" border="0"/></a></xsl:if></td>
        <td bgcolor="#294563" align="center" width="100%"><a href="{$config/project-url}"><img src="{$root}{$config/project-logo}" border="0" alt="{$config/project-name} site" /></a></td>
        <td bgcolor="#294563" rowspan="2" valign="top">
          <xsl:if test="not($config/disable-search) or $config/disable-search='false' and $config/searchsite-domain and $config/searchsite-name">
          <form method="get" action="http://www.google.com/search" onsubmit="q.value = query.value + ' site:{$config/searchsite-domain}'" target="_blank">
            <table bgcolor="#4C6C8F" cellpadding="0" cellspacing="0" border="0" summary="search">
              <tr>
                <td colspan="3"><img src="{$spacer}" alt="" width="1" height="10" /></td>
              </tr>
              <tr>
                <td><img src="{$spacer}" alt="" width="1" height="1" /></td>
                <td nowrap="nowrap">
                  <input type="hidden" name="q"/>
                  <input type="text" id="query" size="15"/><img src="{$spacer}" alt="" width="5" height="1" /><input type="submit" value="Search" name="Search"/>
                  <br />
                  <font color="white" size="2" face="Arial, Helvetica, Sans-serif">
                  the <xsl:value-of select="$config/searchsite-name"/> site
<!-- setting search options off for the moment -->
<!--
                    <input type="radio" name="web" value="web"/>web site&#160;&#160;<input type="radio" name="mail" value="mail"/>mail lists
-->
                  </font>
                </td>
                <td><img src="{$spacer}" alt="" width="1" height="1" /></td>
              </tr>
              <tr>
                <td><img src="{$skin-img-dir}/search-left.gif" width="9" height="10" border="0" alt="" /></td>
                <td><img src="{$spacer}" alt="" width="1" height="1" /></td>
                <td><img src="{$skin-img-dir}/search-right.gif" width="9" height="10" border="0" alt="" /></td>
              </tr>
            </table>
          </form>
          </xsl:if>
        </td>
        <td bgcolor="#294563"><img src="{$spacer}" alt="" width="10" height="10" /></td>
      </tr>
      <tr>
        <td colspan="2" bgcolor="#294563" valign="bottom">
            <xsl:apply-templates select="div[@class='tab']"/>
        </td>
        <td bgcolor="#294563"><img src="{$spacer}" height="1" width="1" alt="" /></td>
      </tr>
      <tr>
        <td colspan="4" bgcolor="#4C6C8F"><img src="{$spacer}" alt="" height="10" width="1" /></td>
      </tr>
    </table>
    <!-- ======================================  Menu and Content table ====================================== -->
    <table cellspacing="0" cellpadding="0" border="0" width="100%" bgcolor="#ffffff" summary="page content">
      <tr>
        <td valign="top">
          <table cellpadding="0" cellspacing="0" border="0" summary="menu">
            <tr>
              <td valign="top" rowspan="3">
                <table cellspacing="0" cellpadding="0" border="0" summary="blue line">
                  <tr><td bgcolor="#294563"><img src="{$spacer}" alt="" height="1" width="10" /></td></tr>
                  <tr><td bgcolor="#CFDCED"><font face="Arial, Helvetica, Sans-serif" size="4" color="#4C6C8F">&#160;</font></td></tr>
                  <tr><td bgcolor="#294563"><img src="{$spacer}" alt="" height="1" width="10" /></td></tr>
                </table>
              </td>
              <td bgcolor="#294563"><img src="{$spacer}" alt="" height="1" width="1" /></td>
              <td bgcolor="#4C6C8F" valign="bottom"><img src="{$spacer}" alt="" height="10" width="10" /></td>
              <td bgcolor="#4C6C8F" valign="top" nowrap="nowrap">
                  <xsl:apply-templates select="div[@class='menu']"/>
              </td>
              <td bgcolor="#4C6C8F" valign="bottom"><img src="{$spacer}" alt="" height="10" width="10" /></td>
              <td bgcolor="#294563"><img src="{$spacer}" alt="" height="1" width="1" /></td>
            </tr>
            <tr>
              <td bgcolor="#4C6C8F" rowspan="2" colspan="2" align="left" valign="bottom"><img src="{$skin-img-dir}/menu-left.gif" alt="" border="0" width="10" height="10" /></td>
              <td bgcolor="#4C6C8F"><img src="{$spacer}" alt="" border="0" width="10" height="10" /></td>
              <td bgcolor="#4C6C8F" rowspan="2" colspan="2" align="right" valign="bottom"><img src="{$skin-img-dir}/menu-right.gif" alt="" border="0" width="10" height="10" /></td>
            </tr>
            <tr>
              <td bgcolor="#294563" height="1"><img src="{$spacer}" alt="" height="1" width="1" /></td>
            </tr>
          </table>
        </td>
        <td width="100%" valign="top">
          <table cellspacing="0" cellpadding="0" border="0" width="100%" summary="content">
            <tr><td bgcolor="#294563" colspan="4"><img src="{$spacer}" alt="" height="1" width="10" /></td></tr>
            <tr>
              <td bgcolor="#CFDCED" width="10" align="left"><img src="{$spacer}" alt="" height="1" width="10" /></td>
              <td bgcolor="#CFDCED" width="50%" align="left">
<!-- ========================================= Page number ===================================== -->
                <font face="Arial, Helvetica, Sans-serif" size="3" color="#4C6C8F">
                &#160;
<!--
                  <b>Page 1 of 5</b>
-->
                </font>
                  <img src="{$spacer}" alt="" height="8" width="10" />
              </td>
              <td bgcolor="#CFDCED" width="50%" align="right">
<!--  ====================================== page navigation ===================================== -->
                <font face="Arial, Helvetica, Sans-serif" size="3" color="#4C6C8F">
                &#160;
<!--
                  <b>&#171; prev&#160;&#160;<font size="4">[3]</font>&#160;&#160;next &#187;</b>
-->
                </font>
                  <img src="{$spacer}" alt="" height="8" width="10" />
              </td>
              <td bgcolor="#CFDCED" width="10"><img src="{$spacer}" alt="" height="1" width="10" /></td>
            </tr>
            <tr><td bgcolor="#294563" colspan="4"><img src="{$spacer}" alt="" height="1" width="10" /></td></tr>
            <tr>
              <td width="10" align="left"><img src="{$spacer}" alt="" height="1" width="10" /></td>
              <td width="100%" align="left">
                <xsl:apply-templates select="div[@class='content']"/>
              </td>
              <td width="10"><img src="{$spacer}" alt="" height="1" width="10" /></td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
<!-- footer -->
    <table border="0" height="20" width="100%" cellpadding="0" cellspacing="0" summary="footer">
      <tr><td bgcolor="#4C6C8F" height="1" colspan="2"><img src="{$spacer}" alt="" width="1" height="1" /><a href="{$skin-img-dir}/label.gif"></a><a href="{$skin-img-dir}/page.gif"></a><a href="{$skin-img-dir}/chapter.gif"></a><a href="{$skin-img-dir}/chapter_open.gif"></a><a href="{$skin-img-dir}/current.gif"></a><a href="/favicon.ico"></a></td></tr>
      <tr>
        <td align="center" class="copyright" bgcolor="#CFDCED" colspan="2">
          <font face="Arial, Helvetica, Sans-Serif" size="2">Copyright &#169;
          <xsl:value-of select="$config/year"/>&#160;<xsl:value-of
          select="$config/vendor"/>. All rights reserved.<script language="JavaScript" type="text/javascript"><![CDATA[<!--
              document.write(" - "+"Last Published: " + document.lastModified);
            //  -->]]></script></font>
        </td>
      </tr>
      <tr>
      <td class="logos" bgcolor="#CFDCED" align="left">
        <xsl:if test="$config/host-logo and not($config/host-logo = '')">
            <a href="{$config/host-url}"><img src="{$config/host-logo}" alt="{$config/host-name} logo" border="0"/></a>
        </xsl:if>
      </td>
      <td class="logos" bgcolor="#CFDCED" align="right">
        <xsl:if test="$resource = 'index.html' and $config/credits">
          <div align="right">
          <xsl:for-each select="$config/credits/credit">
            <xsl:variable name="name" select="name"/>
            <xsl:variable name="url" select="url"/>
            <xsl:variable name="image" select="image"/>
            <xsl:variable name="width" select="width"/>
            <xsl:variable name="height" select="height"/>
            <a href="{$url}" valign="top">
            <img alt="{$name} logo" border="0">
              <xsl:attribute name="src">
                <xsl:if test="not(starts-with($image, 'http://'))"><xsl:value-of select="$root"/></xsl:if>
                <xsl:value-of select="$image"/>
              </xsl:attribute>
              <xsl:if test="$width"><xsl:attribute name="width"><xsl:value-of select="$width"/></xsl:attribute></xsl:if>
              <xsl:if test="$height"><xsl:attribute name="height"><xsl:value-of select="$height"/></xsl:attribute></xsl:if>
            </img>
            <img src="{$spacer}" border="0" alt="" width="5" height="1" />
            </a>
          </xsl:for-each>
            </div>
        </xsl:if>
        </td>
      </tr>
    </table>
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
