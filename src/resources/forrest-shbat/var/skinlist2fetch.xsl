<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   
    <xsl:param name="skin-name"/>

	<xsl:template match="skins">
      <project default="fetchskin">
      	<target name="fetchskin">
         <get>
           <xsl:attribute  name="src" >http://www.apache.org/~nicolaken/whiteboard/forrestskins/<xsl:value-of select="$skin-name"/>.fsj</xsl:attribute>
           <xsl:attribute  name="dest">${forrest.home}/context/skins/<xsl:value-of select="$skin-name"/>.fsj</xsl:attribute>
         </get>
	    </target>
	  </project>
	</xsl:template>
	
	<xsl:template match="skin">
	    <echo><xsl:value-of select="@name"/></echo>
	</xsl:template>	
	
</xsl:stylesheet>