<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
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
									<td id="jakartaLogoTD" valign="middle" align="left"><a href="@group-logo.href@"><img id="jakartaLogo" src="@group-logo.src@" border="0"/></a></td>
									<td id="projectLogoTD" valign="middle" align="right"><a href="@project-logo.href@"><img id="projectLogo" src="@project-logo.src@" border="0"/></a></td>
						</tr>
			</table></div>
			<!-- end header -->
			<!-- breadcrumb trail (javascript-generated) -->
			<div id="breadcrumbs">
              <a href="@link1.href@" class="menu">@link1@ &gt;</a>
              <a href="@link2.href@" class="menu">@link2@ &gt;</a>
              <a href="@link3.href@" class="menu">@link3@</a>    			
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
						 Copyright &#x00A9;@year@ @vendor@. All Rights Reserved.

			</div>
  <a href="http://jakarta.apache.org/ant/"><img align="right" src="skin/images/ant_logo_medium.gif" alt="Ant Logo" border="0"/></a> 
  <a href="http://xml.apache.org/cocoon/"><img align="right" src="skin/images/built-with-cocoon.gif" alt="Cocoon Logo" border="0"/></a> 
  <a href="http://www.krysalis.org/"><img align="right" src="skin/images/krysalis-logo-small.gif" alt="Krysalis Logo" border="0"/></a> 
  <a href="http://www.krysalis.org/centipede/"><img align="right" src="skin/images/centipede-logo-small.gif" alt="Krysalis Centipede Logo" border="0"/></a> 

			<!-- end footer -->
			
</body>
</html>
</xsl:template>
</xsl:stylesheet>
