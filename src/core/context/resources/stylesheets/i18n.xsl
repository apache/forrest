<?xml version="1.0"?>

<!--+
    | Add i18n tags so it can be processer.
    |
    | CVS $Id: i18n.xsl,v 1.1 2003/10/20 15:38:24 nicolaken Exp $
    +-->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:i18n="http://apache.org/cocoon/i18n/2.1" >

  <xsl:import href="copyover.xsl"/>

  <xsl:template match="@label">
   <xsl:attribute name="i18n:attr">label</xsl:attribute>
   <xsl:attribute name="label">
     <xsl:value-of select="." />
   </xsl:attribute>
     <xsl:apply-templates />
  </xsl:template>
  
  <!-- FIXME: Need support for more than one attribute
  <xsl:template match="@description">
   <xsl:attribute name="i18n:attr">description</xsl:attribute>
   <xsl:attribute name="description">
     <xsl:value-of select="." />
   </xsl:attribute>
     <xsl:apply-templates />
  </xsl:template>
  -->

</xsl:stylesheet>
