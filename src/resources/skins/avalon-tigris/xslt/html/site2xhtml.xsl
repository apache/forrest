<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <!-- Default skinconf.xml in the skins/ directory -->
  <xsl:param name="config-file" select="'../../../../skinconf.xml'"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>
  <xsl:param name="path"/>
  <xsl:include href="pathutils.xsl"/>
  <xsl:include href="dotdots.xsl"/>
  <xsl:include href="renderlogo.xsl"/>

  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
 
  <xsl:variable name="filename">
    <xsl:call-template name="filename">
      <xsl:with-param name="path" select="$path"/>
    </xsl:call-template>
  </xsl:variable>
  
  <xsl:template match="site">
    <html>
      <head>
        <title><xsl:value-of select="div[@class='content']/table/tr/td/h1"/></title>
         <link rel="stylesheet" href="{$root}skin/site.css" type="text/css"/>
      </head>
      <body>

    <xsl:comment>================= start Banner ==================</xsl:comment>
      <div class="banner">
        <table border="0" cellspacing="0" cellpadding="8" width="100%">
          <tr>
            <xsl:comment>================= start Group Logo ==================</xsl:comment>
            <xsl:if test="$config/group-name">
            <td align="left">
              <div class="groupLogo">
                <xsl:call-template name="renderlogo">
                  <xsl:with-param name="name" select="$config/group-name"/>
                  <xsl:with-param name="url" select="$config/group-url"/>
                  <xsl:with-param name="logo" select="$config/group-logo"/>
                  <xsl:with-param name="root" select="$root"/>
                </xsl:call-template>
              </div>
            </td>
            </xsl:if>
            <xsl:comment>================= end Group Logo ==================</xsl:comment>
            <xsl:comment>================= start Project Logo ==================</xsl:comment>
            <td align="right">
              <div class="projectLogo">
                <xsl:call-template name="renderlogo">
                  <xsl:with-param name="name" select="$config/project-name"/>
                  <xsl:with-param name="url" select="$config/project-url"/>
                  <xsl:with-param name="logo" select="$config/project-logo"/>
                  <xsl:with-param name="root" select="$root"/>
                </xsl:call-template>
              </div>
            </td>
            <xsl:comment>================= end Project Logo ==================</xsl:comment>
          </tr>
        </table>
      </div>
    <xsl:comment>================= end Banner ==================</xsl:comment>

    <xsl:comment>================= start Main ==================</xsl:comment>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      <xsl:comment>================= start Status ==================</xsl:comment>
      <tr class="status">
        <td class="breadcrumb">
          <xsl:comment>================= start BreadCrumb ==================</xsl:comment>
          <xsl:for-each select="$config/trail/link">
            <xsl:if test="position()!=1">|</xsl:if>
            <a href="{@href}"><xsl:value-of select="@name"/></a>
          </xsl:for-each>
          <xsl:comment>================= end BreadCrumb ==================</xsl:comment>
        </td>
        <td class="tabs">
          <div align="right">
          <xsl:comment>================= start Tabs ==================</xsl:comment>
          <xsl:apply-templates select="div[@class='tab']"/>
          <xsl:comment>================= end Tabs ==================</xsl:comment>
          </div>
        </td>
      </tr>
      <xsl:comment>================= end Status ==================</xsl:comment>

      <tr valign="top">
        <xsl:comment>================= start Menu ==================</xsl:comment>
        <td width="20%">
          <div class="menuColumn">
            <xsl:apply-templates select="div[@class='menuBar']"/>
          </div>
        </td>
        <xsl:comment>================= end Menu ==================</xsl:comment>

        <xsl:comment>================= start Content ==================</xsl:comment>
        <td>
          <div class="contentColumn">
            <xsl:apply-templates select="div[@class='content']"/>
          </div>
        </td>
        <xsl:comment>================= end Content ==================</xsl:comment>
      </tr>
    </table>
    <xsl:comment>================= end Main ==================</xsl:comment>

    <xsl:comment>================= start Footer ==================</xsl:comment>
    <div class="footer">
    <table border="0" width="100%" cellpadding="0"
           cellspacing="0" summary="footer">
      <tr>
        <xsl:comment>================= start Copyright ==================</xsl:comment>
        <td colspan="2">
          <div align="center">
            <div class="copyright">
              Copyright &#169; <xsl:value-of select="$config/year"/>&#160;<xsl:value-of
              select="$config/vendor"/>. All rights reserved.
            </div>
          </div>
        </td>
        <xsl:comment>================= end Copyright ==================</xsl:comment>
      </tr>
      <tr>
      <td align="left">
        <xsl:comment>================= start Host ==================</xsl:comment>
        <xsl:if test="$config/host-logo and not($config/host-logo = '')">
          <div align="left">
            <div class="host">
              <xsl:call-template name="renderlogo">
                <xsl:with-param name="name" select="$config/host-name"/>
                <xsl:with-param name="url" select="$config/host-url"/>
                <xsl:with-param name="logo" select="$config/host-logo"/>
                <xsl:with-param name="root" select="$root"/>
              </xsl:call-template>
            </div>
          </div>
        </xsl:if>
        <xsl:comment>================= end Host ==================</xsl:comment>
      </td>
      <td align="right">
        <xsl:comment>================= start Credits ==================</xsl:comment>
        <div align="right">
        <div class="credit">
        <xsl:if test="$filename = 'index.html' and $config/credits">
          <xsl:for-each select="$config/credits/credit">
            <xsl:call-template name="renderlogo">
              <xsl:with-param name="name" select="name"/>
              <xsl:with-param name="url" select="url"/>
              <xsl:with-param name="logo" select="image"/>
              <xsl:with-param name="root" select="$root"/>
              <xsl:with-param name="width" select="width"/>
              <xsl:with-param name="height" select="height"/>
            </xsl:call-template>
          </xsl:for-each>
        </xsl:if>
        </div>
        </div>
        <xsl:comment>================= end Credits ==================</xsl:comment>
        </td>
      </tr>
    </table>
    </div>
    <xsl:comment>================= end Footer ==================</xsl:comment>

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
