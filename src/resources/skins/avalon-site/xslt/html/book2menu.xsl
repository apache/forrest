<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:param name="resource"/>

  <xsl:template match="book">
    <menu>
      <xsl:apply-templates/>
    </menu>
  </xsl:template>

  <xsl:template match="project">
  </xsl:template>
<!--
  <xsl:template match="menu[position()=1]">
    <xsl:apply-templates/>
  </xsl:template>
-->
  <xsl:template match="menu">
    <div id="submenu">
      <h4><xsl:value-of select="@label"/></h4>
       <ul>      
        <xsl:apply-templates/>
      </ul>     
    </div>     
  </xsl:template>

  <xsl:template match="menu-item">
   <li> 
    <xsl:if test="not(@type) or @type!='hidden'">
       <xsl:choose>
         <xsl:when test="@href=$resource">
          <xsl:value-of select="@label"/>
         </xsl:when>
         <xsl:otherwise>
          <a href="{@href}"><xsl:value-of select="@label"/></a>
        </xsl:otherwise>
       </xsl:choose>
     </xsl:if>
    </li>      
  </xsl:template>

  <xsl:template match="external">
    <xsl:if test="not(@type) or @type!='hidden'">
     <li>
      <a href="{@href}"><xsl:value-of select="@label"/></a>
     </li> 
    </xsl:if>
  </xsl:template>

  <xsl:template match="node()|@*" priority="-1"/>
</xsl:stylesheet>

