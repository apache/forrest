<?xml version="1.0"?>

<!--+
    | Replace element for the value on the project descriptor 
    | xmlns:for has to be replaced for the final version
    |
    | Author: Juan Jose Pablos "cheche@apache.org"
    | 
    | CVS $\Id$
    +-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:for="http://xml.apache.org/forrest" version="1.0">

<xsl:import href="copyover.xsl"/>

<xsl:param name="config-file" select="'../../skinconf.xml'"/>
  <xsl:variable name="config" select="document($config-file)/skinconfig"/>

<xsl:template match="for:project-name">
    <xsl:value-of select="$config/project-name"/>
  </xsl:template>

<xsl:template match="for:group-name">
    <xsl:value-of select="$config/group-name"/>
  </xsl:template>
</xsl:stylesheet>
