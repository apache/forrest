<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:include href = "split.xsl"/>

  <xsl:param name="isfaq"/>
  <xsl:param name="resource"/> <!-- Filename part, eg 'index' from index.html' -->
  <xsl:param name="dir"/>
  <xsl:include href="dotdots.xsl"/>

  <xsl:variable name="root">
    <xsl:call-template name="dotdots">
      <xsl:with-param name="path" select="$dir"/>
    </xsl:call-template>
  </xsl:variable>
 
  <xsl:variable name="skin-img-dir" select="concat(string($root), 'skin/images')"/>

  <xsl:template match="document">
    <div class="content">
      <xsl:if test="normalize-space(header/title)!=''">
        <table class="title">
          <tr> 
            <td valign="middle"> 
              <h1>
                <xsl:value-of select="header/title"/>
              </h1>
            </td>
            <!--td align="center" width="80" nowrap><a href="" class="dida"><img src="images/singlepage.gif"><br>
              single page<br>
              version</a></td>
            <td align="center" width="80" nowrap="nowrap"><a href="{$resource}.pdf" class="dida"><img border="0" src="{$skin-img-dir}/printer.gif"/><br/>
              print-friendly<br/>
              version</a></td>-->
          </tr>
        </table>
      </xsl:if>
      <xsl:if test="normalize-space(header/subtitle)!=''">
        <h3>
          <xsl:value-of select="header/subtitle"/>
        </h3>
      </xsl:if>
      <xsl:if test="header/authors">
        <p>
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
      <xsl:apply-templates select="body"/>
    </div>
  </xsl:template>
  <xsl:template match="body">
    <xsl:if test="section and not($isfaq='true')">
      <ul class="minitoc">
        <xsl:for-each select="section">
          <li>
            <a href="#{generate-id()}">
              <xsl:value-of select="title"/>
            </a>
            <xsl:if test="section">
              <ul class="minitoc">
                <xsl:for-each select="section">
                  <li>
                    <a href="#{generate-id()}">
                      <xsl:value-of select="title"/>
                    </a>
                  </li>
                </xsl:for-each>
              </ul>
            </xsl:if>
          </li>
        </xsl:for-each>
      </ul>
    </xsl:if>
    <xsl:apply-templates/>
  </xsl:template>
  <!--  section handling
  - <a name/> anchors are added if the id attribute is specified
  - generated anchors are still included for TOC - what should we do about this?
  - FIXME: provide a generic facility to process section irrelevant to their
    nesting depth
-->
  <xsl:template match="section">
    <a name="{generate-id()}"/>
    <xsl:if test="normalize-space(@id)!=''">
      <a name="{@id}"/>
    </xsl:if>
    <div class="section">
    <h3>
      <xsl:value-of select="title"/>
    </h3>
    <xsl:apply-templates select="*[not(self::title)]"/>
    </div>
  </xsl:template>
  <xsl:template match="section/section">
    <a name="{generate-id()}"/>
    <xsl:if test="normalize-space(@id)!=''">
      <a name="{@id}"/>
    </xsl:if>
    <div class="subsection">
    <h4>
      <xsl:value-of select="title"/>
    </h4>
    <xsl:apply-templates select="*[not(self::title)]"/>
    </div>
  </xsl:template>
  <xsl:template match="note | warning | fixme">
    <div class="frame {local-name()}">
      <div class="label">
        <xsl:choose>
          <xsl:when test="local-name() = 'note'">Note</xsl:when>
          <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
          <xsl:otherwise>Fixme (
               <xsl:value-of select="@author"/>

               )</xsl:otherwise>
        </xsl:choose>
      </div>
      <div class="content">
        <xsl:apply-templates/>
      </div>
    </div>
  </xsl:template>
  <xsl:template match="link">
    <a href="{@href}">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  <xsl:template match="jump">
    <a href="{@href}" target="_top">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  <xsl:template match="fork">
    <a href="{@href}" target="_blank">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  <xsl:template match="p[@xml:space='preserve']">
  <div class="pre">
    <xsl:apply-templates/>
  </div>
  </xsl:template>
  <xsl:template match="source">
    <pre class="code">
<!-- Temporarily removed long-line-splitter ... gives out-of-memory problems -->
      <xsl:apply-templates/>
<!--
    <xsl:call-template name="format">
    <xsl:with-param select="." name="txt" /> 
     <xsl:with-param name="width">80</xsl:with-param> 
     </xsl:call-template>
-->
    </pre>
  </xsl:template>
  <xsl:template match="anchor">
    <a name="{@id}"/>
  </xsl:template>
  <xsl:template match="icon">
    <img src="{@src}" alt="{@alt}">
      <xsl:if test="@height">
        <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
      </xsl:if>
      <xsl:if test="@width">
        <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
      </xsl:if>
    </img>
  </xsl:template>
  <xsl:template match="code">
    <span class="codefrag"><xsl:value-of select="."/></span>
  </xsl:template>
  <xsl:template match="figure">
    <div align="center">
      <img src="{@src}" alt="{@alt}" class="figure">
        <xsl:if test="@height">
          <xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="@width">
          <xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute>
        </xsl:if>
      </img>
    </div>
  </xsl:template>
  <xsl:template match="table">
    <table cellpadding="4" cellspacing="1" class="ForrestTable">
      <xsl:if test="@cellspacing"><xsl:attribute name="cellspacing"><xsl:value-of select="@cellspacing"/></xsl:attribute></xsl:if>
      <xsl:if test="@cellpadding"><xsl:attribute name="cellpadding"><xsl:value-of select="@cellpadding"/></xsl:attribute></xsl:if>
      <xsl:if test="@border"><xsl:attribute name="border"><xsl:value-of select="@border"/></xsl:attribute></xsl:if>
      <xsl:if test="@class"><xsl:attribute name="class"><xsl:value-of select="@class"/></xsl:attribute></xsl:if>
      <xsl:if test="@bgcolor"><xsl:attribute name="bgcolor"><xsl:value-of select="@bgcolor"/></xsl:attribute></xsl:if>
      <xsl:apply-templates/>
    </table>
  </xsl:template>
  <xsl:template match="node()|@*" priority="-1">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
