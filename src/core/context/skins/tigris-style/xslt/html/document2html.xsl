<?xml version="1.0"?>
<!--
This stylesheet contains the majority of templates for converting documentv11
to HTML.  It renders XML as HTML in this form:

  <div class="content">
   ...
  </div>

..which site2xhtml.xsl then combines with HTML from the index (book2menu.xsl)
and tabs (tab2menu.xsl) to generate the final HTML.

$Id: document2html.xsl,v 1.2 2003/12/26 21:03:54 nicolaken Exp $
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:import href="../../../common/xslt/html/document2html.xsl"/>
  
  <xsl:template match="document">
    <div class="content">
     <div id="topmodule" align="right">
      <table border="0" cellspacing="0" cellpadding="3" width="100%">
       <!-- ( ================= middle NavBar ================== ) -->
        <tr>
        <td class="tasknav" >
          <div align="left">
           <!-- 
                site2html.xsl will search for this as 
                   
                   td[@class='tasknav']/div[@align='left']

                to insert the breadcrumbs.   
          -->
          </div>
        </td>
        <td id="issueid" class="tasknav">
        <div align="right">
            <xsl:call-template name="pdflink"/>
            <xsl:call-template name="printlink"/>
            <xsl:call-template name="xmllink"/>
	         &#160;
        </div>
        </td>
       </tr>
      </table>
     </div>
     
    <!-- ( ================= Content================== ) -->
    <div id="bodycol">
      <xsl:if test="normalize-space(header/title)!=''">
        <div id="apphead">
         <h2><em><xsl:value-of select="header/title"/></em></h2>
        </div>
      </xsl:if>
      <xsl:if test="normalize-space(header/subtitle)!=''">
        <h3><em><xsl:value-of select="header/subtitle"/></em></h3>
      </xsl:if>

      <div id="projecthome" class="app">
        <xsl:apply-templates select="body"/>

       <xsl:if test="header/authors">
        <p align="right">
          <font size="-2">
            <xsl:for-each select="header/authors/person">
              <xsl:choose>
                <xsl:when test="position()=1">by&#160;</xsl:when>
                <xsl:otherwise>,&#160;</xsl:otherwise>
              </xsl:choose>
              <xsl:value-of select="@name"/>
            </xsl:for-each>
          </font>
        </p>
      </xsl:if>
       </div>
      </div>
    </div>
  </xsl:template>

  <xsl:template match="body">

    <xsl:if test="section and $max-depth&gt;0 and not($notoc='true') and contains($minitoc-location,'menu')">
      <toc>
        <xsl:for-each select="section">
          <tocc>
            <toca>
              <xsl:attribute name="href">
                <xsl:text>#</xsl:text><xsl:call-template name="generate-id"/>
              </xsl:attribute>
              <xsl:value-of select="title"/>
            </toca>
            <xsl:if test="section">
              <toc2>
                <xsl:for-each select="section">
                  <tocc>
                    <toca>
                      <xsl:attribute name="href">
                        <xsl:text>#</xsl:text><xsl:call-template name="generate-id"/>
                      </xsl:attribute>
                      <xsl:value-of select="title"/>
                    </toca>
                  </tocc>
                </xsl:for-each>
              </toc2>
            </xsl:if>
          </tocc>
        </xsl:for-each>
      </toc>
    </xsl:if>
    
   <xsl:if test="$max-depth&gt;0 and not($notoc='true') and contains($minitoc-location,'page')" >
      <xsl:call-template name="minitoc">
        <xsl:with-param name="tocroot" select="."/>
        <xsl:with-param name="depth">1</xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template name="generate-id">
    <xsl:choose>
      <xsl:when test="@id">
        <xsl:value-of select="@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="generate-id(.)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@id">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="section">
    <a name="{generate-id()}"/>
    <xsl:apply-templates select="@id"/>

    <xsl:variable name = "level" select = "count(ancestor::section)+1" />

    <xsl:choose>
      <xsl:when test="$level=1">
       <div class="h3">
        <h3><xsl:value-of select="title"/></h3>
        <xsl:apply-templates/>
      </div>  
      </xsl:when>
      <xsl:when test="$level=2">
       <div class="h4">
        <h4><xsl:value-of select="title"/></h4>
        <xsl:apply-templates select="*[not(self::title)]"/>
      </div>  

      </xsl:when>
      <!-- If a faq, answer sections will be level 3 (1=Q/A, 2=part) -->
      <xsl:when test="$level=3 and $notoc='true'">
        <h4 class="faq"><xsl:value-of select="title"/></h4>
        <div align="right"><a href="#{@id}-menu">^</a></div>
        <div style="margin-left: 15px">
          <xsl:apply-templates select="*[not(self::title)]"/>
        </div>
      </xsl:when>
      <xsl:when test="$level=3">
        <h4><xsl:value-of select="title"/></h4>
        <xsl:apply-templates select="*[not(self::title)]"/>

      </xsl:when>

      <xsl:otherwise>
        <h5><xsl:value-of select="title"/></h5>
        <xsl:apply-templates select="*[not(self::title)]"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>  

  <xsl:template match="fixme">
    <div class="warningmessage">
      <p><strong>Fixme (<xsl:value-of select="@author"/>)</strong></p>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="note">
    <div class="infomessage">
      <p><strong>Note</strong></p>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="warning">
    <div class="errormessage">
      <p><strong>Warning</strong></p>
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="link">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="jump">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="fork">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="p[@xml:space='preserve']">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="source">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="anchor">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="icon">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="code">
    <xsl:apply-imports/>
  </xsl:template>

  <xsl:template match="figure">
    <xsl:apply-imports/>
  </xsl:template>
  
  <xsl:template match="table">
   <div class="h4">
    <xsl:apply-templates select="@id"/>
    <xsl:if test="caption">
        <h4><xsl:value-of select="caption"/></h4>
    </xsl:if> 
    <xsl:apply-templates select="caption"/>
      <table border="1" cellspacing="2" cellpadding="3" width="100%" class="grid">
        <xsl:if test="@cellspacing"><xsl:attribute name="cellspacing"><xsl:value-of select="@cellspacing"/></xsl:attribute></xsl:if>
        <xsl:if test="@cellpadding"><xsl:attribute name="cellpadding"><xsl:value-of select="@cellpadding"/></xsl:attribute></xsl:if>
        <xsl:if test="@border"><xsl:attribute name="border"><xsl:value-of select="@border"/></xsl:attribute></xsl:if>
        <xsl:if test="@class"><xsl:attribute name="class"><xsl:value-of select="@class"/></xsl:attribute></xsl:if>
        <xsl:if test="@bgcolor"><xsl:attribute name="bgcolor"><xsl:value-of select="@bgcolor"/></xsl:attribute></xsl:if>
      
        <xsl:apply-templates/>
    </table>
   </div>    
  </xsl:template>

  <xsl:template match="caption">
    <!-- do not show caption elements, they are already in other places-->  
  </xsl:template>
  
  <xsl:template match="title">
    <!-- do not show title elements, they are already in other places-->
  </xsl:template>

  <!-- Generates the PDF link -->
  <xsl:template name="pdflink">
    <xsl:if test="$dynamic-page='false'">
      <xsl:if test="not($config/disable-pdf-link) or $disable-pdf-link = 'false'"> 
        <a href="{$filename-noext}.pdf"><img border="0" src="{$skin-img-dir}/pdfdoc.gif" alt="PDF"/> PDF</a>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
    <!-- Generates the XML link -->
  <xsl:template name="xmllink">
    <xsl:if test="$dynamic-page='false'">
      <xsl:if test="$disable-xml-link = 'false'">
        <a href="{$filename-noext}.xml"><img border="0" src="{$skin-img-dir}/xmldoc.gif" alt="xml"/> xml</a>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  
    <!-- Generates the "printer friendly version" link -->
  <xsl:template name="printlink">
    <xsl:if test="$disable-print-link = 'false'"> 
<script type="text/javascript" language="Javascript">
function printit() {  
if (window.print) {
    window.print() ;  
} else {
    var WebBrowser = '&lt;OBJECT ID="WebBrowser1" WIDTH="0" HEIGHT="0" CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2">&lt;/OBJECT>';
document.body.insertAdjacentHTML('beforeEnd', WebBrowser);
    WebBrowser1.ExecWB(6, 2);//Use a 1 vs. a 2 for a prompting dialog box    WebBrowser1.outerHTML = "";  
}
}
</script>

<script type="text/javascript" language="Javascript">
var NS = (navigator.appName == "Netscape");
var VERSION = parseInt(navigator.appVersion);
if (VERSION > 3) {
    document.write('  <a href="javascript:printit()">');
    document.write('    <img border="0"  src="{$skin-img-dir}/printer.gif" alt="Print this Page"/>');
    document.write('  print</a>');
}
</script>

    </xsl:if>
  </xsl:template>
  
</xsl:stylesheet>
