<?xml version="1.0"?>

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- Default skinconf.xml in the skins/ directory -->
	<xsl:param name="config-file" select="'../../../../skinconf.xml'"/>
	<xsl:variable name="config" select="document($config-file)/skinconfig"/>
	<xsl:param name="dir" select="'UNDEFINED'"/>
	<xsl:include href="dotdots.xsl"/>

	<xsl:variable name="root">
		<xsl:call-template name="dotdots">
		<xsl:with-param name="path" select="$dir"/>
		</xsl:call-template>
	</xsl:variable>

	<xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>
	<xsl:variable name="spacer" select="concat($root, 'skin/images/spacer.gif')"/>

<xsl:template match="/">
<html>
<head>
			<title><xsl:value-of select="/site/document/title"/></title>
			<meta name="author" value="Leo Simons" />
			<meta name="email" value="leosimons@apache.org" />
			
			<link rel="stylesheet" type="text/css" href="skin/common.css" />
</head>
<body>
			<!-- header -->
			<div id="header"><table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td id="jakartaLogoTD" valign="middle" align="left">
							<xsl:if test="$config/group-url">
									<a href="{$config/group-url}"><img id="jakartaLogo" src="{$config/group-logo}" border="0" alt="{$config/group-name} logo"/></a>
							</xsl:if>
							</td>
									<td id="projectLogoTD" valign="middle" align="right"><a href="{$config/project-url}"><img id="projectLogo" src="{$config/project-logo}" border="0" alt="{$config/project-name}"/></a></td>
						</tr>
			</table></div>
			<!-- end header -->
			<!-- breadcrumb trail (javascript-generated) -->
			<div id="breadcrumbs">
			<xsl:if test="not($config/trail/link1/@name = '')">
				<a href="{$config/trail/link1/@name}" class="menu"><xsl:value-of select="$config/trail/link1/@name"/> &gt;</a>
			</xsl:if>
			<xsl:if test="not($config/trail/link2/@name = '')">
				<a href="{$config/trail/link2/@name}" class="menu"><xsl:value-of select="$config/trail/link2/@name"/> &gt;</a>
			</xsl:if>
			<xsl:if test="not($config/trail/link3/@name = '')">
				<a href="{$config/trail/link3/@name}" class="menu"><xsl:value-of select="$config/trail/link3/@name"/></a>
			</xsl:if>
				<script language="JavaScript1.2" type="text/javascript">
				<!--
					function sentenceCase(str) {
						var lower = str.toLowerCase();
						return lower.substr(0,1).toUpperCase() + lower.substr(1);
					}
					function getDirsAsArray() {
						var trail = document.location.pathname.split("/");
						var lastdir = (trail[trail.length-1].indexOf(".html") != -1)? trail.length-2 : trail.length-1;
						var urlprefix = "/avalon/";
						var postfix = " &gt"; 
						for(var i = 1; i <= lastdir; i++) {
							document.writeln('<a href=' + urlprefix + trail[i] + ' class="menu">' + sentenceCase(trail[i]) + '</a>'+postfix);
							urlprefix += trail[i] + "/";
							if(i == lastdir-1) postfix = ":";
						}
					}
					getDirsAsArray();
				// -->
				</script>
			</div>
			<!-- end breadcrumb trail -->
			<!-- main section of page -->
			<div id="main"><table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
									<td valign="top">
									
	<!-- menu -->		
	<div id="menu">						
	  <xsl:copy-of select="/site/menu/node()|@*"/>
    </div>
									<!-- end menu column -->
									</td>
									<!-- spacer -->
									<td width="10">&#160;</td>
 									<td valign="top" width="100%">
									<!-- contents column -->
										
	<!-- contents -->								
    <div id="title"><h1><xsl:value-of select="/site/document/title"/></h1></div>
      <div id="contents">    
       <xsl:copy-of select="/site/document/body/node()|@*"/>
      </div>
									<!-- end contents column -->

<script language="JavaScript">
<![CDATA[<!-- 
document.write("last modified: " + document.lastModified); 
//  -->]]>
</script>									</td>
						</tr>
			</table></div>
			<!-- end main section of page -->
			<!-- footer -->
			<div id="footer">
						 Copyright &#x00A9;<xsl:value-of select="$config/year"/>&#160;<xsl:value-of
          select="$config/vendor"/>  All Rights Reserved.

			</div>
    <table>
      <tr>
      <td class="logos" bgcolor="#CFDCED" colspan="1" align="left">
        <xsl:if test="$config/host-logo and not($config/host-logo = '')">
            <a href="{$config/host-url}"><img src="{$config/host-logo}" alt="{$config/host-name} logo" border="0"/></a>
        </xsl:if>
      </td>
      <td class="logos" bgcolor="#CFDCED" colspan="5" align="right">
        <xsl:if test="$config/credits">
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

			<!-- end footer -->
			
</body>
</html>
</xsl:template>
</xsl:stylesheet>
