<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="document">
    <td colspan="2" valign="top" class="content">
      <table>
        <tr>
          <td valign="middle">
            <xsl:choose>
              <xsl:when test="normalize-space(header/title)!=''">
                <h2>
                  <xsl:value-of select="header/title"/>
                </h2>
              </xsl:when>
              <xsl:otherwise> </xsl:otherwise>
            </xsl:choose>
          </td>
          <td align="center" width="80" nowrap="nowrap">
            <a href="#" class="dida">
              <img src="images/printer.gif"/>
              <br/>printer-friendly<br/>version</a>
          </td>
        </tr>
      </table>
      <xsl:if test="header/authors">
        <font size="-2">
          <p>
            <xsl:for-each select="header/authors/person">
              <xsl:choose>
                <xsl:when test="position()=1">by </xsl:when>
                <xsl:otherwise>, </xsl:otherwise>
              </xsl:choose>
              <xsl:value-of select="@name"/>
            </xsl:for-each>
          </p>
        </font>
      </xsl:if>
      <xsl:apply-templates select="body"/>
    </td>
  </xsl:template>

  <xsl:template match="body">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="section">
    <h3>
      <xsl:value-of select="@title"/>
    </h3>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="section/section">
    <h4>
      <xsl:value-of select="@title"/>
    </h4>
    <xsl:apply-templates/>
  </xsl:template>
  
  <xsl:template match="note | warning | fixme">
    <div class="frame {local-name()}">
      <div class="label">
        <xsl:choose>
          <xsl:when test="local-name() = 'note'">Note</xsl:when>
          <xsl:when test="local-name() = 'warning'">Warning</xsl:when>
          <xsl:otherwise>Fixme (<xsl:value-of select="@author"/>)</xsl:otherwise>
        </xsl:choose>
      </div>
      <div class="content"><xsl:apply-templates/></div>
    </div>
  </xsl:template>

<!--
  <xsl:template match="link | jump | fork">
-->
  <xsl:template match="link | fork">
    <a href="{@href}"><xsl:apply-templates/></a>
  </xsl:template>
  
  <xsl:template match="source">
    <pre class="code"><xsl:apply-templates/></pre>
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
    <table class="table" cellpadding="4" cellspacing="1">
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
