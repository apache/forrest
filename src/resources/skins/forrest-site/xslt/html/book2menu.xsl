<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:template match="book">
      <div class="menu">
         <ul>
            <xsl:apply-templates select="menu" />
         </ul>
      </div>
   </xsl:template>

   <xsl:template match="menu">
      <li>
         <xsl:value-of select="@label" />

         <xsl:if test="menu-item | external">
            <ul>
               <xsl:apply-templates select="menu-item | external" />
            </ul>
         </xsl:if>
      </li>
   </xsl:template>

   <xsl:template match="menu-item[@type='hidden']" />

   <xsl:template match="external[@type='hidden']" />

   <xsl:template match="menu-item | external">
      <li>
         <a href="{@href}">
            <xsl:value-of select="@label" />
         </a>
      </li>
   </xsl:template>
</xsl:stylesheet>

