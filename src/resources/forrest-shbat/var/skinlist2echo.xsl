<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="skins">
      <project default="echoskins">
      	<target name="echoskins">
      	<echo>Available skins:</echo>
  	      <xsl:apply-templates select="skin" />
	    </target>
	  </project>
	</xsl:template>
	
	<xsl:template match="skin">
	    <echo><xsl:value-of select="@name"/></echo>
	</xsl:template>	
	
</xsl:stylesheet>