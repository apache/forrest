<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:param name="resource"/>

  <xsl:template match="book">
    <menu>
      <xsl:apply-templates/>
    </menu>
  </xsl:template>

<!--
  <xsl:template match="menu[position()=1]">
    <xsl:apply-templates/>
  </xsl:template>
-->

  <xsl:template match="menu">
     <br/><b><xsl:value-of select="@label"/></b>&#160;
     <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="menu-item">-&#160;
    <xsl:if test="not(@type) or @type!='hidden'">
       <xsl:choose>
         <xsl:when test="starts-with(@href, $resource)">
          <xsl:value-of select="@label"/>
         </xsl:when>
         <xsl:otherwise>
          <a href="{@href}"><xsl:value-of select="@label"/></a>
        </xsl:otherwise>
       </xsl:choose>
     </xsl:if>&#160;
  </xsl:template>

  <xsl:template match="external">
    <xsl:if test="not(@type) or @type!='hidden'">&#160;-
      <a href="{@href}" target="_blank"><xsl:value-of select="@label"/></a>&#160;
    </xsl:if>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1"/>
</xsl:stylesheet>

