<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:NetUtils="org.apache.cocoon.util.NetUtils"
                version="1.0">

  <xsl:param name="resource"/>

  <xsl:template match="book">
    <menu>
      <xsl:apply-templates/>
    </menu>
  </xsl:template>

  <xsl:template match="project">
  </xsl:template>

  <xsl:template match="menu[position()=1]">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="menu">
     <!-- Encode label to escape any reserved characters such as space -->
     <xsl:variable name="encLabel" select="NetUtils:encodePath(@label)"/>
     <tr>
      <td valign="top" bgcolor="#959595" background="skin/images/label-background_b.gif">
       <img src="skin/images/separator.gif"/><br/>
       <span class="menutitle"><xsl:value-of select="@label"/></span></td>
     </tr>
     <xsl:apply-templates/>
  </xsl:template>


  <xsl:template match="menu-item">
    <xsl:if test="not(@type) or @type!='hidden'">
     <xsl:variable name="encLabel" select="NetUtils:encodePath(@label)"/>
     <tr>
      <td bgcolor="#959595" valign="top">
       <xsl:attribute name="background">skin/images/label-background_a.gif</xsl:attribute>
       <xsl:choose>
         <xsl:when test="@href=$resource">
           <span class="menuselected"><xsl:value-of select="@label"/></span>
         </xsl:when>
         <xsl:otherwise>
           <a href="{@href}" class="menu"><xsl:value-of select="@label"/></a>
         </xsl:otherwise>
       </xsl:choose>
      </td>
     </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template match="external">
    <xsl:if test="not(@type) or @type!='hidden'">
     <xsl:variable name="encLabel" select="NetUtils:encodePath(@label)"/>
     <tr><td bgcolor="#959595" background="skin/images/label-background_a.gif" valign="top">
        <a href="{@href}" target="new" class="menu"><xsl:value-of select="@label"/></a>
	</td>
     </tr>
    </xsl:if>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1"/>
</xsl:stylesheet>

