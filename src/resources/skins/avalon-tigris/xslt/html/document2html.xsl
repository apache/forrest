<?xml version="1.0"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

<!-- ====================================================================== -->
<!-- document section -->
<!-- ====================================================================== -->

 <xsl:template match="/">
  <!-- checks if this is the included document to avoid neverending loop -->
  <xsl:if test="not(book)">
      <document>
      <xsl:choose>
		<xsl:when test="document/header/title">
		      <title><xsl:value-of select="document/header/title"/></title>
		</xsl:when>
		<xsl:otherwise>
			<title>NO TITLE</title>
		</xsl:otherwise>
	</xsl:choose>
      <body>
        <xsl:apply-templates/>
        <xsl:if test="document/header/authors">
            <div align="right" id="authors">       
               <xsl:for-each select="document/header/authors/person">
                     <xsl:choose>
                        <xsl:when test="position()=1">by&#160;</xsl:when>

                        <xsl:otherwise>,&#160;</xsl:otherwise>
                     </xsl:choose>
                     <!-- <a href="mailto:{@email}"> -->
                      <xsl:value-of select="@name" />
                     <!-- </a> -->
                  </xsl:for-each>
              </div>
         </xsl:if>         
      </body>
      </document>
   </xsl:if>
 
   <xsl:if test="book">
    <xsl:apply-templates/>
   </xsl:if>
  </xsl:template>

<!-- ====================================================================== -->
<!-- header section -->
<!-- ====================================================================== -->

 <xsl:template match="header">
  <!-- ignore on general document -->
 </xsl:template>

<!-- ====================================================================== -->
<!-- body section -->
<!-- ====================================================================== -->

   <xsl:template match="section">
	
	 <xsl:variable name = "level" select = "count(ancestor::section)+1" />
	 
	 <xsl:choose>
	 	<xsl:when test="$level=1">
	 	  <div class="h3"><h3><xsl:value-of select="title"/></h3></div>
	      <xsl:apply-templates/>
	 	</xsl:when>
	 	<xsl:when test="$level=2">
	 	  <div class="h4"><h4><xsl:value-of select="title"/></h4></div>
	      <xsl:apply-templates/>
	 	</xsl:when>
	 	<xsl:when test="$level=3">
	 	  <div class="h2"><h2><xsl:value-of select="title"/></h2></div>
	      <xsl:apply-templates/>
	 	</xsl:when>
	 	<xsl:otherwise>
	 	  <div class="h5"><h5><xsl:value-of select="title"/></h5></div>
	      <xsl:apply-templates/>	 	 
	 	</xsl:otherwise>
	 </xsl:choose>

	</xsl:template>  

 <xsl:template match="title">
 </xsl:template>
 	       
<!-- ====================================================================== -->
<!-- footer section -->
<!-- ====================================================================== -->

 <xsl:template match="footer">
  <!-- ignore on general documents -->
 </xsl:template>

<!-- ====================================================================== -->
<!-- paragraph section -->
<!-- ====================================================================== -->

  <xsl:template match="p">
    <p><xsl:apply-templates/></p>
  </xsl:template>

  <xsl:template match="note">
   <p><i><xsl:apply-templates/></i></p>
  </xsl:template>

  <xsl:template match="source">
    <pre><xsl:apply-templates/></pre>
  </xsl:template>
  
  <xsl:template match="//source/font">
    <font color="{@color}"><xsl:apply-templates/></font>
  </xsl:template>
    
  <xsl:template match="fixme">
   <!-- ignore on documentation -->
  </xsl:template>

<!-- ====================================================================== -->
<!-- list section -->
<!-- ====================================================================== -->

 <xsl:template match="ul|ol|dl">
  <blockquote>
   <xsl:copy>
    <xsl:apply-templates/>
   </xsl:copy>
  </blockquote>
 </xsl:template>
 
 <xsl:template match="li">
  <xsl:copy>
   <xsl:apply-templates/>
  </xsl:copy>
 </xsl:template>

 <xsl:template match="sl">
  <ul>
   <xsl:apply-templates/>
  </ul>
 </xsl:template>

 <xsl:template match="dt">
  <li>
   <strong><xsl:value-of select="."/></strong>
   <xsl:text> - </xsl:text>
   <xsl:apply-templates select="dd"/>   
  </li>
 </xsl:template>

<!-- ====================================================================== -->
<!-- table section -->
<!-- ====================================================================== -->

  <xsl:template match="table">
    <table>
      <caption><xsl:value-of select="caption"/></caption>
      <xsl:apply-templates/>
    </table>
  </xsl:template>

  <xsl:template match="tr">
    <tr><xsl:apply-templates/></tr>
  </xsl:template>

  <xsl:template match="th">
    <td colspan="{@colspan}" rowspan="{@rowspan}">
        <b><xsl:apply-templates/></b>&#160;
    </td>
  </xsl:template>

  <xsl:template match="td">
    <td colspan="{@colspan}" rowspan="{@rowspan}">
        <xsl:apply-templates/>&#160;
    </td>
  </xsl:template>

  <xsl:template match="tn">
    <td colspan="{@colspan}" rowspan="{@rowspan}">
      &#160;
    </td>
  </xsl:template>
  
  <xsl:template match="caption">
    <!-- ignore since already used -->
  </xsl:template>

<!-- ====================================================================== -->
<!-- markup section -->
<!-- ====================================================================== -->

 <xsl:template match="strong">
   <b><xsl:apply-templates/></b>
 </xsl:template>

 <xsl:template match="em">
    <i><xsl:apply-templates/></i>
 </xsl:template>

 <xsl:template match="code">
    <code><xsl:apply-templates/></code>
 </xsl:template>
 
<!-- ====================================================================== -->
<!-- images section -->
<!-- ====================================================================== -->

 <xsl:template match="figure">
  <p>
  <xsl:choose>
   <xsl:when test="string(@width) and string(@height)">
   <img src="{@src}" alt="{@alt}" width="{@width}" height="{@height}" border="0"/>
   </xsl:when>
   <xsl:otherwise>
   <img src="{@src}" alt="{@alt}" border="0"/>
   </xsl:otherwise>
  </xsl:choose>
  </p>
 </xsl:template>
 
 <xsl:template match="img">
   <img src="{@src}" alt="{@alt}" border="0"/>
 </xsl:template>

 <xsl:template match="icon">
   <img src="{@src}" alt="{@alt}" border="0"/>
 </xsl:template>

<!-- ====================================================================== -->
<!-- links section -->
<!-- ====================================================================== -->

 <xsl:template match="link">
   <a href="{@href}"><xsl:apply-templates/></a>
 </xsl:template>

 <xsl:template match="connect">
  <xsl:apply-templates/>
 </xsl:template>

 <xsl:template match="jump">
   <a href="{@href}#{@anchor}"><xsl:apply-templates/></a>
 </xsl:template>

 <xsl:template match="fork">
   <a href="{@href}" target="_blank"><xsl:apply-templates/></a>
 </xsl:template>

 <xsl:template match="anchor">
   <a name="{@id}"><xsl:comment>anchor</xsl:comment></a>
 </xsl:template>  

<!-- ====================================================================== -->
<!-- specials section -->
<!-- ====================================================================== -->

 <xsl:template match="br">
  <br/>
 </xsl:template>

</xsl:stylesheet>
