<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:NetUtils="org.apache.cocoon.util.NetUtils"
    version="1.0">

	<xsl:param name="header"/>

<xsl:template match="/">
<html>
      <head>
        <title><xsl:value-of select="/site/document/title"/></title>
<style type="text/css">
a.menu {
	color: #FFFFFF;
    text-align:left;               
    font-size:12px;
    font-family: Verdana, Arial, Helvetica, sans-serif;
    font-weight:plain;
    text-decoration:none;
    padding-left: 14px
}
.menutitle {
	color: #000000;
    text-align:left;               
    font-size:10px;
    font-family: Verdana, Arial, Helvetica, sans-serif;
    font-weight:bold;
    padding-left: 8px
}
.menuselected {
	color: white;
    text-align:left;               
    font-size:12px;
    font-family: Verdana, Arial, Helvetica, sans-serif;
    font-weight:bold;
    padding-left: 14px
}
</style>     
 <!--<link rel ="stylesheet" type="text/css" href="skin/xml-apache.css" title="Style"/>-->
      </head>

      <body text="#000000" link="#039acc" vlink="#0086b2" alink="#cc0000"
            topmargin="4" leftmargin="4" marginwidth="4" marginheight="4"
            bgcolor="#ffffff">
        <!-- THE TOP BAR (HEADER) -->
        <table width="100%" cellspacing="0" cellpadding="0" border="0">
          <tr>
            <td height="60" rowspan="3" valign="top" align="left" background="@group-logo.href@">
              <img height="60" src="@group-logo.src@" hspace="0" vspace="0" border="0"/>
            </td>
            <td width="100%" height="5" valign="top" align="left" colspan="2" background="skin/images/line.gif">
              <img width="1" height="5" src="skin/images/line.gif" hspace="0" vspace="0" border="0" align="left"/>
            </td>
            <td width="29" height="60"  rowspan="3" valign="top" align="left">
              <img width="29" height="60" src="skin/images/right.gif" hspace="0" vspace="0" border="0"/>
            </td>
          </tr>
          <tr>
            <td width="100%" height="35" valign="top" align="left" bgcolor="#0086b2">
              <font size="5" face="Verdana, Arial, Helvetica, sans-serif" color="#ffffff">&#160;&#160;<xsl:value-of select="/site/document/title"/></font>
            </td>
            <td width="100%" height="35" valign="top" align="right" bgcolor="#0086b2">
            <a href="@project-logo.href@"><img  height="35" src="@project-logo.src@" hspace="0" vspace="0" border="0"/></a>
            </td>
          </tr>
          <tr>
            <td align="right" bgcolor="#0086b2" height="20" valign="top" width="100%" colspan="2" background="skin/images/bottom.gif">
               <table border="0" cellpadding="0" cellspacing="0" width="288">
                <tr>
                   <td height="20" valign="top" align="left"></td>
                  <td height="20" valign="top" align="left"></td>
                  <td height="20" valign="top" align="left"></td>
                   </tr>
                   
              </table>              
            </td>
          </tr>
        </table>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr width="100%">
	

            
	        <!-- THE SIDE BAR -->
	  		<td width="120" valign="top" align="left"> 
			<table bgcolor="#a0a0a0" border="0" cellpadding="0" cellspacing="0" width="120">
			      <tr>
					<td align="left" valign="top">
						<img border="0" height="14" hspace="0" src="skin/images/join.gif" vspace="0" width="120"/>
						<br/>
					</td>
				</tr>
				<xsl:copy-of select="/site/menu/node()|@*"/>
				<tr>
					<td valign="top" align="left">
						<img border="0" height="14" hspace="0" src="skin/images/close.gif" vspace="0" width="120"/>
						<br/>
					</td>
				</tr>
			</table>
		</td>

		
	    <!-- THE CONTENTS -->
		<td>
			<table border="0" cellpadding="0" cellspacing="15">
				<tr><td><xsl:copy-of select="/site/document/body/node()|@*"/></td></tr>
			</table>
		</td>
		
		
	</tr>
</table>
 
 
            
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td bgcolor="#0086b2">
      <img height="1" src="skin/images/dot.gif" width="1"/>
    </td>
  </tr>
  <tr>
    <td align="center">
      <font color="#0086b2" face="arial,helvetica,sanserif" size="-1">
        <i>Copyright &#169; @year@ @vendor@. All Rights Reserved.</i>
      </font>
    </td>
  </tr>
        <tr>
          <td width="100%" align="right">
			<br/>
          </td>
        </tr>        
        <tr>
          <td width="100%" align="right">
            <a href="http://krysalis.org/"><img src="skin/images/krysalis-compatible.jpg" alt="Krysalis Logo" border="0"/></a> 
            <a href="http://xml.apache.org/cocoon/"><img src="skin/images/built-with-cocoon.gif" alt="Cocoon Logo" border="0"/></a> 
            <a href="http://jakarta.apache.org/ant/"><img src="skin/images/ant_logo_medium.gif" alt="Ant Logo" border="0"/></a> 
          </td>
        </tr>  
</table>
</body>


</html>
</xsl:template>
</xsl:stylesheet>
